package jp.naist.sd.kenja.factextractor.ast;

import java.lang.reflect.Modifier;
import java.util.List;

import jp.naist.sd.kenja.factextractor.Blob;
import jp.naist.sd.kenja.factextractor.Tree;
import jp.naist.sd.kenja.factextractor.Treeable;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

public class ASTMethod implements Treeable{
	
	private Blob body;
	private Blob parameters;
	private Blob modifiers;
	
	private Tree root; 
	
	private static final String BODY_BLOB_NAME = "body";
	private static final String PARAMETERS_BLOB_NAME = "parameters";
	private static final String MODIFIERS_BLOB_NAME = "modifiers";
	
	private boolean isConstructor;
	
	private String rootTreeName;

	protected ASTMethod(){
		
	}
	
	protected ASTMethod(MethodDeclaration node){
		rootTreeName = getTreeName(node);
		root = new Tree(rootTreeName);
		
		isConstructor = node.isConstructor();
		setBody(node);
		setParameters(node.parameters());
		
		int methodModifiers = node.getModifiers();
		setModifiers(methodModifiers);
	}
	
	private String getTreeName(MethodDeclaration node){
		StringBuilder result = new StringBuilder(node.getName().toString());
		result.append("(");
		for(Object item: node.parameters()){
			SingleVariableDeclaration parameter = (SingleVariableDeclaration)item;
			result.append(parameter.getType().toString());
			//result.append(" ");
			//result.append(parameter.getName());
			result.append(",");
		}
		int lastIndex = result.lastIndexOf(",");
		if(lastIndex > 0)
			result.deleteCharAt(lastIndex);
		result.append(")");
		return result.toString();
	}
	
	private void setBody(MethodDeclaration node){
		body = new Blob(BODY_BLOB_NAME);
		if(node.getBody() == null)
			body.setBody("");
		else
			body.setBody(node.getBody().toString());

		root.append(body);
	}
	
	private void setParameters(List parametersList){	
		parameters = new Blob(PARAMETERS_BLOB_NAME);
		root.append(parameters);
		String parameterBody = "";
		for(Object item: parametersList){
			SingleVariableDeclaration parameter = (SingleVariableDeclaration)item;
			parameterBody += parameter.getType().toString();
			parameterBody += " ";
			parameterBody += parameter.getName();
			parameterBody += "\n";
		}
		parameters.setBody(parameterBody);
	}

	private void setModifiers(int modifiers_num){
		modifiers = new Blob(MODIFIERS_BLOB_NAME);
		root.append(modifiers);
		
		if (Modifier.isPrivate(modifiers_num)) {
			modifiers.setBody("private\n");
		} else if (Modifier.isProtected(modifiers_num)) {
			modifiers.setBody("protected\n");
		} else if (Modifier.isPublic(modifiers_num)) {
			modifiers.setBody("public\n");
		} else {
			modifiers.setBody("internal\n");
		}
	}

	public String getName() {
		return rootTreeName;
	}

	public void conflict(int number) {
		StringBuilder builder = new StringBuilder();
		builder.append(rootTreeName);
		builder.append(".conflicted");
		builder.append(number);
		root.setName(builder.toString());
	}

	public boolean isConstructor(){
		return isConstructor;
	}
	
	public static ASTMethod fromMethodDeclaralation(MethodDeclaration node){
		return new ASTMethod(node);
	}

	@Override
	public Tree getTree() {
		return root;
	}
}
