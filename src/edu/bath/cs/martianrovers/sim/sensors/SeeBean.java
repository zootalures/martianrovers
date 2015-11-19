package edu.bath.cs.martianrovers.sim.sensors;

import edu.bath.cs.martianrovers.sim.Rover;
import edu.bath.cs.martianrovers.sim.Sensor;
import edu.bath.cs.martianrovers.sim.SimState;

public class SeeBean extends Sensor {

	@Override
	public String getName() {
		return "can see bean";
	}

	@Override
	public String getShortName() {
		return "SEE(Bn) ";
	}

	@Override
	public boolean sense(SimState sim, Rover rover) {
		return sim.numBeans(rover.getLoc()) > 0;
	}

}
