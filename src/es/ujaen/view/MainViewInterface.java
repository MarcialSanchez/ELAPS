package es.ujaen.view;

/**
 * Created by blitzer on 1/02/16.
 */
public interface MainViewInterface {

    void refreshInfoBox(String infoString);

    void appendConsoleMessages(String message);

    void enableRunAnalysisButton();

    void disableRunAnalysisButton();

    String getActualProjectPath();

    String getActualJavaPath();
}
