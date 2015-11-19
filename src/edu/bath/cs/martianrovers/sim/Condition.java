package edu.bath.cs.martianrovers.sim;

public class Condition {
	public enum ConditionState {
		YES, NO, DONTCARE {
			@Override
			public String toString() {
				// TODO Auto-generated method stub
				return " - ";
			}
		}
	};

	public Condition(Sensor sensor) {
		this.sensor = sensor;
	}

	ConditionState state = ConditionState.DONTCARE;

	public ConditionState getState() {
		return state;
	}

	public void setState(ConditionState state) {
		this.state = state;
	}

	Sensor sensor;

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}

	@Override
	public String toString() {
		return "(" + sensor.toString() + "==" + state.toString() + ")";
	}
}
