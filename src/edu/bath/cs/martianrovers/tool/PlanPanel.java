package edu.bath.cs.martianrovers.tool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.omg.CORBA.TCKind;

import com.lowagie.text.Font;

import edu.bath.cs.martianrovers.sim.Action;
import edu.bath.cs.martianrovers.sim.ActionGroup;
import edu.bath.cs.martianrovers.sim.Plan;
import edu.bath.cs.martianrovers.sim.Rule;
import edu.bath.cs.martianrovers.sim.Sensor;
import edu.bath.cs.martianrovers.sim.Simulation;
import edu.bath.cs.martianrovers.sim.Condition.ConditionState;

public class PlanPanel extends JPanel {

	Plan plan;

	public Plan getPlan() {
		return plan;
	}

	public void setPlan(Plan plan) {
		this.plan = plan;
		tableModel.fireTableDataChanged();
	}

	Simulation simulation;

	public Simulation getSimulation() {
		return simulation;
	}

	public void setSimulation(Simulation simulation) {
		this.simulation = simulation;

		setupTable();

	}

	JToolBar toolbar;

	JTable rulesTable;

	class PlanTableModel extends AbstractTableModel {
		@Override
		public int getColumnCount() {
			return simulation.getSensors().size()
					+ simulation.getActionGroups().size() + 1;
		}

		@Override
		public int getRowCount() {
			return plan.getRules().size();
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			// TODO Auto-generated method stub
			if (columnIndex != 0)
				return true;
			return false;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

			Rule r = plan.getRules().get(rowIndex);
			if (columnIndex > 0
					&& columnIndex <= simulation.getSensors().size()) {
				int sidx = columnIndex - 1;
				Sensor sens = simulation.getSensors().get(sidx);
				ConditionState cs = (ConditionState) aValue;
				r.setCondition(sens, cs);

			} else if (columnIndex > (simulation.getSensors().size())) {
				int agidx = columnIndex - (simulation.getSensors().size() + 1);
				ActionGroup ag = simulation.getActionGroups().get(agidx);
				Action act = (Action) aValue;
				r.setActionForGroup(ag, act);
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Rule r = plan.getRules().get(rowIndex);
			int nsensors = simulation.getSensors().size();
			if (columnIndex == 0) {
				return rowIndex;
			} else if (columnIndex < nsensors + 1) {
				Sensor s = simulation.getSensors().get(columnIndex - 1);
				return r.getState(s);
			} else {
				int agidx = columnIndex - (nsensors + 1);
				ActionGroup ag = simulation.getActionGroups().get(agidx);
				Action a = r.getActionForGroup(ag);
				return a;
			}
		}
	}

	PlanTableModel tableModel;

	public void setupTable() {

		tableModel.fireTableStructureChanged();
		TableColumnModel tcm = rulesTable.getColumnModel();
		TableColumn idxtc = tcm.getColumn(0);
		idxtc.setHeaderValue("#");
		idxtc.setPreferredWidth(10);

		for (int i = 1; i <= simulation.getSensors().size(); i++) {

			Sensor sens = simulation.getSensors().get(i - 1);
			TableColumn tc = tcm.getColumn(i);
			tc.setHeaderValue(sens.getShortName());
			// tc.setCellRenderer(new TableCellRenderer() {
			// @Override
			// public Component getTableCellRendererComponent(JTable table,
			// Object value, boolean isSelected, boolean hasFocus,
			// int row, int column) {
			// final int sensorIdx = column - 1;
			// Sensor sensor = sim.getSensors().get(sensorIdx);
			// Rule rule = plan.getRules().get(row);
			// return new SensorStateCheckbox(rule, sensor);
			// }
			// });
			JComboBox combo = new JComboBox(ConditionState.values());
			tc.setCellEditor(new DefaultCellEditor(combo));
			DefaultTableCellRenderer render = new DefaultTableCellRenderer();
			tc.setPreferredWidth(60);

			render.setToolTipText(sens.getName());
			render.setBackground(new Color(0.8f, 0.93f, 0.93f));
			tc.setCellRenderer(render);

		}

		for (int ai = 0; ai < simulation.getActionGroups().size(); ai++) {
			TableColumn tc = tcm.getColumn(simulation.getSensors().size() + 1
					+ ai);

			List<Action> actions = new ArrayList<Action>();
			actions.add(null);
			ActionGroup ag = simulation.getActionGroups().get(ai);
			tc.setHeaderValue(ag.getName());
			for (Action a : simulation.getActions()) {
				if (a.getGroup().equals(ag)) {
					actions.add(a);
				}
			}
			JComboBox combo = new JComboBox(actions.toArray(new Action[] {}));
			tc.setCellEditor(new DefaultCellEditor(combo));
			DefaultTableCellRenderer render = new DefaultTableCellRenderer();

			tc.setPreferredWidth(200);
			if (ai == 0) {
				render.setBorder(new LineBorder(Color.gray, 3));
			}
			tc.setCellRenderer(render);
		}

	}

	public PlanPanel(final Simulation sims, Plan initPlan) {
		this.simulation = sims;
		this.plan = initPlan;
		rulesTable = new JTable(tableModel = new PlanTableModel());
		rulesTable.setFillsViewportHeight(true);
		setLayout(new BorderLayout());
		// setLayout(new FlowLayout());

		Image addRule;
		Image deleteRule;
		Image upRule;
		Image dnRule;
		try {
			addRule = MartianRovers
					.loadImage("/edu/bath/cs/martianrovers/add.png");
			deleteRule = MartianRovers
					.loadImage("/edu/bath/cs/martianrovers/remove.png");
			upRule = MartianRovers
					.loadImage("/edu/bath/cs/martianrovers/up.png");
			dnRule = MartianRovers
					.loadImage("/edu/bath/cs/martianrovers/down.png");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		setupTable();
		toolbar = new JToolBar();

		AbstractAction addRuleAction = new AbstractAction("Add Rule",
				new ImageIcon(addRule)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = 0;
				if (rulesTable.getSelectedRow() > 0) {
					i = rulesTable.getSelectedRow();
				} else {
					i = 0;
				}
				plan.addRule(new Rule(), i);
				tableModel.fireTableRowsInserted(i, i);

			}
		};

		AbstractAction delRuleAction = new AbstractAction("Delete Rule",
				new ImageIcon(deleteRule)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (rulesTable.getSelectedRow() >= 0) {
					int i = rulesTable.getSelectedRow();
					plan.removeRule(rulesTable.getSelectedRow());
					tableModel.fireTableDataChanged();
				}
			}

		};

		AbstractAction upRuleAction = new AbstractAction("Up Rule",
				new ImageIcon(upRule)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = rulesTable.getSelectedRow();
				if (i > 0 && i < plan.getRules().size()) {
					Rule r = plan.getRules().get(i);
					plan.removeRule(i);
					plan.addRule(r, i - 1);

					tableModel.fireTableRowsUpdated(i - 1, i);

				}

			}
		};

		AbstractAction dnRuleAction = new AbstractAction("Down Rule",
				new ImageIcon(dnRule)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = rulesTable.getSelectedRow();
				if (i >= 0 && i < plan.getRules().size() - 1) {
					Rule r = plan.getRules().get(i);
					plan.removeRule(i);
					plan.addRule(r, i + 1);

					tableModel.fireTableRowsUpdated(i, i);
					tableModel.fireTableRowsInserted(i + 1, i + 1);

				}

			}
		};

		toolbar.add(addRuleAction);
		toolbar.add(delRuleAction);
		toolbar.add(upRuleAction);
		toolbar.add(dnRuleAction);
		 add(toolbar, BorderLayout.PAGE_START);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(rulesTable.getTableHeader(), BorderLayout.PAGE_START);
		panel.add(rulesTable, BorderLayout.CENTER);
		add(panel, BorderLayout.CENTER);

	}
}
