package edu.bath.cs.martianrovers.sim.actions;

import edu.bath.cs.martianrovers.sim.Action;
import edu.bath.cs.martianrovers.sim.ActionGroup;
import edu.bath.cs.martianrovers.sim.Rover;
import edu.bath.cs.martianrovers.sim.SimState;

public class DropBean extends Action {

	public DropBean(ActionGroup group) {
		super(group);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void act(SimState sim, Rover rover) {
		sim.dropBean(rover.getLoc());
	}

	@Override
	public boolean canAct(SimState sim, Rover rover) {
		return true;
	}

	@Override
	public String getName() {
		return "drop bean";
	}

}
