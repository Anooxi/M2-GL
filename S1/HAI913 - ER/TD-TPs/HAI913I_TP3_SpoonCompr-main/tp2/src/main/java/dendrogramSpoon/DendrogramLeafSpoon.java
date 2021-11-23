package dendrogramSpoon;

import java.util.ArrayList;

import models.ClassCoupleSpoon;

public class DendrogramLeafSpoon extends DendrogramCompositSpoon {
	String name;
	public DendrogramLeafSpoon(String s) {
		this.name = s;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public DendrogramCompositSpoon getChildLeft() {
		return null;
	}

	@Override
	public DendrogramCompositSpoon getChildRight() {
		return null;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public double getValue(DendrogramCompositSpoon other, ArrayList<ClassCoupleSpoon> classCouples) {
		double output = 0;
		if(other.isLeaf()==true) {
			for(ClassCoupleSpoon couple : classCouples) {
				if(couple.isSameCouple(this.getName(),other.getName())){
						output = couple.getCouplageMetricValue();
						}
			}
		}
		else{
			output += getValue(other.getChildLeft(),classCouples);
			output += getValue(other.getChildRight(),classCouples);
			output/=2;
		}
		return output;
	}
	public String toString() {
		return ('"'+this.name+'"');
	}

}
