package jp.naist.sd.kenja.factextractor.ast;

import jp.naist.sd.kenja.factextractor.Blob;
import jp.naist.sd.kenja.factextractor.Blobable;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.lang.reflect.Modifier;

/**
 * A class which represents Field of Java for Historage.
 * 
 * @author Kenji Fujiwara
 *
 */
public class ASTField implements Blobable {

  /**
   * map of field name and blobs.
   */
  private Multimap<String, Blob> fieldMap;

  /**
   * Default constructor of ASTField.
   */
  public ASTField() {
    fieldMap = HashMultimap.create();
  }

  /**
   * Factory Method for ASTField.
   * 
   * @param node
   *          FiledDeclaration node of Eclipse AST
   */
  public void parseFieldDeclaration(FieldDeclaration node) {
    for (Object obj : node.fragments()) {
      VariableDeclarationFragment fragment = (VariableDeclarationFragment) obj;
      String fieldName = fragment.getName().toString();

      int fieldModifier = node.getModifiers();
      String fieldVisibility = getVisibility(fieldModifier);
      Blob fieldBlob = new Blob(fieldVisibility + "\n", fieldName);

      if (fieldMap.containsKey(fieldName)) {
        int numConflicted = 0;
        for (Blob field : fieldMap.get(fieldName)) {
          conflict(field, fieldName, numConflicted++);
        }
        conflict(fieldBlob, fieldName, numConflicted);
      }
      fieldMap.put(fieldName, fieldBlob);
    }
  }

  /**
   * Method for conflicting of field name.
   * 
   * @param blob
   *          Blob of the new conflicted field
   * @param fieldName
   *          name of conflicted field
   * @param numConflicted
   *          number of conflicted declarations
   */
  private void conflict(Blob blob, String fieldName, int numConflicted) {
    String newName = fieldName + ".conflicted" + numConflicted;
    blob.setName(newName);
  }

  /**
   * Return visibility of a field.
   * 
   * @param modifier
   *          modifier number which is given from FieldDeclaration
   * @return string of field visibility
   */
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
