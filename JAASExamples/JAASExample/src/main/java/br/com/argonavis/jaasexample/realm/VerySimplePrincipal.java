/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.jaasexample.realm;

import java.security.Principal;

/**
 *
 * @author helderdarocha
 */
public class VerySimplePrincipal implements Principal {
    
    private String name;

    VerySimplePrincipal(String username) {
        this.name = username;
    }

    @Override
    public String getName() {
        return name;
    }
    
}
