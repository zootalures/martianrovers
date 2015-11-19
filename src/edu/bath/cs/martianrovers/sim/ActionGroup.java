package edu.bath.cs.martianrovers.sim;

public class ActionGroup implements Comparable<ActionGroup> {
	String name;

	public String getName() {
		return name;
	}

	public ActionGroup(String name, int index) {
		super();
		this.index = index;
		this.name = name;
	}

	int index;

	@Override
	public int compareTo(ActionGroup o) {
		return ((Integer) index).compareTo(o.index);
	}
}
