/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.jaasexample;

import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

/**
 * IMPORTANT: To execute this example, either: - place the java.login.config
 * file in your home directory (${user.home}), or - pass it via system property
 * -Djava.security.auth.login.config=<path-to>/java.login.config Example: java
 * -Djava.security.auth.login.config=classes/java.login.config -cp
 * JAASExample-1.0.0.jar br.com.argonavis.jaasexample.AuthenticationExample
 *
 * @author helderdarocha
 */
public class AuthenticationExample {

    public static void main(String[] args) {
        try {
            LoginContext loginCtx = authenticate();
            
            for(Principal principal: loginCtx.getSubject().getPrincipals()) {
                System.out.println("Principal: " + principal.getName());
            }

        } catch (LoginException ex) {
            Logger.getLogger(AuthenticationExample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static LoginContext authenticate() throws LoginException {
        
        if (!System.getProperties().containsKey("java.security.auth.login.config")) {
            System.err.println("FATAL ERROR: Must configure property with path to java.login.config file - see javadocs for this class.");
            System.exit(1);
        }
        
        // Command-line callback handler (com.sun.security.auth.callback.TextCallbackHandler)
        //LoginContext loginCtx = new LoginContext("SimpleJaasConfig", new TextCallbackHandler());

        // Simple dialog callback handler (com.sun.security.auth.callback.DialogCallbackHandler)
        //LoginContext loginCtx = new LoginContext("SimpleJaasConfig", new DialogCallbackHandler());
        
        // Another DialogCallbackHandler - see source in br.com.argonavis.jaasexample.SwingCallbackHandler
        LoginContext loginCtx = new LoginContext("SimpleJaasConfig", new SwingCallbackHandler());

        loginCtx.login();
        System.out.println("Authentication was successful!");

        return loginCtx;
    }
}
