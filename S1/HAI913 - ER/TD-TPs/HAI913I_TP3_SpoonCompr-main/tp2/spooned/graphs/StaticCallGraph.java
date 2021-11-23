package graphs;
import java.io.IOException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import utility.Utility;
import visitors.ClassDeclarationsCollector;
import visitors.MethodDeclarationsCollector;
import visitors.MethodInvocationsCollector;
public class StaticCallGraph extends CallGraph {
    /* CONSTRUCTORS */
    public StaticCallGraph(String projectPath) {
        super(projectPath);
    }

    /* METHODS */
    public static StaticCallGraph createCallGraph(String projectPath, CompilationUnit cUnit) {
        StaticCallGraph graph = new StaticCallGraph(projectPath);
        ClassDeclarationsCollector classCollector = new ClassDeclarationsCollector();
        cUnit.accept(classCollector);
        for (TypeDeclaration cls : classCollector.getClasses()) {
            graph.addClass(cls);
            MethodDeclarationsCollector methodCollector = new MethodDeclarationsCollector();
            cls.accept(methodCollector);
            for (MethodDeclaration method : methodCollector.getMethods())
                graph.addInvocations(cls, method);

        }
        return graph;
    }

    public static StaticCallGraph createCallGraph(String projectPath) throws IOException {
        StaticCallGraph graph = new StaticCallGraph(projectPath);
        for (CompilationUnit cUnit : graph.parser.parseProject()) {
            StaticCallGraph partial = StaticCallGraph.createCallGraph(projectPath, cUnit);
            graph.addClasses(partial.getClasses());
            graph.addNodes(partial.getNodes());
            graph.addInvocations(partial.getInvocations());
            graph.addMethodDeclarationsMappings(partial.getMethodDeclarationsMap());
        }
        return graph;
    }

    private boolean addInvocations(TypeDeclaration cls, MethodDeclaration methodDeclaration) {
        if (methodDeclaration.getBody() != null) {
            MethodInvocationsCollector invocationCollector = new MethodInvocationsCollector();
            this.addNode(Utility.getMethodFullyQualifiedName(methodDeclaration));
            this.addInvocations(cls, methodDeclaration, invocationCollector);
            this.addSuperInvocations(methodDeclaration, invocationCollector);
        }
        return methodDeclaration.getBody() != null;
    }

    private void addInvocations(TypeDeclaration cls, MethodDeclaration method, MethodInvocationsCollector invocationCollector) {
        method.accept(invocationCollector);
        for (MethodInvocation invocation : invocationCollector.getMethodInvocations())
            this.addInvocation(method, invocation);

    }

    private void addSuperInvocations(MethodDeclaration method, MethodInvocationsCollector invocationCollector) {
        for (SuperMethodInvocation superInvocation : invocationCollector.getSuperMethodInvocations())
            this.addInvocation(method, superInvocation);

    }
}