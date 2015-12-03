package es.ujaen;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import es.ujaen.Exceptions.NoFilesInPathException;
import me.tomassetti.symbolsolver.javaparsermodel.JavaParserFacade;
import me.tomassetti.symbolsolver.javaparsermodel.JavaParserFactory;
import me.tomassetti.symbolsolver.model.declarations.MethodDeclaration;
import me.tomassetti.symbolsolver.model.invokations.MethodUsage;
import me.tomassetti.symbolsolver.model.resolution.SymbolReference;
import me.tomassetti.symbolsolver.model.resolution.TypeSolver;
import me.tomassetti.symbolsolver.model.typesystem.TypeUsage;
import me.tomassetti.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import me.tomassetti.symbolsolver.resolution.typesolvers.JarTypeSolver;
import me.tomassetti.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import me.tomassetti.symbolsolver.resolution.typesolvers.JreTypeSolver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by blitzer on 20/11/15.
 */
public class Searcher {
    Searcher(){}

    public static Collection searchReferences(File projectRoot, List<CompilationUnit> cUnits, String methodName){
        return search(projectRoot, cUnits, "reference", methodName);
    }

    public static Collection searchDeclarations(File projectRoot, List<CompilationUnit> cUnits, String methodName){
        return search(projectRoot, cUnits, "declaration", methodName);
    }

    private static Collection search(File projectRoot, List<CompilationUnit> cUnits, String searchType, String methodName ){ // Este metodo se encarga de realizar todas las busquedas según "searchType"
        Collection<SearchMatch> matches = new ArrayList<SearchMatch>();

        if(searchType == "reference"){

            CombinedTypeSolver typeSolver = new CombinedTypeSolver();  //Typesolver es la clase que contendrá todos los lugares donde se buscaran las clases importadas
            typeSolver.add(new JreTypeSolver());                       // Añadimos el JRE por defecto para las clases que incluye Java

            try {
                List<File> jarFiles = lookPathForJarFiles(projectRoot);
                for(File jar : jarFiles){
                    typeSolver.add(new JarTypeSolver(jar.getAbsolutePath())); //Buscamos y añadimos al typesolver todos los JAR que encontremos dentro del proyecto.
                }
                List<File> srcFolders = lookPathForSourceFolders(projectRoot);
                for(File src : srcFolders){
                    typeSolver.add(new JavaParserTypeSolver(src.getAbsoluteFile()));  //Añadimos las carpetas "src" con los ficheros fuente del proyecto al typesolver.
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoFilesInPathException e) {
                e.printStackTrace();
            }

            MethodCallVisitor visitor = new MethodCallVisitor();   //Instanciamos el visitor que se encargará de recorrer los nodos.
            visitor.setActualSearch(methodName);                   //Asignamos la busqueda actual extraida del fichero sink.xml.
            visitor.setTypeSolver(typeSolver);                     //Asignamos el typeSolver que hemos configurado anteriormente.

            for(CompilationUnit cUnit : cUnits){                   //Por cada CompilationUnit (Ficheros.java) pasamos el visitor en busca de referencias al metodo que buscamos

                visitor.setCUnit(cUnit);
                visitor.visit(cUnit, null);

            }
            matches = visitor.getMatches();                        //Tras escanear todos los documentos obtenemos la lista con los matches que hayamos encontrado.

        }
        if(searchType == "declaration"){
            for(CompilationUnit cUnit : cUnits){                                    // Buscamos en cada uno de las CompilationUnit (corresponden a los ficheros java del proyecto)
                DeclarationInfo info = CompilationUnitManager.getCUnitInfo(cUnit);  // Obtenemos la info del CompilationUnit, al parsear cada uno de los ficheros guardamos la info de todas las declaraciones.
                Node nodo = info.getMethodDeclarator(methodName);                   // Si hay una un metodo con el mismo nombre nos devolverá el nodo de este, si no devuelve null.
                if(nodo != null){
                    matches.add(new SearchMatch(methodName, nodo, cUnit));
                }
            }
        }


        return matches;
    }

    private static List<File> lookPathForJarFiles(File directory) throws NoFilesInPathException {
        File[] files= directory.listFiles();
        if(files == null){
            throw new NoFilesInPathException();
        }
        List<File> jarFiles = new ArrayList<>();

        for (File file : files) {
            if (file.getName().endsWith(".jar")) {
                jarFiles.add(file);
            }
            if (file.isDirectory()) {
                List<File> tmpList = lookPathForJarFiles(file);
                jarFiles.addAll(tmpList);
            }
        }
        return jarFiles;
    }

    private static List<File> lookPathForSourceFolders(File directory) throws NoFilesInPathException {
        File[] files= directory.listFiles();
        if(files == null){
            throw new NoFilesInPathException();
        }
        List<File> srcFolders = new ArrayList<>();

        for (File file : files) {
            if (file.isDirectory()) {
                if (file.getName().equals("src")) {
                    srcFolders.add(file);
                }else{
                    List<File> tmpList = lookPathForSourceFolders(file);
                    srcFolders.addAll(tmpList);
                }

            }
        }
        return srcFolders;
    }

    private static class SearchMatch {
        private String name;
        private Node matchNode;
        private CompilationUnit cUnit;

        SearchMatch(String _name, Node _matchNode, CompilationUnit _cUnit){
            name = _name;
            matchNode = _matchNode;
            cUnit = _cUnit;
        }
    }

    private static class MethodCallVisitor extends VoidVisitorAdapter {
        private ArrayList<SearchMatch> matches = new ArrayList<>();
        private static String actualSearch;
        private static CombinedTypeSolver typeSolver;
        private static CompilationUnit actualCUnit;

        @Override
        public void visit(MethodCallExpr node, Object arg) {
            if(node.getName().equals(actualSearch)){
                System.out.println(node.getBeginLine());
                MethodUsage solvedMethod = JavaParserFacade.get(typeSolver).solveMethodAsUsage(node);
                String qualifiedName=solvedMethod.declaringType().getQualifiedName()+"."+solvedMethod.getName()+"(";
                for(int i = 0; i<solvedMethod.getParamTypes().size();i++){

                    if(solvedMethod.getParamTypes().size()>1 && i!=0){
                        qualifiedName = qualifiedName.concat(",");
                    }
                    String type = solvedMethod.getParamTypes().get(i).describe();
                    qualifiedName = qualifiedName.concat(type.substring(type.lastIndexOf(".")+1));
                }
                qualifiedName = qualifiedName.concat(")");
                System.out.println(qualifiedName);
                matches.add(new SearchMatch(qualifiedName, node, actualCUnit));
            }
            super.visit(node, arg);
        }
        /*
        public void visit(QualifiedNameExpr node, Object arg){
            System.out.println(node.toString());
        }*/

        public void setActualSearch(String name){
            actualSearch = name;
        }
        public void setTypeSolver(CombinedTypeSolver newtypeSolver){
            typeSolver = newtypeSolver;
        }
        public void setCUnit(CompilationUnit cUnit){ actualCUnit = cUnit; }

        public ArrayList<SearchMatch> getMatches(){
            return matches;
        }

    }
}
