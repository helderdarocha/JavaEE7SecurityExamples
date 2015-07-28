/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.javaeesecurity.jsf;

import br.com.argonavis.javaeesecurity.entity.Usuario;
import br.com.argonavis.javaeesecurity.facade.UsuarioFacade;
import java.io.Serializable;
import java.security.Principal;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author helderdarocha
 */
@Named("usuarioBean")
@RequestScoped
public class UsuarioBean implements Serializable {
    @EJB
    private UsuarioFacade usuarioFacade;
    

    private String userid;
    private Usuario usuario;
    
    @PostConstruct
    public void init() {
        try {
            usuario = usuarioFacade.findByName(userid);
        } catch (Exception e) {
            usuario = new Usuario();
        }
    }

    public String getAvatar() {
        if (usuario.getAvatar() == null) {
            return "images/" + userid + ".png";
        }
        return usuario.getAvatar();
    }

    public String getNome() {
        if(usuario != null) {
            return usuario.getNome();
        } else {
            return null;
        }
    }

    public void setNome(String nome) {
        usuario.setNome(nome);
        usuarioFacade.edit(usuario);
    }

    public String getEmail() {
        if(usuario != null) {
            return usuario.getEmail();
        } else {
            return null;
        }
    }

    public void setEmail(String email) {
        usuario.setEmail(email);
        usuarioFacade.edit(usuario);
    }

    public String getUserid() {
        return userid;
    }
    
    public String getPassword() {
        return usuario.getPassword();
    }


    public void setUserid(String userid) {
        this.userid = userid;
    }
    
    /**
     * Apenas o usuário logado (userid) ou administrador podem ver botões de edição.
     */
    public boolean getAutorizado() {
        ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
        Principal principal = ectx.getUserPrincipal();
        
        if(principal == null || userid == null) {
            return false;
        }
        
        if(userid.equals(principal.getName()) || ectx.isUserInRole("administrador")) {
            return true;
        }
        
        return false;
    }

}
