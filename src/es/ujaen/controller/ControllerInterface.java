package es.ujaen.controller;

import javax.swing.tree.TreeModel;

/**
 * Created by blitzer on 1/02/16.
 */
public interface ControllerInterface {

    void runAnalysis();

    TreeModel getDetectionsTree();

    void setProjectInfo(String newProjectPath, String newJavaPath);
}
