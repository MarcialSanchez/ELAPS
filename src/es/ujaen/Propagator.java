package es.ujaen;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;

import javax.lang.model.element.Name;
import javax.swing.event.HyperlinkEvent;
import java.util.Collection;
import java.util.List;

/**
 * Created by blitzer on 11/12/15.
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
        HistoryNode actualHistoryNode = new HistoryNode(parent, match.getMatchNode(), "MethodCallExpr");
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
            processVariableDeclarator((VariableDeclarator) expression, "Declaration" ,parent);

        }
    }

    public static void processLiteral(LiteralExpr expression, String type, HistoryNode parent){  // Expresiones literales: "Cadena" , 1, 1.22
        /**
         * Con una expresion literal detenemos la propagación y se considera no contaminado
         */
        HistoryNode actualHistoryNode = new HistoryNode(parent, expression, type, false);
    }

    public static void processBinary(BinaryExpr expression, String type, HistoryNode parent){    // Expresiones Binarias, como una concatenación de cadenas
        HistoryNode actualHistoryNode = new HistoryNode(parent, expression, type);
        for(Node component : expression.getChildrenNodes()){
            processExpression(component, "", actualHistoryNode);
        }
    }

    public static void processNameExpr(NameExpr expression, String type, HistoryNode parent){
        HistoryNode actualHistoryNode = new HistoryNode(parent, expression, type);
        List<Node>  assigns = Searcher.searchAssignments(expression, actualMatch);
        for(Node assign : assigns){
            processExpression(assign, "", actualHistoryNode);
        }
    }

    public static void processAssignExpr(AssignExpr expression, String type, HistoryNode parent){
        HistoryNode actualHistoryNode = new HistoryNode(parent, expression, type);
        processExpression(expression.getValue(), "" ,actualHistoryNode);
    }

    public static void processVariableDeclarator(VariableDeclarator expression, String type, HistoryNode parent){
        HistoryNode actualHistoryNode = new HistoryNode(parent, expression, type);
        processExpression(expression.getInit(), "" ,actualHistoryNode);
    }

    public static void processMethodCall(MethodCallExpr expression, String type, HistoryNode parent){
        HistoryNode actualHistoryNode = new HistoryNode(parent, expression, type);
        //TODO

    }

    public static void setDescriptions(Collection<XmlManager.SourceDescription> newSources, Collection<XmlManager.DerivationDescription> newDerived, Collection<XmlManager.SafeDescription> newSafes){
        sources = newSources;
        derived = newDerived;
        safes = newSafes;
    }

}
