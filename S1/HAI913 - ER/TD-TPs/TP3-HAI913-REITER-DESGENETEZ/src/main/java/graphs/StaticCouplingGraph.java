package graphs;

import org.eclipse.jdt.core.dom.*;
import utility.Utility;
import visitors.ClassDeclarationsCollector;
import visitors.MethodDeclarationsCollector;
import visitors.MethodInvocationsCollector;

import java.io.IOException;

public class StaticCouplingGraph extends CouplingGraph {

    /* CONSTRUCTORS */
    public StaticCouplingGraph(String projectPath) throws IOException {
        super(projectPath);
    }

    /* METHODS */
    public static StaticCouplingGraph createCouplingGraph(String projectPath, CompilationUnit cUnit) throws IOException {
        StaticCouplingGraph graph = new StaticCouplingGraph(projectPath);
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

    public static StaticCouplingGraph createCouplingGraph(String projectPath) throws IOException {
        StaticCouplingGraph graph = new StaticCouplingGraph(projectPath);

        for (CompilationUnit unit : graph.parser.parseProject()) {
            StaticCouplingGraph partial = createCouplingGraph(projectPath, unit);
            graph.addClasses(partial.getClasses());
            graph.addInvocations(partial.getCalls());
        }
        return graph;
    }

    private void addInvocations(TypeDeclaration classe, MethodDeclaration methodDeclaration) {
        if (methodDeclaration.getBody() != null) {
            MethodInvocationsCollector invocationCollector = new MethodInvocationsCollector();
            this.addInvocations(classe, methodDeclaration, invocationCollector);
            this.addSuperInvocations(classe, methodDeclaration, invocationCollector);
        }

        methodDeclaration.getBody();
    }

    private void addInvocations(TypeDeclaration classe, MethodDeclaration method,
                                MethodInvocationsCollector invocationCollector) {
        method.accept(invocationCollector);
        for (MethodInvocation invocation : invocationCollector.getMethodInvocations())
            this.addInvocation(classe, invocation);
    }

    private void addSuperInvocations(TypeDeclaration classe, MethodDeclaration method, MethodInvocationsCollector invocationCollector) {
        for (SuperMethodInvocation superInvocation : invocationCollector.getSuperMethodInvocations())
            this.addInvocation(classe, superInvocation);
    }

    public void addClass(TypeDeclaration declaration) {
        String className = Utility.getClassFullyQualifiedName(declaration);
        addClass(className);
    }

}
