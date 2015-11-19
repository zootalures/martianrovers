package edu.bath.cs.martianrovers.sim.sensors;

import edu.bath.cs.martianrovers.sim.Rover;
import edu.bath.cs.martianrovers.sim.Sensor;
import edu.bath.cs.martianrovers.sim.SimState;

public class HaveSample extends Sensor {

	@Override
	public String getName() {
		return "carying sample";
	}

	@Override
	public String getShortName() {
		return "GOT(Spl)";
	}

	@Override
	public boolean sense(SimState sim, Rover rover) {
		return rover.isHaveSample();
	}

}
