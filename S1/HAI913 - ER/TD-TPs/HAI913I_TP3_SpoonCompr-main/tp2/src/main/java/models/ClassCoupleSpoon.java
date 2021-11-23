package models;

public class ClassCoupleSpoon {
	
	String classA;
	String classB;
	
	double couplingValue;

	public ClassCoupleSpoon(String classA, String classB, double value) {
		this.classA = classA;
		this.classB = classB;
		this.couplingValue = value;
	}
	
	
	public String getClassNameA() {
		return this.classA;
	}

	public String getClassNameB() {
		return this.classB;
	}


	public double getCouplageMetricValue() {
		return this.couplingValue;
	}

	public boolean isSameCouple(String s1, String s2) {

		boolean case1 = s1.equals(classA) && s2.equals(classB);
		boolean case2 = s1.equals(classB) && s2.equals(classA);
		
		return (case1||case2);
	}
	
	public String toString() {
		return  "Couple : " + this.classA + " - " + this.classB + " = " + this.couplingValue + "\n";
	}
}
