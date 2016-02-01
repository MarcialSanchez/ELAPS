package es.ujaen.model;

import es.ujaen.Exceptions.NoFilesInPathException;

import java.io.IOException;

/**
 * Created by blitzer on 1/02/16.
 */
public interface EngineInterface {

    HistoryNode run() throws NoFilesInPathException, IOException, com.github.javaparser.ParseException;

    String getProjectRootPath()throws IOException;

    String getProjectJavaRootPath()throws IOException;

    void setProjectInfo(String projectRoot, String javaRoot);
}
