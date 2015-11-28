package redes;

import java.math.BigInteger;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que se encarga de emular al emisor de los paquetes el cual se
 * encarga de estar enviando estos paquetes, pero esta ocasión cifrando
 * los datos con RSA
 * @author Alcantara Jimenez Joel Armando.
 */
public class Emisor extends Thread{
    
    String archivo;
    ConcurrentLinkedQueue<Segment> buffer;
    ComunicationChannel canal;
    Rsa llaveE;
    Rsa llaveR;
    byte[] bytes;
    int totalPaquetes;
    int sobrante;
    
    /**
     * Constructor de clase que recibe el nombre del archivo que se va a enciar
     * así como el canal que se utilizara y el buffer de las alertas.
     * Para la nueva version también los datos de llaves para el cifrado.
     * @param archivo
     * @param buffer
     * @param canal
     * @param llaveE
     * @param llaveR
     */
    public Emisor(String archivo, ConcurrentLinkedQueue<Segment> buffer, ComunicationChannel canal,Rsa llaveE, Rsa llaveR){
        try {
            this.archivo = archivo;
            this.buffer = buffer;
            this.canal = canal;
            this.bytes = getBytesFromFile();
            this.totalPaquetes = (bytes.length/65535);
            this.sobrante = bytes.length-(totalPaquetes*65535);
            this.llaveE = llaveE;
            this.llaveR = llaveR;
            totalPaquetes = sobrante!=0? totalPaquetes+1:totalPaquetes;
        } catch (IOException ex) {
            Logger.getLogger(Emisor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * *Metodo que se encarga de generar a partir del archivo un arreglo de 
     * bytes que lo representan.
     * @return arreglo de bytes
     * @throws IOException 
     */
    public byte[] getBytesFromFile() throws IOException{        
        File file = new File(archivo);
        long length = file.length();

        if (length > Integer.MAX_VALUE) {
            throw new IOException("File is too large!");
        }

        byte[] bytes = new byte[(int)length];
        int offset = 0;
        int numRead = 0;

        InputStream is = new FileInputStream(file);
        try {
            while (offset < bytes.length
                   && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
                offset += numRead;
            }
        } finally {
            is.close();
        }
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
        return bytes;
    }
    
    /**
     * Metodo que se encarga de generar un arreglo de paquetes que serán aquellos
     * que se enviaran a traves del canal.
     * @return Arreglo de Segmentos a enviar.
     */
    public TcpSegment[] paquetes(){
        TcpSegment[] paquetes = new TcpSegment[totalPaquetes];
        int cont = 0;
        System.out.println(bytes.length);
        System.out.print(totalPaquetes);
        for(int i = 0; i < bytes.length;){
            int tope = 65535;
            if(bytes.length-i<=sobrante)
                tope = sobrante;
            System.out.println(tope);
            byte[] datos = new byte[tope];
            for(int j = 0;j<tope;j++){
                datos[j] = bytes[i++];
            }
            // El Cliente firma con su privada:
            BigInteger firmado = llaveE.encrypt(new BigInteger(datos), llaveE.getPrivateKey());

            // El Cliente cifra con la publica del servidor:
            BigInteger cifrado = llaveE.encrypt(firmado, llaveR.getPublicKey());
            paquetes[cont] = new TcpSegment(datos, cifrado, cont++, totalPaquetes, archivo, bytes.length);
        }
        return paquetes;
    }
    
    /**
     * Metodo heredado de la clase Thread que se encarga de estar enviando
     * a traves del canal los segmentos.
     */
    public void run(){
        TcpSegment[] paquetes = paquetes();
        while(true){
            for(Segment s: paquetes)
                canal.sendDataSegment(s);
        }
    }
    
}
