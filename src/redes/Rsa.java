package redes;

/*
    Author:     Alberto Gil Tesa
    WebSite:    http://giltesa.com
    License:    CC BY-NC-SA 3.0
                http://goo.gl/CTYnN

    File:       Rsa.java
    Date:       26/02/2013
    
    NOTAS:
        http://es.wikipedia.org/wiki/RSA
*/


import java.math.BigInteger;
import java.security.SecureRandom;


public class Rsa
{
    private BigInteger n; // Clave compartida
    private BigInteger d; // Clave privada (no se comparte)
    private BigInteger k; // Clave publica para la otra parte
    private final int  K = 0, D = 0, N = 1;


    /**
     * Constructor que genera las claves publica y privada de tamaño bitlen indicado.
     * 
     * @param bitlen
     */
    public Rsa(int bitlen)
    {
        // Calculamos los dos numeros primos p y q:
        SecureRandom r = new SecureRandom();
        BigInteger p = new BigInteger(bitlen / 2, 100, r);
        BigInteger q = new BigInteger(bitlen / 2, 100, r);

        //n = p*q, v= (p-1)*(q-1)
        n = p.multiply(q);
        BigInteger v = ( p.subtract(BigInteger.ONE) ).multiply(q.subtract(BigInteger.ONE));

        // Calculamos k como el numero impar mas pequeño relativamente primo con v
        k = new BigInteger("3");
        while( v.gcd(k).intValue() > 1 )
            k = k.add(new BigInteger("2"));

        // Calculamos d de modo que (d * k) MOD V = 1
        d = k.modInverse(v);
    }


    /**
     * Pasa el BigInteger normal a cifrado usando la clave publica.
     * 
     * @param message
     * @return
     */
    public BigInteger encrypt(BigInteger message)
    {
        return message.modPow(k, n);
    }


    /**
     * Pasa el BigInteger normal a cifrado usando la clave publica recibida.
     * 
     * @param message
     * @param key
     * @return
     */
    public BigInteger encrypt(BigInteger message, BigInteger[] key)
    {
        return message.modPow(key[K], key[N]);
    }


    /**
     * Pasa el BigInteger de cifrado a normal usando la clave privada.
     * 
     * @param message
     * @return
     */
    public BigInteger decrypt(BigInteger message)
    {
        return message.modPow(d, n);
    }


    /**
     * Pasa el BigInteger de cifrado a normal usando la clave privada recibida.
     * 
     * @param message
     * @return
     */
    public BigInteger decrypt(BigInteger message, BigInteger[] key)
    {
        return message.modPow(key[D], key[N]);
    }


    /**
     * Devuelve la clave publica.
     * 
     * @return
     */
    public BigInteger[] getPublicKey()
    {
        return new BigInteger[] { k, n };
    }


    /**
     * Devuelve la clave privada.
     * 
     * @return
     */
    public BigInteger[] getPrivateKey()
    {
        return new BigInteger[] { d, n };
    }

}