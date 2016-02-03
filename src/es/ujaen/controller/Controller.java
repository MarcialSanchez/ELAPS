package es.ujaen.controller;

import es.ujaen.Exceptions.NoFilesInPathException;
import es.ujaen.model.EngineInterface;
import es.ujaen.model.HistoryNode;
import es.ujaen.view.MainView;
import es.ujaen.view.MainViewInterface;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by blitzer on 1/02/16.
 */
public class Controller implements ControllerInterface{

    private EngineInterface engine;
    private MainViewInterface mainView;
    private HistoryNode detections = null;

    private List<AnalysisObserver> analysisObserverList;

    public Controller(EngineInterface _engine){

        analysisObserverList = new ArrayList<>();

        engine = _engine;
        mainView = new MainView(_engine,this);
    }
    public void registerAnalysisObserver(AnalysisObserver o){//todo
        analysisObserverList.add(o);
        o.refreshDetectionsTree();
    }

    protected void notifyAnalysisChange(){
        for (AnalysisObserver o: analysisObserverList) {
            o.refreshDetectionsTree();
        }
    }

    public void runAnalysis(){
        try{
            detections = engine.run();
            notifyAnalysisChange();
            if(detections.getChildren().isEmpty()){
                mainView.appendConsoleMessages("No vulnerabilities found");
            }
        }catch (NoFilesInPathException e){
            mainView.appendConsoleMessages("-Error: The selected directory has no files.");
        } catch (com.github.javaparser.ParseException e) {
            mainView.appendConsoleMessages("-Error: There is a problem when parsing the java files. Probably due to an error in the compilation, check for errors and try again.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeTreeFromHistoryNode(HistoryNode historyNode, DefaultMutableTreeNode treeNode){
        for(HistoryNode child: historyNode.getChildren()){
            DefaultMutableTreeNode treeChild = new DefaultMutableTreeNode(child);
            treeNode.add(treeChild);
            makeTreeFromHistoryNode(child, treeChild);
        }
    }

    public DefaultMutableTreeNode getDetectionsTree(){
        if(detections == null){
            return null;
        }
        DefaultMutableTreeNode rootTree = new DefaultMutableTreeNode(detections);
        makeTreeFromHistoryNode(detections, rootTree);
        return rootTree;
    }

    @Override
    public void setProjectInfo(String newProjectPath, String newJavaPath) {
        engine.setProjectInfo(newProjectPath,newJavaPath);
    }

}
