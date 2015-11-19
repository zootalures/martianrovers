package edu.bath.cs.martianrovers.sim.actions;

import java.awt.Point;

public class Movement {

	static double unit = (4.0 * Math.sqrt(2)) + 4.0;
	static double diag = Math.sqrt(2);

	public static class Unit {
		public Unit(int dx, int dy, double p) {
			this.dx = dx;
			this.dy = dy;
			this.prob = p;
		}

		int dx, dy;
		double prob;

		void translate(Point px) {
			px.x += dx;
			px.y += dy;
		}
	}

	public static Unit units[];

	static {
		double acc;
		Unit newu[] = { new Unit(0, 1, acc = diag / unit),
				new Unit(1, 1, acc += 1.0 / unit),
				new Unit(1, 0, acc += diag / unit),
				new Unit(1, -1, acc += 1.0 / unit),
				new Unit(0, -1, acc += diag / unit),
				new Unit(-1, -1, acc += 1.0 / unit),
				new Unit(-1, 0, acc += diag / unit),
				new Unit(-1, 1, acc += 1.0 / unit) };
		units = newu;
	};
	// public static Unit units[] = { new Unit(0, 1, 0.25), new Unit(1, 0,
	// 0.50),
	// new Unit(0, -1, 0.75), new Unit(-1, 0, 1.0), };

}
