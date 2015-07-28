/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.jaspictut.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple factory for authentication services. If you add a new one, 
 * include it in the constructor;
 * 
 * @author helderdarocha
 */
public class AuthenticationServiceFactory {
    // static initializer
    private static AuthenticationServiceFactory factory = new AuthenticationServiceFactory();
    public static AuthenticationServiceFactory getFactory() {
        return factory;
    } 
    
    private Map<String, AuthenticationService> services = new HashMap<>();
    
    /**
     * Initializes a simple registry of auth services.
     */
    private AuthenticationServiceFactory() {
        services.put("google", new GoogleSignInService());
        services.put("facebook", new FacebookConnectService());
        // include new services here
    }
    
    /**
     * Returns an authentication service
     * @param key
     * @return 
     */
    public AuthenticationService getService(String key) {
        return services.get(key);
    }
    

    
}
