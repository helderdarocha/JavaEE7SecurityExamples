/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.jaspictut.config;

import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.security.auth.message.config.AuthConfigProvider;
import javax.security.auth.message.config.ClientAuthConfig;
import javax.security.auth.message.config.ServerAuthConfig;

/**
 * Based on http://arjan-tijms.omnifaces.org/2012/11/implementing-container-authentication.html
 */
class AuthConfigProviderImpl implements AuthConfigProvider {

    private static final String CALLBACK_HANDLER = "authconfigprovider.client.callbackhandler";
    private Map<String, String> providerProperties;

    public AuthConfigProviderImpl() {
    }

    public AuthConfigProviderImpl(Map<String, String> props, AuthConfigFactory factory) {
        this.providerProperties = props;
        // Self-registration mandatory if factory provided
        if (factory != null) {
            factory.registerConfigProvider(this, null, null, "Self-registration");
        }
    }

    @Override
    public ClientAuthConfig getClientAuthConfig(String layer, String appContext, CallbackHandler handler) throws AuthException {
        if(handler == null) {
            handler = createDefaultCallbackHandler();
        }
        ClientAuthConfig cac = new ClientAuthConfigImpl(layer, appContext, handler, providerProperties);
        return cac;
    }

    @Override
    public ServerAuthConfig getServerAuthConfig(String layer, String appContext, CallbackHandler handler) throws AuthException {
        if(handler == null) {
            handler = createDefaultCallbackHandler();
        }
        ServerAuthConfig sac = new ServerAuthConfigImpl(layer, appContext, handler, providerProperties);
        return sac;
    }

    @Override
    public void refresh() {
    }

    private CallbackHandler createDefaultCallbackHandler() throws AuthException {
        String className = System.getProperty(CALLBACK_HANDLER);
        if(className == null) {
            throw new AuthException("No default handler set via System Property: " + CALLBACK_HANDLER);
        }
        
        try {
            Class clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            return (CallbackHandler)clazz.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new AuthException(e.getMessage());
        }
    }

}
