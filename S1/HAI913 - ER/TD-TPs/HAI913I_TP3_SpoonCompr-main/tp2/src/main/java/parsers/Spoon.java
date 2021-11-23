package parsers;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processors.ASTProcessor;
import java.util.regex. * ;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import processors.Processor;
import loggers.ConsoleLogger;
import loggers.FileLogger;
import loggers.LogRequest;
import loggers.StandardLogRequestLevel;
import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.compiler.Environment;
import spoon.processing.AbstractProcessor;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtStatementListImpl;
import utility.Utility;
;

public class Spoon {
	
	private FileLogger loggerChain;
	
	ArrayList<CtMethod> methodList = new ArrayList<CtMethod>();
	ArrayList<CtType> typeList = new ArrayList<CtType>();
	ArrayList<CtInvocation> invocationList = new ArrayList<CtInvocation>();
	
	
	 Map<String, Map<String, Double>> CouplingGraph  = new HashMap<>();
	
	
	
	CtModel model;
	Launcher ourLauncher;
	int nbMethod = 0;
	int nbClass = 0;
	long nbAppels = 0;
	String path;
	
	public Spoon(String path, CtModel model,Launcher ourLaucher) {
		this.model = model;
		this.path = path;
		setLoggerChain();
	}
	
	protected void setLoggerChain() {
		loggerChain = new FileLogger(StandardLogRequestLevel.DEBUG);
		loggerChain.setNextLogger(new ConsoleLogger(StandardLogRequestLevel.INFO));
	}

	
	
	public Map<String, Map<String, Double>> getCouplingGraph() {
		return this.CouplingGraph;
	}
	
	
	

// GET UTILITY DATA FOR SPOON
public void getDataWithSpoon(CtModel model,Launcher ourLauncher) {
	

	long result = 0;
	
	for (CtType<?> type : model.getAllTypes()) { 
		if(type.isClass()) {
			nbClass++;
			typeList.add(type);

		}
		for (CtMethod<?> method : type.getAllMethods()) { 
			if(!methodList.contains(method)) {
				methodList.add(method);
				nbMethod += 1;
			}
			
			

			for (CtInvocation<?> methodInvocation : Query.getElements(method,
					new TypeFilter<CtInvocation<?>>(CtInvocation.class))) { 
				if (methodInvocation.getTarget().getType() != null) {
					
					if ((!methodInvocation.getTarget().getType().getTypeDeclaration().getQualifiedName().equals("void"))
							&& (!methodInvocation.getTarget().getType().getTypeDeclaration().getQualifiedName().contains("java.io."))
							&&  (!methodInvocation.getTarget().getType().getTypeDeclaration().getQualifiedName().contains("java.util."))
							 && (!methodInvocation.getTarget().getType().getTypeDeclaration().getQualifiedName().contains("java.lang"))
							) {
						        	
						         
						invocationList.add(methodInvocation);
						result++;
					}
				}
			}
			
		}
	
	}
	
	nbAppels = result;
}

//END GET UTILITY DATA FOR SPOON


public long getNbRelations(String classNameA, String classNameB) {
	long cpt = 0;
	for (CtType<?> type : model.getAllTypes()) { 
		if (classNameB.equals(type.getQualifiedName())) { 
			for (CtMethod<?> method : type.getAllMethods()) { 
				for(CtInvocation<?> methodInvocation : Query.getElements(method,
						new TypeFilter<CtInvocation<?>>(CtInvocation.class))) {
					if (methodInvocation.getTarget().getType() != null) {
					if (classNameA.equals(
							methodInvocation.getTarget().getType().getTypeDeclaration().getQualifiedName())) {
						cpt++;
						}
					}
				}
			}
		}
	}
	return cpt;
}

public double getCouplingMetric(String classNameA, String classNameB) {
	double nbRelations = this.getNbRelations(classNameA, classNameB);
	if(nbRelations != 0) {
	System.out.println("Nombre d'appels de : " + classNameA + "  --> (vers) " + classNameB + " = " + nbRelations);
	}
	double result = (nbRelations) / (nbAppels);
	return result;
}


public void createCouplingGraph() throws IOException {
	String classNameA, classNameB;
	Double couplingValue;
	Double metricVerif = 0.0;
	for (CtType<?> typeClassA : model.getAllTypes()) { 
		classNameA = typeClassA.getQualifiedName();
		for (CtType<?> typeClassB : model.getAllTypes()) {
			classNameB = typeClassB.getQualifiedName();
			if(!classNameA.equals(classNameB)) {
				couplingValue = getCouplingMetric(classNameA, classNameB);
				metricVerif += couplingValue;
				if (couplingValue > 0) {
					if (!CouplingGraph.containsKey(classNameA)) {
						CouplingGraph.put(classNameA, new HashMap<String, Double>());
					}
					CouplingGraph.get(classNameA).put(classNameB, couplingValue);
				}
			}
	
		}

	}
	System.out.println("Métrique total (sans appel java.io, java.lang, java.util) : " + metricVerif);
	//saveGraphAsPNGSpoon(CouplingGraph);
}


// CALL_GRAPH WITH SPOON 
public String toString() {
	StringBuilder builder = new StringBuilder();
	ArrayList<String> listVu = new ArrayList<String>();
	
	String invocName = "";
	
	builder.append("Static Call Graph");
	builder.append("\nMethods: "+nbMethod+".");
    builder.append("\nInvocations: "+nbAppels+".");
    builder.append("\nClass: "+nbClass+".");
    builder.append("\n");
    
    for (CtType<?> type : model.getAllTypes()) { 
		if(type.isClass()) {
			nbClass++;
			typeList.add(type);
			
		}
		
		System.out.println(type.getQualifiedName());
		builder.append(type.getQualifiedName() + ":" + "\n");
		for (CtMethod<?> method : type.getAllMethods()) { 
			if(!methodList.contains(method)) {
				methodList.add(method);
				nbMethod += 1;
			}
			
			
			int nbE = 0;
			for (CtInvocation<?> methodInvocation : Query.getElements(method,
					new TypeFilter<CtInvocation<?>>(CtInvocation.class))) { 
				
				 if(listVu.contains(method.getSignature())) {
    	        	 nbE +=1;
    	         }
    	         else {
    	        	 listVu.add(method.getSignature());
    	        	 nbE = 1;
    	         }
				if (methodInvocation.getTarget().getType() != null) {
					
					if ((!methodInvocation.getTarget().getType().getTypeDeclaration().getQualifiedName().equals("void"))
							&& (!methodInvocation.getTarget().getType().getTypeDeclaration().getQualifiedName().contains("java.io."))
							&&  (!methodInvocation.getTarget().getType().getTypeDeclaration().getQualifiedName().contains("java.util."))
							&& (!methodInvocation.getTarget().getType().getTypeDeclaration().getQualifiedName().contains("java.lang"))
							) {
						
						
						        	
						invocName = methodInvocation.getTarget().getType().getTypeDeclaration().getQualifiedName();
						invocationList.add(methodInvocation);
					}
					
				}
				
				builder.append("\t---> " + invocName  + "::" + method.getSignature() + " " +  "[" + nbE + " time(s)" + "]" + "\n");
	 	     	builder.append("\n");
			}
			

			
		}
    }
    System.out.println(builder.toString());
	
    return builder.toString();
}

// SAVE AS PNG AND DOT GENERATION
public  String getCoupleGraphAsDotSpoon(Map<String, Map<String, Double>> couple) {
	StringBuilder res = new StringBuilder("digraph G {\n");
	String coupling = "";
	for (String classNameA : couple.keySet()) {
		for (String classNameB : couple.get(classNameA).keySet()) {
			coupling = couple.get(classNameA).get(classNameB) + " ";
			res.append('"').append(classNameA).append('"').append(" -> ").append('"').append(classNameB).append('"')
					.append(" [ label = \"").append(coupling).append("\"] ");
		}
	}
	res.append("\n}");
	return res.toString();
}


public void saveGraphAsPNGSpoon(Map<String, Map<String, Double>> couple) throws IOException{
	MutableGraph g = new Parser().read(this.getCoupleGraphAsDotSpoon(couple));
	Graphviz.fromGraph(g).height(1920).render(Format.PNG).toFile(new File("grapheCouplageSpoon.png"));

}


//END SAVE AS PNG AND DOT GENERATION
public void log() {
	loggerChain.setFilePath(path + "static-callgraphSpoon.info");
	loggerChain.log(new LogRequest(this.toString(), 
			StandardLogRequestLevel.DEBUG));
}


//m�thode d'ajout d'�l�ment sensors
	public void addSensorsStatement(CtMethod<?> method,Launcher ourLauncher) {
		
		CtCodeSnippetStatement snippet = ourLauncher.getFactory().Core().createCodeSnippetStatement();
		
		 String value = "";
		 value = String.format("if (%s == null) "
				+ "throw new IllegalArgumentException(\"[Spoon inserted check] null passed as parameter\");",
				method.getSimpleName());
		
		 snippet.setValue(value);

		// we insert the snippet at the beginning of the method body.
		if (method.getBody() != null) {
			method.getBody().insertBegin(snippet);
			System.out.println("M�thode : " + method.getSimpleName() + "\n Corps de la m�thode : " + method.getBody());
		}
}
	

	
	// ADD UTILITY DATA FOR SPOON (BONUS)
	public void addDataWithSpoon(CtModel model,Launcher ourLauncher) {
		

		long result = 0;
		
		for (CtType<?> type : model.getAllTypes()) { 
			if(type.isClass()) {
				nbClass++;
				typeList.add(type);

			}
			for (CtMethod<?> method : type.getAllMethods()) { 
				if(!methodList.contains(method)) {
					methodList.add(method);
					nbMethod += 1;
				}
				
				addSensorsStatement(method,ourLauncher);
				

				for (CtInvocation<?> methodInvocation : Query.getElements(method,
						new TypeFilter<CtInvocation<?>>(CtInvocation.class))) { 
					if (methodInvocation.getTarget().getType() != null) {
						
						if ((!methodInvocation.getTarget().getType().getTypeDeclaration().getQualifiedName().equals("void"))
								&& (!methodInvocation.getTarget().getType().getTypeDeclaration().getQualifiedName().contains("java.io."))
								&&  (!methodInvocation.getTarget().getType().getTypeDeclaration().getQualifiedName().contains("java.util."))
								 && (!methodInvocation.getTarget().getType().getTypeDeclaration().getQualifiedName().contains("java.lang"))
								) {
							        	
							         
							invocationList.add(methodInvocation);
							result++;
						}
					}
				}
				
			}
		
		}
		
		nbAppels = result;
	}


}