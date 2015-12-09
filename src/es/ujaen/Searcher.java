package es.ujaen;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import es.ujaen.Exceptions.NoFilesInPathException;
import me.tomassetti.symbolsolver.javaparsermodel.JavaParserFacade;
import me.tomassetti.symbolsolver.javaparsermodel.UnsolvedSymbolException;
import me.tomassetti.symbolsolver.javaparsermodel.UnsolvedTypeException;
import me.tomassetti.symbolsolver.model.invokations.MethodUsage;
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
    static private CombinedTypeSolver typeSolver = null;
    static public final int REFERENCE = 1;
    static public final int DECLARATION = 2;
    Searcher(){
    }

    public static Collection searchReferences(File projectRoot, List<CompilationUnit> cUnits, String methodName){
        return search(projectRoot, cUnits, REFERENCE, methodName);
    }

    public static Collection searchDeclarations(File projectRoot, List<CompilationUnit> cUnits, String methodName){
        return search(projectRoot, cUnits, DECLARATION, methodName);
    }

    private static Collection search(File projectRoot, List<CompilationUnit> cUnits, int searchType, String methodName ){ // Este metodo se encarga de realizar todas las busquedas según "searchType"
        Collection<SearchMatch> matches = new ArrayList<SearchMatch>();

        if(searchType == REFERENCE){

            generateTypeSolver(projectRoot);
            MethodCallVisitor visitor = new MethodCallVisitor();   //Instanciamos el visitor que se encargará de recorrer los nodos.
            visitor.setActualSearch(methodName);                   //Asignamos la busqueda actual extraida del fichero sink.xml.
            visitor.setTypeSolver(typeSolver);                     //Asignamos el typeSolver que hemos configurado anteriormente.

            for(CompilationUnit cUnit : cUnits){                   //Por cada CompilationUnit (Ficheros.java) pasamos el visitor en busca de referencias al metodo que buscamos

                visitor.setCUnit(cUnit);
                visitor.visit(cUnit, null);

            }
            matches = visitor.getMatches();                        //Tras escanear todos los documentos obtenemos la lista con los matches que hayamos encontrado.
        }
        if(searchType == DECLARATION){
            for(CompilationUnit cUnit : cUnits){                                    // Buscamos en cada uno de las CompilationUnit (corresponden a los ficheros java del proyecto)
                DeclarationInfo info = CompilationUnitManager.getCUnitInfo(cUnit);  // Obtenemos la info del CompilationUnit, al parsear cada uno de los ficheros guardamos la info de todas las declaraciones.
                Node nodo = info.getMethodDeclarator(methodName);                   // Si hay una un metodo con el mismo nombre nos devolverá el nodo de este, si no devuelve null.
                if(nodo != null){
                    matches.add(new SearchMatch(methodName, nodo, cUnit, true));
                }
            }
        }


        return matches;
    }
    private static CombinedTypeSolver generateTypeSolver(File projectRoot){
        if(typeSolver == null){
            typeSolver = new CombinedTypeSolver();  //Typesolver es la clase que contendrá todos los lugares donde se buscaran las clases importadas
            typeSolver.add(new JreTypeSolver());                       // Añadimos el JRE por defecto para las clases que incluye Java

            try {
                List<File> jarFiles = lookPathForJarFiles(projectRoot);
                for (File jar : jarFiles) {
                    typeSolver.add(new JarTypeSolver(jar.getAbsolutePath())); //Buscamos y añadimos al typesolver todos los JAR que encontremos dentro del proyecto.
                }
                List<File> srcFolders = lookPathForSourceFolders(projectRoot);
                for (File src : srcFolders) {
                    typeSolver.add(new JavaParserTypeSolver(src.getAbsoluteFile()));  //Añadimos las carpetas "src" con los ficheros fuente del proyecto al typesolver.
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoFilesInPathException e) {
                e.printStackTrace();
            }
        }
        return typeSolver;
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

    private static List<File> lookPathForSourceFolders(File directory) throws NoFilesInPathException { //TODO la ruta se la pediremos al usuario
        File[] files= directory.listFiles();
        if(files == null){
            throw new NoFilesInPathException();
        }
        List<File> srcFolders = new ArrayList<>();

        for (File file : files) {
            if (file.isDirectory()) {
                if (file.getName().equals("java")) {
                    srcFolders.add(file);
                }else{
                    List<File> tmpList = lookPathForSourceFolders(file);
                    srcFolders.addAll(tmpList);
                }

            }
        }
        return srcFolders;
    }

    private static class MethodCallVisitor extends VoidVisitorAdapter {
        private ArrayList<SearchMatch> matches = new ArrayList<>();
        private static String actualSearchComplete;
        private static String actualSearchName;
        private static CombinedTypeSolver typeSolver;
        private static CompilationUnit actualCUnit;

        @Override
        public void visit(MethodCallExpr node, Object arg) {
            MethodUsage solvedMethod = null;
            if(actualSearchName.equals(node.getName())) {
                try {
                    solvedMethod = JavaParserFacade.get(typeSolver).solveMethodAsUsage(node); //Intentamos resolver el tipo del nodo usando Java-symbol-solver, esta manera es la más eficaz de identificar un método pero no siempre es posible.

                    String qualifiedName = getMethodQualifiedNameFromDeclaration(solvedMethod);

                    if (actualSearchComplete.equals(qualifiedName)) {
                        matches.add(new SearchMatch(qualifiedName, node, actualCUnit, true));
                    }
                } catch (Exception e) {                 //Si falla el análisis de tipo, comprobamos simplemente el nombre y los parametros
                    String signature = null;
                    try {
                        signature = getMethodSignatureFromCall(node);
                        if(actualSearchName.equals(signature)){
                            matches.add(new SearchMatch(signature,node,actualCUnit,false));
                        }
                    }catch(UnsolvedTypeException | UnsolvedSymbolException u){
                        //e.printStackTrace();  //TODO resolver estas excepciones
                        System.out.println("UnsolvedException - "+node.toString()+" - "+node.getArgs().size()+" - "+node.getTypeArgs().toString());
                    }catch(UnsupportedOperationException unsop){
                        System.out.println("UnsuportedOperationException - "+node.toString());
                        unsop.printStackTrace();
                    }catch(RuntimeException r){
                        System.out.println("RuntimeException resolving node - "+node.toString());
                    }
                }
            }

            super.visit(node, arg);
        }

        private String getMethodQualifiedNameFromDeclaration(MethodUsage solvedMethod){  //Obtenemos el Qualified Name del método obtenido a través de la resolución de tipos
            String qualifiedName = solvedMethod.declaringType().getQualifiedName() + "." + solvedMethod.getName() + "(";
            for (int i = 0; i < solvedMethod.getParamTypes().size(); i++) {

                if (solvedMethod.getParamTypes().size() > 1 && i != 0) {
                    qualifiedName = qualifiedName.concat(",");
                }
                String type = solvedMethod.getParamTypes().get(i).describe();
                qualifiedName = qualifiedName.concat(type.substring(type.lastIndexOf(".") + 1));
            }
            qualifiedName = qualifiedName.concat(")");
            return qualifiedName;
        }
        private String getMethodSignatureFromCall(MethodCallExpr node){  //Obtenemos la signatura del método según los datos de la llamada
            String signature = node.getName() + "(";
            for (int i = 0; i < node.getArgs().size(); i++){
                if (node.getArgs().size() > 1 && i != 0){
                    signature = signature + ",";
                }
                if(node.getArgs().get(i) instanceof ArrayAccessExpr){  //TODO- esto soluciona los problemas de Java-symbol-solver con los accesos a un array usando corchetes <code>array[0]</code>
                    TypeUsage typeOfTheNode = JavaParserFacade.get(typeSolver).getType(node.getArgs().get(i).getChildrenNodes().get(0));
                    signature = signature + typeOfTheNode.describe();

                }else {
                    TypeUsage typeOfTheNode = JavaParserFacade.get(typeSolver).getType(node.getArgs().get(i));  //Dado un nodo 'Expression' obtenemos su tipo utilizando la herramienta Java-symbol-solver
                    signature = signature + typeOfTheNode.describe();
                }

            }
            signature = signature + ")";
            return signature;
        }

        public void setActualSearch(String name){

            actualSearchComplete = name;
            actualSearchName = name.substring(name.lastIndexOf(".") + 1, name.indexOf("("));
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
