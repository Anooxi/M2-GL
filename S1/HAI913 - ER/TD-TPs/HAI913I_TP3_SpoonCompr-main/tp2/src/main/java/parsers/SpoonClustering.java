package parsers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import models.ClassCoupleSpoon;
import models.Cluster;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtType;
import parsers.Spoon;

public class SpoonClustering extends Spoon {
	
	ArrayList<Cluster> cluster;

	public SpoonClustering(String path, CtModel model,Launcher our) {
		super(path, model, our);
		this.cluster = new ArrayList<Cluster>();
	}
	
	
	
	@SuppressWarnings("unlikely-arg-type")
	public ArrayList<Cluster> InitialiseClusterSpoon() {
		Cluster toAdd;
		for (CtType<?> type : model.getAllTypes()) {
			if (!this.cluster.contains(type.getQualifiedName())){
				toAdd = Cluster.createClusterStatic(type.getQualifiedName());
				this.cluster.add(toAdd);
			}
		}
		return this.cluster;
	}
	
	

	public void createClustering(ArrayList<Cluster> clusters,ArrayList<ClassCoupleSpoon> couplesOfClasses) throws IOException {
		Cluster clusterA, clusterB, partieGauche, partieDroite;
		double CouplingMax, tempCouplingMax;
		int partieGaucheI, partieDroiteI;
		double metricVerif = 0.0;

		//Algo du cours
		Cluster tempCluster;
		ArrayList<String> tempClasses;
		System.out.println("\nCréation de la hiérarchie des clusters :");
		while (clusters.size() > 1) {
			partieGaucheI = 0;
			partieDroiteI = 0;
			CouplingMax = 0;
			for (int i = 0; i < clusters.size(); i++) {
				
				clusterA = clusters.get(i);
				for (int j = 0; j < clusters.size(); j++) {
					
					clusterB = clusters.get(j);
					if (i != j) {
						
						tempCouplingMax = Cluster.getMetricCluster(clusterA, clusterB, couplesOfClasses);
						if (tempCouplingMax > CouplingMax) {
							CouplingMax = tempCouplingMax;
							partieGaucheI = i;
							partieDroiteI = j;
						}
					}
				}
			}
			if (CouplingMax > 0) {
				partieGauche = clusters.get(partieGaucheI);
				partieDroite = clusters.get(partieDroiteI);
				
				
				tempClasses = new ArrayList<String>(partieGauche.getClassList());
				tempCluster = new Cluster(tempClasses, CouplingMax);
				tempCluster.add(partieDroite.getClassList());
				metricVerif += CouplingMax;
				
				//Algo du cours : enlève, enlève, ajoute
				
				clusters.remove(partieGauche);
				clusters.remove(partieDroite);
				clusters.add(tempCluster);
				
				
				
				// En cas de modifications
				
				
				System.out.println("\n FUSION ENTRE CLUSTERS ");
				System.out.println("Fusion de : ");
				partieGauche.getClassList().forEach(classe -> System.out.println("Classe :  " + classe.toString()));
				System.out.println("Avec : ");
				partieDroite.getClassList().forEach(classe -> System.out.println("Classe :  " + classe.toString()));
				System.out.println(" Nouvelle valeur du couplage : " + CouplingMax + "\n");
			}
			else {
				//Pas de fusion
				break;
			}
		}
		showClusteringFinal(this.cluster);
	}
	
	
	
	
	public void showClusteringFinal(ArrayList<Cluster> clusters) {
		System.out.println("Affichage des clusters (si la valeur du couplage est > 0");
		for (Cluster cluster : clusters) {
			if(cluster.totalCoupling != 0) {
			System.out.println("Cluster : ");
			for (String className : cluster.className) {
				System.out.println("Classe membre : " + className);
			}
			
			System.out.println("Valeur de la métrique de couplage total de ce cluster : " + cluster.totalCoupling +"\n");
			System.out.println("\n");
			}
		}
	}
	
	public ArrayList<ClassCoupleSpoon> createCouple(Spoon spoonCouplingGraph) {

		ArrayList<ClassCoupleSpoon> coupledClassesList = new ArrayList<ClassCoupleSpoon>();
		ClassCoupleSpoon newClassCoupleSpoon;
		Map<String, Double> tempMap;
		for (String classA : spoonCouplingGraph.getCouplingGraph().keySet()) {
			tempMap = spoonCouplingGraph.getCouplingGraph().get(classA);
			for (String classB : tempMap.keySet()) {
				newClassCoupleSpoon = new ClassCoupleSpoon(classA, classB, tempMap.get(classB));
				coupledClassesList.add(newClassCoupleSpoon);
			}
		}
		return coupledClassesList;
	}
	
	

	public  ArrayList<ArrayList<String>> getClusterClass() {
		ArrayList<ArrayList<String>> className = new ArrayList<ArrayList<String>>();
		ArrayList<String> temp = new ArrayList<String>();
		for(Cluster i : this.cluster) {
			temp = i.getClassList();
			className.add(temp);
			System.out.println(i.getClassList().toString());
		}
		
		return className;
	}
	
	public String getGraphAsDot() {
		StringBuilder builder = new StringBuilder("digraph G {\n");
		Integer c = 1;
		ArrayList<ArrayList<String>> clusters = getClusterClass();
		
		
		for(ArrayList<String> cluster :  clusters) {
			String clusterName = "C"+c;
			//les feuilles
			if(cluster.size()==2) {
				builder.append('"'+ cluster.get(0) +'"').append(" -> ").append('"'+clusterName+'"').append(" ");
				builder.append('"'+cluster.get(1)+'"').append(" -> ").append('"'+clusterName+'"').append(" ");
			}
			else//On verifie si le cluster et sous ensemble d'un autre
				{
				Integer IndexPlusGrandSousEnsemble=-1;
				Integer GrandPlusGrandSousEnsemble=0;
				for (int i = 0; i < c-1; i++) {
					//cluster actuel
					ArrayList<String> array1 = new ArrayList<String>(cluster);
					//ancien cluster
					ArrayList<String> array2 = new ArrayList<String>(clusters.get(i));
				
					//si 2 est sous ensembles alors
					if(array1.containsAll(array2) && array2.size()> GrandPlusGrandSousEnsemble) {
						IndexPlusGrandSousEnsemble=i;
						}
				}
				if (IndexPlusGrandSousEnsemble>-1) {
					ArrayList<String> array1 = new ArrayList<String>(cluster);
					ArrayList<String> array2 = new ArrayList<String>(clusters.get(IndexPlusGrandSousEnsemble));
					array1.removeAll(array2);
					
					builder.append('"'+"C"+(IndexPlusGrandSousEnsemble+1)+'"').append(" -> ").append('"'+clusterName+'"').append(" ");
					for(String s : array1) {
						builder.append('"'+s+'"').append(" -> ").append('"'+clusterName+'"').append(" ");
					}
				}
			}
			c++;
		}		
		builder.append("\n}");
		return builder.toString();
	}
	
	public void saveGraphAsPNG() throws IOException {
		MutableGraph g = new Parser().read(this.getGraphAsDot());
		Graphviz.fromGraph(g).render(Format.PNG).toFile(new File("dendrogram.png"));
	}

}
