package edu.bath.cs.martianrovers.sim;

public abstract class Action {
	public abstract String getName();

	public abstract boolean canAct(SimState sim, Rover rover);

	public abstract void act(SimState sim, Rover rover);

	ActionGroup group;

	public Action(ActionGroup group) {
		super();
		this.group = group;
	}

	public ActionGroup getGroup() {
		return group;
	}

	public void setGroup(ActionGroup group) {
		this.group = group;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getName();
	}

}
