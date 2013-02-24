package evolvingPlants.simulation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import evolvingPlants.Hub;

public class Simulation
	{
		// Globally fixed variables
		private final Color skyBlue = new Color(150, 150, 255);
		public final double leafOpacity = 0.33;
		public final int stalkLength = 20;
		public final int leafSize = 12;

		// User adjustable variables
		public double minPlantSpacing = 5;
		public double uvIntensity = 2;
		public double geneCompatability = 0.1; // only 1/10 difference is
												// allowed

		// Simulation variables
		private LightMap lightMap = new LightMap(800, 550);
		BufferedImage lightImage = new BufferedImage(800, 550, BufferedImage.TYPE_INT_RGB);

		private Genes currentGenes = new Genes(20);
		public ArrayList<Point> seedsToAdd = new ArrayList<Point>(5);

		private ArrayList<Seed> seeds = new ArrayList<Seed>(20);

		ArrayList<Plant> plants = new ArrayList<Plant>(40);

		public Simulation(int width)
			{

			}

		public void tick(double secondsPassed)
			{
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
				g.setColor(Color.CYAN);
				g.fillRect(0, 0, 200, Hub.canvasHeight);
				g.fillRect(1000, 0, 200, Hub.canvasHeight);

				g.setColor(skyBlue);
				g.fillRect(200, 0, 800, 550);
				// EXTREMELY SLOW! was expected really, perhaps pause game when
				// showing the lighting?
				//g.drawImage(lightMap.getLightMap(lightImage, 0), 200, 0, Hub.simWindow.getObserver());
				g.setColor(Color.GREEN);
				g.fillRect(200, 550, 800, 50);

				g.setClip(200, 0, 800, Hub.canvasHeight);
				
				for (Seed s : seeds)
					s.render(g);

				for (Plant p : plants)
					p.render(g);
				
				g.setClip(null);
			}

		public final void addSeed(double x, double y, Genes genes, double energy)
			{
				seeds.add(new Seed(x, y, genes, energy));
			}

		public final void addPlant(Plant newPlant)
			{
				plants.add(newPlant);
			}

		public final void addShadow(double nodeX, double nodeY, Color leafColour)
			{
				int x = (int) nodeX - (leafSize / 2) - 200;
				lightMap.addShadow(x, (int) nodeY, leafSize, leafColour);
			}

		public final void removeShadow(double nodeX, double nodeY, Color leafColour)
			{
				int x = (int) nodeX - (leafSize / 2) - 200;
				lightMap.removeShadow(x, (int) nodeY, leafSize, leafColour);
			}

		public final boolean isSpaceAt(double x)
			{
				for (Plant p : plants)
					if (Math.abs(p.plantX - x) < Hub.simWindow.sim.minPlantSpacing)
						return false;
				return true;
			}

		public double photosynthesizeAt(double d, int y, Color leafColour)
			{
				double energyGained = 0;

				int[] shadowCols = lightMap.getLightAt((int) d, y, leafColour);

				energyGained += leafColour.getRed() - (leafColour.getRed() - shadowCols[0]);
				energyGained += leafColour.getGreen() - (leafColour.getGreen() - shadowCols[1]);
				energyGained += leafColour.getBlue() - (leafColour.getBlue() - shadowCols[2]);
				/*
				 * if leaf colour is (255, 0, 100), energy gained is 255 + 0 +
				 * 100 in white (255, 255, 255) light. if light was (134, 255,
				 * 130) for same leaf colour energy gained would be 135 + 0 +
				 * 100.
				 */
				/*
				 * If energy gained is over 500 no extra energy is actually
				 * gained, this stops pressure for plants to evolve completely
				 * black leaves. Allows for plants with different leaf colours
				 * to simply compete for space.
				 */
				return Math.max(energyGained, 500) / 100.0;
			}

		public void mousePressed(MouseEvent e)
			{
				seedsToAdd.add(e.getPoint());
			}
	}