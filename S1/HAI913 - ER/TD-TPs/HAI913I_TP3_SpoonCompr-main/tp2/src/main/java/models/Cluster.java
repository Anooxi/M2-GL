package models;

import java.util.ArrayList;

public class Cluster {
	
	public ArrayList<String> className;
	public double totalCoupling;
	
	
	public ArrayList<String> getClassList() {
		return this.className;
	}
	
	public double getCoupling() {
		return this.totalCoupling;
	}
	

	public Cluster(ArrayList<String> classes, double metricCouplingValue) {
		this.className = classes;
		this.totalCoupling = metricCouplingValue;
	}
	
	
	public Cluster(Cluster addOne) {
		super();
		this.className = new ArrayList<String>();
		for(String c: addOne.getClassList()) {
			this.className.add(c);
		}
	
		this.totalCoupling =  addOne.getCoupling();
	}
	
	public static Cluster createClusterStatic(String className) {
		ArrayList<String> classes = new ArrayList<String>();
		classes.add(className);
		Cluster cluster = new Cluster(classes, 0);
		return cluster;
	}
	
	public void add(ArrayList<String> ToAdd) {
		for (String classToAdd : ToAdd) {
			if (!this.getClassList().contains(classToAdd)) {
				this.className.add(classToAdd);
			}
		}
	}

	public String toString() {
		String res = null;
		
		for(String i : this.className) {
			res += "Classe : " + i; 
		}
		res += "valeur de la métrique de couplage de ce cluster : " + this.totalCoupling + "\n";
		return res;
 	}

	public int size() {
		int cpt = 0;
		for(String i : className) {
			cpt +=1;
		}
		return cpt;
	}
	
	
	public static  double getMetricCluster(Cluster clusterA, Cluster clusterB,
			ArrayList<ClassCoupleSpoon> couplesOfClasses) {
		double couplingValue = 0;
		for (String classOfClusterA : clusterA.getClassList()) {
			for (String classOfClusterB : clusterB.getClassList()) {
				if (!classOfClusterA.equals(classOfClusterB)) {
					for (ClassCoupleSpoon couple : couplesOfClasses) {
						if (couple.getClassNameA().equals(classOfClusterA)
								&& couple.getClassNameB().equals(classOfClusterB)) {
							couplingValue += couple.getCouplageMetricValue();
						}
					}
				}
			}
		}
		return couplingValue;
	}
	


	
}
