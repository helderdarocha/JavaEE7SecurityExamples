/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.javaeesecurity.facade;

import br.com.argonavis.javaeesecurity.entity.Usuario;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author helderdarocha
 */
@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> {
    @PersistenceContext(unitName = "JavaEESecurityPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioFacade() {
        super(Usuario.class);
    }
    
    public Usuario findByName(String userid) {
        Query q = em.createQuery("select u from Usuario u where u.userid = :userid", Usuario.class);
        q.setParameter("userid", userid);
        return (Usuario)q.getSingleResult();
    }
    
    public List<Usuario> findByNames(String[] names) {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for(int i = 0; i < names.length; i++) {
            builder.append("'").append(names[i]).append("'");
            if(i < names.length-1) {
                builder.append(",");
            }
        }
        builder.append(")");
        String inclause = builder.toString();
        
        return em.createQuery("select u from Usuario u where u.userid in"+inclause, Usuario.class).getResultList();
             
    }
    private static final Logger LOG = Logger.getLogger(UsuarioFacade.class.getName());
    
}
