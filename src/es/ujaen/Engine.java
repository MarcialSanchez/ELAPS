package es.ujaen;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import es.ujaen.Exceptions.NoFilesInPathException;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * Created by blitzer on 16/11/15.
 */
public class Engine {

    private Project project;

    Engine(String rootUrl) throws Exception{
        project = new Project(rootUrl);
    }

    void run(){
        Searcher.searchReferences(project.getProjectRoot(),project.getCompilationUnits(),"getMedia");
    }


    /*
    Class used to store project info like path and a list with all Java Source Files
     */
    private class Project{
        private File projectRoot;
        private List<File> javaFiles ;
        private List<CompilationUnit> cUnits;

        Project(String _path)throws Exception{
            projectRoot = new File(_path);
            javaFiles = lookPathForJavaFiles(projectRoot);
            cUnits = parseJavaFilesList(javaFiles);
        }

        private List<CompilationUnit> parseJavaFilesList(List<File> javaFiles) throws Exception{
            List<CompilationUnit> cUnitsList = new ArrayList<>();
            for(File file : javaFiles) {
                CompilationUnit cu = JavaParser.parse(file);    // parse the file
                cUnitsList.add(cu);
            }
            return cUnitsList;
        }

        private List<File> lookPathForJavaFiles(File directory) throws NoFilesInPathException {
            File[] files= directory.listFiles();
            if(files == null){
                throw new NoFilesInPathException();
            }
            List<File> javaFiles = new ArrayList<>();

            for (File file : files) {
                if (file.getName().endsWith(".java")) {
                    javaFiles.add(file);
                }
                if (file.isDirectory()) {
                    List<File> tmpList = lookPathForJavaFiles(file);
                    javaFiles.addAll(tmpList);
                }
            }
            return javaFiles;
        }

        public File getProjectRoot() {
            return projectRoot;
        }

        public void setProjectRoot(String projectPath) {
            projectRoot = new File(projectPath);
        }

        public List<File> getJavaFiles() {
            return javaFiles;
        }

        public List<CompilationUnit> getCompilationUnits(){
            return cUnits;
        }
    }


}
