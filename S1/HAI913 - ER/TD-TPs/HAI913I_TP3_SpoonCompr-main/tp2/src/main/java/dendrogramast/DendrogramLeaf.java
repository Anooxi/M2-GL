package dendrogramast;

import graphs.CallGraph;
import models.ClassCouples;

public class DendrogramLeaf extends DendrogramComposit {
	String name;

	
	public DendrogramLeaf(String name) {
		this.name = name;
	}

	public DendrogramComposit getChildLeft() {
		return null;
	}

	public DendrogramComposit getChildRight() {
		return null;
	}

	public String getName() {
		return this.name;
	}

	public boolean isLeaf() {
		return true;
	}

	public double getValue(DendrogramComposit other,ClassCouples classCouples) {
		double output = 0;
		if(other.isLeaf()==true) {
			output = classCouples.getValueInCoupleFromClassNames(this.name , other.getName());

			}
		else {
			output += getValue(other.getChildLeft(),classCouples);
			output += getValue(other.getChildRight(),classCouples);
			output/=2;
		}
		return output;
	}
	
	public String toString(){
		return ('"'+this.name+'"');
	}

}
