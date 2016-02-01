package es.ujaen.view;

import javax.swing.tree.TreeModel;
import java.io.IOException;

/**
 * Created by blitzer on 1/02/16.
 */
public interface MainViewInterface {

    void refreshDetectionsTree();

    void refreshInfoBox();

    void appendConsoleMessages(String message);

    void enableRunAnalysisButton();

    void disableRunAnalysisButton();

    String getActualProjectPath();

    String getActualJavaPath();
}
