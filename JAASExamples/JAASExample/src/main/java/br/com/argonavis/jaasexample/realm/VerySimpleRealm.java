/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.jaasexample.realm;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author helderdarocha
 */
public class VerySimpleRealm {

    // Realm is simple database with users and passwords
    private static final Map<String, String> users = new HashMap<>();

    static {
        users.put("faisca", RealmUtils.convertToHex(RealmUtils.makePasswordHash("1234"))); 
        users.put("fumaca", RealmUtils.convertToHex(RealmUtils.makePasswordHash("1234"))); 
    }
    
    public static void authenticate(String user, char[] password) throws AuthenticationException {
        if(!users.containsKey(user)) {
            throw new AuthenticationException("Unknown user: " + user);
        }
        
        // Convert received password to SHA-1 Hash
        String receivedHash = RealmUtils.convertToHex(RealmUtils.makePasswordHash(password));
        
        // Compare received hash to stored hash
        if(!users.get(user).equals(receivedHash)) {
            throw new AuthenticationException("Wrong password for " + user);
        }
    }

}
