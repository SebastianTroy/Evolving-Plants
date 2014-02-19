package evolvingPlants.simulation;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import evolvingPlants.Main;

public class Simulation
	{
		// Globally fixed variables
		private static final Color skyBlue = new Color(150, 150, 255);

		// User adjustable variables
		public double geneCompatability = 0.1; // lower is less compatible
		public boolean showLighting = false;

		//
		public boolean paused = false;
		public double secondsPassed = 0;
		public double secondsBetweenTicks = 1;
		public boolean reset = false;

		// Simulation variables
		public double simWidth, simX = 0;
		public LightMap lightMap;
		BufferedImage lightImage;

		// plants added by user
		public Genome currentGenes;
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
				if (!paused)
					this.secondsPassed += secondsPassed;

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
						LightFilter newFilter = new LightFilter((int) -simX + 5, 250, (int) Main.simWindow.filterWidthSlider.getValue(), (int) Main.simWindow.filterOpacitySlider.getValue());
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

				while (this.secondsPassed >= secondsBetweenTicks)
					{
						this.secondsPassed -= secondsBetweenTicks;

						// ----------------------------------
						/* REALLY SUPER SLOW */
						if (showLighting)
							updateLighting();
						/* REALLY SUPER SLOW */
						// ----------------------------------

						// Add new seedlings to Array
						for (Point p : plantsAddedByUser)
							plants.add(new Plant(new Genome(currentGenes, false), (int) p.getX()));
						plantsAddedByUser.clear();
						// Add new seedlings to Array
						for (Plant p : plantsToAdd)
							plants.add(p);
						plantsToAdd.clear();
						// Process plants and remove dead ones
						for (int p = 0; p < plants.size(); p++)
							{
								Plant plant = plants.get(p);
								plant.tick();
								if (!plant.alive)
									{
										plants.remove(p);
										p--;
									}
							}
					}
			}

		public void render(Graphics2D g)
			{
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
			}

		public final void addPlant(Plant newPlant)
			{
				plants.add(newPlant);
			}

		public final void addShadow(double nodeX, double nodeY, int leafSize, int leafTransparency)
			{
				int x = (int) nodeX - (leafSize / 2);
				lightMap.addShadow(x, (int) nodeY, leafSize, leafTransparency);
			}

		public final void removeShadow(double nodeX, double nodeY, int leafSize, int leafTransparency)
			{
				int x = (int) nodeX - (leafSize / 2);
				lightMap.removeShadow(x, (int) nodeY, leafSize, leafTransparency);
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
								boolean plantSelected = false;
								for (Plant plant : plants)
									if (!plantSelected && plant.contains(point))
										{
											plant.selected = true;
											plantSelected = true;
										}
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