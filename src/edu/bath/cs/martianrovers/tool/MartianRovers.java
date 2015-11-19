package edu.bath.cs.martianrovers.tool;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.bath.cs.martianrovers.sim.Plan;
import edu.bath.cs.martianrovers.sim.Rover;
import edu.bath.cs.martianrovers.sim.Rule;
import edu.bath.cs.martianrovers.sim.SimState;
import edu.bath.cs.martianrovers.sim.Simulation;
import edu.bath.cs.martianrovers.sim.SimState.SimListener;
import edu.bath.cs.martianrovers.sim.actions.MoveRandomly;

public class MartianRovers implements SimListener {
	JFrame main;
	// Simulation simulation = simulations[0];
	SimState curState;
	VizPanel vizPanel;
	SimProperties simProperties = new SimProperties();
	JLabel tick;
	JLabel numCollected;
	JLabel simStatus;
	Thread runThread;
	boolean running = false;

	public SimProperties getSimProperties() {
		return simProperties;
	}

	public void setSimProperties(SimProperties simProperties) {
		this.simProperties = simProperties;
		simStatusPanel.setSimProperties(simProperties);
	}

	SimAnalysisPanel simStatusPanel;

	public static class SimProperties {
		int speed = 200;
		int width = 80;
		int height = 80;
		int seed = 1;
		int simWidth = 10;
		int nRovers = 20;
		int nObstacles = 30;
		int maxObstacleSize = 4;
		int nSampleLocs = 10;
		int nSamplesPerLocation = 15;
		int numSteps = 15000;
		int chartInterval = 100;
	}

	private void setSimState(SimState curState) {
		if (curState != null)
			curState.removeListener(this);

		this.curState = curState;
		vizPanel.setSim(curState);
		simStatusPanel.setState(curState);
		curState.addSimListener(this);
	}

	public static void main(String[] args) {
		MartianRovers s = new MartianRovers(args.length > 0);

	}

	@Override
	public void simTicked(SimState ss, Plan plan) {
		tick.setText(ss.getTimeStep() + "/" + simProperties.numSteps);
		numCollected.setText(ss.getNumCollected() + "/" + ss.getTotalSamples());

	}

	Plan makeTestPlan(Simulation sim) {
		Plan plan = new Plan();
		Rule moveRandomly = new Rule();
		moveRandomly.addAction(sim.getAction(MoveRandomly.class));
		plan.addRule(moveRandomly, 0);
		return plan;
	}

	List<Point> getCircularArea(Rectangle clip, Point p, int d) {
		assert (d >= 0);
		List<Point> points = new ArrayList<Point>();
		for (int x = -d; x < (2 * d); x++) {
			for (int y = -d; y < (2 * d); y++) {
				Point np = new Point(p.x + x, p.y + y);
				double dist = p.distance(np);
				if (dist <= (double) d && clip.contains(np)) {
					points.add(np);
				}
			}
		}
		return points;
	}

	boolean pointIsClear(Point p, SimState sim) {
		return !sim.getMothershipLoc().equals(p) && sim.numBeans(p) == 0
				&& sim.numSamples(p) == 0 && !sim.isObstacle(p);
	}

	boolean areaIsClear(List<Point> area, SimState sim) {
		for (Point p : area) {
			if (!pointIsClear(p, sim))
				return false;
		}
		return true;
	}

	SimState makeRandomSimState(SimProperties simp) {
		Random r = new Random(simp.seed);
		SimState state = new SimState(simp.width, simp.height, new Point(
				simp.width / 2, simp.height / 2));
		for (int i = 0; i < simp.nObstacles; i++) {
			Point rpoint;
			List<Point> area;
			int obsSize = r.nextInt(simp.maxObstacleSize);

			do {
				rpoint = new Point(r.nextInt(simp.width), r
						.nextInt(simp.height));
				area = getCircularArea(state.getRect(), rpoint, obsSize);
			} while (!areaIsClear(area, state));

			for (Point p : area) {
				state.setObstacle(p, true);
			}
		}
		for (int i = 0; i < simp.nSampleLocs; i++) {
			Point rpoint;
			do {
				rpoint = new Point(r.nextInt(simp.width), r
						.nextInt(simp.height));
			} while (rpoint.equals(state.getMothershipLoc())
					|| state.isObstacle(rpoint));
			state.setNumSamples(rpoint, simp.nSamplesPerLocation);
		}

		for (int i = 0; i < simp.nRovers; i++) {
			state.addRover(new Rover(state.getMothershipLoc()));
		}
		return state;

	}

	Runnable runSim = new Runnable() {
		@Override
		public void run() {
			while (running) {
				if (running && curState != null && planPanel.getPlan() != null)

					if (curState.getTimeStep() > simProperties.numSteps) {
						running = false;
					} else {
						curState.step(planPanel.getPlan());
					}
				try {
					Thread.sleep(simProperties.speed);
				} catch (InterruptedException e) {

				}
				vizPanel.repaint();
			}
		}
	};

	public JPanel makeAnalsysPane() {
		// JPanel panel = new JPanel();

		return simStatusPanel = new SimAnalysisPanel(this);
	}

	public JPanel makeSimPane() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JToolBar vizToolBar = new JToolBar();

		Image startImage;
		Image resetImage;

		try {
			startImage = loadImage("/edu/bath/cs/martianrovers/player_start.png");
			resetImage = loadImage("/edu/bath/cs/martianrovers/reset.png");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Action start = new AbstractAction("start", new ImageIcon(startImage)) {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (running) {
					running = false;
				} else {
					runThread = new Thread(runSim);
					runThread.start();
					running = true;
				}
			}
		};
		Action reset = new AbstractAction("Reset", new ImageIcon(resetImage)) {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				curState = makeRandomSimState(simProperties);
				setSimState(curState);
				vizPanel.repaint();
			}

		};
		final JSlider speedSlider = new JSlider(10, 500);
		speedSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				simProperties.speed = speedSlider.getMaximum()
						- speedSlider.getValue();

			}
		});
		// speedSlider.setPreferredSize(new Dimension(300, 9));
		speedSlider.setValue(simProperties.speed);
		// Action speedup = new AbstractAction("speed up") {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// simProperties.speed = Math.max(0, simProperties.speed - 20);
		//
		// }
		// };
		// Action slowDown = new AbstractAction("slow down") {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// simProperties.speed = Math.min(500, simProperties.speed + 20);
		// }
		// };

		vizToolBar.add(reset);
		vizToolBar.add(start);
		// vizToolBar.add(speedup);
		// vizToolBar.add(slowDown);
		vizToolBar.add(speedSlider);
		panel.add(vizToolBar, BorderLayout.NORTH);
		vizPanel = new VizPanel();
		vizPanel.setSim(curState);
		vizPanel.resize();

		panel.add(vizPanel, BorderLayout.CENTER);

		JPanel status = new JPanel();

		status.setLayout(new BorderLayout());
		status.setPreferredSize(new Dimension(200, 0));
		tick = new JLabel("Tick");
		numCollected = new JLabel("Collected");
		simStatus = new JLabel();
		simStatus.setText("status");
		status.add(tick, BorderLayout.NORTH);
		status.add(numCollected, BorderLayout.CENTER);
		status.add(simStatus, BorderLayout.SOUTH);
		panel.add(status, BorderLayout.EAST);

		vizPanel.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				synchronized (curState) {
					int x = e.getX();
					int y = e.getY();
					int cx = x / vizPanel.tilesize;
					int cy = y / vizPanel.tilesize;

					if (curState.getRect().contains(cx, cy)) {
						Point px = new Point(cx, cy);
						String status = "";
						status += String.format("b:%d/s:%d/g:%.2f", curState
								.numBeans(px), curState.numSamples(px),
								curState.getGradient()[cx][cy]);
						simStatus.setText(status);
					}
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
		return panel;

	}

	static BufferedImage loadImage(String path) throws IOException {
		InputStream is = VizPanel.class.getResourceAsStream(path);
		if (is == null)
			throw new FileNotFoundException("Unable to find image " + path);
		return ImageIO.read(is);

	}

	PlanPanel planPanel;

	public MartianRovers(final boolean useIdeal) {

		main = new JFrame("Martian Rovers");
		Simulation sims[] = new Simulation[] { Simulation.simpleSimulation(),
				Simulation.simpleSimulationBeans(),
				Simulation.simpleSimulationBeacons() };

		curState = makeRandomSimState(simProperties);
		main.setMinimumSize(new Dimension(800, 600));
		// main.setLayout(new GridLayout());
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add("Plan", planPanel = new PlanPanel(sims[0],
				useIdeal ? sims[0].getIdealPlan() : new Plan()));
		tabbedPane.add("Simulation", makeSimPane());
		tabbedPane.add("Analysis", makeAnalsysPane());

		JMenu menu = new JMenu("Simulation Types");

		for (final Simulation sim : sims) {
			menu.add(new AbstractAction(sim.getName()) {

				@Override
				public void actionPerformed(ActionEvent arg0) {

					if (planPanel.simulation != sim) {
						planPanel.setSimulation(sim);
						if (useIdeal) {
							planPanel.setPlan(sim.getIdealPlan());
						}
					}

				}
			});
		}
		JMenuBar mb = new JMenuBar();
		mb.add(menu);
		main.setJMenuBar(mb);

		main.add(tabbedPane);
		main.pack();
		main.setVisible(true);

		main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		main.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				System.exit(0);
			}

		});

		vizPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON2) {
					curState = makeRandomSimState(simProperties);
					vizPanel.setSim(curState);
					vizPanel.repaint();
				} else if (e.getButton() == MouseEvent.BUTTON1) {
					if (planPanel.getPlan() != null) {
						curState.step(planPanel.getPlan());
						vizPanel.repaint();
					}
				}
			}
		});

		setSimState(curState);

	}
}
