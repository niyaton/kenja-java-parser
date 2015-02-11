package jp.naist.sd.kenja.factextractor;

import java.io.File;
import java.io.IOException;

import jp.naist.sd.kenja.factextractor.ast.ASTCompilation;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;

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

  private void parseSourcecodeAndWriteSyntaxTree(char[] src, String outputPath) {
    File outputFile = new File(outputPath);
    parseSourcecodeAndWriteSyntaxTree(src, outputFile);
  }

  private void parseSourcecodeAndWriteSyntaxTree(char[] src, File outputFile) {
    parseSourcecode(src);
    writeASTAsFileTree(outputFile);
  }

  private void parseBlobs(String repositoryPath, String syntaxTreeDirPath) {
    File repoDir = new File(repositoryPath);
    try {
      Repository repo = new FileRepository(repoDir);

      for (String line : IOUtils.readLines(System.in)) {
        line = StringUtils.strip(line);

        ObjectId obj = ObjectId.fromString(line);
        ObjectLoader loader = repo.open(obj);

        char[] src = IOUtils.toCharArray(loader.openStream());
        File outputFile = new File(syntaxTreeDirPath, line);
        parseSourcecodeAndWriteSyntaxTree(src, outputFile);

      }
    } catch (IOException e) {
      e.printStackTrace();
    }

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
    if (args.length > 2) {
      System.out.println("Usage(1): path_of_output_file");
      System.out.println("Usage(2); path_of_git_repository path_of_syntax_trees_dir");
      return;
    }

    GitTreeCreator creator = new GitTreeCreator();

    if (args.length == 1) {
      try {
        char[] src = IOUtils.toCharArray(System.in);
        creator.parseSourcecodeAndWriteSyntaxTree(src, args[0]);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      creator.parseBlobs(args[0], args[1]);
    }
  }
}
