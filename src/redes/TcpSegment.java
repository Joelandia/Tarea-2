/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redes;

import java.math.BigInteger;

/**
 * Metodo que se encarga de emular un paquete de datos a enviar.
 * @author Alcantara Jimenez Joel Armando
 */
public class TcpSegment implements Segment{
    
    byte[] arreglo; //En la priver version se mandaban los bytes directamente
    BigInteger cifrado; //ahora se manda un dato cifrado.
    int numPaquete;
    int totalPaquete;
    int totalBytes;
    byte[] suma;
    String nombre;
    
    public TcpSegment(byte[] arreglo, BigInteger cifrado, int numPaquete, int totalPaquete, String nombre, int totalBytes){
        //this.arreglo = arreglo; Parte de la primer version sin cifrar.
        this.arreglo = arreglo;
        this.cifrado = cifrado;
        this.numPaquete = numPaquete;
        this.totalPaquete = totalPaquete;
        suma = getChecksum();
        this.nombre = nombre;
        this.totalBytes = totalBytes;
    }
    
    /**
	 * Método que se encarga de regresar el payload del segmento
	 * es decir, el arreglo de bytes de datos.
	 * @return <byte[]>
	 */
	public byte[] getPayload(){
            return arreglo;
        }
	/**
	 * Método que se encarga de modificar el payload del segmento
	 * @param newPayload <byte[]> nuevo payload del segmento
	 */
	public void setPayload(byte[] newPayload){
            arreglo = newPayload;
        }
	/**
	 * Método que no dice si el segmento se produjo por un error 
	 * o no.
	 * @return <boolean> 
	 */
	public boolean isWarningSegment(){
            return suma.equals(getChecksum());
        }
	
	/**
	 * Método que regresa la suma en complemento A1.
	 * @return <byte[]>
	 */
	public byte[] getChecksum(){
            byte sum = 0;
            byte[] arr = {sum};
            for(int i = 0; i < arreglo.length; i++)
                sum = (byte)(sum + arreglo[i]);
            sum = (byte)~(sum-1);
            arr[0] = sum;
            return arr;
        }
        
	/**
	 * Método que modifica el checksum de un segmento
	 * @param checksum <byte[]> nuevo checksum del segmento
	 */
	public void setChecksum(byte[] newChecksum){
            suma = newChecksum;
        }
	
	/**
	 * Método que regresa qué número de segmento es, de todo 
	 * el conjunto de segmentos de datos.
	 * @return <int>
	 */
	public int getNumberOfSegment(){
            return numPaquete;
        }
	
	/**
	 * Método que regresa cuantos segmentos en total se mandaron
	 * del cliente al servidor.
	 * @return <int>
	 */
	public int getNumberTotal(){
            return totalPaquete;
        }
}
