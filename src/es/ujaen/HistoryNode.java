package es.ujaen;

import com.github.javaparser.ast.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by blitzer on 14/12/15.
 */


public class HistoryNode {

    public static final int NOT_END = 0;
    public static final int NOT_POISONED = 1;
    public static final int POISONED = 2;
    public static final int CANT_CONTINUE = 3;

    private HistoryNode parent;
    private List<HistoryNode> children = new ArrayList<>();
    private Node expression;
    private String nodeType ;
    private Integer level;
    private int historyType;

    public HistoryNode(){
        this.level = 0;
    }
    public HistoryNode(HistoryNode parent, Node expression, String nodeType, int historyType) {
        this.parent = parent;
        parent.addChildren(this);
        this.historyType = historyType;
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
        return historyType==2;
    }

    public Boolean isEndNode(){
        return historyType == 1 || historyType == 2;
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
                if(historyType == 3){
                    output = output + " Can't continue; Any declaration of the method found";
                }else{
                    output = output + " -> Poisoned end: " + isPoisoned();
                }

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
