package edu.bath.cs.martianrovers.sim;

public abstract class Sensor {
	public abstract String getName();

	public abstract boolean sense(SimState sim, Rover rover);

	public abstract String getShortName();

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getName();
	}
}
