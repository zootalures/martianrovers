package edu.bath.cs.martianrovers.sim;

import java.util.ArrayList;
import java.util.List;

import edu.bath.cs.martianrovers.sim.Condition.ConditionState;
import edu.bath.cs.martianrovers.sim.actions.DoNothing;
import edu.bath.cs.martianrovers.sim.actions.DropBean;
import edu.bath.cs.martianrovers.sim.actions.DropSample;
import edu.bath.cs.martianrovers.sim.actions.FlashNegBeacon;
import edu.bath.cs.martianrovers.sim.actions.FlashPosBeacon;
import edu.bath.cs.martianrovers.sim.actions.MoveAwayFromBase;
import edu.bath.cs.martianrovers.sim.actions.MoveDownGradient;
import edu.bath.cs.martianrovers.sim.actions.MoveRandomly;
import edu.bath.cs.martianrovers.sim.actions.MoveTowardBase;
import edu.bath.cs.martianrovers.sim.actions.MoveUpGradient;
import edu.bath.cs.martianrovers.sim.actions.TakeBean;
import edu.bath.cs.martianrovers.sim.actions.TakeSample;
import edu.bath.cs.martianrovers.sim.sensors.AtBase;
import edu.bath.cs.martianrovers.sim.sensors.HaveSample;
import edu.bath.cs.martianrovers.sim.sensors.SeeBean;
import edu.bath.cs.martianrovers.sim.sensors.SeeSample;

public class Simulation {

	String name;
	Plan idealPlan;

	public Plan getIdealPlan() {
		return idealPlan;
	}

	public void setIdealPlan(Plan idealPlan) {
		this.idealPlan = idealPlan;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	List<ActionGroup> actionGroups = new ArrayList<ActionGroup>();
	List<Sensor> sensors = new ArrayList<Sensor>();

	public List<ActionGroup> getActionGroups() {
		return actionGroups;
	}

	public List<Sensor> getSensors() {
		return sensors;
	}

	List<Action> actions = new ArrayList<Action>();

	public List<Action> getActions() {
		return actions;
	}

	public Sensor getSensor(Class<? extends Sensor> type) {
		for (Sensor s : sensors) {
			if (type.isInstance(s))
				return s;
		}
		throw new IllegalArgumentException("Sensor " + type + " not found");
	}

	public Action getAction(Class<? extends Action> type) {
		for (Action action : actions) {
			if (type.isInstance(action))
				return action;
		}
		throw new IllegalArgumentException("Action " + type + " not found");
	}

	public static Simulation simpleSimulation() {
		Simulation sim = new Simulation();
		sim.setName("Sim 1) Basic Movement");
		sim.sensors.add(new AtBase());
		sim.sensors.add(new HaveSample());
		sim.sensors.add(new SeeSample());

		ActionGroup sampleHopper = new ActionGroup("Sample arm", 2);
		ActionGroup wheels = new ActionGroup("Wheels", 3);

		sim.actionGroups.add(sampleHopper);
		sim.actionGroups.add(wheels);

		sim.actions.add(new DropSample(sampleHopper));
		sim.actions.add(new TakeSample(sampleHopper));
		sim.actions.add(new MoveTowardBase(wheels));
		sim.actions.add(new MoveAwayFromBase(wheels));
		sim.actions.add(new MoveRandomly(wheels));

		Plan plan = new Plan();

		Rule r1 = new Rule();
		r1.setCondition(sim.getSensor(HaveSample.class), ConditionState.YES);
		r1.setCondition(sim.getSensor(AtBase.class), ConditionState.YES);
		r1.addAction(sim.getAction(DropSample.class));

		Rule r2 = new Rule();
		r2.setCondition(sim.getSensor(HaveSample.class), ConditionState.YES);
		r2.setCondition(sim.getSensor(AtBase.class), ConditionState.NO);
		r2.addAction(sim.getAction(MoveTowardBase.class));

		Rule r3 = new Rule();
		r3.setCondition(sim.getSensor(HaveSample.class), ConditionState.NO);
		r3.setCondition(sim.getSensor(SeeSample.class), ConditionState.YES);
		r3.addAction(sim.getAction(TakeSample.class));

		Rule r5 = new Rule();
		r5.setCondition(sim.getSensor(HaveSample.class), ConditionState.NO);
		r5.addAction(sim.getAction(MoveRandomly.class));

		plan.addRule(r1, 0);
		plan.addRule(r2, 1);
		plan.addRule(r3, 2);
		plan.addRule(r5, 3);
		sim.setIdealPlan(plan);

		return sim;

	}

	public static Simulation simpleSimulationBeans() {
		Simulation sim = new Simulation();
		sim.setName("Sim 2) Movement + Beans");
		sim.sensors.add(new AtBase());
		sim.sensors.add(new HaveSample());
		sim.sensors.add(new SeeBean());
		sim.sensors.add(new SeeSample());

		ActionGroup beanHopper = new ActionGroup("Bean hopper", 1);
		ActionGroup sampleHopper = new ActionGroup("Sample arm", 2);
		ActionGroup wheels = new ActionGroup("Wheels", 3);

		sim.actionGroups.add(beanHopper);
		sim.actionGroups.add(sampleHopper);
		sim.actionGroups.add(wheels);

		sim.actions.add(new DropBean(beanHopper));
		sim.actions.add(new TakeBean(beanHopper));
		sim.actions.add(new DropSample(sampleHopper));
		sim.actions.add(new TakeSample(sampleHopper));
		sim.actions.add(new MoveTowardBase(wheels));
		sim.actions.add(new MoveAwayFromBase(wheels));
		sim.actions.add(new MoveRandomly(wheels));

		Plan plan = new Plan();

		Rule r1 = new Rule();
		r1.setCondition(sim.getSensor(HaveSample.class), ConditionState.YES);
		r1.setCondition(sim.getSensor(AtBase.class), ConditionState.YES);
		r1.addAction(sim.getAction(DropSample.class));

		Rule r2 = new Rule();
		r2.setCondition(sim.getSensor(HaveSample.class), ConditionState.YES);
		r2.setCondition(sim.getSensor(AtBase.class), ConditionState.NO);
		r2.addAction(sim.getAction(DropBean.class));
		r2.addAction(sim.getAction(MoveTowardBase.class));

		Rule r3 = new Rule();
		r3.setCondition(sim.getSensor(HaveSample.class), ConditionState.NO);
		r3.setCondition(sim.getSensor(SeeSample.class), ConditionState.YES);
		r3.addAction(sim.getAction(TakeSample.class));

		Rule r4 = new Rule();
		r4.setCondition(sim.getSensor(HaveSample.class), ConditionState.NO);
		r4.setCondition(sim.getSensor(SeeBean.class), ConditionState.YES);
		r4.addAction(sim.getAction(TakeBean.class));
		r4.addAction(sim.getAction(MoveAwayFromBase.class));

		Rule r5 = new Rule();
		r5.setCondition(sim.getSensor(HaveSample.class), ConditionState.NO);
		r5.setCondition(sim.getSensor(SeeBean.class), ConditionState.NO);
		r5.addAction(sim.getAction(MoveRandomly.class));

		plan.addRule(r1, 0);
		plan.addRule(r2, 1);
		plan.addRule(r3, 2);
		plan.addRule(r4, 3);
		plan.addRule(r5, 4);
		sim.setIdealPlan(plan);

		return sim;

	}

	public static Simulation simpleSimulationBeacons() {
		Simulation sim = new Simulation();
		sim.setName("Sim 3) Beans + Beacons");
		sim.sensors.add(new AtBase());
		sim.sensors.add(new HaveSample());
		sim.sensors.add(new SeeBean());
		sim.sensors.add(new SeeSample());

		ActionGroup beacon = new ActionGroup("Beacon", 4);
		ActionGroup beanHopper = new ActionGroup("Bean hopper", 1);
		ActionGroup sampleHopper = new ActionGroup("Sample arm", 2);
		ActionGroup wheels = new ActionGroup("Wheels", 3);

		sim.actionGroups.add(beacon);
		sim.actionGroups.add(beanHopper);
		sim.actionGroups.add(sampleHopper);
		sim.actionGroups.add(wheels);

		sim.actions.add(new DropBean(beanHopper));
		sim.actions.add(new TakeBean(beanHopper));
		sim.actions.add(new DropSample(sampleHopper));
		sim.actions.add(new TakeSample(sampleHopper));
		sim.actions.add(new MoveTowardBase(wheels));
		sim.actions.add(new MoveAwayFromBase(wheels));
		sim.actions.add(new MoveRandomly(wheels));
		sim.actions.add(new MoveDownGradient(wheels));
		sim.actions.add(new MoveUpGradient(wheels));
		sim.actions.add(new FlashPosBeacon(beacon));
		sim.actions.add(new FlashNegBeacon(beacon));

		Plan plan = new Plan();

		Rule r1 = new Rule();
		r1.setCondition(sim.getSensor(HaveSample.class), ConditionState.YES);
		r1.setCondition(sim.getSensor(AtBase.class), ConditionState.YES);
		r1.addAction(sim.getAction(DropSample.class));

		Rule r2 = new Rule();
		r2.setCondition(sim.getSensor(HaveSample.class), ConditionState.YES);
		r2.setCondition(sim.getSensor(AtBase.class), ConditionState.NO);
		r2.addAction(sim.getAction(DropBean.class));
		r2.addAction(sim.getAction(MoveTowardBase.class));
		r2.addAction(sim.getAction(FlashNegBeacon.class));

		Rule r3 = new Rule();
		r3.setCondition(sim.getSensor(HaveSample.class), ConditionState.NO);
		r3.setCondition(sim.getSensor(SeeSample.class), ConditionState.YES);
		r3.addAction(sim.getAction(TakeSample.class));

		Rule r4 = new Rule();
		r4.setCondition(sim.getSensor(HaveSample.class), ConditionState.NO);
		r4.setCondition(sim.getSensor(SeeBean.class), ConditionState.YES);
		r4.addAction(sim.getAction(TakeBean.class));
		r4.addAction(sim.getAction(MoveAwayFromBase.class));
		r4.addAction(sim.getAction(FlashNegBeacon.class));

		Rule r5 = new Rule();
		r5.setCondition(sim.getSensor(HaveSample.class), ConditionState.NO);
		r5.setCondition(sim.getSensor(SeeBean.class), ConditionState.NO);
		r5.addAction(sim.getAction(MoveDownGradient.class));
		r5.addAction(sim.getAction(FlashPosBeacon.class));

		plan.addRule(r1, 0);
		plan.addRule(r2, 1);
		plan.addRule(r3, 2);
		plan.addRule(r4, 3);
		plan.addRule(r5, 4);
		sim.setIdealPlan(plan);

		return sim;

	}

	public Simulation() {

	}

}
