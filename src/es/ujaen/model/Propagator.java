package es.ujaen.model;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Marcial J. Sánchez Santiago on 11/12/15.
 */
public class Propagator {

    private static Collection<XmlManager.SourceDescription> sources;
    private static Collection<XmlManager.DerivationDescription> derived;
    private static Collection<XmlManager.SafeDescription> safes;
    private static SearchMatch actualMatch;

    public static void processMatch(SearchMatch match, HistoryNode parent){

        /**
         * Comprobamos el sink al que pertenece, el parámetro vulnerable y procesamos la expresion del argumento correspondiente
         */
        actualMatch = match;
        HistoryNode actualHistoryNode = new HistoryNode(parent, match.getMatchNode(), "MethodCallExpr", HistoryNode.NOT_END);
        Integer argPosition = match.getMatchedSink().getVulnerableParameter();
        Node vulnerableArg = match.getMatchNode().getArgs().get(argPosition);
        processExpression(vulnerableArg, "Expression", actualHistoryNode);

    }
    public static void processExpression(Node expression, String type, HistoryNode parent){

        if(expression instanceof LiteralExpr){
            processLiteral((LiteralExpr)expression, "LiteralExpr", parent);
        }

        if(expression instanceof NameExpr){
            processNameExpr((NameExpr) expression, "NameExpr", parent);
        }

        if(expression instanceof BinaryExpr){
            processBinary((BinaryExpr) expression, "BinaryExpr", parent);
        }

        if(expression instanceof MethodCallExpr){
            processMethodCall((MethodCallExpr)expression, "MethodCallExpr", parent);
        }
        if (expression instanceof AssignExpr){
            processAssignExpr((AssignExpr) expression, "Assignation", parent);
        }
        if (expression instanceof VariableDeclarator){
            processVariableDeclarator((VariableDeclarator) expression, "Declaration", parent);

        }
    }

    public static void processLiteral(LiteralExpr expression, String type, HistoryNode parent){
        /**
         * Con una expresion literal detenemos la propagación y se considera no contaminado
         * Expresiones literales: "Cadena", 1, 1.22
         */
        HistoryNode actualHistoryNode = new HistoryNode(parent, expression, type, HistoryNode.NOT_POISONED);
    }

    public static void processBinary(BinaryExpr expression, String type, HistoryNode parent){
        /**
         * Expresiones Binarias, como una concatenación de cadenas
         */
        HistoryNode actualHistoryNode = new HistoryNode(parent, expression, type, HistoryNode.NOT_END);
        for(Node component : expression.getChildrenNodes()){
            processExpression(component, "", actualHistoryNode);
        }
    }

    public static void processNameExpr(NameExpr expression, String type, HistoryNode parent){
        HistoryNode actualHistoryNode = new HistoryNode(parent, expression, type, HistoryNode.NOT_END);
        List<Node>  assigns = Searcher.searchAssignments(expression, actualMatch);
        for(Node assign : assigns){
            processExpression(assign, "", actualHistoryNode);
        }
    }

    public static void processAssignExpr(AssignExpr expression, String type, HistoryNode parent){
        HistoryNode actualHistoryNode = new HistoryNode(parent, expression, type, HistoryNode.NOT_END);
        processExpression(expression.getValue(), "" ,actualHistoryNode);
    }

    //TODO Procesar Parametro

    public static void processVariableDeclarator(VariableDeclarator expression, String type, HistoryNode parent){
        if(expression.getInit() != null) {
            HistoryNode actualHistoryNode = new HistoryNode(parent, expression, type, HistoryNode.NOT_END);
            processExpression(expression.getInit(), "", actualHistoryNode);
        }
    }

    public static void processMethodCall(MethodCallExpr expression, String type, HistoryNode parent){
        //System.out.println(expression.toString());
        if(parent.containsAncestor(expression)){
            HistoryNode actualHistoryNode = new HistoryNode(parent, expression, type, HistoryNode.RECURSION);
            return;
        }
        CompilationUnit cUnit = CompilationUnitManager.getCompilationUnitFromNode(expression);
        /**
         * Comprobar si el método corresponde con algun método SOURCE
         */
        for(XmlManager.SourceDescription source : sources) {
            SearchMatch sourceMatch = Searcher.checkMethodCallExprAgainstMethodQualifiedName(source.getID(), expression, cUnit, source);
            if (sourceMatch != null) {
                HistoryNode actualHistoryNode = new HistoryNode(parent, expression, type, HistoryNode.POISONED);
                return;
            }
        }
        /**
         * Si el método no corresponde a ningún método SOURCE buscamos la declaración del método y analizamos todos sus returns
         */
        MethodDeclaration declaration = CompilationUnitManager.getMethodDeclaration(expression.getName());
        if(declaration != null) {

            HistoryNode actualHistoryNode = new HistoryNode(parent, expression, type, HistoryNode.NOT_END);
            List<ReturnStmt> returns = getMethodReturns(declaration);
            for (ReturnStmt ret: returns) {
                processExpression(ret.getExpr(), "", actualHistoryNode);
            }
            return;
        }
        /**
         * Si no encontramos declaración del método verificamos si es alguno de los métodos DERIVED, si lo es pasamos a procesar la variable que llama a este método
         */
        for(XmlManager.DerivationDescription derivation : derived){
            SearchMatch derivationMatch = Searcher.checkMethodCallExprAgainstMethodQualifiedName(derivation.getID(), expression, cUnit, derivation);
            if(derivationMatch != null){
                HistoryNode actualHistoryNode = new HistoryNode(parent, expression, type, HistoryNode.DERIVATION);
                processExpression(expression.getChildrenNodes().get(0),"",actualHistoryNode);
                return;
            }
        }
        /**
         * Si no se da ninguno de los casos anteriores entonces paramos el análisis en este punto e indicamos en el historial que no se ha podido profundizar
         */
        HistoryNode actualHistoryNode = new HistoryNode(parent, expression, type, HistoryNode.CANT_CONTINUE);
    }

    public static void setDescriptions(Collection<XmlManager.SourceDescription> newSources, Collection<XmlManager.DerivationDescription> newDerived, Collection<XmlManager.SafeDescription> newSafes){
        sources = newSources;
        derived = newDerived;
        safes = newSafes;
    }

    private static List<ReturnStmt> getMethodReturns(Node node){

        List<ReturnStmt> returns = new ArrayList<>();
        if(node instanceof ReturnStmt){
            returns.add((ReturnStmt)node);
        }else{
            if(!node.getChildrenNodes().isEmpty()) {
                for (Node child : node.getChildrenNodes()) {
                    returns = getMethodReturns(child);
                }
            }
        }
        return returns;
    }

}
