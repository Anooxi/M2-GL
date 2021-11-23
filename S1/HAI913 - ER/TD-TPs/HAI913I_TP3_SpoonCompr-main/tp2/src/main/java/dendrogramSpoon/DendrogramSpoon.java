package dendrogramSpoon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import graphs.CallGraph;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import models.ClassCoupleSpoon;
import parsers.Spoon;
import parsers.SpoonClustering;
import spoon.Launcher;
import spoon.reflect.CtModel;

public class DendrogramSpoon {
	ArrayList<ClassCoupleSpoon> classCouples;
	List<DendrogramCompositSpoon> nodes;
	SpoonClustering spoonClustering;
	//Spoon spoon;
	public DendrogramSpoon(String projectPath, CtModel model,Launcher our, Spoon analyze) {
		this.spoonClustering = new SpoonClustering(projectPath, model,our);
		this.classCouples = spoonClustering.createCouple(analyze);
		this.nodes = initNodes();
		System.out.println("longeur de l'arraylist de couples " + nodes.size());
	}
	
	private DendrogramCompositSpoon getMostCoupledNodePair(List<DendrogramCompositSpoon> nodes){
		DendrogramCompositSpoon childLeft = null ;
		DendrogramCompositSpoon childRight = null;
		double mostNodeValue = -1;
		
		for (int i = 0; i <nodes.size(); i++) {
			for (int j = i+1; j < nodes.size(); j++) {
				double nodeValue = nodes.get(i).getValue(nodes.get(j), classCouples);
				if(nodeValue>mostNodeValue){
					childLeft=nodes.get(i);
					childRight= nodes.get(j);
					mostNodeValue=nodeValue;
					//System.out.println(nodeValue);
				}
			}
		}
		return new DendrogramNodeSpoon(childLeft,childRight);
	}
	
	public void clustering() {
		int i = 0;
		DendrogramCompositSpoon mostCoupledNodePair = null ;
		while(nodes.size()>1) {
			mostCoupledNodePair = getMostCoupledNodePair(nodes);
			nodes.remove(mostCoupledNodePair.getChildLeft());
			nodes.remove(mostCoupledNodePair.getChildRight());
			nodes.add(mostCoupledNodePair);
			i++;
		}
	}
	
	private List<DendrogramCompositSpoon> initNodes() {
		List<DendrogramCompositSpoon> output = new ArrayList<DendrogramCompositSpoon>();
		ArrayList<String> classes = new ArrayList<String>();
		for(ClassCoupleSpoon couple : classCouples) {
			classes.add(couple.getClassNameA());
			classes.add(couple.getClassNameB());
		}
		//Supression des doublons
		Set<String> set = new HashSet<>(classes);
		classes.clear();
		classes.addAll(set);
		
		
		for(String s : classes) {
			DendrogramCompositSpoon leaf = new DendrogramLeafSpoon(s);	
			output.add(leaf);
		}
		return output;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for(DendrogramCompositSpoon d : nodes) {
			builder.append(d).append("\n");
		}

		return builder.toString();
	}
	

	public String getGraphAsDot() {
		StringBuilder builder = new StringBuilder("digraph \"DendrogramSpoon\" {\n");
		builder.append(this.toString());
		builder.append("\n}");
		return builder.toString();
	}
	
	public void saveGraph() {
		try {
			FileWriter fw = new FileWriter("DendrogramGraphSpoon.dot", false);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);
			out.println(this.getGraphAsDot());
			out.close();
			bw.close();
			fw.close();
		} catch (IOException e) {
			System.err.println("Exception ecriture fichier");
			e.printStackTrace();
		}}
	public void saveGraphAsPNG() throws IOException {
		MutableGraph g = new Parser().read(this.getGraphAsDot());
		Graphviz.fromGraph(g).render(Format.PNG).toFile(new File("dendrogramSpoon.png"));
	}
	public void createFiles() throws IOException {
		this.saveGraph();
		this.saveGraphAsPNG();
	}
	
}
