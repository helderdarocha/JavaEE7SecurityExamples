/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.javaeesecurity.ejb;

import java.io.Serializable;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBAccessException;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

/**
 *
 * @author helderdarocha
 */
@Stateless
public class ImagemBean implements Serializable {
    
    @Resource 
    SessionContext ctx;

    public String dominioPublico() {
        return "minipeixe.jpg";
    }

    @RolesAllowed({"administrador", "amigo"})
    public String restrito() {
        return "lupita.jpg";
    }
    
    @RolesAllowed("administrador")
    public String privado() {
        return "armenia.jpg";
    }

    public String pessoal(String userid) {
        if(ctx.getCallerPrincipal().getName().equals(userid) || ctx.isCallerInRole("administrador")) {
            return userid+".png";
        } else {
            throw new EJBAccessException("Apenas administradores ou o próprio usuário podem ver imagem.");
        }
    }
}
