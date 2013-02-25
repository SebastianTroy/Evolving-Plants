package evolvingPlants.simulation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import evolvingPlants.Hub;

public class Simulation
	{

		boolean showLighting = false;

		// Globally fixed variables
		private final Color skyBlue = new Color(150, 150, 255);

		// User adjustable variables
		public double minPlantSpacing = 5;
		public double uvIntensity = 2;
		public double geneCompatability = 0.1; // only 1/10 difference is
												// allowed

		// Simulation variables
		public double simWidth, simX = 0;

		private LightMap lightMap;
		BufferedImage lightImage;

		private Genes currentGenes = new Genes(20);
		public ArrayList<Point> seedsToAdd = new ArrayList<Point>(5);

		private ArrayList<Seed> seeds = new ArrayList<Seed>(20);

		ArrayList<Plant> plants = new ArrayList<Plant>(40);

		public Simulation(int width)
			{
				simWidth = width;
				lightMap = new LightMap(width, 550);
				lightImage = new BufferedImage(800, 550, BufferedImage.TYPE_INT_RGB);
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
				g.setColor(skyBlue);
				g.fillRect(200, 0, 800, 550);
				// EXTREMELY SLOW! was expected really, perhaps pause game when
				// showing the lighting?
				if (showLighting)
					g.drawImage(lightMap.getLightMap(lightImage, (int) -simX), 200, 0, Hub.simWindow.getObserver());
				g.setColor(Color.GREEN);
				g.fillRect(200, 550, 800, 50);

				for (Seed s : seeds)
					s.render(g, (int) simX + 200);

				for (Plant p : plants)
					p.render(g, (int) simX + 200);

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
				plants.add(newPlant);
			}

		public final boolean isSpaceAt(double x)
		{
			for (Plant p : plants)
				if (Math.abs(p.plantX - x) < Hub.simWindow.sim.minPlantSpacing)
					return false;
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

				return Math.min(energyGained, 255) / 50.0;
			}

		public void mousePressed(MouseEvent e)
			{
				if (e.getY() > 70 && e.getY() < 550 && e.getX() > 200 && e.getX() < 1000)
					{
						Point p = e.getPoint();
						p.x -= simX + 200;
						seedsToAdd.add(p);
					}
			}

		public void keyPressed(KeyEvent e)
			{
				if (e.getKeyChar() == 's')
					showLighting = !showLighting;
			}
	}