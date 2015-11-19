package edu.bath.cs.martianrovers.sim.sensors;

import edu.bath.cs.martianrovers.sim.Rover;
import edu.bath.cs.martianrovers.sim.Sensor;
import edu.bath.cs.martianrovers.sim.SimState;

public class SeeSample extends Sensor {

	@Override
	public String getName() {
		return "Can See Sample";
	}

	@Override
	public String getShortName() {
		return "SEE(Spl)";
	}

	@Override
	public boolean sense(SimState sim, Rover rover) {
		return sim.numSamples(rover.getLoc()) > 0;
	}

}
