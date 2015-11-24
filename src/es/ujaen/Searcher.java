package es.ujaen;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import me.tomassetti.symbolsolver.javaparsermodel.JavaParserFacade;
import me.tomassetti.symbolsolver.model.resolution.TypeSolver;
import me.tomassetti.symbolsolver.model.typesystem.TypeUsage;
import me.tomassetti.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import me.tomassetti.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import me.tomassetti.symbolsolver.resolution.typesolvers.JreTypeSolver;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by blitzer on 20/11/15.
 */
public class Searcher {
    Searcher(){}

    public static Collection searchReferences(List<CompilationUnit> cUnits, String methodName){
        return search(cUnits, "reference", methodName);
    }

    public static Collection searchDeclarations(List<CompilationUnit> cUnits, String methodName){
        return search(cUnits, "declaration", methodName);
    }

    private static Collection search(List<CompilationUnit> cUnits, String searchType, String methodName ){ // Este metodo se encarga de realizar todas las busquedas según "searchType"
        Collection<SearchMatch> matches = new ArrayList<SearchMatch>();

        if(searchType == "reference"){
            MethodCallVisitor visitor = new MethodCallVisitor();
            visitor.setActualSearch(methodName);

            CombinedTypeSolver typeSolver = new CombinedTypeSolver();
            typeSolver.add(new JreTypeSolver());
            typeSolver.add(new JavaParserTypeSolver(new File("/media/Almacenamiento/Drive/Ujaen/Septimo/Proyecto/SRCPeliculasBD")));
            visitor.setTypeSolver(typeSolver);

            for(CompilationUnit cUnit : cUnits){

                visitor.visit(cUnit, null);

            }
            matches = visitor.getMatches();

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
        private static TypeSolver typeSolver;

        @Override
        public void visit(MethodCallExpr node, Object arg) {
            if(node.getName().equals("executeUpdate")){
                TypeUsage typeOfTheNode = JavaParserFacade.get(typeSolver).getType(node);
                System.out.println(typeOfTheNode.toString());

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

        public void setTypeSolver(TypeSolver newtypeSolver){
            typeSolver = newtypeSolver;
        }
        public ArrayList<SearchMatch> getMatches(){
            return matches;
        }

    }
}
