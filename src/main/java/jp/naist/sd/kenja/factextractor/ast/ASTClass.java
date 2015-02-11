package jp.naist.sd.kenja.factextractor.ast;

//import java.util.HashSet;

import jp.naist.sd.kenja.factextractor.Blob;
import jp.naist.sd.kenja.factextractor.Tree;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
//import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * A class which represents Class of Java for Historage.
 * 
 * @author Kenji Fujiwara
 */
public class ASTClass extends ASTType {

  /**
   * Name of root directory which store fields.
   */
  private static final String FIELD_ROOT_NAME = "[FE]";

  /**
   * Name of root directory which store constructors.
   */
  private static final String CONSTURCTOR_ROOT_NAME = "[CS]";

  /**
   * Name of root directory which store inner classes.
   */
  private static final String INNER_CLASS_ROOT_NAME = "[CN]";

  /**
   * root Tree of fields.
   */
  private Tree fieldRoot = new Tree(FIELD_ROOT_NAME);

  /**
   * root Tree of constructors.
   */
  private Tree constructorRoot = new Tree(CONSTURCTOR_ROOT_NAME);

  /**
   * root Tree of inner classes.
   */
  private Tree innerClassRoot = new Tree(INNER_CLASS_ROOT_NAME);

  /**
   * Blob which represents super class of the class.
   */
  private Blob superClass = null;

  /**
   * Construct ASTClass from Eclipse AST TypeDeclaration class.
   * 
   * @param typeDec
   *          TypeDeclaration class of Eclipse AST.
   */
  protected ASTClass(TypeDeclaration typeDec) {
    super(typeDec.getName().toString());

    if (typeDec.getSuperclassType() != null) {
      superClass = new Blob("extend");
      superClass.setBody(typeDec.getSuperclassType().toString() + "\n");
      root.append(superClass);
    }

    root.append(fieldRoot);
    root.append(constructorRoot);

    Multimap<String, ASTMethod> methodMap = HashMultimap.create();
    for (MethodDeclaration methodDec : typeDec.getMethods()) {
      ASTMethod method = ASTMethod.fromMethodDeclaralation(methodDec);

      if (method.isConstructor()) {
        constructorRoot.append(method.getTree());
      } else {
        methodRoot.append(method.getTree());
        if (methodMap.containsKey(method.getName())) {
          int numConflicted = 0;
          for (ASTMethod astMethod : methodMap.get(method.getName())) {
            astMethod.conflict(numConflicted++);
          }
          method.conflict(numConflicted);
        }
        methodMap.put(method.getName(), method);
      }
    }

    ASTField astField = new ASTField();
    for (FieldDeclaration fieldDec : typeDec.getFields()) {
      astField.parseFieldDeclaration(fieldDec);
    }
    fieldRoot.addAll(astField.getBlobs());

    if (typeDec.getTypes().length > 0) {
      root.append(innerClassRoot);
      for (TypeDeclaration innerTypeDec : typeDec.getTypes()) {
        ASTClass innnerClass = ASTClass.fromTypeDeclaration(innerTypeDec);
        innerClassRoot.append(innnerClass.getTree());
      }
    }
  }

  /**
   * Factory Method of ASTClass.
   * 
   * @param node
   *          A TypeDeclaration of the class.
   * @return ASTClass which is corresponding to node.
   */
  public static ASTClass fromTypeDeclaration(TypeDeclaration node) {
    return new ASTClass(node);
  }

}
