/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redes;

import java.math.BigInteger;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que se encarga de emular al receptor de paquetes el cual lo 
 * juntará y creara un nuevo archivo de los paquetes recibidos.
 * @author Alcantara Jimenez Joel Armando.
 */
public class Receptor extends Thread{
    
    ConcurrentLinkedQueue<Segment> buffer;
    TcpSegment segmento;
    int longitud;
    TcpSegment[] paquetes;
    String archivo = "copia";
    int contador = 0;
    boolean finalizado = false;
    Rsa llaveE, llaveR;
    
    /**
     * Constructor de clase, que recibe el buffer de donde se obtendrán los
     * paquetes, así como un paquete inicial para determinar la información 
     * del archivo que recibirá.
     * Para la nueva versión, también las llaves para el descifrado.
     * @param buffer
     * @param segmento 
     * @param llaveE 
     * @param llaveR 
     */
    public Receptor(ConcurrentLinkedQueue<Segment> buffer, TcpSegment segmento,Rsa llaveE, Rsa llaveR){
        this.buffer = buffer;
        this.segmento = segmento;
        this.llaveE = llaveE;
        this.llaveR = llaveR;
        longitud = segmento.totalPaquete;
        archivo += segmento.nombre;
        System.out.println("Total de paquetes " + segmento.totalPaquete);
        paquetes = new TcpSegment[longitud];
    }
    
    /**
     * Metodo que se encarga de convertir un arreglo de bytes en un archivo con
     * el nombre recibido como parametro.
     * @param array
     * @param nombre
     * @throws IOException 
     */
    public void byteArrayToFile(byte[] array, String nombre) throws IOException{
        FileOutputStream stream = new FileOutputStream(nombre);
        stream.write(array);
    }
    
    /**
     * Este metodo se encarga de generar el arreglo de bytes correspondiente 
     * para que se recupere el archivo enviado.
     * @throws IOException 
     */
    public void generaArchivo() throws IOException{
        byte[] bytes = new byte[segmento.totalBytes];
        int cont = 0;
        for(TcpSegment s: paquetes){ 
            // El Servidor usa su clave privada para descifrar:
            BigInteger descifrado = llaveR.decrypt(s.cifrado);

            // El Servidor usa la clave publica del cliente para Desfirmar:
            BigInteger desfirmado = llaveR.decrypt(descifrado, llaveE.getPublicKey());
            byte[] arreglo = desfirmado.toByteArray();
            for(byte b: arreglo){
                bytes[cont++] = b;
            }
        }
        byteArrayToFile(bytes, archivo);
    }
    
    /**
     * Metodo encargado de verificar si ya se han recibido todos los paquetes.
     * @return true si ya estan todos los paquetes, false eoc.
     */
    public boolean isReady(){
        return longitud == contador;
    }
    
    /**
     * Metodo encargado de ingresar un segmento al arreglo de segmentos, haciendo
     * el cotejo de que este segmento no sea duplicado.
     * @param segmento 
     */
    private void meteSegmento(TcpSegment segmento){
        if(paquetes[segmento.numPaquete]==null){
            paquetes[segmento.numPaquete] = segmento;
            contador++;
    
        }
    }
    
    /**
     * Metodo que se encarga de hacer que el receptor se encuentre trabajando
     * mientras no haya recibido todo el archivo.
     */
    public void run(){
        while(!finalizado){
            if(!buffer.isEmpty()){
                TcpSegment auxiliar = (TcpSegment)buffer.poll();
                if(!auxiliar.isWarningSegment()){
                    meteSegmento(auxiliar);
                    System.out.println("Llevo " + contador + " paquetes ingresados");
                    System.out.println("Meti el numero" + auxiliar.numPaquete);
                }
            }
            if(isReady()){
                try {
                    generaArchivo();
                    finalizado = true;
                    System.out.println("Ya se ha generado la copia del archivo en el mismo directorio.");
                } catch (IOException ex) {
                    Logger.getLogger(Receptor.class.getName()).log(Level.SEVERE, null, ex);
                }
                finalizado = true;
            }
        }
    }
    
}
