package edu.bath.cs.martianrovers.sim.sensors;

import edu.bath.cs.martianrovers.sim.Rover;
import edu.bath.cs.martianrovers.sim.Sensor;
import edu.bath.cs.martianrovers.sim.SimState;

public class AtBase extends Sensor {

	@Override
	public String getName() {
		return "at base";
	}

	@Override
	public String getShortName() {
		return "AT(Base)";
	}

	@Override
	public boolean sense(SimState sim, Rover rover) {
		return rover.getLoc().equals(sim.getMothershipLoc());
	}

}
