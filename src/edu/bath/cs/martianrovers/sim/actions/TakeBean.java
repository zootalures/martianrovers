package edu.bath.cs.martianrovers.sim.actions;

import edu.bath.cs.martianrovers.sim.Action;
import edu.bath.cs.martianrovers.sim.ActionGroup;
import edu.bath.cs.martianrovers.sim.Rover;
import edu.bath.cs.martianrovers.sim.SimState;

public class TakeBean extends Action {

	public TakeBean(ActionGroup group) {
		super(group);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void act(SimState sim, Rover rover) {
		sim.takeBean(rover.getLoc());

	}

	@Override
	public boolean canAct(SimState sim, Rover rover) {
		return sim.numBeans(rover.getLoc()) > 0;
	}

	@Override
	public String getName() {
		return "take bean";
	}

}
