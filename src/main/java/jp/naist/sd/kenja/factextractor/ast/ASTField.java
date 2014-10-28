package jp.naist.sd.kenja.factextractor.ast;

import jp.naist.sd.kenja.factextractor.Blob;
import jp.naist.sd.kenja.factextractor.Blobable;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.lang.reflect.Modifier;

public class ASTField implements Blobable{

	private Multimap<String, Blob> fieldMap;

	public ASTField() {
		fieldMap = HashMultimap.create();
	}

	public void parseFieldDeclaration(FieldDeclaration node) {
		for(Object obj: node.fragments()){
			VariableDeclarationFragment fragment = (VariableDeclarationFragment)obj;
			String fieldName = fragment.getName().toString();

			int fieldModifier = node.getModifiers();
			String fieldVisibility = getVisibility(fieldModifier);
			Blob fieldBlob = new Blob(fieldVisibility + "\n", fieldName);

			if (fieldMap.containsKey(fieldName)) {
				int i = 0;
				for (Blob field : fieldMap.get(fieldName)) {
					conflict(field, fieldName, i++);
				}
				conflict(fieldBlob, fieldName, i);
			}
			fieldMap.put(fieldName, fieldBlob);
		}
	}
	
	private void conflict(Blob blob, String fieldName, int numConflicted) {
		String newName = fieldName + ".conflicted" + numConflicted;
		blob.setName(newName);
	}

	private String getVisibility(int modifier) {
		if (Modifier.isPrivate(modifier)) {
			return "[private]";
		} else if (Modifier.isProtected(modifier)) {
			return "[protected]";
		} else if (Modifier.isPublic(modifier)) {
			return "[public]";
		} else {
			return "[internal]";
		}
	}

	@Override
	public Iterable<Blob> getBlobs() {
		return fieldMap.values();
	}
}
