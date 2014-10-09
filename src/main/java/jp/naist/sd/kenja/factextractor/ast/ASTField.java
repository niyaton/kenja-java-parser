package jp.naist.sd.kenja.factextractor.ast;

import java.util.ArrayList;
import java.util.List;

import jp.naist.sd.kenja.factextractor.Blob;
import jp.naist.sd.kenja.factextractor.Blobable;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class ASTField implements Blobable{

	private List<Blob> blobs = new ArrayList<Blob>();

	public ASTField(){

	}

	public void parseFieldDeclaration(FieldDeclaration node) {
		for(Object obj: node.fragments()){
			VariableDeclarationFragment fragment = (VariableDeclarationFragment)obj;
			blobs.add(new Blob(fragment.getName().toString()));
		}
	}
	
	@Override
	public Iterable<Blob> getBlobs() {
		return blobs;
	}
}
