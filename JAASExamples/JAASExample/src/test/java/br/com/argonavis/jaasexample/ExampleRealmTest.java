/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.jaasexample;

import br.com.argonavis.jaasexample.realm.RealmUtils;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author helderdarocha
 */
public class ExampleRealmTest {

    public ExampleRealmTest() {
    }


    @Test
    public void testMakePasswordHashString() throws Exception {
        String password = "$bé1 \tя\u3643";
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        sha.update(password.getBytes(StandardCharsets.UTF_8));
        byte[] expectedResult = sha.digest();

        assertArrayEquals(expectedResult, RealmUtils.makePasswordHash(password));
    }
    
    @Test
    public void testMakePasswordHashChars() throws Exception {
        char[] password = {'$', 'b', 'é', '1', ' ', '\t', 'я', '\u0043'};
        byte[] array = RealmUtils.charArrayToByteArray(password);
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        sha.update(array);
        byte[] expectedResult = sha.digest();

        assertArrayEquals(expectedResult, RealmUtils.makePasswordHash(password));
    }

    @Test
    public void testConvertToHex() {
        char[] charArray = {'$', 'b', 'é', '1', ' ', '\t', 'я', '\u0043'};
        byte[] array = RealmUtils.charArrayToByteArray(charArray); // tested
        StringBuilder sb = new StringBuilder();
        for (byte b : array) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        System.out.println("Result: " + sb.toString());
        assertEquals(sb.toString(), RealmUtils.convertToHex(array));
    }

}
