package jp.naist.sd.kenja.factextractor.ast;

import java.util.HashSet;

import jp.naist.sd.kenja.factextractor.Blob;
import jp.naist.sd.kenja.factextractor.Tree;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class ASTClass extends ASTType {

	private final String FIELD_ROOT_NAME = "[FE]";
	private final String CONSTURCTOR_ROOT_NAME = "[CS]";

	private Tree fieldRoot = new Tree(FIELD_ROOT_NAME);
	private Tree constructorRoot = new Tree(CONSTURCTOR_ROOT_NAME);
	
	private Blob superClass = null;

	protected ASTClass(TypeDeclaration typeDec) {
		super(typeDec.getName().toString());
		
		//System.out.println(typeDec.getSuperclass());
		if(typeDec.getSuperclassType() != null){
			//System.out.println(typeDec.getSuperclassType().toString());
			superClass = new Blob("extend");
			superClass.setBody(typeDec.getSuperclassType().toString() + "\n");
			root.append(superClass);
		}
		
		root.append(fieldRoot);
		root.append(constructorRoot);

		//HashSet<String> tmpHashSet = new HashSet<String>();
		Multimap<String, ASTMethod> methodMap = HashMultimap.create();
		for (MethodDeclaration methodDec : typeDec.getMethods()) {
			//if(tmpHashSet.contains(methodDec.getName().toString())){
			//	System.out.println(methodDec.getName());
			//	continue;
			//}
			//tmpHashSet.add(methodDec.getName().toString());
			ASTMethod method = ASTMethod.fromMethodDeclaralation(methodDec);

			if(method.isConstructor()){
				constructorRoot.append(method.getTree());
			}
			else{
				methodRoot.append(method.getTree());
				if (methodMap.containsKey(method.getName())) {
					int i = 0;
					for (ASTMethod astMethod : methodMap.get(method.getName())) {
						astMethod.conflict(i++);
					}
					method.conflict(i);
				}
				methodMap.put(method.getName(), method);
			}
			// TODO overload methods
		}

		for (FieldDeclaration fieldDec : typeDec.getFields()) {
			
			ASTField field = ASTField.fromFieldDeclaration(fieldDec);
			for(Blob blob:field.getBlobs()){
				String blob_name = blob.getName().toString();
				if(fieldRoot.hasBlob(blob_name)) {
					blob.setName(blob_name+"_conflicted");					
				}
				
			}
			
			fieldRoot.addAll(field.getBlobs());
			
		}
	}

	public static ASTClass fromTypeDeclaration(TypeDeclaration node) {
		return new ASTClass(node);
	}

}
