package edu.bath.cs.martianrovers.sim.actions;

import edu.bath.cs.martianrovers.sim.Action;
import edu.bath.cs.martianrovers.sim.ActionGroup;
import edu.bath.cs.martianrovers.sim.Rover;
import edu.bath.cs.martianrovers.sim.SimState;

public class FlashNegBeacon extends Action {

	public FlashNegBeacon(ActionGroup group) {
		super(group);
	}

	@Override
	public void act(SimState sim, Rover rover) {
		sim.setBeacon(rover.getLoc(), false);
	}

	@Override
	public boolean canAct(SimState sim, Rover rover) {

		return true;
	}

	@Override
	public String getName() {
		return "Flash Beacon -";
	}

}
