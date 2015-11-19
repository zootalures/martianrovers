package edu.bath.cs.martianrovers.sim.actions;

import java.awt.Point;

import edu.bath.cs.martianrovers.sim.Action;
import edu.bath.cs.martianrovers.sim.ActionGroup;
import edu.bath.cs.martianrovers.sim.Rover;
import edu.bath.cs.martianrovers.sim.SimState;
import edu.bath.cs.martianrovers.sim.actions.Movement.Unit;

public class MoveRandomly extends Action {

	public MoveRandomly(ActionGroup group) {
		super(group);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void act(SimState sim, Rover rover) {
		Point p = null;
		Point loc = rover.getLoc();
		int tried = 0;
		do {

			double val = Math.random();
			for (Unit u : Movement.units) {
				if (val < u.prob) {
					p = new Point(loc);
					u.translate(p);
					break;
				}
			}
			tried++;
			if (tried > 10) {
				return;
			}
		} while (sim.isObstacle(p));

		sim.moveRover(rover, p);
	}

	@Override
	public boolean canAct(SimState sim, Rover rover) {
		return true;
	}

	@Override
	public String getName() {
		return "move randomly";
	}

}
