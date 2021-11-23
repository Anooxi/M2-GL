package dendrogramSpoon;

import java.util.ArrayList;

import models.ClassCoupleSpoon;

public abstract class DendrogramCompositSpoon {
	public abstract double getValue(DendrogramCompositSpoon other,ArrayList<ClassCoupleSpoon> classCouples);
	public abstract String getName();
	public abstract DendrogramCompositSpoon getChildLeft();
	public abstract DendrogramCompositSpoon getChildRight();
	public abstract boolean isLeaf();
}

