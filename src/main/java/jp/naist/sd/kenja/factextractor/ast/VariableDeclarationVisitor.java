package jp.naist.sd.kenja.factextractor.ast;

import com.google.common.collect.Ordering;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import java.util.ArrayList;
import java.util.List;

public class VariableDeclarationVisitor extends ASTVisitor {
    private List<String> variables;

    public VariableDeclarationVisitor() {
        variables = new ArrayList<String>();
    }

    public List<String> getVariables() {
        return variables;
    }

    public void sortVariableList() {
        variables = Ordering.from(String.CASE_INSENSITIVE_ORDER).sortedCopy(variables);
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {
        StringBuilder variableNameBase = new StringBuilder();
        variableNameBase.append(node.getType());
        variableNameBase.append(" ");
        for (Object obj : node.fragments()) {
            StringBuilder variableName = new StringBuilder(variableNameBase);
            VariableDeclarationFragment fragment = (VariableDeclarationFragment)obj;
            variableName.append(fragment.getName());
            for (int dimension = 0; dimension < fragment.getExtraDimensions(); dimension++) {
                variableName.append("[]");
            }
            variables.add(variableName.toString());
        }

        return true;
    }
}
