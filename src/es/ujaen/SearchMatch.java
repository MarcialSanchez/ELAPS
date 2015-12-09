package es.ujaen;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

/**
 * Created by blitzer on 3/12/15.
 */
public class SearchMatch {
    private String name;
    private Node matchNode;
    private CompilationUnit cUnit;
    private Boolean fullSignatureCheck;

    SearchMatch(String _name, Node _matchNode, CompilationUnit _cUnit, Boolean _check) {
        name = _name;
        matchNode = _matchNode;
        cUnit = _cUnit;
        fullSignatureCheck = _check;
    }

    public String getName() {
        return name;
    }

    public Node getMatchNode() {
        return matchNode;
    }

    public CompilationUnit getcUnit() {
        return cUnit;
    }
}
