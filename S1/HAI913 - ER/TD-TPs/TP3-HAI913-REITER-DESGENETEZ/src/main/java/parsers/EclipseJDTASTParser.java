package parsers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class EclipseJDTASTParser extends Parser<ASTParser> {

    /* CONSTRUCTOR */
    public EclipseJDTASTParser(String projectPath) {
        super(projectPath);
    }

    /* METHODS */
    public void setParser(int apiLevel, int kind, String complianceLevel, boolean resolveBindings,
                          boolean bindingsRecovery, String encoding) {
        parser = ASTParser.newParser(apiLevel);
        parser.setKind(kind);
        Hashtable<String, String> javaCoreOptions = JavaCore.getOptions();
        JavaCore.setComplianceOptions(complianceLevel, javaCoreOptions);
        parser.setCompilerOptions(javaCoreOptions);
        parser.setEnvironment(new String[]{getProjectBinPath()},
                new String[]{getProjectSrcPath()},
                new String[]{encoding}, true);
        parser.setResolveBindings(resolveBindings);
        parser.setBindingsRecovery(bindingsRecovery);
    }

    public CompilationUnit parse(File sourceFile) throws IOException {
        configure();
        Charset platformCharset = null;
        parser.setSource(FileUtils.readFileToString(sourceFile, platformCharset).toCharArray());
        parser.setUnitName(sourceFile.getAbsolutePath());

        return (CompilationUnit) parser.createAST(null);
    }

    public List<CompilationUnit> parseProject() throws IOException {

        List<CompilationUnit> cUnits = new ArrayList<>();

        for (File sourceFile : listJavaProjectFiles())
            cUnits.add(parse(sourceFile));

        return cUnits;
    }

    @Override
    public void configure() {
        setParser(AST.JLS11, ASTParser.K_COMPILATION_UNIT,
                JavaCore.VERSION_1_8, true, false, "UTF-8");
    }
}
