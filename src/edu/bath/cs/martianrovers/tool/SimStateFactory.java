package edu.bath.cs.martianrovers.tool;

import edu.bath.cs.martianrovers.sim.SimState;
import edu.bath.cs.martianrovers.tool.MartianRovers.SimProperties;

public interface SimStateFactory {
	SimState createSimState();
}
