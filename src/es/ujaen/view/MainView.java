package es.ujaen.view;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

/**
 * Created by Marcial J. Sánchez Santiago on 28/01/16.
 */
public class MainView extends javax.swing.JFrame{
    private JPanel rootPanel;
    private JTextPane consolePanel;
    private JTree detectionsTree;
    private JTextPane helpPanel;

    public MainView(){
        super("MainFrame");

        setContentPane(rootPanel);
        initComponents();

        Dimension tamPantalla = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension tamVentana = this.getSize();

        this.setLocation((tamPantalla.width-tamVentana.width)/2,
                (tamPantalla.height-tamVentana.height)/2);


        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

    }

    private javax.swing.JMenuItem buttonNewProject;
    private javax.swing.JMenuItem buttonEditProject;
    private javax.swing.JMenuItem buttonExit;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu menuProyect;
    private javax.swing.JMenu menuRun;

    private void initComponents() {
        jMenuBar1 = new javax.swing.JMenuBar();
        menuProyect = new javax.swing.JMenu();
        menuRun = new javax.swing.JMenu();
        buttonExit = new javax.swing.JMenuItem();
        buttonNewProject = new javax.swing.JMenuItem();
        buttonEditProject = new javax.swing.JMenuItem();

        menuProyect.setText("Project");
        buttonExit.setText("Exit");
        buttonNewProject.setText("New Project");
        buttonEditProject.setText("Edit Project");

        menuProyect.add(buttonNewProject);
        menuProyect.add(buttonEditProject);
        menuProyect.add(buttonExit);

        jMenuBar1.add(menuProyect);

        setJMenuBar(jMenuBar1);

        UIDefaults defs = UIManager.getDefaults();
        defs.put("consolePanel.background", new ColorUIResource(255, 255, 255));
        defs.put("consolePanel.inactiveBackground", new ColorUIResource(255,255,255));

        pack();
    }
}
