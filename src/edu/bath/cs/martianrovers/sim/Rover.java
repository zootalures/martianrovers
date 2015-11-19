package edu.bath.cs.martianrovers.sim;

import java.awt.Point;

public class Rover {
	Point loc;

	Rule lastRule;

	public Rule getLastRule() {
		return lastRule;
	}

	public void setLastRule(Rule lastRule) {
		this.lastRule = lastRule;
	}

	public Rover(Point loc) {
		super();
		this.loc = loc;
	}

	public Point getLoc() {
		return loc;
	}

	public void setLoc(Point loc) {
		this.loc = loc;
	}

	public boolean isHaveSample() {
		return haveSample;
	}

	public void setHaveSample(boolean haveSample) {
		this.haveSample = haveSample;
	}

	boolean haveSample = false;
}
