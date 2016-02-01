package es.ujaen.controller;

import es.ujaen.Exceptions.NoFilesInPathException;
import es.ujaen.model.EngineInterface;
import es.ujaen.model.HistoryNode;
import es.ujaen.view.MainView;
import es.ujaen.view.MainViewInterface;

import javax.swing.tree.TreeModel;
import java.io.IOException;
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
        engine = _engine;
        mainView = new MainView(_engine,this);
    }
    public void registerAnalysisObserver(AnalysisObserver o){
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
        }catch (NoFilesInPathException e){

        } catch (com.github.javaparser.ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TreeModel getDetectionsTree(){
        if(detections == null){
            return null;
        }
        return (TreeModel)detections;
    }

    @Override
    public void setProjectInfo(String newProjectPath, String newJavaPath) {
        engine.setProjectInfo(newProjectPath,newJavaPath);
    }


}
