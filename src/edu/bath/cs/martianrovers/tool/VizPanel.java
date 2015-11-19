package edu.bath.cs.martianrovers.tool;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JPanel;

import edu.bath.cs.martianrovers.sim.Rover;
import edu.bath.cs.martianrovers.sim.SimState;

public class VizPanel extends JPanel {

	SimState sim;

	int tilesize = 8;

	TexturePaint backgroundTex;

	Image rocks[];
	Image mothership;
	Image rover;
	Image roverFull;
	BufferedImage backgroundPicture;

	public VizPanel() {
		setDoubleBuffered(true);
		try {
			BufferedImage moontile = MartianRovers
					.loadImage("/edu/bath/cs/martianrovers/moontile.png");
			backgroundTex = new TexturePaint(moontile, new Rectangle2D.Double(
					0.0, 0.0, moontile.getWidth(), moontile.getHeight()));

			List<Image> rocklist = new ArrayList<Image>();
			for (int i = 0; i < 10; i++) {
				try {
					Image img = MartianRovers
							.loadImage("/edu/bath/cs/martianrovers/moonrock-"
									+ i + ".png");
					rocklist.add(img);
				} catch (Exception e) {
					break;
				}
			}

			if (rocklist.isEmpty())
				throw new RuntimeException("No rock images found");

			rocks = rocklist.toArray(new Image[0]);
			mothership = MartianRovers
					.loadImage("/edu/bath/cs/martianrovers/mothership.png");
			rover = MartianRovers
					.loadImage("/edu/bath/cs/martianrovers/rover.png");
			roverFull = MartianRovers
					.loadImage("/edu/bath/cs/martianrovers/rover-full.png");

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		assert (rocks.length > 0);
		assert (backgroundTex != null);
		assert (rover != null);
		assert (mothership != null);
	}

	public int getTilesize() {
		return tilesize;
	}

	public void setTilesize(int tilesize) {
		this.tilesize = tilesize;
		resize();
	}

	public void resize() {
		if (sim != null) {
			setMinimumSize(new Dimension(sim.getWidth() * tilesize, sim
					.getHeight()
					* tilesize));
		}
	}

	public void drawBackground(Graphics2D g2dtarget) {
		if (sim != null && backgroundPicture == null) {
			backgroundPicture = new BufferedImage(sim.getWidth() * tilesize,
					sim.getHeight() * tilesize, BufferedImage.TYPE_4BYTE_ABGR);

			Graphics2D g2d = (Graphics2D) backgroundPicture.getGraphics();
			g2d.setPaint(backgroundTex);
			g2d.clearRect(0, 0, getWidth(), getHeight());
			g2d.fillRect(0, 0, sim.getWidth() * tilesize, sim.getHeight()
					* tilesize);

			Point topCorner = new Point();
			Point pt = new Point();

			for (Point p : sim.getObstacles().keySet()) {
				topCorner.x = p.x * tilesize;
				topCorner.y = p.y * tilesize;
				g2d.drawImage(rocks[p.hashCode() % rocks.length], topCorner.x,
						topCorner.y, tilesize, tilesize, null);

			}
		}

		g2dtarget.drawImage(backgroundPicture, 0, 0, backgroundPicture
				.getWidth(), backgroundPicture.getHeight(), null);

	}

	public SimState getSim() {
		return sim;
	}

	public void setSim(SimState sim) {
		this.sim = sim;
		resize();
	}

	boolean displayGradients = true;

	BufferedImage gradient;

	protected void paintComponent(java.awt.Graphics graphics) {
		Graphics2D g2d = (Graphics2D) graphics;
		if (sim != null)
			synchronized (sim) {
				drawBackground(g2d);
				Point topCorner = new Point();
				for (Entry<Point, Integer> e : sim.getBeans().entrySet()) {
					topCorner.x = e.getKey().x * tilesize;
					topCorner.y = e.getKey().y * tilesize;
					if (e.getValue() > 0) {
						g2d.setColor(new Color(1.0f, 0f, 0f, Math.min(1.0f,
								0.1f + (0.1f * e.getValue()))));
						g2d.fillRect(topCorner.x, topCorner.y, tilesize,
								tilesize);
					}
				}

				for (Entry<Point, Integer> e : sim.getSamples().entrySet()) {
					topCorner.x = e.getKey().x * tilesize;
					topCorner.y = e.getKey().y * tilesize;
					if (e.getValue() > 0) {
						g2d.setColor(new Color(0f, 1f, 0f, Math.min(1.0f,
								0.2f + (0.2f * e.getValue()))));
						g2d.fillRect(topCorner.x, topCorner.y, tilesize,
								tilesize);
					}
				}

				if (displayGradients) {
					if (gradient == null) {
						gradient = new BufferedImage(sim.getWidth(), sim
								.getHeight(), BufferedImage.TYPE_INT_ARGB);
					}
					Graphics2D gg2d = gradient.createGraphics();
					gg2d.setBackground(new Color(0f, 0f, 0f, 0f));
					gg2d.clearRect(0, 0, sim.getWidth(), sim.getHeight());
					for (int x = 0; x < sim.getWidth(); x++)
						for (int y = 0; y < sim.getHeight(); y++) {
							double val = sim.getGradient()[x][y];
							if (val < 0) {
								gg2d
										.setColor(new Color(0.0f, 1.0f, 1.0f,
												Math.max(0.0f, Math.min(1.0f,
														(float) -val))));
							} else {
								gg2d
										.setColor(new Color(0.0f, 0.0f, 1.0f,
												Math.max(0.0f, Math.min(1.0f,
														(float) val))));

							}
							gg2d.drawLine(x, y, x + 1, y + 1);
						}

					g2d.drawImage(gradient, 0, 0, sim.getWidth() * tilesize,
							sim.getHeight() * tilesize, null);
				}

				for (Rover r : sim.getRovers()) {
					g2d.drawImage(r.isHaveSample() ? roverFull : rover, r
							.getLoc().x
							* tilesize, r.getLoc().y * tilesize, tilesize,
							tilesize, null);
				}

				g2d.drawImage(mothership, sim.getMothershipLoc().x * tilesize,
						sim.getMothershipLoc().y * tilesize, tilesize,
						tilesize, null);

			}
	}
}
