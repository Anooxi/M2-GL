package parsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import models.ClassCoupleSpoon;
import models.Cluster;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtType;

public class SpoonClusteringPartition extends SpoonClustering {
	
	public SpoonClusteringPartition(String tEST_PROJECT_PATH, CtModel model, Launcher ourLauncher) {
		super(tEST_PROJECT_PATH, model, ourLauncher);
	}

	


	public Stack<Cluster> createPartition(ArrayList<Cluster> clusters,ArrayList<ClassCoupleSpoon> couplesOfClasses) throws IOException {
		Cluster clusterA, clusterB, partieGauche, partieDroite;
		Stack<Cluster> PileCluster = new Stack<Cluster>();
		double CouplingMax, tempCouplingMax;
		int indexPartA, indexPartB;
		

		//Algo du cours
		Cluster tempCluster;
		ArrayList<String> tempClasses;
		while (clusters.size() > 1) {
			indexPartA = 0;
			indexPartB = 0;
			CouplingMax = 0;
			for (int i = 0; i < clusters.size(); i++) {
				clusterA = clusters.get(i);
				for (int j = 0; j < clusters.size(); j++) {
					clusterB = clusters.get(j);
					if (i != j) {
						
						tempCouplingMax = Cluster.getMetricCluster(clusterA, clusterB, couplesOfClasses);
						if (tempCouplingMax > CouplingMax) {
							CouplingMax = tempCouplingMax;
							indexPartA = i;
							indexPartB = j;
						}
					}
				}
			}
			if (CouplingMax > 0) {
				partieGauche = clusters.get(indexPartA);
				partieDroite = clusters.get(indexPartB);
				
				
				tempClasses = new ArrayList<String>(partieGauche.getClassList());
				tempCluster = new Cluster(tempClasses, CouplingMax);
				tempCluster.add(partieDroite.getClassList());
				
				//Algo du cours : enlève, enlève, ajoute
				
				clusters.remove(partieGauche);
				clusters.remove(partieDroite);
				clusters.add(tempCluster);
				
				
				PileCluster.add(new Cluster(partieGauche));
				PileCluster.add(new Cluster(partieDroite));
				PileCluster.add(new Cluster(tempCluster));
			}
			else {
				//Plus de fusion possible
				break;
			}
			
			
		}
		
		return PileCluster;

	}
	
	
	public ArrayList<Cluster> indentationClusterAlgorithm(ArrayList<Cluster> clusters,ArrayList<ClassCoupleSpoon> couplesOfClasses) throws IOException {
		
		Stack<Cluster> PileCluster = createPartition(clusters,couplesOfClasses);
		ArrayList<Cluster> resultList = new ArrayList<Cluster>();
		Cluster pere,filsGauche,filsDroite;
		double moyenne = 0;
		
		while (!PileCluster.isEmpty()) {
			pere = PileCluster.pop();
			filsGauche = PileCluster.pop();
			filsDroite = PileCluster.pop();
			
			System.out.println("\n - Couplage du père : "+ pere.getCoupling());
			moyenne = (filsGauche.getCoupling() + filsDroite.getCoupling() ) / 2;
			System.out.println("\n - Moyenne des couplages des fils : "+ moyenne);
			if(pere.getCoupling() > moyenne) {
				System.out.println( "\n Donc --> Le père est ajouté à la partition");
				resultList.add(pere);
			
		}
			else {
				System.out.println("\n La valeur du couplage du père est inférieur à la moyenne des valeurs du couplage des fils, on continue");
			}
	}
		
		showClusteringFinal(resultList);
		return resultList;
	}
	
	

}
