package edu.bath.cs.martianrovers.sim.actions;

import java.awt.Point;

import edu.bath.cs.martianrovers.sim.Action;
import edu.bath.cs.martianrovers.sim.ActionGroup;
import edu.bath.cs.martianrovers.sim.Rover;
import edu.bath.cs.martianrovers.sim.SimState;
import edu.bath.cs.martianrovers.sim.actions.Movement.Unit;

public class MoveAwayFromBase extends Action {

	public MoveAwayFromBase(ActionGroup group) {
		super(group);
		// TODO Auto-generated constructor stub
	}

	// static int mx[] = new int[] { 0, 1, 1, 1, 0, -1, -1, -1, };
	// static int my[] = new int[] { 1, 1, 0, -1, -1, -1, 0, 1 };

	@Override
	public void act(SimState sim, Rover rover) {

		Point loc = rover.getLoc();
		double distance = loc.distance(sim.getMothershipLoc());
		Point furthest = null;
		for (Unit u : Movement.units) {
			Point p = new Point(loc);
			u.translate(p);
			if (sim.isObstacle(p))
				continue;
			double cdist = p.distance(sim.getMothershipLoc());
			if (furthest == null)
				furthest = p;
			else {
				if (cdist > distance) {
					furthest = p;
					distance = cdist;
				}
			}
		}
		if (furthest != null)
			sim.moveRover(rover, furthest);
	}

	@Override
	public boolean canAct(SimState sim, Rover rover) {
		return true;
	}

	@Override
	public String getName() {
		return "move away from mothership";
	}

}
