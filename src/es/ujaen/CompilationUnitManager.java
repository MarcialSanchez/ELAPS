package es.ujaen;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by blitzer on 30/10/15.
 */
public class CompilationUnitManager {

    static Map<CompilationUnit, DeclarationInfo> cunitsinfo = new HashMap<CompilationUnit, DeclarationInfo>();
    private static final Logger log = Logger.getLogger( CompilationUnitManager.class.getName() );

    public static CompilationUnit getCompilationUnitFromNode(Node node){
        while(!(node instanceof CompilationUnit)){
            node = node.getParentNode();
        }
        return (CompilationUnit)node;
    }

    public static DeclarationInfo getCUnitInfo(CompilationUnit cunit) {
        DeclarationInfo info = cunitsinfo.get(cunit);
        if(info == null) {
            DeclarationFinderVisitor visitor = new DeclarationFinderVisitor();
            visitor.visit(cunit, null);
            info = visitor.getInfo();
            // save for future use
            cunitsinfo.put(cunit, info);
        }
        return info;
    }

    private static class DeclarationFinderVisitor extends VoidVisitorAdapter {

        DeclarationInfo info = new DeclarationInfo();
        MethodDeclaration fCurrentMethod = null;

        //////////////////////////////// VISITORS START ////////////////////////////////
        @Override
        public void visit(MethodDeclaration node, Object arg) {
            log.log(Level.FINE, "NODO METODO VISITADO");
            fCurrentMethod = node;
            System.out.println(node.getDeclarationAsString());
            info.setMethodDeclarator(node.getName(),node);
            super.visit(node, arg);
            fCurrentMethod = null;
        }

        @Override
        public void visit(VariableDeclarator node, Object arg) {
            VariableDeclaratorId name = node.getId();
            boolean isFinal = ModifierSet.isFinal(node.getClass().getModifiers());
            if(isFinal) {
                info.setFinal(name);
            }
            info.setVariableDeclarator(getKey(name), node);
            log.log(Level.FINE, "1. Encountered " + node);
        }
        @Override
        public void visit(Parameter node, Object arg) {
            VariableDeclaratorId name = node.getId();
            info.setParam(name);
            boolean isFinal = ModifierSet.isFinal(node.getClass().getModifiers());
            if(isFinal) {
                info.setFinal(name);
            }

            info.setVariableDeclarator(getKey(name), node);
            log.log(Level.FINE, "1. Encountered " + node);
        }

        /*public void endVisit(MethodDeclaration node) {
            fCurrentMethod = null;
        }*/
        //////////////////////////////// END OF VISITORS ////////////////////////////////

        private String getKey(VariableDeclaratorId name) {
            String result = null;
            if(fCurrentMethod != null) {
                result = fCurrentMethod.getName() + "/" + name.getName();
            }else {
                result = "GLOBAL" + name.getName();
            }
            log.log(Level.FINE, "Creating key " + result);

            return result;
        }

//		public boolean visit(VariableDeclarationStatement node) {
//			if(TRACE) System.err.println("5. Encountered " + node);
//			return true;
//		}

        public DeclarationInfo getInfo() {
            return info;
        }
    }

    /*

*/

}

