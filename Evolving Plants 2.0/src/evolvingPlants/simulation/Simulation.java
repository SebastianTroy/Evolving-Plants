package evolvingPlants.simulation;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import evolvingPlants.Hub;

public class Simulation
	{
		// Globally fixed variables
		private final Color skyBlue = new Color(150, 150, 255);

		// User adjustable variables
		public double uvIntensity = 2;
		public double geneCompatability = 0.1; // lower is less compatible
		public boolean showLighting = false;

		// Simulation variables
		public double simWidth, simX = 0;

		public LightMap lightMap;
		BufferedImage lightImage;

		// seeds added by user
		private Genes currentGenes = new Genes(20);
		public ArrayList<Point> seedsToAdd = new ArrayList<Point>(5);

		private ArrayList<Seed> seeds = new ArrayList<Seed>(20);

		ArrayList<Plant> plants = new ArrayList<Plant>(40);

		public Simulation(int width)
			{
				simWidth = width;
				lightMap = new LightMap(width, 550);
				lightImage = new BufferedImage(800, 550, BufferedImage.TYPE_INT_RGB);

				Hub.simWindow.currentCursor = Hub.simWindow.plantSeedCursor;
			}

		public void tick(double secondsPassed)
			{
				secondsPassed *= Hub.simWindow.playbackSpeed.getSliderValue();

				// Add new seedlings to Array
				for (Point p : seedsToAdd)
					addSeed(p.getX(), p.getY(), currentGenes, currentGenes.seedEnergy);
				seedsToAdd.clear();

				// Remove germinated seeds
				for (int i = 0; i < seeds.size(); i++)
					if (seeds.get(i).exists == false)
						seeds.remove(i);

				// Remove dead plants
				for (int i = 0; i < plants.size(); i++)
					if (plants.get(i).alive == false)
						plants.remove(i);

				for (Seed s : seeds)
					s.tick(secondsPassed);

				for (Plant p : plants)
					p.tick(secondsPassed);
			}

		public void render(Graphics g)
			{
				int simX = (int) this.simX;

				g.setColor(skyBlue);
				g.fillRect(200, 0, 800, 550);
				// EXTREMELY SLOW! was expected really, perhaps pause game when
				// showing the lighting?
				if (showLighting)
					g.drawImage(lightMap.getLightMap(lightImage, -simX), 200, 0, Hub.simWindow.getObserver());
				g.setColor(Color.GREEN);
				g.fillRect(200, 550, 800, 50);

				for (Seed s : seeds)
					s.render(g, simX + 200);

				for (Plant p : plants)
					p.render(g, simX + 200);

				g.setColor(Color.CYAN);
				g.fillRect(0, 0, 200, Hub.canvasHeight);
				g.fillRect(1000, 0, 200, Hub.canvasHeight);
			}

		public final void addSeed(double x, double y, Genes genes, double energy)
			{
				seeds.add(new Seed(x, y, genes, energy));
			}

		public final void addPlant(Plant newPlant)
			{
				if (isSpaceAt(newPlant.plantX, newPlant.sizeCategory))
					plants.add(newPlant);
			}

		public final boolean isSpaceAt(double x, int sizeCategory)
			{
				int smallSpacing = (int) Hub.simWindow.smallPlantSpacingSlider.getSliderValue();
				int mediumSpacing = (int) Hub.simWindow.mediumPlantSpacingSlider.getSliderValue();
				int largeSpacing = (int) Hub.simWindow.largePlantSpacingSlider.getSliderValue();
				switch (sizeCategory)
					{
						case Plant.SMALL:
							for (Plant p : plants)
								if (p.sizeCategory == Plant.SMALL)
									if (Math.abs(p.plantX - x) < smallSpacing)
										return false;
							break;
						case Plant.MEDIUM:
							for (Plant p : plants)
								if (p.sizeCategory == Plant.MEDIUM)
									if (Math.abs(p.plantX - x) < mediumSpacing)
										return false;
							break;
						case Plant.LARGE:
							for (Plant p : plants)
								if (p.sizeCategory == Plant.LARGE)
									if (Math.abs(p.plantX - x) < largeSpacing)
										return false;
							break;
					}
				return true;
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

		public double photosynthesizeAt(double d, int y, Color leafColour, Color shadowColour)
			{
				double energyGained = 0;

				int[] availableLight = lightMap.getLightMinusShadowAt((int) d, y, shadowColour);

				energyGained += Math.max(0, (availableLight[0] - leafColour.getRed()));
				energyGained += Math.max(0, (availableLight[1] - leafColour.getGreen()));
				energyGained += Math.max(0, (availableLight[2] - leafColour.getBlue()));
				/*
				 * The leaf colour represents the light a leaf DOESN'T absorb.
				 */
				/*
				 * If energy gained is over 255 no extra energy is actually
				 * gained, this stops pressure for plants to evolve completely
				 * black leaves. Allows for plants with different leaf colours
				 * to simply compete for space.
				 */

				return Math.min(energyGained, 255);
			}

		public void mousePressed(MouseEvent e)
			{
				if (e.getY() > 70 && e.getY() < 550 && e.getX() > 200 && e.getX() < 1000)
					{
						Point point = e.getPoint();
						point.x -= simX + 200;

						if (Hub.simWindow.currentCursor == Hub.simWindow.plantSeedCursor)
							{
								seedsToAdd.add(point);
							}
						else if (Hub.simWindow.currentCursor == Cursor.getDefaultCursor())
							{
								for (Plant plant : plants)
									if (plant.contains(point))
										plant.selected = true;
									else if (plant.selected)
										plant.selected = false;
							}
						else if (Hub.simWindow.currentCursor == Hub.simWindow.getGenesCursor)
							{
								for (Plant plant : plants)
									if (plant.contains(point))
										{
											currentGenes = plant.getGenesCopy();
											System.out.println(new String(plant.getGenesCopy().getGenes()));
										}
							}
						else if (Hub.simWindow.currentCursor == Hub.simWindow.killPlantCursor)
							{
								for (Plant plant : plants)
									if (plant.contains(point))
										plant.kill();
							}
					}
			}

		public void keyPressed(KeyEvent e)
			{
				if (e.getKeyChar() == 's')
					showLighting = !showLighting;
			}
	}