package edu.bath.cs.martianrovers.sim.actions;

import java.awt.Point;
import java.util.Random;

import edu.bath.cs.martianrovers.sim.Action;
import edu.bath.cs.martianrovers.sim.ActionGroup;
import edu.bath.cs.martianrovers.sim.Rover;
import edu.bath.cs.martianrovers.sim.SimState;
import edu.bath.cs.martianrovers.sim.actions.Movement.Unit;

public class MoveDownGradient extends Action {

	public MoveDownGradient(ActionGroup group) {
		super(group);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void act(SimState sim, Rover rover) {

		Point loc = rover.getLoc();
		double curGradient = Double.MAX_VALUE;
		Point lowest = null;
		double gradients[][] = sim.getGradient();
		Random r = new Random();
		int l = Movement.units.length;
		int rs = r.nextInt(l);

		for (int i = (rs + 1) % l; i != rs; i = (i + 1) % l) {
			Unit u = Movement.units[i];
			Point p = new Point(loc);

			u.translate(p);
			if (sim.isObstacle(p))
				continue;
			double cgrad = gradients[p.x][p.y] / p.distance(loc);

			// System.err.println("distance from " + sim.getMothershipLoc()
			// + " to " + p + " is " + cdist);
			if (lowest == null) {
				lowest = p;
				curGradient = cgrad;
			} else {
				if (cgrad < curGradient) {
					lowest = p;
					curGradient = cgrad;
				}
			}
		}
		if (lowest != null)
			sim.moveRover(rover, lowest);
	}

	@Override
	public boolean canAct(SimState sim, Rover rover) {
		return true;
	}

	@Override
	public String getName() {
		return "move down gradient";
	}

}
