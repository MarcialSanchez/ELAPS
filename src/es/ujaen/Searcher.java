package es.ujaen;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

/**
 * Created by blitzer on 20/11/15.
 */
public class Searcher {
    Searcher(){}

    public static Collection searchReferences(CompilationUnit cUnit, String methodName){
        return search(cUnit, "reference", methodName);
    }

    public static Collection searchDeclarations(CompilationUnit cUnit, String methodName){
        return search(cUnit, "declaration", methodName);
    }

    private static Collection search(CompilationUnit cUnit, String searchType, String methodName ){ // Este metodo se encarga de realizar todas las busquedas según "searchType"
        Collection<SearchMatch> matches = new ArrayList<SearchMatch>();

        if(searchType == "reference"){

        }
        if(searchType == "declaration"){

        }


        return matches;
    }

    private class SearchMatch {
        private String name;
        private Node matchNode;
        private CompilationUnit cUnit;

        SearchMatch(String _name, Node _matchNode){
            name = _name;
            matchNode = _matchNode;
        }
    }

    private static class MethodCallVisitor extends VoidVisitorAdapter {
        ArrayList<SearchMatch> matches = new ArrayList<>();

        @Override
        public void visit(MethodCallExpr node, Object arg) {
            super.visit(node, arg);
        }

    }
}
