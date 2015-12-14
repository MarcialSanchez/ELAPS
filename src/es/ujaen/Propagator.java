package es.ujaen;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.Collection;

/**
 * Created by blitzer on 11/12/15.
 */
public class Propagator {

    private static Collection<XmlManager.SourceDescription> sources;
    private static Collection<XmlManager.DerivationDescription> derived;
    private static Collection<XmlManager.SafeDescription> safes;

    public static void processNode(MethodCallExpr matchNode, HistoryNode parent){

    }
    public static void processExpression(Node expression, String type, HistoryNode parent){

    }

    public static void processPrimitive(Node expression, String type, HistoryNode parent){

    }

    public static void processVariable(Node expression, String type, HistoryNode parent){

    }

    public static void processConcatenation(Node expression, String type, HistoryNode parent){

    }

    public static void processMethodCall(Node expression, String type, HistoryNode parent){

    }

    public static void setDescriptions(Collection<XmlManager.SourceDescription> newSources, Collection<XmlManager.DerivationDescription> newDerived, Collection<XmlManager.SafeDescription> newSafes){
        sources = newSources;
        derived = newDerived;
        safes = newSafes;
    }

}
