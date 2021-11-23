package models;

import java.util.ArrayList;

public class Method {
	String name;
	ArrayList<MethodInvocated> methodsInvocated = new ArrayList<MethodInvocated>();
	
	public Method(String name2){
		this.name=name2;
	}	
	
	public Method() {
		// TODO Auto-generated constructor stub
	}

	public Integer numberOfInvocations() {
		Integer rslt = 0;
		for(MethodInvocated methodInvocated :methodsInvocated) {
			rslt+=methodInvocated.numberOfTime;
		}
		return rslt;
	}
	public String getName() {
		return name;
	}
	public ArrayList<MethodInvocated> getMethodsInvocated() {
		return methodsInvocated;
	}
	/*Verifie si la method a deja etais invoque*/
	public void addMethodInvocation(MethodInvocated methodInvocated) {
		methodsInvocated.add(methodInvocated);
	}
	/*Verifie si la method a deja etais invoque*/
	public void addMethodInvocation(String name , String fromClass) {
		boolean isAlreadyIn = false;
		
		/*Si c'est le cas, incremente le nombre de fois appele*/
		for(int i =0 ; i<methodsInvocated.size(); i++)
			if (methodsInvocated.get(i).isSame(name, fromClass)) {
				isAlreadyIn = true;
				methodsInvocated.get(i).setNumberOfTime(methodsInvocated.get(i).getNumberOfTime() + 1);
			}
		/*Sinon, ajouter la method*/
		if(!isAlreadyIn) {
			methodsInvocated.add(new MethodInvocated(name, fromClass));
		}
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();		
		builder.append("MethodName : " + this.getName()+ "\n");		
		for(MethodInvocated method : methodsInvocated)
			builder.append(method.toString());
		builder.append("\n");
		return builder.toString();
	}
	
}