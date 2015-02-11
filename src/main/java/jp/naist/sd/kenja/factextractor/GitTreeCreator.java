package jp.naist.sd.kenja.factextractor;

import java.io.File;
import java.io.IOException;

import jp.naist.sd.kenja.factextractor.ast.ASTCompilation;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class GitTreeCreator {
  private Tree root = new Tree("");

  private ASTCompilation compilation;

  public GitTreeCreator() {
  }

  private void parseSourcecode(char[] src) {
    ASTParser parser = ASTParser.newParser(AST.JLS4);

    parser.setSource(src);

    NullProgressMonitor nullMonitor = new NullProgressMonitor();
    CompilationUnit unit = (CompilationUnit) parser.createAST(nullMonitor);

    compilation = new ASTCompilation(unit, root);
  }

  public void writeASTAsFileTree(File outputFile) {
    try {
      TreeWriter writer = new TextFormatTreeWriter(outputFile);
      writer.writeTree(compilation.getTree());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("please input path of output file.");
      return;
    }

    File outputFile = new File(args[0]);
    GitTreeCreator creator = new GitTreeCreator();

    try {
      creator.parseSourcecode(IOUtils.toCharArray(System.in));
      creator.writeASTAsFileTree(outputFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
