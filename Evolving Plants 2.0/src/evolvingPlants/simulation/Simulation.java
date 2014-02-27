package evolvingPlants.simulation;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

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
		private LinkedList<LightFilter> filters = new LinkedList<LightFilter>();
		private Iterator<LightFilter> filterIterator;

		// Plants in the simulation
		LinkedList<Plant> plantsToAdd = new LinkedList<Plant>();
		LinkedList<Plant> plants = new LinkedList<Plant>();
		private Iterator<Plant> plantIterator;

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
						plants.clear();
						filters.clear();
						lightMap = new LightMap((int) simWidth, 550);
						lightImage = new BufferedImage(800, 550, BufferedImage.TYPE_INT_RGB);

						reset = false;
					}

				if (addFilter)
					{
						LightFilter newFilter = new LightFilter((int) -simX + 5, 250, (int) Main.simWindow.filterWidthSlider.getValue(), (int) Main.simWindow.filterOpacitySlider.getValue());
						filters.addFirst(newFilter);
						addFilter = false;

						lightMap.addShadow(newFilter.x, newFilter.y, newFilter.width, newFilter.shadowColour);
						if (showLighting)
							updateLighting();
					}

				filterIterator = filters.iterator();
				while (filterIterator.hasNext())
					{
						LightFilter f = filterIterator.next();

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
								filterIterator.remove();
							}
					}

				// Do stuff that can happen more than once a frame
				while (this.secondsPassed >= secondsBetweenTicks)
					{
						// Add new plants to our array
						plants.addAll(plantsToAdd);
						plantsToAdd.clear();

						// Process plants and remove dead ones
						plantIterator = plants.iterator();
						Plant plant;
						while (plantIterator.hasNext())
							{
								plant = plantIterator.next();
								plant.tick();
								if (!plant.alive)
									plantIterator.remove();
							}

						this.secondsPassed -= secondsBetweenTicks;
					}
				// ----------------------------------
				/* ##### REALLY SUPER SLOW ##### */
				if (showLighting)
					updateLighting(); // call as little as possible
				/* ##### REALLY SUPER SLOW ##### */
				// ----------------------------------
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

				// Draw plants
				plantIterator = plants.iterator();
				while (plantIterator.hasNext())
					plantIterator.next().render(g, simX + 200);

				// Draw filters
				filterIterator = filters.iterator();
				while (filterIterator.hasNext())
					filterIterator.next().render(g, simX + 200);

				g.setColor(Color.CYAN);
				g.fillRect(0, 0, 200, Main.canvasHeight);
				g.fillRect(1000, 0, 200, Main.canvasHeight);
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
								plantsToAdd.addFirst(new Plant(new Genome(currentGenes, false), point.x));
							}
						else if (Main.simWindow.currentCursor == Cursor.getDefaultCursor())
							{
								plantIterator = plants.iterator();

								boolean plantSelected = false;

								Plant plant;
								while (plantIterator.hasNext())
									{
										plant = plantIterator.next();
										if (!plantSelected && plant.contains(point))
											{
												plant.selected = true;
												plantSelected = true;
											}
										else if (plant.selected)
											plant.selected = false;
									}							}
						else if (Main.simWindow.currentCursor == Main.simWindow.getGenesCursor)
							{
								plantIterator = plants.iterator();

								Plant plant;
								while (plantIterator.hasNext())
									{
										plant = plantIterator.next();
										if (plant.contains(point))
											{
												currentGenes = plant.getGenesCopy();
												if (Main.DEBUG)
													currentGenes.printGenome();
											}
									}
							}
						else if (Main.simWindow.currentCursor == Main.simWindow.killPlantCursor)
							{
								plantIterator = plants.iterator();

								Plant plant;
								while (plantIterator.hasNext())
									{
										plant = plantIterator.next();
										if (plant.contains(point))
											plant.kill();
									}
							}
						else if (Main.simWindow.currentCursor == Main.simWindow.moveFilterCursor)
							{
								Point adjustedPoint = new Point((int) (e.getX() + -simX - 200), e.getY());

								Iterator<LightFilter> tempIter = filters.iterator();
								LightFilter f;
								while (tempIter.hasNext())
									{
										f = tempIter.next();
										if (f.containsPoint(adjustedPoint))
											filterBeingMoved = f;
									}
							}
						else if (Main.simWindow.currentCursor == Main.simWindow.deleteFilterCursor)
							{
								Point adjustedPoint = new Point((int) (e.getX() + -simX - 200), e.getY());
								Iterator<LightFilter> tempIter = filters.iterator();
								while (tempIter.hasNext())
									if (tempIter.next().containsPoint(adjustedPoint))
										tempIter.remove();
							}
					}
			}

		public void mouseDragged(MouseEvent e)
			{
				Point point = new Point((int) (e.getX() + -simX - 200), e.getY());

				if (Main.simWindow.currentCursor == Main.simWindow.killPlantCursor)
					{
						plantIterator = plants.iterator();

						Plant plant;
						while (plantIterator.hasNext())
							{
								plant = plantIterator.next();
								if (plant.contains(point))
									plant.kill();
							}
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