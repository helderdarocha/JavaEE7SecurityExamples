/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.jaasexample;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

/**
 *
 * @author helderdarocha
 */
public class SwingCallbackHandler implements CallbackHandler {
    
    private final String username;
    private final char[] password;

    public SwingCallbackHandler() {
        LoginDialog dialog = new LoginDialog("User Authentication Required");
        this.username = dialog.getUser();
        this.password = dialog.getPass();
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof NameCallback) {
                NameCallback nc = (NameCallback) callback;
                nc.setName(username);
            } else if (callback instanceof PasswordCallback) {
                PasswordCallback pc = (PasswordCallback) callback;
                pc.setPassword(password);
            } else {
                throw new UnsupportedCallbackException(callback);
            }
        }

    }

}
