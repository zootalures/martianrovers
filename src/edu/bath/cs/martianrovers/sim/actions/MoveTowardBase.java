package edu.bath.cs.martianrovers.sim.actions;

import java.awt.Point;

import edu.bath.cs.martianrovers.sim.Action;
import edu.bath.cs.martianrovers.sim.ActionGroup;
import edu.bath.cs.martianrovers.sim.Rover;
import edu.bath.cs.martianrovers.sim.SimState;
import edu.bath.cs.martianrovers.sim.actions.Movement.Unit;

public class MoveTowardBase extends Action {

	public MoveTowardBase(ActionGroup group) {
		super(group);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void act(SimState sim, Rover rover) {

		Point loc = rover.getLoc();
		double distance = loc.distance(sim.getMothershipLoc());
		Point nearest = null;
		for (Unit u : Movement.units) {
			Point p = new Point(loc);

			u.translate(p);
			if (sim.isObstacle(p))
				continue;
			double cdist = p.distance(sim.getMothershipLoc());
			// System.err.println("distance from " + sim.getMothershipLoc()
			// + " to " + p + " is " + cdist);
			if (nearest == null) {
				nearest = p;
				distance = cdist;
			} else {
				if (cdist < distance) {
					nearest = p;
					distance = cdist;
				}
			}
		}
		if (nearest != null)
			sim.moveRover(rover, nearest);
	}

	@Override
	public boolean canAct(SimState sim, Rover rover) {
		return !rover.getLoc().equals(sim.getMothershipLoc());
	}

	@Override
	public String getName() {
		return "move toward mothership";
	}

}
