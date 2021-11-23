package models;

import java.util.ArrayList;

import graphs.CallGraph;
import loggers.ConsoleLogger;
import loggers.FileLogger;
import loggers.LogRequest;
import loggers.StandardLogRequestLevel;
import metric.GenerateClassesAndContent;
import processors.ASTProcessor;

public class ClassCouples extends ASTProcessor {
	private CallGraph callGraph;
	GenerateClassesAndContent generateClassesAndContent;
	static ArrayList<ClassAndContent> classes = new ArrayList<ClassAndContent>();
	static ArrayList<ClassCouple> couples = new ArrayList<ClassCouple>();
	double totalNumberOfCallsBetweenClasses;
	private FileLogger loggerChain;
	
	public ClassCouples(String projectPath, CallGraph callGraph) {
		super(projectPath);
		this.callGraph = callGraph;
		this.generateClassesAndContent = new GenerateClassesAndContent(callGraph);
		this.classes = generateClassesAndContent.getClasses();
		this.couples = this.getCouples();
		if(this.couples.size()>0)
			totalNumberOfCallsBetweenClasses = couples.get(0).getTotalNumberOfCallsBetweenClasses();
		System.out.println("ProcessorClustering Conctructor: Au total il y a "+ totalNumberOfCallsBetweenClasses + " invocations de classes \n");
		setLoggerChain();
	}
	private static ArrayList<ClassCouple> getCouples() {
		ArrayList<ClassCouple> couplesList= new ArrayList<ClassCouple>();
		for(int i=0 ; i<classes.size(); i++) {
			for(int j = i+1; j<classes.size(); j++) {
				couplesList.add(new ClassCouple(classes.get(i),classes.get(j)));
			}
		}
		return  couplesList;
	}
	
	public Integer getNumberOfClasses() {return classes.size();}
	
	public ArrayList<String> getClasses() {
		ArrayList<String> output= new ArrayList<String>();
		
		for(ClassAndContent classAndContent : classes) {
			output.add(classAndContent.getName());
		}
		return output;
	}
	
	public double getValueInCoupleFromClassNames(String name1, String name2) {
		for(ClassCouple classCouple : couples) {
			if(classCouple.isSameCouple(name1, name2)) {
				return classCouple.getValue();
			}
		}
		return -1;
	}
	
	/* METHODS */
	protected void setLoggerChain() {
		loggerChain = new FileLogger(StandardLogRequestLevel.DEBUG);
		loggerChain.setNextLogger(new ConsoleLogger(StandardLogRequestLevel.INFO));
	}
	public void log() {
		loggerChain.setFilePath(parser.getProjectPath()+"processor_clustering.info");
		loggerChain.log(new LogRequest(this.toString(), 
				StandardLogRequestLevel.DEBUG));
	}

}
