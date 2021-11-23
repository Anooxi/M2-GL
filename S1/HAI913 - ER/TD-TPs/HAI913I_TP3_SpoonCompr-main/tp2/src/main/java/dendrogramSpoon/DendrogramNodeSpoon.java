package dendrogramSpoon;

import java.util.ArrayList;

import models.ClassCoupleSpoon;

public class DendrogramNodeSpoon extends DendrogramCompositSpoon {

	String name;
	DendrogramCompositSpoon childLeft;
	DendrogramCompositSpoon childRight;
	static Integer cluster = 0;

	public DendrogramNodeSpoon(DendrogramCompositSpoon childLeft, DendrogramCompositSpoon childRight) {
		this.childLeft = childLeft;
		this.childRight =childRight;
		this.name = new String('"'+"C"+cluster.toString()+'"');
		cluster++;
	}


	@Override
	public String getName() {
		return name;
	}

	@Override
	public DendrogramCompositSpoon getChildLeft() {
		return childLeft;
	}

	@Override
	public DendrogramCompositSpoon getChildRight() {
		return childRight;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public double getValue(DendrogramCompositSpoon other, ArrayList<ClassCoupleSpoon> classCouples) {

		if(other.isLeaf()==true) {
			return other.getValue(this, classCouples);
		}
		
		double output = this.getChildLeft().getValue(other.getChildRight(), classCouples);
		output += this.getChildLeft().getValue(other.getChildLeft(), classCouples);
		output += this.getChildRight().getValue(other.getChildRight(), classCouples);
		output += this.getChildRight().getValue(other.getChildLeft(), classCouples);

		return output/4;
		
	}


	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		if(getChildLeft()!= null && getChildRight()!=null) {
			if(this.getChildLeft().isLeaf()&& this.getChildRight().isLeaf()){
				builder.append("\n" + this.getName()+" -> "+ childLeft.toString());
				builder.append("\n" + this.getName()+" -> "+ childRight.toString()+"\n");
			}
			else if( this.getChildLeft().isLeaf()==false && this.getChildRight().isLeaf()==false){
				builder.append("\n" + this.getName() +" -> " +this.getChildLeft().getName());
				builder.append("\n" + this.getName() +" -> " +this.getChildRight().getName());
				builder.append(this.getChildRight().toString());
				builder.append(this.getChildLeft().toString());
			}

			else if((this.getChildLeft().isLeaf()==false) && this.getChildRight().isLeaf()==true){
				builder.append("\n" + this.getName() +" -> " +this.getChildLeft().getName());
				builder.append("\n" + this.getName() +" -> " +this.getChildRight().toString());
				builder.append(this.getChildLeft().toString());
			}
			else if(this.getChildLeft().isLeaf()==true && this.getChildRight().isLeaf()==false){
				builder.append("\n" + this.getName() +" -> " + this.getChildLeft().toString());
				builder.append("\n" + this.getName() +" -> " +this.getChildRight().getName());
				builder.append(this.getChildRight().toString());
			}
		}
		return builder.toString();
	}
}
