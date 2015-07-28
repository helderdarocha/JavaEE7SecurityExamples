/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.jaasexample;

import br.com.argonavis.privileged.test.TestAction;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessControlException;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

/**
 * IMPORTANT: How to execute this using the command line: java
 * -Djava.security.auth.login.config=classes/java.login.config
 * -Djava.security.policy=classes/example.policy -Djava.security.manager -cp
 * JAASExample-1.0.0.jar:../../JAASExampleTestAction/target/JAASExampleTestAction-1.0.0.jar
 * br.com.argonavis.jaasexample.AuthorizationExample
 *
 * @author helderdarocha
 */
public class AuthorizationExample {

    public static void main(String[] args) {

        try {
            LoginContext loginCtx = AuthenticationExample.authenticate();

            Subject subject = loginCtx.getSubject();

            for (Principal principal : subject.getPrincipals()) {
                System.out.println("Principal: " + principal.getName());
            }

            // Only user faisca can do this
            PrivilegedAction action1 = new TestAction();
            
            System.out.println("Will try to execute privileged action 1 (file and property)");
            try {
                Subject.doAsPrivileged(subject, action1, null);
            } catch (AccessControlException e) {
                System.out.println("Access is DENIED!\n" + e);
            }
            
            // Only user fumaca can do this
            PrivilegedAction action2 = new PrivilegedAction() {
                @Override
                public Object run() {
                    System.out.println("Will try to connect to an URL (www.example.com)");
                    URL url = null;
                    try {
                        url = new URL("http://www.example.com");
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(AuthorizationExample.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (url != null) {
                        try (InputStream is = url.openStream()) {
                            System.out.println("Contents of the URL\n--------------\n");
                            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                            String line;
                            StringBuilder buffer = new StringBuilder();
                            while ((line = reader.readLine()) != null) {
                                buffer.append(line).append("\n");
                            }
                            System.out.println(buffer.toString());
                        } catch (IOException ex) {
                            Logger.getLogger(AuthorizationExample.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    return null;
                }
            };

            System.out.println("Will try to execute privileged action 2 (socket and URL)");
            try {
                Subject.doAsPrivileged(subject, action2, null);
            } catch (AccessControlException e) {
                System.out.println("Access is DENIED!\n" + e);
            }

        } catch (LoginException ex) {
            Logger.getLogger(AuthenticationExample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
