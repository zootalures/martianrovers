package edu.bath.cs.martianrovers.sim;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SimState {
	Point mothershipLoc;
	int numCollected = 0;
	int timeStep = 0;

	enum BeaconState {
		ADD, SUBTRACT, OFF
	}

	int numSamples;

	List<Rover> rovers = new LinkedList<Rover>();
	Map<Point, Integer> beans = new HashMap<Point, Integer>();
	Map<Point, Integer> samples = new HashMap<Point, Integer>();
	Map<Point, Boolean> obstacles = new HashMap<Point, Boolean>();
	Map<Point, Integer> roverGrid = new HashMap<Point, Integer>();

	static class Beacon {
		Point point;
		BeaconState state;
	}

	List<Beacon> beacons = new LinkedList<Beacon>();
	List<Beacon> prevBeacons = new LinkedList<Beacon>();
	double[][] gradient;

	int width, height;

	public int getTimeStep() {
		return timeStep;
	}

	public Point getMothershipLoc() {
		return mothershipLoc;
	}

	public void setMothershipLoc(Point mothershipLoc) {
		this.mothershipLoc = mothershipLoc;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Map<Point, Integer> getBeans() {
		return beans;
	}

	public void addRover(Rover rover) {
		assert (rover.getLoc() != null);
		rovers.add(rover);
		roverGrid.put(rover.getLoc(), numRovers(rover.getLoc()) + 1);
	}

	public List<Rover> getRovers() {
		return rovers;
	}

	public SimState(int width, int height, Point mothership) {
		this.mothershipLoc = mothership;
		this.width = width;
		this.height = height;
		buildGradient();
	}

	public double[][] getGradient() {
		return gradient;
	}

	public void takeSample(Point loc) {
		Integer ns = numSamples(loc);
		if (ns != null && ns > 0) {
			samples.put(loc, ns - 1);
		} else {
			throw new IllegalArgumentException("Not enough samples at " + loc);
		}
	}

	public void dropSample(Point loc) {
		if (loc.equals(mothershipLoc)) {
			numCollected++;
		} else {
			Integer ns = numSamples(loc);
			samples.put(loc, (ns == null ? 1 : ns + 1));
		}

	}

	double mothershipSignal = 0.5;
	int maxBeaconRange =10;
	double beaconStrength = 0.2;
	double beaconTemplate[][];
	Point templateCenter;
	Dimension templateSize;

	void buildTemplate() {
		beaconTemplate = new double[maxBeaconRange * 2 + 1][maxBeaconRange * 2 + 1];
		templateCenter = new Point((maxBeaconRange * 2 + 1) / 2,
				(maxBeaconRange * 2 + 1) / 2);
		Point ip = new Point();
		templateSize = new Dimension((maxBeaconRange * 2 + 1),
				(maxBeaconRange * 2 + 1));
		for (ip.x = 0; ip.x < templateSize.width; ip.x++) {
			for (ip.y = 0; ip.y < templateSize.height; ip.y++) {
				double beaconDist = ip.distance(templateCenter);
				double val = (1.0 - (beaconDist / maxBeaconRange))
						* beaconStrength;
				beaconTemplate[ip.x][ip.y] = Math.max(0.0, val);
			}

		}
	}

	public void applyBeacon(Point p, boolean add) {
		Point ip = new Point();
		if (beaconTemplate == null)
			buildTemplate();
		Rectangle rect = getRect();
		for (int x = 0; x < templateSize.width; x++) {
			for (int y = 0; y < templateSize.width; y++) {
				Point tp = new Point(p);
				tp.translate(x - maxBeaconRange, y - maxBeaconRange);
				if (rect.contains(tp)) {
					if (add)
						gradient[tp.x][tp.y] += beaconTemplate[x][y];
					else
						gradient[tp.x][tp.y] -= beaconTemplate[x][y];

				}
			}
		}

	}

	boolean useMotherShipGradient = false;

	public void buildGradient() {
		if (gradient == null) {
			gradient = new double[width][height];

			if (useMotherShipGradient) {
				double maxDist = new Point(0, 0).distance(mothershipLoc);
				for (int x = 0; x < width; x++) {

					for (int y = 0; y < height; y++) {
						Point curP = new Point(x, y);
						double mothershipDist = curP.distance(mothershipLoc);

						double bval = 1.0 - (mothershipDist / maxDist);

						gradient[x][y] = bval * mothershipSignal;
					}
				}
			}
		}

		if (prevBeacons.size() > 0)
			for (int x = 0; x < width; x++)
				Arrays.fill(gradient[x], 0, height, 0.0);

		// for (Beacon ob : prevBeacons) {
		// applyBeacon(ob.point, ob.state != BeaconState.ADD);
		//
		// }
		for (Beacon b : beacons) {
			applyBeacon(b.point, b.state == BeaconState.ADD);
		}
	}

	public boolean isObstacle(Point p) {
		return !p.equals(getMothershipLoc())
				&& (p.x < 0 || p.y < 0 || p.x >= width || p.y >= height
						|| obstacles.get(p) != null || numRovers(p) > 0);
	}

	public interface SimListener {
		public void simTicked(SimState ss, Plan plan);
	}

	List<SimListener> listeners = new LinkedList<SimListener>();

	public void addSimListener(SimListener listener) {
		listeners.add(listener);
	}

	public void removeListener(SimListener listner) {
		listeners.remove(listner);

	}

	public void takeBean(Point loc) {
		int nb;
		if ((nb = numBeans(loc)) > 0) {
			beans.put(loc, nb - 1);
		} else {
			throw new IllegalArgumentException("Not enough beans at " + loc);
		}
	}

	public void dropBean(Point loc) {
		Integer nb = beans.get(loc);
		beans.put(loc, nb == null ? 1 : nb + 1);
	}

	public int numSamples(Point loc) {
		Integer ns = samples.get(loc);
		return ns == null ? 0 : ns;

	}

	public int numBeans(Point loc) {
		Integer nb = beans.get(loc);
		return nb == null ? 0 : nb;
	}

	public void setNumBeans(Point loc, int nbeans) {
		beans.put(loc, nbeans);
	}

	public void setNumSamples(Point loc, int nbeans) {
		samples.put(loc, nbeans);
		numSamples += nbeans;
	}

	public void setObstacle(Point loc, boolean bool) {
		if (bool)
			obstacles.put(loc, true);
		else
			obstacles.remove(loc);
	}

	public Map<Point, Integer> getSamples() {
		return samples;
	}

	public Map<Point, Boolean> getObstacles() {
		return obstacles;
	}

	public Map<Point, Integer> getRoverGrid() {
		return roverGrid;
	}

	public int numRovers(Point loc) {
		Integer numRovers = roverGrid.get(loc);
		return numRovers == null ? 0 : numRovers;
	}

	public void moveRover(Rover rover, Point p) {
		Point oldpoint = rover.getLoc();

		rover.setLoc(p);
		roverGrid.put(oldpoint, numRovers(oldpoint) - 1);

		roverGrid.put(p, numRovers(p) + 1);
	}

	public synchronized void step(Plan plan) {
		prevBeacons = beacons;
		beacons = new LinkedList<Beacon>();
		List<Rover> rrovers = new ArrayList<Rover>(rovers);
		Collections.shuffle(rrovers);
		ROVER: for (Rover rover : rrovers) {
			for (Rule rule : plan.getRules()) {
				if (rule.matches(this, rover)) {
					// System.err.println("Running: " + rover.toString() + ":"
					// + rule);
					for (Action a : rule.getActions()) {
						if (a.canAct(this, rover))
							a.act(this, rover);
					}
					rover.setLastRule(rule);
					continue ROVER;
				}
			}
		}

		for (SimListener l : listeners) {
			l.simTicked(this, plan);
		}
		buildGradient();
		timeStep++;
	}

	public Rectangle getRect() {
		return new Rectangle(0, 0, width, height);
	}

	public int getNumCollected() {
		return numCollected;
	}

	public int getTotalSamples() {
		return numSamples;
	}

	public void setBeacon(Point loc, boolean b) {
		Beacon bcn = new Beacon();
		bcn.point = loc;
		bcn.state = b ? BeaconState.ADD : BeaconState.SUBTRACT;
		beacons.add(bcn);

	}
}
