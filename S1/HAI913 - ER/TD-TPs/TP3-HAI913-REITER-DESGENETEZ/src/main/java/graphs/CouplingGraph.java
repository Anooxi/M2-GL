package graphs;

import loggers.ConsoleLogger;
import loggers.FileLogger;
import loggers.LogRequest;
import loggers.StandardLogRequestLevel;
import org.eclipse.jdt.core.dom.*;
import processors.ASTProcessor;
import utility.Utility;

import java.io.File;
import java.util.*;
import java.util.stream.LongStream;

public class CouplingGraph extends ASTProcessor {

    /* ATTRIBUTES */
    private FileLogger loggerChain;
    private Set<String> classes = new TreeSet<>();
    //Map<Nom de la classe, Map<Nom de la classe, Nombre d'appels> classCalls
    private Map<String, Map<String, Integer>> calls = new TreeMap<>();

    /* CONSTRUCTORS */
    public CouplingGraph(String projectPath) {
        super(projectPath);
        setLoggerChain();
    }

    /* METHODS */
    /* Logs */
    protected void setLoggerChain() {
        loggerChain = new FileLogger(StandardLogRequestLevel.DEBUG);
        loggerChain.setNextLogger(new ConsoleLogger(StandardLogRequestLevel.INFO));
    }

    public void log() {
        loggerChain.setFilePath(parser.getProjectSrcPath() + "static-couplingGraph.info");
        loggerChain.log(new LogRequest(this.toString(),
                StandardLogRequestLevel.DEBUG));
    }

    public boolean addClass(String className) {
        return classes.add(className);
    }

    public void addInvocation(String classFrom, String classTo) {
        if (calls.containsKey(classFrom)) {
            if (calls.get(classFrom).containsKey(classTo)) {
                int callCount = calls.get(classFrom).get(classTo);
                calls.get(classFrom).put(classTo, callCount + 1);
            } else {
                calls.get(classFrom).put(classTo, 1);
            }
        } else {
            calls.put(classFrom, new TreeMap<>());
            calls.get(classFrom).put(classTo, 1);
        }
    }

    public void addInvocation(String source, String destination, int occurrences) {

        if (calls.containsKey(source)) {
            int oldCount;
            oldCount = calls.get(source).getOrDefault(destination, 0);
            calls.get(source).put(destination, oldCount + occurrences);
        } else {
            calls.put(source, new HashMap<>());
            calls.get(source).put(destination, occurrences);
        }

    }

    public void addInvocation(TypeDeclaration source, Expression destination) {
        String sourceClass = Utility.getClassFullyQualifiedName(source);
        String invokedMethod = getInvokedMethodSignature(destination);

        if (!isBusinessMethod(invokedMethod))
            return;

        String calledMethodClass = Utility.getAnyMethodInvocationDeclaringClass((MethodInvocation) destination);
        addInvocation(sourceClass, calledMethodClass);
    }

    private boolean isBusinessMethod(String invokedMethodSignature) {
        String declaringTypeFQN = invokedMethodSignature.split("::")[0];
        int indexOfTypeDotInFQN = declaringTypeFQN.lastIndexOf(".");
        String containingPackageFQN = declaringTypeFQN.substring(0, indexOfTypeDotInFQN);
        return new File(
                parser.getProjectSrcPath(),
                containingPackageFQN.replace(".", File.separator)
        ).exists();
    }

    private String getInvokedMethodSignature(Expression invocation) {
        if (invocation instanceof MethodInvocation)
            return Utility
                    .getMethodInvocationDefaultFormat(
                            (MethodInvocation) invocation);
        else
            return Utility
                    .getMethodSuperInvocationDefaultFormat(
                            (SuperMethodInvocation) invocation);
    }

    public void addClasses(Set<String> classes) {
        this.classes.addAll(classes);
    }

    public void addInvocations(Map<String, Map<String, Integer>> map) {
        for (String source : map.keySet())
            for (String destination : map.get(source).keySet())
                this.addInvocation(source, destination, map.get(source).get(destination));
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\nNumber of classes : ")
                .append(getClassCount())
                .append("\nTotal number of calls : ")
                .append(getNumberOfCalls())
                .append("\nClass calls :");
        for (String entry : calls.keySet()) {
            builder.append("\n\tClass : ")
                    .append(entry)
                    .append(" : ");
            for (String classCalled : calls.get(entry).keySet()) {
                builder.append("\n\t\t ---> Class : ")
                        .append(classCalled)
                        .append(" [")
                        .append(calls.get(entry).get(classCalled))
                        .append(" time(s)]");
            }
        }
        return builder.toString();
    }

    public Set<String> getClasses() {
        return classes;
    }

    public Map<String, Map<String, Integer>> getCalls() {
        return calls;
    }

    public long getNumberOfCalls() {
        return calls.keySet()
                .stream()
                .map(source -> calls.get(source))
                .map(Map::values)
                .flatMap(Collection::stream)
                .flatMapToLong(value -> LongStream.of((long) value))
                .sum();
    }

    public long getClassCount() {
        return classes.size();
    }


}
