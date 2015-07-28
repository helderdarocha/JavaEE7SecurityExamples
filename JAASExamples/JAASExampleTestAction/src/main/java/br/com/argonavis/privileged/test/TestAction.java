/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.privileged.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author helderdarocha
 */
public class TestAction implements PrivilegedAction {

    @Override
    public Object run() {
        // Non privileged operation
        System.out.println("This is a string");
        
        // Privileged access to properties
        System.out.println("This is your user.home property: " + System.getProperty("user.home"));
        
        // Privileged access to files
        File file = new File("../pom.xml");
        if(file.exists()) {
            BufferedReader reader = null;
            try {
                System.out.println("Reading the contents of pom.xml:\n-------------\n");
                reader = new BufferedReader(new FileReader(file));
                String line;
                StringBuilder buffer = new StringBuilder();
                while((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }   System.out.println(buffer.toString());
            } catch (IOException ex) {
                Logger.getLogger(TestAction.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if(reader != null) reader.close();
                } catch (IOException ex) {
                    Logger.getLogger(TestAction.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            System.out.println("pom.xml does not exist!");
        }
        
        return null;
    }
    
}
