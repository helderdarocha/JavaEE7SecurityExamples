/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.javaeesecurity.jsf;

import br.com.argonavis.javaeesecurity.ejb.ImagemBean;
import br.com.argonavis.javaeesecurity.entity.Usuario;
import br.com.argonavis.javaeesecurity.facade.UsuarioFacade;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 *
 * @author helderdarocha
 */
@Named("bibliotecaBean")
@RequestScoped
public class BibliotecaBean implements Serializable {
    
    @Inject java.security.Principal principal;

    @EJB
    private UsuarioFacade usuarioFacade;
    @EJB
    private ImagemBean imagemBean;

    private String userid;
    private List<Usuario> usuarios;

    private String acessoNegado = "acessonegado.jpg";

    @PostConstruct
    public void init() {
        //ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
        //userid = ectx.getUserPrincipal().getName();
        
        userid = principal.getName();
        
        usuarios = usuarioFacade.findAll();
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getImagem1() {
        try {
            return imagemBean.dominioPublico();
        } catch (Exception e) {
            return acessoNegado;
        }
    }

    public String getImagem2() {
        try {
            return imagemBean.restrito();
        } catch (EJBAccessException e) {
            return acessoNegado;
        }
    }

    public String getImagem3() {
        try {
            return imagemBean.privado();
        } catch (EJBAccessException e) {
            return acessoNegado;
        }
    }

    public String getImagem4() {
        try {
            return imagemBean.pessoal(userid);
        } catch (EJBAccessException e) {
            return acessoNegado;
        }
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

}
