package es.ujaen.view;

import es.ujaen.controller.AnalysisObserver;
import es.ujaen.controller.ControllerInterface;
import es.ujaen.model.EngineInterface;
import es.ujaen.model.HistoryNode;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.text.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * Created by Marcial J. Sánchez Santiago on 28/01/16.
 */
public class MainView extends javax.swing.JFrame implements MainViewInterface, AnalysisObserver, TreeSelectionListener{
    private String appVersion = " - ELAPS 1.0 -";

    private EngineInterface engine;
    private ControllerInterface controller;

    private ProjectConfiguration newProjectDialog;
    private ProjectConfiguration editProjectDialog;

    private JPanel rootPanel;
    private JTextPane consolePanel;
    private JTree detectionsTree;
    private JTextArea helpPanel;
    private JScrollPane scrollTree;
    private JScrollPane scrollPane;

    public MainView(EngineInterface engine, ControllerInterface controller){
        this.engine = engine;
        this.controller = controller;

        newProjectDialog = new ProjectConfiguration(this, true, controller);
        editProjectDialog = new ProjectConfiguration(this, true, controller);
        setContentPane(rootPanel);
        refreshTitle(appVersion);
        initComponents();
        setWindowPosition();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        controller.registerAnalysisObserver(this);

        setVisible(true);

    }

    private void refreshTitle(String title){
        this.setTitle(title);
    }

    private void setWindowPosition(){
        Dimension tamPantalla = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension tamVentana = this.getSize();

        this.setLocation((tamPantalla.width-tamVentana.width)/2,
                (tamPantalla.height-tamVentana.height)/2);
    }

    private javax.swing.JMenuItem buttonNewProject;
    private javax.swing.JMenuItem buttonEditProject;
    private javax.swing.JMenuItem buttonExit;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu menuProject;
    private javax.swing.JButton buttonRunAnalysis;

    private StyledDocument consoleOutput;
    private SimpleAttributeSet keyWord;

    private StyledDocument helpOutput;
    private SimpleAttributeSet helpKeyWord;

    private void initComponents() {
        /**
         * Creating components for the MENU bar
         */
        jMenuBar1 = new javax.swing.JMenuBar();
        menuProject = new javax.swing.JMenu();
        buttonRunAnalysis = new javax.swing.JButton();
        buttonRunAnalysis.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                requestRunAnalysis();
            }
        });
        buttonExit = new javax.swing.JMenuItem();
        buttonNewProject = new javax.swing.JMenuItem();
        buttonNewProject.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                requestNewProjectAction();
            }
        });
        buttonEditProject = new javax.swing.JMenuItem();
        buttonEditProject.setEnabled(false);
        buttonEditProject.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                requestEditProjectAction();
            }
        });
        buttonExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { exitButtonActionPerformed(e);
            }
        });

        menuProject.setText("Project");
        buttonExit.setText("Exit");
        buttonNewProject.setText("New Project");
        buttonEditProject.setText("Edit Project");

        menuProject.add(buttonNewProject);
        menuProject.add(buttonEditProject);
        menuProject.add(buttonExit);

        jMenuBar1.add(menuProject);
        jMenuBar1.add(buttonRunAnalysis);

        setJMenuBar(jMenuBar1);

        //Disable button until a project is added
        disableRunAnalysisButton();
        buttonRunAnalysis.setIcon(new javax.swing.ImageIcon("resources/icons/runEnabled.png"));
        buttonRunAnalysis.setDisabledIcon(new javax.swing.ImageIcon("resources/icons/runDisabled.png"));

        //Tree initialization
        detectionsTree.setModel(null);
        detectionsTree.setPreferredSize(null);
        detectionsTree.addTreeSelectionListener(this);

        scrollTree.setViewportView(detectionsTree);

        //Defining style and default text for the Console Panel
        Color bgColor = Color.DARK_GRAY;
        UIDefaults defaults = new UIDefaults();
        defaults.put("TextPane[Enabled].backgroundPainter", bgColor);
        consolePanel.putClientProperty("Nimbus.Overrides", defaults);
        consolePanel.putClientProperty("Nimbus.Overrides.InheritDefaults", true);
        consolePanel.setBackground(bgColor);

        consoleOutput = consolePanel.getStyledDocument();
        keyWord = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWord, Color.WHITE);
        StyleConstants.setBackground(keyWord, Color.DARK_GRAY);
        StyleConstants.setFontFamily(keyWord, "Source Code Pro");
        try {
            consoleOutput.insertString(0, "Wellcome to the ELAPS (Everywhere Lightweight Program for Security)\n", null);
        }catch(Exception e) { System.out.println(e); }


        helpPanel.setSelectionColor(Color.BLUE);
        helpPanel.append("Information Panel");

        UIDefaults defs = UIManager.getDefaults();
        defs.put("consolePanel.background", new ColorUIResource(255, 255, 255));
        defs.put("consolePanel.inactiveBackground", new ColorUIResource(255,255,255));

        pack();
    }

    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                detectionsTree.getLastSelectedPathComponent();

        if (node == null) return;

        HistoryNode selectedNode = (HistoryNode)node.getUserObject();
        refreshInfoBox(selectedNode.getAssociatedVulnerability().getInfo());
    }

    private void expandAllNodes(JTree tree, int startingIndex, int rowCount){
        for(int i=startingIndex;i<rowCount;++i){
            tree.expandRow(i);
        }

        if(tree.getRowCount()!=rowCount){
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }

    private void requestRunAnalysis(){
        controller.runAnalysis();
    }

    private void requestNewProjectAction() {
        newProjectDialog.resetFields();
        newProjectDialog.setVisible(true);
        buttonEditProject.setEnabled(true);
        enableRunAnalysisButton();
        refreshTitle(newProjectDialog.getProjectPathField()+appVersion);
        appendConsoleMessages("Added Path for new project");
        appendConsoleMessages("Project Root Path: "+getActualProjectPath());
        appendConsoleMessages("Java Root Package Path: "+getActualJavaPath());
    }

    private void requestEditProjectAction() {
        try {
            editProjectDialog.setInfo(engine.getProjectRootPath(), engine.getProjectJavaRootPath());
            editProjectDialog.setVisible(true);
            refreshTitle(newProjectDialog.getProjectPathField()+appVersion);
            appendConsoleMessages("Edited Path for project");
            appendConsoleMessages("Project Root Path: "+getActualProjectPath());
            appendConsoleMessages("Java Root Package Path: "+getActualJavaPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    @Override
    public void refreshDetectionsTree() {
        detectionsTree.setModel(new DefaultTreeModel(controller.getDetectionsTree()));
        expandAllNodes(detectionsTree, 0, detectionsTree.getRowCount());
        detectionsTree.setVisibleRowCount(detectionsTree.getRowCount());
    }

    @Override
    public void refreshInfoBox(String infoString) {

        helpPanel.setText(infoString);
    }

    @Override
    public void appendConsoleMessages(String message) {
        try {
            consoleOutput.insertString(consoleOutput.getLength(),message+"\n",keyWord);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void enableRunAnalysisButton() {
        buttonRunAnalysis.setEnabled(true);
    }

    @Override
    public void disableRunAnalysisButton() {
        buttonRunAnalysis.setEnabled(false);
    }

    @Override
    public String getActualProjectPath(){
        try {
            return engine.getProjectRootPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getActualJavaPath(){
        try {
            return engine.getProjectJavaRootPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
