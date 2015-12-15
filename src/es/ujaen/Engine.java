package es.ujaen;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import es.ujaen.Exceptions.NoFilesInPathException;
import javassist.expr.MethodCall;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by blitzer on 16/11/15.
 */
public class Engine {

    private Project project;
    private static final String SINKS_XML = "sinks.xml";
    private static final String SOURCES_XML = "sources.xml";
    private static final String SAFES_XML = "safes.xml";
    private static final String DERIVED_XML = "derived.xml";

    Engine(String rootUrl) throws Exception{
        project = new Project(rootUrl);
    }

    void run(){
        Collection<XmlManager.SinkDescription> sinks = XmlManager.readSinks(SINKS_XML);
        Collection<SearchMatch> matches = new ArrayList<>();
        for(XmlManager.SinkDescription sink : sinks){
            //System.out.println("Looking for: "+sink.getID());
            Collection<SearchMatch> actualSinkMatches = Searcher.searchReferences(project.getProjectRoot(), project.getCompilationUnits(), sink);
            if(actualSinkMatches != null){
                matches.addAll(actualSinkMatches);
            }
        }
        System.out.println(matches.size());
        System.out.println("End Sink Search");

        if(matches != null && !matches.isEmpty()){
            Collection<XmlManager.SourceDescription> sources = XmlManager.readSources(SOURCES_XML);
            Collection<XmlManager.DerivationDescription> derived = XmlManager.readDerivators(DERIVED_XML);
            Collection<XmlManager.SafeDescription> safes = XmlManager.readSafes(SAFES_XML);
            HistoryNode masterRoot = new HistoryNode();
            Propagator.setDescriptions(sources,derived,safes);
            for(SearchMatch match : matches) {
                /**
                 * Realizamos la propagación hacia atras en cada uno de los métodos sink que hayamos encontrado.
                 * Se poblará masterRoot con todos los pasos dados en cada nodo.
                 * masterRoot tendrá un hijo por cada match.
                 */
                Propagator.processMatch(match,masterRoot);
            }
            if(masterRoot.getChildren().size() != matches.size()){
                System.out.println("Something happend in the propagation");
            }else{
                System.out.println("Propagation end correctly");
            }
        }else{
            System.out.println("Any vulnerable method found");
        }
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
