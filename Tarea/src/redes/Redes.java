package redes;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * Clase que se encarga de emular el programa donde se corren los 2 hilos,
 * el emisor y receptor, estos utilizando el canal de comunicación previsto.
 * @author Alcantara Jimenez Joel Armando.
 */
public class Redes {

    /**
     * 
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        try{
            System.out.println("Cual es el nombre del archivo?");
            java.util.Scanner in = new java.util.Scanner(System.in);
            String archivo = in.next();
            ConcurrentLinkedQueue<Segment> warningsIn = new ConcurrentLinkedQueue<>();
            ConcurrentLinkedQueue<Segment> warningsOut = new ConcurrentLinkedQueue<>();

            ConcurrentLinkedQueue<Segment> bufferIn = new ConcurrentLinkedQueue<>();
            ConcurrentLinkedQueue<Segment> bufferOut = new ConcurrentLinkedQueue<>();

            ComunicationChannel canal = new ComunicationChannel(10, bufferIn, bufferOut, warningsIn, warningsOut);

            Rsa llaveE = new Rsa(1024);
            Rsa llaveR = new Rsa(1024);

            Emisor emisor = new Emisor(archivo, warningsIn, canal, llaveE, llaveR);
            emisor.start();
            TcpSegment segmento = (TcpSegment)bufferOut.peek();
            while(segmento==null)
                segmento = (TcpSegment)bufferOut.peek();
            Receptor receptor = new Receptor(bufferOut, segmento,llaveE, llaveR);
            receptor.start();
        }catch(Exception e){
            System.out.println("Error el archivo no existe o lo ingresaste mal!");
        }
    }
    
}
