package es.ujaen.model;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import es.ujaen.Exceptions.NoFilesInPathException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Marcial J. Sánchez Santiago on 16/11/15.
 */
public class Engine implements EngineInterface{

    private Project project;
    private static final String SINKS_XML = "sinks.xml";
    private static final String SOURCES_XML = "sources.xml";
    private static final String SAFES_XML = "safes.xml";
    private static final String DERIVED_XML = "derived.xml";

    public void setProjectInfo(String projectRoot, String javaRoot){
        project = new Project(projectRoot, javaRoot);
    }

    public String getProjectRootPath() throws IOException{
        return project.getProjectRoot().getCanonicalPath();
    }

    public String getProjectJavaRootPath() throws IOException{
        return project.getJavaRoot().getCanonicalPath();
    }

    public HistoryNode run() throws NoFilesInPathException, IOException, com.github.javaparser.ParseException{
        project.parseProjectFiles();
        Collection<XmlManager.SinkDescription> sinks = XmlManager.readSinks(SINKS_XML);
        Collection<SearchMatch> matches = new ArrayList<>();
        for(XmlManager.SinkDescription sink : sinks){
            //System.out.println("Looking for: "+sink.getID());
            Collection<SearchMatch> actualSinkMatches = Searcher.searchReferences(project.getProjectRoot(), project.getJavaRoot(), project.getCompilationUnits(), sink);
            if(actualSinkMatches != null){
                matches.addAll(actualSinkMatches);
            }
        }
        System.out.println("Sink Methods found: "+matches.size());
        System.out.println("End Sink Search");

        if(!matches.isEmpty()){
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
                System.out.println("Something happened in the propagation");
            }else{
                //masterRoot.printHistoryInConsole();
                System.out.println("Propagation end correctly");
                HistoryNode filteredRoot = filterPositiveDetections(masterRoot);
                System.out.println("Filtered");
                //filteredRoot.printHistoryInConsole();
                return filteredRoot;
            }
        }else{
            System.out.println("No vulnerable method found");
        }
        return null;
    }

    private HistoryNode filterPositiveDetections(HistoryNode root){
        HistoryNode newHistoryRoot = new HistoryNode();
        for(HistoryNode branch: root.getChildren()){
            if(branch.isBranchPoisoned()){
                newHistoryRoot.addChildren(branch);
            }
        }
        return newHistoryRoot;
    }


    /*
    Class used to store project info like path and a list with all Java Source Files
     */
    private class Project{
        private File projectRoot;
        private File javaRoot;
        private List<File> javaFiles ;
        private List<CompilationUnit> cUnits;

        Project(String _projectPath, String _javaPath){
            projectRoot = new File(_projectPath);
            javaRoot = new File(_javaPath);
        }

        public void parseProjectFiles() throws NoFilesInPathException, com.github.javaparser.ParseException, IOException{
            javaFiles = lookPathForJavaFiles(projectRoot);
            cUnits = parseJavaFilesList(javaFiles);
        }

        private List<CompilationUnit> parseJavaFilesList(List<File> javaFiles) throws IOException, com.github.javaparser.ParseException{
            List<CompilationUnit> cUnitsList = new ArrayList<>();
            for(File file : javaFiles) {
                CompilationUnit cu = JavaParser.parse(file);    // parse the file
                cUnitsList.add(cu);
                CompilationUnitManager.getCUnitInfo(cu);
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
        
        public File getJavaRoot() { return javaRoot; }

        public void setProjectRoot(String projectPath) {
            projectRoot = new File(projectPath);
        }

        public void setJavaRoot(String javaPath) { javaRoot = new File(javaPath);}

        public List<File> getJavaFiles() {
            return javaFiles;
        }

        public List<CompilationUnit> getCompilationUnits(){
            return cUnits;
        }
    }


}
