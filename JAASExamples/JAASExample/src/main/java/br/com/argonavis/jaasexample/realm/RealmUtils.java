/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.jaasexample.realm;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author helderdarocha
 */
public class RealmUtils {

    public static byte[] makePasswordHash(char[] password) {
        byte[] hash = null;
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            sha.update(charArrayToByteArray(password));
            hash = sha.digest();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(VerySimpleRealm.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Configuration error: missing algorithm. Unable to setup realm.");
        }
        return hash;
    }

    public static byte[] makePasswordHash(String password) {
        byte[] hash = null;
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            sha.update(password.getBytes(StandardCharsets.UTF_8));
            hash = sha.digest();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(VerySimpleRealm.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Configuration error: missing algorithm. Unable to setup realm.");
        }
        return hash;
    }

    public static byte[] charArrayToByteArray(char[] chars) {
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = Charset.forName(StandardCharsets.UTF_8.toString()).encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
        return bytes;
    }

    public static String charArrayToHash(char[] chars) {
        byte[] bytes = charArrayToByteArray(chars);
        return convertToHex(bytes);
    }

    /**
     * Must pad 1-digit hex with zeroes: F --> 0F
     *
     * @param hash byte array
     * @return Hex string of byte array
     */
    public static String convertToHex(byte[] hash) {
        StringBuilder buffer = new StringBuilder();
        for (byte b : hash) {
            int number = 255 & b;
            String hexDigit = Integer.toHexString(number);
            if (hexDigit.length() == 1) {
                buffer.append('0');
            }
            buffer.append(hexDigit);
        }
        return buffer.toString();
    }

}
