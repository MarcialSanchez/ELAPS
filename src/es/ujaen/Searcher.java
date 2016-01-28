package es.ujaen;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
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

    public static Collection searchReferences(File projectRoot,File javaRoot, List<CompilationUnit> cUnits, XmlManager.SinkDescription sink){

        Collection<SearchMatch> matches = new ArrayList<SearchMatch>();
        generateTypeSolver(projectRoot, javaRoot);
        MethodCallVisitor visitor = new MethodCallVisitor();   //Instanciamos el visitor que se encargará de recorrer los nodos.
        visitor.setActualSearch(sink);                   //Asignamos la busqueda actual extraida del fichero sink.xml.
        visitor.setTypeSolver(typeSolver);                     //Asignamos el typeSolver que hemos configurado anteriormente.

        for(CompilationUnit cUnit : cUnits){                   //Por cada CompilationUnit (Ficheros.java) pasamos el visitor en busca de referencias al metodo que buscamos

            visitor.setCUnit(cUnit);
            visitor.visit(cUnit, null);

        }
        matches.addAll(visitor.getMatches());                        //Tras escanear todos los documentos obtenemos la lista con los matches que hayamos encontrado.

        return matches;
    }

    private static CombinedTypeSolver generateTypeSolver(File projectRoot, File javaRoot){
        if(typeSolver == null){
            typeSolver = new CombinedTypeSolver();  //Typesolver es la clase que contendrá todos los lugares donde se buscaran las clases importadas
            typeSolver.add(new JreTypeSolver());                       // Añadimos el JRE por defecto para las clases que incluye Java

            try {
                List<File> jarFiles = lookPathForJarFiles(projectRoot);
                jarFiles.addAll(loadLocalJars());
                for (File jar : jarFiles) {
                    typeSolver.add(new JarTypeSolver(jar.getAbsolutePath())); //Buscamos y añadimos al typesolver todos los JAR que encontremos dentro del proyecto.
                }
                typeSolver.add(new JavaParserTypeSolver(javaRoot.getAbsoluteFile()));  //Añadimos las carpetas "src" con los ficheros fuente del proyecto al typesolver.

            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoFilesInPathException e) {
                e.printStackTrace();
            }
        }
        return typeSolver;
    }

    private static List<File> loadLocalJars() throws NoFilesInPathException{
        return lookPathForJarFiles(new File("resources/APIs/tomcat"));
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

    public static SearchMatch checkMethodCallExprAgainstMethodQualifiedName(String qNameForSearch,
                                                                            MethodCallExpr node,
                                                                            CompilationUnit actualCUnit,
                                                                            Object xmlDescriptor
    ){
        MethodUsage solvedMethod = null;
//        if(node.getArgs().size()>0) { //Para comprobar el tipo de los argumentos
//          System.out.println(node.toString());
//          System.out.println(node.getArgs().get(0).getClass());
//        }
        String actualSearchName = getNameFromQualifiedName(qNameForSearch);
        if(actualSearchName.equals(node.getName())) {
            //System.out.println(node.getName()+ " _>  "+ actualSearchName);
            String methodName = ResolveMethodName(node);
            if(methodName != null){
                if (qNameForSearch.equals(methodName)) {
                    return new SearchMatch(methodName, node, actualCUnit, xmlDescriptor, true);
                }
            }
            String nodesignature = null;   //Si falla el análisis de tipo, comprobamos simplemente el nombre y los parametros
            try {
                nodesignature = getMethodSignatureFromCall(node);
            }catch(UnsolvedTypeException | UnsolvedSymbolException u){
                //e.printStackTrace();  //TODO resolver estas excepciones
                //System.out.println("UnsolvedException - "+node.toString()+" - "+node.getArgs().size()+" - "+node.getTypeArgs().toString());
            }catch(UnsupportedOperationException unsop){
                //System.out.println("UnsuportedOperationException - "+node.toString());
            }catch(RuntimeException r){
                //System.out.println("RuntimeException resolving node - "+node.toString());
            }
            //if(xmlDescriptor instanceof XmlManager.DerivationDescription){
            //}
            String methodSearchSignature = getMethodSignatureFromQualifiedName(qNameForSearch);

            if(methodSearchSignature.equals(nodesignature)){
                return new SearchMatch(nodesignature,node,actualCUnit, xmlDescriptor, false);
            }
        }
        return null;
    }

    public static String ResolveMethodName(MethodCallExpr node){
        MethodUsage solvedMethod = null;
        solvedMethod = getMethodDeclaration(node); //Intentamos resolver el tipo del nodo usando Java-symbol-solver, esta manera es la más eficaz de identificar un método pero no siempre es posible.
        if (solvedMethod != null) {
            String qualifiedName = getMethodQualifiedNameFromDeclaration(solvedMethod);
            return qualifiedName;
        }
        return null;
    }

    public static MethodUsage getMethodDeclaration(MethodCallExpr node){
        try{
            return  JavaParserFacade.get(typeSolver).solveMethodAsUsage(node);

        }catch (Exception e){
            return null;
        }
    }

    public static String getNameFromQualifiedName(String qName){
       return qName.substring(qName.lastIndexOf(".") + 1, qName.indexOf("("));
    }

    private static String getMethodQualifiedNameFromDeclaration(MethodUsage solvedMethod){  //Obtenemos el Qualified Name del método obtenido a través de la resolución de tipos
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

    private static String getMethodSignatureFromCall(MethodCallExpr node){  //Obtenemos la signatura del método según los datos de la llamada
        String signature = node.getName() + "(";
        for (int i = 0; i < node.getArgs().size(); i++){
            if (node.getArgs().size() > 1 && i != 0){
                signature = signature + ",";
            }
            if(node.getArgs().get(i) instanceof ArrayAccessExpr){  //TODO- Comprobar futuras actus de J-S-S; esto soluciona los problemas de Java-symbol-solver con los accesos a un array usando corchetes <code>array[0]</code> que no esta actualmente soportado por JavaSS
                TypeUsage typeOfTheNode = JavaParserFacade.get(typeSolver).getType(node.getArgs().get(i).getChildrenNodes().get(0));
                String typeAlone = typeOfTheNode.describe().substring(typeOfTheNode.describe().lastIndexOf(".") + 1, typeOfTheNode.describe().indexOf("["));
                signature = signature + typeAlone;
            }else{
                try {
                    TypeUsage typeOfTheNode = JavaParserFacade.get(typeSolver).getType(node.getArgs().get(i));  //Dado un nodo 'Expression' obtenemos su tipo utilizando la herramienta Java-symbol-solver
                    signature = signature + typeOfTheNode.describe();
                }catch (Exception e){
                    //TODO "Algunos casos específicos fallan al tratar de resolverse con JSS y saltan aqui"
                }
            }
        }
        signature = signature + ")";
        return signature;
    }

    private static String getMethodSignatureFromQualifiedName(String qName){
        return qName.substring(qName.lastIndexOf(".")+1,qName.lastIndexOf(")")+1);
    }

    private static String getReturnType(MethodCallExpr node) {
        MethodUsage solvedMethod = JavaParserFacade.get(typeSolver).solveMethodAsUsage(node);
        return solvedMethod.getDeclaration().getReturnType().describe();
    }

    private static class MethodCallVisitor extends VoidVisitorAdapter {
        private ArrayList<SearchMatch> matches = new ArrayList<>();
        private static XmlManager.SinkDescription actualSink;
        private static String actualSearchComplete;
        private static CompilationUnit actualCUnit;

        @Override
        public void visit(MethodCallExpr node, Object arg) {
            SearchMatch match = checkMethodCallExprAgainstMethodQualifiedName(actualSearchComplete, node, actualCUnit, actualSink);
            if(match != null){
                matches.add(match);
            }

            super.visit(node, arg);
        }

        public void setActualSearch(XmlManager.SinkDescription sink){
            actualSink = sink;
            actualSearchComplete = sink.getID();
        }

        public void setTypeSolver(CombinedTypeSolver newtypeSolver){
            typeSolver = newtypeSolver;
        }

        public void setCUnit(CompilationUnit cUnit){ actualCUnit = cUnit; }

        public ArrayList<SearchMatch> getMatches(){
            return matches;
        }

    }

    public static List<Node> searchAssignments(NameExpr expression, SearchMatch match){
        //TODO añadir a la lista las declaraciones de parámetros
        List<Node> ourAssigns = new ArrayList<>();

        AssignVisitor visitor = new AssignVisitor();
        visitor.visit(match.getcUnit(), null);
        List<Node> allAssigns = visitor.getList();  //Obtenemos todas las expresiones de asignación en el mismo CUnit donde tenemos el nombre
        for(Node assign : allAssigns){
            if(checkPreviousAssignation(expression, assign)){
                ourAssigns.add(assign);
            }
        }

        return ourAssigns;
    }

    private static Boolean checkPreviousAssignation(NameExpr expression, Node node){

        Integer lineOfTheName = expression.getBeginLine();
        Integer lineOfTheAssignment = node.getBeginLine();
        if (lineOfTheAssignment < lineOfTheName) {

            if (node instanceof AssignExpr) {
                AssignExpr assign = (AssignExpr) node;
                if (expression.getName().equals(assign.getTarget().toString())) {
                    return true;

                }
            }
            if (node instanceof VariableDeclarator){
                VariableDeclarator declaration = (VariableDeclarator) node;
                if (expression.getName().equals(declaration.getId().getName())){
                    return true;
                }
            }
        }
        return false;
    }

    private static class AssignVisitor extends VoidVisitorAdapter{
        private List<Node> expressionsFound = new ArrayList<>();

        public void visit(AssignExpr node, Object arg){
            expressionsFound.add(node);
        }

        public void visit(VariableDeclarator node, Object arg){

            if(node.getId() != null){
                expressionsFound.add(node);
            }
        }

        public List<Node> getList(){

            return expressionsFound;
        }
    }
}
