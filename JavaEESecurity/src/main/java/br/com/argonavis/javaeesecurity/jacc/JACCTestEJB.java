/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.javaeesecurity.jacc;

import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.Principal;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.security.auth.Subject;
import javax.security.jacc.EJBMethodPermission;
import javax.security.jacc.EJBRoleRefPermission;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;

/**
 *
 * @author helderdarocha
 */
@Stateless
@DeclareRoles({"especial", "amigo", "outro", "administrador"}) 
@RunAs("administrador")
public class JACCTestEJB { 

    @Resource
    SessionContext ctx;

    private static final String SUBJECT_HANDLER_KEY = "javax.security.auth.Subject.container";

    public String[] getRoles() {
        PermissionCollection pc = null;
        try {
            pc = getPC();
            pc.implies(new EJBRoleRefPermission(null, null));
        } catch (PolicyContextException ex) {
            Logger.getLogger(JACCTestEJB.class.getName()).log(Level.SEVERE, null, ex);
        }
        HashSet<String> roleSet = null;
        Enumeration<Permission> e = pc.elements();
        while (e.hasMoreElements()) {
            Permission p = e.nextElement();
            if (p instanceof EJBRoleRefPermission) {
                String roleRef = p.getActions();
                if (roleSet == null) {
                    roleSet = new HashSet<>();
                }
                if (!roleSet.contains(roleRef) && ctx.isCallerInRole(roleRef)) {
                    roleSet.add(p.getActions());
                }
            }
        }
        if (roleSet != null) {
            return roleSet.toArray(new String[0]);
        }
        return new String[0];
    }
    
    @DenyAll
    public void denied() {}

    @PermitAll
    public String[] getNamesActions() {
        PermissionCollection pc = null;
        try {
            pc = getPC();
            pc.implies(new EJBMethodPermission("", "","", new String[0]));
        } catch (PolicyContextException ex) {
            Logger.getLogger(JACCTestEJB.class.getName()).log(Level.SEVERE, null, ex);
        }
        TreeSet<String> actions = null;
        Enumeration<Permission> e = pc.elements();
        while (e.hasMoreElements()) {
            Permission p = e.nextElement();
            if (p instanceof EJBMethodPermission) {
                if (actions == null) {
                    actions = new TreeSet<>();
                }
                actions.add("EJB Interface: <b><code>" + p.getName() + "</code></b>; Methods: <b><code>" + p.getActions() + "</code></b>");
            }
        }
        if (actions != null) {
            return actions.toArray(new String[0]);
        }
        return new String[0];
    }

    public String getPrincipalName() {
        return ctx.getCallerPrincipal().getName();
    }

    private PermissionCollection getPC() throws PolicyContextException {
        Subject s = (Subject) PolicyContext.getContext(SUBJECT_HANDLER_KEY);

        CodeSource cs = new CodeSource(null, (java.security.cert.Certificate[]) null);
        Principal principals[] = (s == null ? new Principal[0] : s.getPrincipals().toArray(new Principal[0]));
        ProtectionDomain pd = new ProtectionDomain(cs, null, null, principals);

        Policy policy = Policy.getPolicy();
        PermissionCollection pc = policy.getPermissions(pd);
        return pc;
    }

}
