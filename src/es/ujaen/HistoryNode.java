package es.ujaen;

import com.github.javaparser.ast.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by blitzer on 14/12/15.
 */


public class HistoryNode {

    private HistoryNode parent;
    private List<HistoryNode> children = new ArrayList<>();
    private Node expression;
    private String nodeType ;
    private Integer level;
    private Boolean poisoned;
    private Boolean endNode;

    public HistoryNode(){
        this.level = 0;
    }
    public HistoryNode(HistoryNode parent, Node expression, String nodeType, Boolean poisoned) {
        this.parent = parent;
        parent.addChildren(this);
        this.endNode = true;
        this.poisoned = poisoned;
        this.level = parent.getLevel()+1;
        this.expression = expression;
        this.nodeType = nodeType;
    }

    public HistoryNode(HistoryNode parent, Node expression, String nodeType) {
        this.parent = parent;
        parent.addChildren(this);
        this.endNode = false;
        this.poisoned = null;
        this.level = parent.getLevel()+1;
        this.expression = expression;
        this.nodeType = nodeType;
    }

    public void addChildren(HistoryNode child){
            children.add(child);
    }

    public List<HistoryNode> getChildren() {
        return children;
    }

    public HistoryNode getParent() {
        return parent;
    }


    public Node getExpression() {
        return expression;
    }

    public Boolean isPoisoned() {
        return poisoned;
    }

    public Boolean isEndNode(){
        return endNode;
    }

    public Integer getLevel(){
        return level;
    }

    public Boolean isRootNode(){
        return level == 1;
    }

    public Boolean isMasterRootNode(){
        return level == 0;
    }

    public void printHistoryInConsole(){
        String tabulation = "";
        for(Integer nTAb=0;nTAb<level;nTAb++){
            tabulation = tabulation+"   ";
        }
        if( level==1){
            System.out.println("");
        }
        if( expression!=null) {
            String output = tabulation + " - "  + expression.toString() + " - " + nodeType;
            if(isEndNode()){
                output = output + " -> Poisoned end: " + isPoisoned();
            }
            System.out.println(output);

        }else{
            System.out.println("Start Propagation tree: ");
        }
        for(HistoryNode child : children){
            child.printHistoryInConsole();
        }
    }
}
