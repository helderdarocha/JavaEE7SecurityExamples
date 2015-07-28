/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.jaasexample.realm;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

/**
 * Example very simple LoginModule to demonstrate some JAAS authentication features.
 * Based on: http://docs.oracle.com/javase/8/docs/technotes/guides/security/jaas/JAASLMDevGuide.html
 * @author helderdarocha
 */
public class VerySimpleLoginModule implements LoginModule {

    private Subject subject;
    private CallbackHandler callbackHandler;

    private boolean authSuccess = false;
    private boolean commitSuccess = false;
    
    private String username;
    private Principal principal;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        // This example does not use shared state or login options
        // Use javax.security.auth.login.name and javax.security.auth.login.password as keys in
        // sharedState if user/password sharing among LoginModules is desired
    }

    @Override
    public boolean login() throws LoginException {
        if (this.callbackHandler == null) {
            throw new LoginException("No CallbackHandler available!");
        }

        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("username: "); // String is prompt
        callbacks[1] = new PasswordCallback("password: ", false);

        try {
            this.callbackHandler.handle(callbacks); // fetches username and password from callbacks

            NameCallback nameCallback = (NameCallback) callbacks[0];
            PasswordCallback passwordCallback = (PasswordCallback) callbacks[1];

            this.username = nameCallback.getName();
            char[] password = passwordCallback.getPassword();
            if (password == null) {
                password = new char[0];
            }

            passwordCallback.clearPassword();

            // Authenticate with realm
            try {
                VerySimpleRealm.authenticate(this.username, password);
                System.out.println("Authentication successful!");
                this.authSuccess = true;
                return true;
            } catch (AuthenticationException e) {
                System.out.println("Authentication failed.");
                this.authSuccess = false;
                return false;
            }

        } catch (IOException | UnsupportedCallbackException e) {
            throw new LoginException(e.getMessage());
        }
    }

    @Override
    public boolean commit() throws LoginException {
        if (!this.authSuccess) {
            return false;
        } else {
            this.principal = new VerySimplePrincipal(this.username);

            if (!this.subject.getPrincipals().contains(this.principal)) {
                this.subject.getPrincipals().add(this.principal);
            }

            this.username = null;
            this.commitSuccess = true;
            return true;
        }
    }

    @Override
    public boolean abort() throws LoginException {
        if (!this.authSuccess) {
            return false;
        } else {
            if (!this.commitSuccess) {
                this.authSuccess = false;
                this.username = null;
                this.principal = null;
            } else {
                logout();
            }
            return true;
        }
    }

    @Override
    public boolean logout() throws LoginException {
        subject.getPrincipals().remove(this.principal);
        this.authSuccess = false;
        this.commitSuccess = false;
        this.username = null;
        return true;
    }

}
