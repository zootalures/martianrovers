package edu.bath.cs.martianrovers.sim.actions;

import edu.bath.cs.martianrovers.sim.Action;
import edu.bath.cs.martianrovers.sim.ActionGroup;
import edu.bath.cs.martianrovers.sim.Rover;
import edu.bath.cs.martianrovers.sim.SimState;

public class DropSample extends Action {

	@Override
	public void act(SimState sim, Rover rover) {
		sim.dropSample(rover.getLoc());
		rover.setHaveSample(false);
	}

	public DropSample(ActionGroup group) {
		super(group);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canAct(SimState sim, Rover rover) {
		return rover.isHaveSample();
	}

	@Override
	public String getName() {
		return "drop sample";
	}

}
