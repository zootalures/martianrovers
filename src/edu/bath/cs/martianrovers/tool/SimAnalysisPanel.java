package edu.bath.cs.martianrovers.tool;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.jfree.ui.RectangleInsets;

import edu.bath.cs.martianrovers.sim.Plan;
import edu.bath.cs.martianrovers.sim.SimState;
import edu.bath.cs.martianrovers.sim.SimState.SimListener;
import edu.bath.cs.martianrovers.tool.MartianRovers.SimProperties;

public class SimAnalysisPanel extends JPanel implements SimListener {

	int planIdx = 0;

	YIntervalSeriesCollection dataset = new YIntervalSeriesCollection();

	JFreeChart colChart;
	SimProperties simProperties;

	static class IntervalData {
		YIntervalSeries series;
		double means[];
		double meansqs[];
		int nruns;
	}

	Map<Integer, IntervalData> planSeries = new HashMap<Integer, IntervalData>();

	public void reset() {
		dataset.removeAllSeries();
		planSeries.clear();
	}

	JProgressBar progress;

	boolean analysisRunning;

	/**
	 * Runs an analysis on a given state
	 * 
	 * @param plan
	 * @param state
	 */
	public void runAnalyse(final Plan plan, final int numSteps,
			final SimStateFactory factory) {

		runAnalysis.setEnabled(false);
		resetGraph.setEnabled(false);
		Runnable runner = new Runnable() {
			@Override
			public void run() {
				analysisRunning = true;
				stopAnalysis.setEnabled(true);
				progress.setMinimum(0);
				progress.setMaximum(numSteps * simProperties.numSteps);
				TOP: for (int i = 0; i < numSteps; i++) {
					SimState state = factory.createSimState();
					state.addSimListener(SimAnalysisPanel.this);
					while (state.getTimeStep() < simProperties.numSteps) {
						state.step(plan);
						progress.setValue(i * simProperties.numSteps + state.getTimeStep());
						if (!analysisRunning) {
							break TOP;
						}
					}
					state.removeListener(SimAnalysisPanel.this);

				}

				analysisRunning = false;
				runAnalysis.setEnabled(true);
				resetGraph.setEnabled(true);
				stopAnalysis.setEnabled(false);
				progress.setValue(0);
				planIdx++;
			}
		};

		Thread thread = new Thread(runner);
		thread.start();

	}

	@Override
	public void simTicked(SimState ss, Plan plan) {
		final int tick = ss.getTimeStep();

		IntervalData id = planSeries.get(planIdx);
		if (id == null) {
			planSeries.put(planIdx, id = new IntervalData());
			id.series = new YIntervalSeries("plan " + planIdx);
			id.nruns = 0;
			id.means = new double[simProperties.numSteps
					/ simProperties.chartInterval];
			id.meansqs = new double[simProperties.numSteps
					/ simProperties.chartInterval];
			dataset.addSeries(id.series);

		}

		if (tick == 0) {
			id.nruns++;
			// dataset.removeAllSeries();
			//
			// chartSeries = new TimeSeries("collected");
			// chartSeries.setMaximumItemCount(simProperties.numSteps / 10);
			// dataset.addSeries(chartSeries);
			// colChart.fireChartChanged();
		} else {

			if (tick % simProperties.chartInterval == 0) {
				int entryIndex = (tick / simProperties.chartInterval) - 1;
				double val = (double) ss.getNumCollected();

				double delta = val - id.means[entryIndex];
				id.means[entryIndex] += delta / (double) id.nruns;
				id.meansqs[entryIndex] += delta * (val - id.means[entryIndex]);
				final double variance = Math.sqrt(id.meansqs[entryIndex]
						/ (double) id.nruns);

				final YIntervalSeries series = id.series;
				final boolean remove = id.nruns > 1;
				final double mean = id.means[entryIndex];

				try {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (remove) {
								series.remove((double) tick);
							}
							series.add((double) tick, (double) mean, mean
									- variance, mean + variance);

						}
					});
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				// chartSeries.fireSeriesChanged();
			}

		}
	}

	public void setState(SimState state) {
	}

	Action runAnalysis, resetGraph,stopAnalysis;

	public SimAnalysisPanel(final MartianRovers rovers) {
		setLayout(new BorderLayout());

		this.simProperties = rovers.simProperties;
		//
		// dataset.addSeries(chartSeries);
		colChart = ChartFactory.createXYLineChart(null, "Time", "Collcted",
				dataset, PlotOrientation.VERTICAL, true, false, false);

		// colChart.removeLegend();
		XYPlot plot = colChart.getXYPlot();
		ValueAxis axis = plot.getDomainAxis();
		axis.setAutoRange(true);
		axis.setRange(0, simProperties.numSteps);
		axis = plot.getRangeAxis();
		axis.setRange(0.0, simProperties.nSampleLocs
				* simProperties.nSamplesPerLocation);
		plot.setInsets(new RectangleInsets(5D, 5D, 5D, 20D));
		DeviationRenderer deviationrenderer = new DeviationRenderer(true, false);
		deviationrenderer.setSeriesStroke(0, new BasicStroke(3F, 1, 1));
		deviationrenderer.setSeriesStroke(0, new BasicStroke(3F, 1, 1));
		deviationrenderer.setSeriesStroke(1, new BasicStroke(3F, 1, 1));
		deviationrenderer.setSeriesFillPaint(0, new Color(255, 200, 200));
		deviationrenderer.setSeriesFillPaint(1, new Color(200, 200, 255));
		plot.setRenderer(deviationrenderer);
		ChartPanel chartPanel = new ChartPanel(colChart);
		chartPanel.setSize(200, 200);
		chartPanel.setMaximumSize(new Dimension(300, 200));

		JToolBar toolBar = new JToolBar();

		resetGraph = new AbstractAction("Reset Graphs") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				reset();
			}
		};

		runAnalysis = new AbstractAction("Analyse Plan") {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				runAnalyse(rovers.planPanel.getPlan(), 8,
						new SimStateFactory() {

							@Override
							public SimState createSimState() {
								return rovers
										.makeRandomSimState(rovers.simProperties);
							}
						});
			}
		};
		
		stopAnalysis = new AbstractAction("Stop Analysis") {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				analysisRunning = false;	
			}
		};
		
		stopAnalysis.setEnabled(false);
		progress = new JProgressBar();

		toolBar.add(resetGraph);
		toolBar.add(runAnalysis);
		toolBar.add(stopAnalysis);
		toolBar.add(progress);
		add(toolBar, BorderLayout.NORTH);
		add(chartPanel, BorderLayout.CENTER);

	}

	public void setSimProperties(SimProperties simProperties) {
		this.simProperties = simProperties;
	}
}
