package edu.bath.cs.martianrovers.sim;

import java.util.ArrayList;
import java.util.List;

public class Plan {

	List<Rule> rules = new ArrayList<Rule>();

	public List<Rule> getRules() {
		return rules;
	}

	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}

	public void removeRule(int i) {
		rules.remove(i);
	}

	public void addRule(Rule rule, int i) {
		assert (rule != null);
		rules.add(i, rule);
	}

	@Override
	public String toString() {
		String txt = "";
		for (Rule r : rules) {
			txt += r.toString() + "\n";
		}
		return txt;
	}
}
