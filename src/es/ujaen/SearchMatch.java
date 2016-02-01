package es.ujaen;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;

import javax.print.attribute.standard.Sides;

/**
 * Created by Marcial J. Sánchez Santiago on 3/12/15.
 */
public class SearchMatch {
    private String name;
    private MethodCallExpr matchNode;
    private CompilationUnit cUnit;
    private Object xmlDescriptor;
    private Boolean fullSignatureCheck;

    SearchMatch(String _name, MethodCallExpr _matchNode, CompilationUnit _cUnit, Object _xmlDescriptor, Boolean _check) {
        name = _name;
        matchNode = _matchNode;
        cUnit = _cUnit;
        xmlDescriptor = _xmlDescriptor;
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

    public XmlManager.SinkDescription getMatchedSink(){

        if (xmlDescriptor instanceof XmlManager.SinkDescription){
            return (XmlManager.SinkDescription)xmlDescriptor;
        }
        return null;
    }

    public XmlManager.SourceDescription getMatchedSource(){

        if (xmlDescriptor instanceof XmlManager.SourceDescription){
            return (XmlManager.SourceDescription)xmlDescriptor;
        }
        return null;
    }
}
