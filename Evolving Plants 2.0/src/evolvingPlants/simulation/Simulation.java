package evolvingPlants.simulation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import tools.ColTools;

import evolvingPlants.Main;

public class Simulation
	{
		// Globally fixed variables
		private static final Color skyBlue = new Color(150, 150, 255);
		private static final Color dottedLineColour = new Color(0, 0, 0, 75);
		private static final Stroke dottedLineStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 6, 12 }, 0);

		// User adjustable variables
		public double geneCompatability = 0.1; // lower is less compatible
		public boolean showLighting = false;

		//
		private boolean paused = false;
		private double oldWarpSpeed;// Keep track of warp speed when pausing
		private double timePassed = 0;
		private boolean rendered = false;
		public boolean reset = false;

		// Simulation variables
		public double simWidth, simX = 0;
		public LightMap lightMap;
		BufferedImage lightImage;

		// plants added by user
		public Genes currentGenes;
		public ArrayList<Point> plantsAddedByUser = new ArrayList<Point>(5);

		// Light Filters
		public boolean addFilter = false;
		private LightFilter filterBeingMoved = null;
		private ArrayList<LightFilter> filters = new ArrayList<LightFilter>();

		// Seeds and Plants in the simulation
		ArrayList<Plant> plantsToAdd = new ArrayList<Plant>(40);
		ArrayList<Plant> plants = new ArrayList<Plant>(40);

		public Simulation(int width)
			{
				simWidth = width;
				lightMap = new LightMap(width, 550);
				lightImage = new BufferedImage(800, 550, BufferedImage.TYPE_INT_RGB);
			}

		public void tick(double secondsPassed)
			{
				if (reset)
					{
						plantsAddedByUser.clear();
						plantsToAdd.clear();
						plants.clear();
						filters.clear();
						lightMap = new LightMap((int) simWidth, 550);
						lightImage = new BufferedImage(800, 550, BufferedImage.TYPE_INT_RGB);

						reset = false;
					}

				if (addFilter)
					{
						Color filterColour = ColTools.checkColour((int) Main.simWindow.filterRedLightSlider.getValue(), (int) Main.simWindow.filterGreenLightSlider.getValue(),
								(int) Main.simWindow.filterBlueLightSlider.getValue());
						LightFilter newFilter = new LightFilter((int) -simX + 5, 250, (int) Main.simWindow.filterWidthSlider.getValue(), filterColour);
						filters.add(newFilter);
						addFilter = false;

						lightMap.addShadow(newFilter.x, newFilter.y, newFilter.width, newFilter.shadowColour);
						if (showLighting)
							updateLighting();
					}

				for (int i = 0; i < filters.size();)
					{
						LightFilter f = filters.get(i);

						if (f.movedTo != null)
							{
								lightMap.removeShadow(f.x, f.y, f.width, f.shadowColour);
								f.moved();
								lightMap.addShadow(f.x, f.y, f.width, f.shadowColour);

								if (showLighting)
									updateLighting();
							}
						if (!f.exists)
							{
								lightMap.removeShadow(f.x, f.y, f.width, f.shadowColour);
								filters.remove(i);
							}
						else
							i++;
					}

				timePassed += secondsPassed;
				if (timePassed > 0.03)
					{
						timePassed = 0;
						rendered = false;
					}

				secondsPassed *= Main.simWindow.playbackSpeed.getValue();

				// Cap secondsPassed to stop strange things happening at low
				// frame-rates
				if (secondsPassed > 0.2)
					secondsPassed = 0.2;

				for (int i = 0; i < Main.simWindow.warpSpeedSlider.getValue(); i++)
					{
						//----------------------------------
						/*REALLY SUPER SLOW*/
						if (showLighting)
							updateLighting();
						/*REALLY SUPER SLOW*/
						//----------------------------------
						
						// Add new seedlings to Array
						for (Point p : plantsAddedByUser)
							plants.add(new Plant(currentGenes, (int) p.getX()));
						plantsAddedByUser.clear();
						// Add new seedlings to Array
						for (Plant p : plantsToAdd)
							plants.add(new Plant(p, p.plantX));
						plantsToAdd.clear();
						// Remove dead plants
						for (int p = 0; p < plants.size(); p++)
							if (plants.get(p).alive == false)
								plants.remove(p);
						for (Plant p : plants)
							p.tick(secondsPassed);
					}
			}

		public void render(Graphics2D g)
			{
				if (rendered)
					{
						g.setColor(Color.CYAN);
						g.fillRect(0, 0, 200, Main.canvasHeight);
						g.fillRect(1000, 0, 200, Main.canvasHeight);
						return;
					}

				int simX = (int) this.simX;
				g.setColor(skyBlue);
				g.fillRect(200, 0, 800, Plant.plantY);
				if (showLighting)
					g.drawImage(lightImage, 200, 0, Main.simWindow.getObserver());
				g.setColor(Color.GREEN);
				g.fillRect(200, 550, 800, 50);
				for (Plant p : plants)
					p.render(g, simX + 200);
				for (LightFilter f : filters)
					f.render(g, simX + 200);

				g.setColor(Color.CYAN);
				g.fillRect(0, 0, 200, Main.canvasHeight);
				g.fillRect(1000, 0, 200, Main.canvasHeight);

				rendered = true;
			}

		public final void unpause()
			{
				if (!paused)
					return;

				paused = false;

				Main.simWindow.warpSpeedSlider.setValue(oldWarpSpeed);
			}

		public final void pause()
			{
				if (paused)
					return;

				paused = true;

				oldWarpSpeed = Main.simWindow.warpSpeedSlider.getValue();
				Main.simWindow.warpSpeedSlider.setValue(0);
			}

		public final void addPlant(Plant newPlant)
			{
					plants.add(newPlant);
			}

		public final void addShadow(double nodeX, double nodeY, int leafSize, Color leafColour)
			{
				int x = (int) nodeX - (leafSize / 2);
				lightMap.addShadow(x, (int) nodeY, leafSize, leafColour);
			}

		public final void removeShadow(double nodeX, double nodeY, int leafSize, Color leafColour)
			{
				int x = (int) nodeX - (leafSize / 2);
				lightMap.removeShadow(x, (int) nodeY, leafSize, leafColour);
			}

		public double photosynthesizeAt(double x, int y, Color leafColour, Color shadowColour)
			{
				double energyGained = 0;
				// minus shadow to stop leaf shading itself
				int[] availableLight = lightMap.getLightMinusShadowAt((int) x, y, shadowColour);
				energyGained += Math.max(0, (availableLight[0] - leafColour.getRed()));
				energyGained += Math.max(0, (availableLight[1] - leafColour.getGreen()));
				energyGained += Math.max(0, (availableLight[2] - leafColour.getBlue()));
				/*
				 * The leaf colour represents the light a leaf DOESN'T absorb.
				 */
				/*
				 * If energy gained is over 255 the extra energy is subtracted
				 * from the energy gained. In nature photosynthesis is inhibited
				 * by too much light and dark adapted species are actually at a
				 * large disadvantage in normal conditions.
				 */
				if (energyGained > 200)
					energyGained = Math.max(0, 200 - (energyGained - 200));

				return energyGained;
			}

		public void updateLighting()
			{
				lightImage = lightMap.getLightImage(lightImage, (int) -simX);
			}

		public void mousePressed(MouseEvent e)
			{
				if (e.getY() > 70 && e.getY() < 550 && e.getX() > 200 && e.getX() < 1000)
					{
						Point point = e.getPoint();
						point.x -= simX + 200;

						if (Main.simWindow.currentCursor == Main.simWindow.plantSeedCursor)
							{
								plantsAddedByUser.add(point);
							}
						else if (Main.simWindow.currentCursor == Cursor.getDefaultCursor())
							{
								for (Plant plant : plants)
									if (plant.contains(point))
										plant.selected = true;
									else if (plant.selected)
										plant.selected = false;
							}
						else if (Main.simWindow.currentCursor == Main.simWindow.getGenesCursor)
							{
								for (Plant plant : plants)
									if (plant.contains(point))
										{
											currentGenes = plant.getGenesCopy();
										}
							}
						else if (Main.simWindow.currentCursor == Main.simWindow.killPlantCursor)
							{
								for (int i = 0; i < plants.size(); i++)
									if (plants.get(i).contains(point))
										plants.get(i).kill();
							}
						else if (Main.simWindow.currentCursor == Main.simWindow.moveFilterCursor)
							{
								Point adjustedPoint = new Point((int) (e.getX() + -simX - 200), e.getY());

								for (LightFilter f : filters)
									if (f.containsPoint(adjustedPoint))
										filterBeingMoved = f;
							}
						else if (Main.simWindow.currentCursor == Main.simWindow.deleteFilterCursor)
							{
								Point adjustedPoint = new Point((int) (e.getX() + -simX - 200), e.getY());
								for (LightFilter f : filters)
									if (f.containsPoint(adjustedPoint))
										f.exists = false;
							}
					}
			}

		public void mouseDragged(MouseEvent e)
			{
				Point point = new Point((int) (e.getX() + -simX - 200), e.getY());

				if (Main.simWindow.currentCursor == Main.simWindow.killPlantCursor)
					{
						for (int i = 0; i < plants.size(); i++)
							if (plants.get(i).contains(point))
								plants.get(i).kill();
					}

				if (filterBeingMoved != null)
					filterBeingMoved.moving(point);
			}

		public void mouseReleased(MouseEvent e)
			{
				if (filterBeingMoved != null)
					{
						filterBeingMoved.moving(new Point((int) (e.getX() + -simX - 200), e.getY()));
					}

				filterBeingMoved = null;
			}
	}