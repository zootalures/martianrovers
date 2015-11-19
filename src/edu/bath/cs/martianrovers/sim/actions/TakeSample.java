package edu.bath.cs.martianrovers.sim.actions;

import edu.bath.cs.martianrovers.sim.Action;
import edu.bath.cs.martianrovers.sim.ActionGroup;
import edu.bath.cs.martianrovers.sim.Rover;
import edu.bath.cs.martianrovers.sim.SimState;

public class TakeSample extends Action {

	public TakeSample(ActionGroup group) {
		super(group);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void act(SimState sim, Rover rover) {
		sim.takeSample(rover.getLoc());
		rover.setHaveSample(true);
	}

	@Override
	public boolean canAct(SimState sim, Rover rover) {
		return sim.numSamples(rover.getLoc()) > 0 && !rover.isHaveSample();
	}

	@Override
	public String getName() {
		return "take sample";
	}

}
