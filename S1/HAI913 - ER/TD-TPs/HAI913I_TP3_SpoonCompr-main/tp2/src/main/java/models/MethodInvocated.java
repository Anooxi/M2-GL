package models;

public class MethodInvocated {
	String name;
	String classOrigin;
	//Nombre de fois que la method a etais invoque dans la method parent
	Integer numberOfTime = 1;
	
	public MethodInvocated(String name2, String fromClass) {
		this.name = name2;
		this.classOrigin = fromClass;
	}
	public MethodInvocated() {
		// TODO Auto-generated constructor stub
	}
	/*Verifie si la method est la meme*/
	public boolean isSame(String name2, String fromClass) {
		return(this.getName().equals(name2) && this.getClassOrigin().equals(fromClass));
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getClassOrigin() {
		return classOrigin;
	}
	public void setClassOrigin(String classOrigin) {
		this.classOrigin = classOrigin;
	}

	public Integer getNumberOfTime() {
		return numberOfTime;
	}
	public void setNumberOfTime(Integer numberOfTime) {
		this.numberOfTime = numberOfTime;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();		
		builder.append("Invocation : "+this.getName() + " from class " + this.getClassOrigin() + " " +this.getNumberOfTime()+ " time(s)" + "\n");	
		return builder.toString();
	}
	
	
}