/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.jaasexample;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Esta classe oferece um diálogo gráfico para ler o nome de usuário e senha.
 *
 * @author helderdarocha
 */
public class LoginDialog {

    private String title, message, passLabel, nameLabel;
    private String user;
    private char[] pass;
    private boolean closed = false;

    public LoginDialog(String title, String message, String nameLabel, String passLabel) {
        this.title = title;
        this.message = message;
        this.passLabel = passLabel;
        this.nameLabel = nameLabel;
        makeGUI();
    }

    public LoginDialog(String message) {
        this("Login", message, "Name", "Password");
    }

    public LoginDialog() {
        this("Login", "", "Name", "Password");
    }

    private void makeGUI() {
        final JDialog dialog = new JDialog((JFrame) null, title, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().setLayout(new BorderLayout(5, 5));

        JLabel msg = new JLabel(message);
        JLabel nm = new JLabel(nameLabel);
        JLabel ps = new JLabel(passLabel);
        JPanel labels = new JPanel(new GridLayout(2, 1, 2, 2));
        labels.add(nm);
        labels.add(ps);

        final JTextField nmField = new JTextField(15);
        final JTextField psField = new JTextField(15);
        JPanel fields = new JPanel(new GridLayout(2, 1, 2, 2));
        fields.add(nmField);
        fields.add(psField);

        JButton ok = new JButton("Login");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                user = nmField.getText();
                pass = psField.getText().toCharArray();
                dialog.dispose();
            }
        });

        JButton cancel = new JButton("Abort");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });

        JPanel buttons = new JPanel();
        buttons.add(ok);
        buttons.add(cancel);

        dialog.getContentPane().add(BorderLayout.NORTH, msg);
        dialog.getContentPane().add(BorderLayout.SOUTH, buttons);
        dialog.getContentPane().add(BorderLayout.WEST, labels);
        dialog.getContentPane().add(BorderLayout.CENTER, fields);

        dialog.pack();
        dialog.setLocation(300, 300);
        dialog.setVisible(true);
    }

    public String getUser() {
        return user;
    }
    
    public char[] getPass() {
        return pass;
    }

}
