package es.ujaen.controller;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by blitzer on 1/02/16.
 */
public interface ControllerInterface {

    void registerAnalysisObserver(AnalysisObserver o);

    void runAnalysis();

    DefaultMutableTreeNode getDetectionsTree();

    void setProjectInfo(String newProjectPath, String newJavaPath);
}
