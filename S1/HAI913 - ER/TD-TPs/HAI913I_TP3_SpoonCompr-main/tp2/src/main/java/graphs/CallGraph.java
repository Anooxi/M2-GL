package graphs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.LongStream;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import loggers.ConsoleLogger;
import loggers.FileLogger;
import loggers.LogRequest;
import loggers.StandardLogRequestLevel;
import processors.ASTProcessor;
import utility.Utility;

public class CallGraph extends ASTProcessor {
	
	/* ATTRIBUTES */
	private Set<TypeDeclaration> classes = new HashSet<>();
	private Set<String> nodes = new HashSet<String>();
	private Map<String, Map<String, Integer>> invocations = new TreeMap<>();
	private Map<MethodDeclaration, Set<Expression>> methodDeclarationsMap = new HashMap<>();
	private FileLogger loggerChain;

	/* CONSTRUCTORS */
	public CallGraph(String projectPath) {
		super(projectPath);
		setLoggerChain();
	}
	
	/* METHODS */
	protected void setLoggerChain() {
		loggerChain = new FileLogger(StandardLogRequestLevel.DEBUG);
		loggerChain.setNextLogger(new ConsoleLogger(StandardLogRequestLevel.INFO));
	}
	
	public Set<TypeDeclaration> getClasses() {
		return classes;
	}
	
	public Set<String> getNodes() {
		return nodes;
	}
	
	public Map<String, Map<String, Integer>> getInvocations() {
		return invocations;
	}
	
	public Map<MethodDeclaration, Set<Expression>> getMethodDeclarationsMap() {
		return methodDeclarationsMap;
	}
	
	public Set<MethodDeclaration> getMethodDeclarations() {
		return methodDeclarationsMap.keySet();
	}
	
	public long getNbClasses() {
		return classes.size();
	}
	
	public long getNbNodes() {
		return nodes.size();
	}
	
	public long getNbInvocations() {
		return invocations.keySet()
		.stream()
		.map(source -> invocations.get(source))
		.map(destination -> destination.values())
		.flatMap(Collection::stream)
		.flatMapToLong(value -> LongStream.of((long) value))
		.sum();
	}
	
	public boolean addClass(TypeDeclaration cls) {
		return classes.add(cls);
	}
	
	public boolean addClasses(Set<TypeDeclaration> classes) {
		return this.classes.addAll(classes);
	}

	public boolean addNode(String node) {
		return nodes.add(node);
	}

	public boolean addNodes(Set<String> nodes) {
		return this.nodes.addAll(nodes);
	}
	
	public void addInvocation(MethodDeclaration source, Expression destination) {
		String method = Utility.getMethodFullyQualifiedName(source);
		String invokedMethod = getInvokedMethodSignature(destination);

		if (!isBusinessMethod(invokedMethod))
			return;

		if (invocations.containsKey(method)) {
			if (invocations.get(method).containsKey(invokedMethod)) {
				int numberOfArrows = invocations.get(method).get(invokedMethod);
				invocations.get(method).put(invokedMethod, numberOfArrows + 1);
			}

			else {
				methodDeclarationsMap.get(source).add(destination);
				invocations.get(method).put(invokedMethod, 1);
			}
		}

		else {
			methodDeclarationsMap.put(source, new HashSet<Expression>());
			methodDeclarationsMap.get(source).add(destination);
			invocations.put(method, new HashMap<String, Integer>());
			invocations.get(method).put(invokedMethod, 1);
		}
	}
	
	public void addInvocation(String source, String destination, 
			int occurrences) {
		
		if (!invocations.containsKey(source))
			invocations.put(source, new HashMap<String, Integer>());
		
		invocations.get(source).put(destination, occurrences);
	}
	
	public void addInvocations(Map<String, Map<String, Integer>> map) {
		for (String source : map.keySet())
			for (String destination : map.get(source).keySet())
				this.addInvocation(source, destination, map.get(source).get(destination));
	}
	
	public void addMethodDeclarationsMappings(Map<MethodDeclaration, Set<Expression>> map) {
		for (MethodDeclaration methodDeclaration: map.keySet()) {
			if (!methodDeclarationsMap.containsKey(methodDeclaration))
				methodDeclarationsMap.put(methodDeclaration, new HashSet<Expression>());
			
			for (Expression invocation: map.get(methodDeclaration))
				methodDeclarationsMap.get(methodDeclaration).add(invocation);
		}
	}
	
	public boolean isBusinessMethod(String invokedMethodSignature) {
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
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Static Call Graph");
		builder.append("\nClasses: "+classes.size()+".");
		builder.append("\nMethods: "+nodes.size()+".");
		builder.append("\nInvocations: "+getNbInvocations()+".");
		builder.append("\n");
		
		for (String method: invocations.keySet()) {
			builder.append(method + ":\n");
			
			for (String invokedMethod: invocations.get(method).keySet()) {
				builder.append("\t---> " 
						+ invokedMethod
						+ " [" + invocations.get(method).get(invokedMethod) 
						+ " time(s)]\n");
			}
			builder.append("\n");
		}
		
		return builder.toString();
	}
	
	public void print() {
		loggerChain.log(new LogRequest(this.toString(), 
				StandardLogRequestLevel.INFO));
	}
	
	public void log() {
		loggerChain.setFilePath(parser.getProjectSrcPath()+"static-callgraph.info");
		loggerChain.log(new LogRequest(this.toString(), 
				StandardLogRequestLevel.DEBUG));
	}
	
	public ArrayList<String> getClassesName(){
		ArrayList<String> classesName = new ArrayList<String>();
		for(String s : this.getNodes()) {
			String[] words =s.split("::");
			classesName.add(words[0]);
		}
		//suppression des tuples
		Set<String> set = new HashSet<>(classesName);
		classesName.clear();
		classesName.addAll(set);
		return classesName;

	}
	
}
