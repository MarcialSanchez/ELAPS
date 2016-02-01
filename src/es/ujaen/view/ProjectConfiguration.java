package es.ujaen.view;

import es.ujaen.controller.ControllerInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Created by blitzer on 1/02/16.
 */
public class ProjectConfiguration extends JDialog{

    private ControllerInterface controller;

    private JButton buttonProjectRootChooser;
    private JButton buttonJavaRootChooser;
    private JButton cancelButton;
    private JButton acceptButton;
    private JTextField projectPathField;
    private JTextField javaPathField;
    private JPanel configDialog;

    private File projectRootPath;
    private File javaRootPath;

    ProjectConfiguration(java.awt.Frame parent, boolean modal, ControllerInterface controller){
        super(parent, modal);
        this.setTitle("Project Configuration");
        this.controller = controller;

        setContentPane(configDialog);
        initComponents();
        setWindowPosition();

    }

    private void setWindowPosition(){
        Dimension tamPantalla = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension tamVentana = this.getSize();

        this.setLocation((tamPantalla.width-tamVentana.width)/2,
                (tamPantalla.height-tamVentana.height)/2);
    }

    private void initComponents() {
        buttonProjectRootChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser projectPathChooser = new JFileChooser();
                projectPathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = projectPathChooser.showOpenDialog(ProjectConfiguration.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    projectRootPath = projectPathChooser.getSelectedFile();
                    try {
                        projectPathField.setText(projectRootPath.getCanonicalPath());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    //This is where a real application would open the file.
                }
            }
        });

        buttonJavaRootChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser javaPathChooser = new JFileChooser();
                javaPathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = javaPathChooser.showOpenDialog(ProjectConfiguration.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    javaRootPath = javaPathChooser.getSelectedFile();
                    try {
                        javaPathField.setText(javaRootPath.getCanonicalPath());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    //This is where a real application would open the file.
                }
            }
        });

        acceptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acceptButtonActionPerformed(evt);
            }
        });

        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        pack();
    }

    private void acceptButtonActionPerformed(ActionEvent evt){
        this.setVisible(false);
        controller.setProjectInfo(projectPathField.getText(),javaPathField.getText());
    }

    private void cancelButtonActionPerformed(ActionEvent evt){
        this.setVisible(false);
        projectPathField.setText("");
        javaPathField.setText("");
    }

    public void setInfo(String projectPath, String javaPath){
        projectPathField.setText(projectPath);
        javaPathField.setText(javaPath);
    }

    public String getProjectPathField(){
        return projectPathField.getText();
    }

    public String getJavaPathField(){
        return javaPathField.getText();
    }
}
