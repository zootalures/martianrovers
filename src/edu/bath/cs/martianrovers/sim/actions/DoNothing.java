package edu.bath.cs.martianrovers.sim.actions;

import edu.bath.cs.martianrovers.sim.Action;
import edu.bath.cs.martianrovers.sim.ActionGroup;
import edu.bath.cs.martianrovers.sim.Rover;
import edu.bath.cs.martianrovers.sim.SimState;

public class DoNothing extends Action {

	public DoNothing(ActionGroup group) {
		super(group);
	}

	@Override
	public void act(SimState sim, Rover rover) {

	}

	@Override
	public boolean canAct(SimState sim, Rover rover) {

		return true;
	}

	@Override
	public String getName() {
		return "do nothing";
	}

}
