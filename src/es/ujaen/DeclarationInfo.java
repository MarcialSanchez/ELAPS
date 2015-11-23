package es.ujaen;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.body.VariableDeclaratorId;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by blitzer on 16/11/15.
 */
public class DeclarationInfo {

    private Map<String, Node> variablename2node = new HashMap<String, Node>();
    private HashMap<VariableDeclaratorId, Boolean> finalMap = new HashMap<VariableDeclaratorId, Boolean>();
    private HashMap<VariableDeclaratorId, Boolean> paramMap = new HashMap<VariableDeclaratorId, Boolean>();
    private Map<String, Node> methodname2node = new HashMap<>();

    public void setVariableDeclarator(String name, Node node) {
        variablename2node.put(name, node);
    }

    public VariableDeclarator getVariableDeclarator(String name) {
        return (VariableDeclarator) variablename2node.get(name);
    }
    public VariableDeclarator getVariableDeclarator(VariableDeclaratorId name) {
        Node node = name;
        do {
            node = node.getParentNode();
        } while (node != null && !(node instanceof MethodDeclaration));

        String key = null;

        if (node != null) {
            key = node.toString() + "/" + name.getName();
        } else {
            key = "GLOBAL" + name.getName();
        }

        VariableDeclarator var = getVariableDeclarator(key);
        if (var == null && node != null) {
            key = "GLOBAL" + name.getName();
            var = getVariableDeclarator(key);
        }

        return var;
    }

    public void setMethodDeclarator(String name, Node node){
        methodname2node.put(name,node);
    }

    public MethodDeclaration getMethodDeclarator(String name){
        return (MethodDeclaration) methodname2node.get(name);
    }

    public void setFinal(VariableDeclaratorId name) {
        finalMap.put(name, null);
    }

    public boolean isFinal(VariableDeclaratorId name) {
        return finalMap.get(name) != null;
    }

    public void setParam(VariableDeclaratorId name) {
        paramMap.put(name, null);
    }

    public boolean isParam(VariableDeclaratorId name) {
        return paramMap.get(name) != null;
    }




    public String toString() {
        return "VariableDeclarator for [" + variablename2node.size() + "] name(s) & MedhodDeclarator for ["+ methodname2node.size() + "] name(s).";
    }
}