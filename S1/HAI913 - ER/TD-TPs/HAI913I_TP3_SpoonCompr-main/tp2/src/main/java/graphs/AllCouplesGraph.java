package graphs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import metric.GenerateClassesAndContent;
import models.ClassAndContent;
import models.ClassCouple;
import processors.ASTProcessor;

public class AllCouplesGraph extends ASTProcessor {
	private Map<String, Map<String, Double>> couplings;
	private CallGraph callGraph;
	GenerateClassesAndContent generateClassesAndContent;
	static ArrayList<ClassAndContent> classes = new ArrayList<ClassAndContent>();
	static ArrayList<ClassCouple> couples = new ArrayList<ClassCouple>();

	
	public AllCouplesGraph(String projectPath, CallGraph callGraph) {
		super(projectPath);
		this.callGraph = callGraph;
		this.generateClassesAndContent = new GenerateClassesAndContent(callGraph);
		this.classes = generateClassesAndContent.getClasses();
		this.couples = this.getCouples();
		couplings = new HashMap<>();


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
	
	public void createFiles() throws IOException {
		this.saveGraph();
		this.saveGraphAsPNG();
	}
	public String getCoupleGraphAsDot(ClassCouple couple) {
		String className1 = '"'+couple.getClass1().getName()+'"';
		String className2 = '"'+couple.getClass2().getName()+'"';

		Double valueName1 = couple.getNbOfTimeClass1IsCalled()/couple.getTotalNumberOfCallsBetweenClasses();
		Double valueName2 = couple.getNbOfTimeClass2IsCalled()/couple.getTotalNumberOfCallsBetweenClasses();
		//Cas ou il n'y a aucune invocation
		if(valueName1.isNaN()) {
			valueName1=0.0;
			valueName2=0.0;
		}
		StringBuilder res = new StringBuilder();
		res.append(className1).append(" -> ").append(className2).append(" [ label = \"").append(valueName1).append("\"] ");
		res.append(className2).append(" -> ").append(className1).append(" [ label = \"").append(valueName2).append("\"] ");
		return res.toString();
	}

	public String getCouplesGraphAsDot() {
		StringBuilder builder = new StringBuilder("digraph G {\n");
		for(ClassCouple couple : couples) {
			builder.append(getCoupleGraphAsDot(couple));
		}
		builder.append("\n}");
		return builder.toString();
	}
	
	public void saveGraph() {
		try {
			FileWriter fw = new FileWriter("CouplesGraph.dot", false);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);
			out.println(this.getCouplesGraphAsDot());
			out.close();
			bw.close();
			fw.close();
		} catch (IOException e) {
			System.out.println("Exception ecriture fichier");
			e.printStackTrace();
		}}
		

	public void saveGraphAsPNG() throws IOException {
		MutableGraph g = new Parser().read(getCouplesGraphAsDot());
		Graphviz.fromGraph(g).render(Format.PNG).toFile(new File("graphCouples.png"));
	}
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
}