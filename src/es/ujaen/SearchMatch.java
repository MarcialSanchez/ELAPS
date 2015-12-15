package es.ujaen;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;

import javax.print.attribute.standard.Sides;

/**
 * Created by blitzer on 3/12/15.
 */
public class SearchMatch {
    private String name;
    private MethodCallExpr matchNode;
    private CompilationUnit cUnit;
    private XmlManager.SinkDescription sink;
    private Boolean fullSignatureCheck;

    SearchMatch(String _name, MethodCallExpr _matchNode, CompilationUnit _cUnit, XmlManager.SinkDescription _sink, Boolean _check) {
        name = _name;
        matchNode = _matchNode;
        cUnit = _cUnit;
        sink = _sink;
        fullSignatureCheck = _check;
    }

    public String getName() {
        return name;
    }

    public MethodCallExpr getMatchNode() {
        return matchNode;
    }

    public CompilationUnit getcUnit() {
        return cUnit;
    }

    public XmlManager.SinkDescription getMatchedSink(){ return sink; }
}
