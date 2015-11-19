package edu.bath.cs.martianrovers.sim;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.bath.cs.martianrovers.sim.Condition.ConditionState;

/**
 * represents a rule in a plan each rule has a set of conditions which match a
 * sensor to a particular value (YES/NO/DONTCARE)
 * 
 * If all of the condtitions are met, and the action is enactable then the rule
 * matches
 * 
 * @author occ
 * 
 */
public class Rule {

	public Rule() {
	}

	public ConditionState getState(Sensor sens) {
		Condition cond = conditions.get(sens);
		if (cond != null) {
			return cond.getState();
		} else {
			return ConditionState.DONTCARE;
		}
	}

	public void setActionForGroup(ActionGroup ag, Action a) {
		if (a != null)
			actions.put(ag, a);
		else
			actions.remove(ag);
	}

	public Action getActionForGroup(ActionGroup ag) {
		return actions.get(ag);
	}

	public Collection<Condition> getConditions() {
		return Collections.unmodifiableCollection(conditions.values());
	}

	public void setCondition(Sensor sensor, Condition.ConditionState state) {
		if (state.equals(ConditionState.DONTCARE)) {
			conditions.remove(sensor);
		} else {
			Condition cond = conditions.get(sensor);
			if (cond == null) {
				conditions.put(sensor, cond = new Condition(sensor));
			}
			cond.setState(state);
		}

	}

	Map<Sensor, Condition> conditions = new HashMap<Sensor, Condition>();
	SortedMap<ActionGroup, Action> actions = new TreeMap<ActionGroup, Action>();

	public Collection<Action> getActions() {
		return actions.values();
	}

	public void addAction(Action action) {
		actions.put(action.getGroup(), action);
	}

	/**
	 * Determine if this rule matches.
	 * 
	 * iff every condition is satisfied, and the action is enactable
	 * 
	 * @param sim
	 * @param rover
	 * @return
	 */
	public boolean matches(SimState sim, Rover rover) {
		for (Condition c : conditions.values()) {
			boolean senseval = c.getSensor().sense(sim, rover);
			switch (c.getState()) {
			case YES:
				if (!senseval) {
					return false;
				}
				break;
			case NO:
				if (senseval)
					return false;
			case DONTCARE:
			}
		}
//		for (Action a : getActions()) {
//			if (!a.canAct(sim, rover))
//				return false;
//		}
		return true;
	}

	@Override
	public String toString() {
		String cond = "IF ( ";
		boolean first = true;
		for (Condition c : conditions.values()) {
			if (!first) {
				cond += ",";
			}
			first = false;

			cond += c.toString();
		}
		cond += " ) then [";

		first = true;
		for (Action a : getActions()) {
			if (!first)
				cond += ",";
			first = false;
			cond += a.toString();
		}
		cond += "]";
		return cond;
	}
}
