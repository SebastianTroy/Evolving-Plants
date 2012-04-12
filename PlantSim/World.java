package PlantSim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import TroysCode.RenderableObject;
import TroysCode.Tools;
import TroysCode.T.TButton;
import TroysCode.T.TScrollEvent;
import TroysCode.T.TSlider;

public class World extends RenderableObject
	{
		private static final long serialVersionUID = 1L;

		private Pixel[][] photonGrid = new Pixel[800][600];

		private ArrayList<Plant> plantsToAdd = new ArrayList<Plant>(20);
		private ArrayList<Photon> photons = new ArrayList<Photon>(100);
		private ArrayList<Plant> plants = new ArrayList<Plant>(25);

		// Left World options
		private int lightIntensity = 4;
		private int lightEnergy = 25;
		private int plantSpacing = 12;
		public float UVIntensity = 50;
		public float UVDamage = 50;

		private final TSlider photonIntensitySlider = new TSlider(1010, 70, TSlider.HORIZONTAL, 180, 1);
		private final TSlider photonEnergySlider = new TSlider(1010, 110, TSlider.HORIZONTAL, 180, 1);
		private final TSlider plantSpacingSlider = new TSlider(1010, 150, TSlider.HORIZONTAL, 180, 1);
		private final TSlider UVIntensitySlider = new TSlider(1010, 190, TSlider.HORIZONTAL, 180, 1);
		private final TSlider UVDamageSlider = new TSlider(1010, 230, TSlider.HORIZONTAL, 180, 1);

		// Right World options
		private int lightIntensity2 = 4;
		private int lightEnergy2 = 25;
		private int plantSpacing2 = 12;
		public float UVIntensity2 = 50;
		public float UVDamage2 = 50;

		private final TSlider photonIntensitySlider2 = new TSlider(1010, 320, TSlider.HORIZONTAL, 180, 1);
		private final TSlider photonEnergySlider2 = new TSlider(1010, 360, TSlider.HORIZONTAL, 180, 1);
		private final TSlider plantSpacingSlider2 = new TSlider(1010, 400, TSlider.HORIZONTAL, 180, 1);
		private final TSlider UVIntensitySlider2 = new TSlider(1010, 440, TSlider.HORIZONTAL, 180, 1);
		private final TSlider UVDamageSlider2 = new TSlider(1010, 480, TSlider.HORIZONTAL, 180, 1);

		// Universal options
		private boolean viewLight = true;

		private final TButton viewPhotonsButton = new TButton(390, 5, "View Light? on/off");
		private final TButton randomiseColourButton = new TButton(220, 5, "Randomise plant Colours");
		private final TButton resetButton = new TButton(1010, 520, 175, 40, "RESET");

		// Plant Options
		private Plant selectedPlant;

		private final TButton reColourButton = new TButton(5, 30, "Re-Colour");
		private final TButton reColourRelativesButton = new TButton(75, 30, "Re-Colour Relatives");

		// Genes options
		private Genes currentGenes = new Genes(null, null);

		private final TButton getGenesButton = new TButton(65, 190, "Get Genes from Plant");

		private final TSlider maxAgeSlider = new TSlider(10, 230, TSlider.HORIZONTAL, 180, 1);
		private final TSlider stemLengthSlider = new TSlider(10, 270, TSlider.HORIZONTAL, 180, 1);
		private final TSlider stemsPerSeedSlider = new TSlider(10, 310, TSlider.HORIZONTAL, 180, 1);
		private final TSlider stemsPerLeafSlider = new TSlider(10, 350, TSlider.HORIZONTAL, 180, 1);
		private final TSlider chanceGrowingStemsSlider = new TSlider(10, 390, TSlider.HORIZONTAL, 180, 1);
		private final TSlider maxStemsSlider = new TSlider(10, 430, TSlider.HORIZONTAL, 180, 1);
		private final TSlider stemAngleVarSlider = new TSlider(10, 470, TSlider.HORIZONTAL, 180, 1);
		private final TSlider seedEnergySlider = new TSlider(10, 510, TSlider.HORIZONTAL, 180, 1);
		private final TSlider alphaValueSlider = new TSlider(10, 550, TSlider.HORIZONTAL, 180, 1);

		// Mouse

		private float mouseX = 0;
		private float mouseY = 0;

		private final byte SELECT = 0;
		private final byte PLANT = 1;
		private final byte KILL = 2;
		private final byte GETGENES = 3;
		private final byte RECOLOUR = 4;

		private byte mouseState = SELECT;

		private final TButton reColourSelectButton = new TButton(610, 5, "RE-Colour Plant");
		private final TButton mouseSelectButton = new TButton(712, 5, "Select Plant");
		private final TButton mousePlantButton = new TButton(789, 5, "Plant Seed");
		private final TButton mouseKillButton = new TButton(860, 5, "Kill Plant");
		private final TButton mouseGetGenesButton = new TButton(920, 5, "Get Genes");

		public World()
			{
			}

		@Override
		protected void initiate()
			{
				for (int x = 0; x < 800; x++)
					for (int y = 0; y < 600; y++)
						photonGrid[x][y] = new Pixel();

				// Left World components
				addTComponent(photonIntensitySlider);
				addTComponent(photonEnergySlider);
				addTComponent(plantSpacingSlider);
				addTComponent(UVIntensitySlider);
				addTComponent(UVDamageSlider);

				// Right World components
				addTComponent(photonIntensitySlider2);
				addTComponent(photonEnergySlider2);
				addTComponent(plantSpacingSlider2);
				addTComponent(UVIntensitySlider2);
				addTComponent(UVDamageSlider2);

				// Universal Options
				addTComponent(viewPhotonsButton);
				addTComponent(randomiseColourButton);
				addTComponent(resetButton);

				// Plant components
				addTComponent(reColourButton);
				addTComponent(reColourRelativesButton);

				// Genes Components
				addTComponent(getGenesButton);
				addTComponent(maxAgeSlider);
				addTComponent(stemLengthSlider);
				addTComponent(stemsPerSeedSlider);
				addTComponent(stemsPerLeafSlider);
				addTComponent(chanceGrowingStemsSlider);
				addTComponent(maxStemsSlider);
				addTComponent(stemAngleVarSlider);
				addTComponent(seedEnergySlider);
				addTComponent(alphaValueSlider);

				// Mouse Components
				addTComponent(reColourSelectButton);
				addTComponent(mouseSelectButton);
				addTComponent(mousePlantButton);
				addTComponent(mouseKillButton);
				addTComponent(mouseGetGenesButton);
			}

		@Override
		protected void refresh()
			{
				// add 10 equally spaced new plants
				for (int i = 0; i < 10; i++)
					for (int tries = 0; tries < 10; tries++)
						addPlant(new Plant(new Plant(new Plant(null, (i * 80) + 240, 500, 250), (i * 80) + 240, 500, 250), (i * 80) + 240, 500, 250));

				setGeneSliders();
			}

		@Override
		protected void tick()
			{
				for (int i = 0; i < lightIntensity; i++)
					{
						photons.add(new Photon(Tools.randInt(200, 599), lightEnergy));
					}

				for (int i = 0; i < lightIntensity2; i++)
					{
						photons.add(new Photon(Tools.randInt(600, 999), lightEnergy2));
					}

				// Remove dead lightBeams
				Photon[] photonsCopy = new Photon[photons.size()];
				photons.toArray(photonsCopy);
				for (Photon p : photonsCopy)
					if (p.exists == false)
						{
							photonGrid[(int) (p.x - 200)][(int) p.y].photon = null;
							photons.remove(p);
						}

				// Remove dead plants
				Plant[] plantsCopy = new Plant[plants.size()];
				plants.toArray(plantsCopy);
				for (Plant e : plantsCopy)
					if (e.exists == false)
						plants.remove(e);

				// Add new seedlings to Array
				for (Plant p : plantsToAdd)
					plants.add(p);
				plantsToAdd.clear();

				// Process entities
				for (Entity e : photons)
					e.tick();
				for (Entity e : plants)
					e.tick();

				// update photon positions
				for (Photon p : photons)
					{
						photonGrid[(int) p.x - 200][(int) p.y].photon = null;
						p.y++;
						photonGrid[(int) p.x - 200][(int) p.y].photon = p;
					}

				// Check if leaves contain light beams
				for (Plant plant : plants)
					for (Leaf leaf : plant.leaves)
						for (int x = (int) (leaf.x - 12); x < leaf.x + 12; x++)
							for (int y = (int) (leaf.y - 12); y < leaf.y + 12; y++)
								if (x - 200 > 0 && x < 1000 && photonGrid[x - 200][y].photon != null)
									leaf.containsPhoton(photonGrid[x - 200][y].photon);
			}

		@Override
		protected void renderObject(Graphics g)
			{
				g.setColor(new Color(100, 100, 255));
				g.fillRect(200, 0, 800, 600);

				if (viewLight || photonIntensitySlider.inUse || photonIntensitySlider2.inUse)
					for (Photon p : photons)
						p.render(g);

				for (Entity p : plants)
					p.render(g);

				g.setColor(Color.GRAY);
				g.fillRect(1000, 0, 200, 600);

				g.setColor(Color.BLACK);
				g.drawString("World Options:", 1008, 20);
				g.drawString("LEFT SIDE:", 1008, 50);
				g.drawString("Light Intensity: " + lightIntensity, 1020, 70);
				g.drawString("Light Energy: " + lightEnergy, 1020, 110);
				g.drawString("Plant Spacing: " + plantSpacing, 1020, 150);
				g.drawString("Chance of mutation: " + UVIntensity, 1020, 190);
				g.drawString("Size of mutation: " + UVDamage, 1020, 230);

				g.drawString("RIGHT SIDE:", 1008, 300);
				g.drawString("Light Intensity: " + lightIntensity2, 1020, 320);
				g.drawString("Light Energy: " + lightEnergy2, 1020, 360);
				g.drawString("Plant Spacing: " + plantSpacing2, 1020, 400);
				g.drawString("Chance of mutation: " + UVIntensity2, 1020, 440);
				g.drawString("Size of mutation: " + UVDamage2, 1020, 480);

				g.setColor(Color.GRAY);
				g.fillRect(0, 0, 200, 600);

				g.setColor(Color.BLACK);
				g.drawString("Selected Plant:", 8, 20);

				if (selectedPlant != null)
					{
						g.drawString("Age:           " + selectedPlant.age, 10, 80);
						g.drawString("/ " + selectedPlant.genes.maxAge, 120, 80);

						g.drawString("Stems:      " + (int) (selectedPlant.genes.maxStems - selectedPlant.numberOfStemsLeft), 10, 100);
						g.drawString("/ " + selectedPlant.genes.maxStems, 120, 100);
						g.drawString("Stem Length:            " + selectedPlant.genes.maxStemLength, 10, 120);

						g.drawString("Stored Energy:          " + selectedPlant.energy, 10, 140);
						g.drawString("Energy per Seed:     " + selectedPlant.genes.seedEnergy, 10, 160);
						g.drawString("Leaf Transparency:      " + ((255f - selectedPlant.genes.colour.getAlpha()) / 2.55f) + " %", 10, 180);
					}

				g.drawString("GENES:", 6, 210);
				g.drawString("Max Age: " + currentGenes.maxAge, 14, 230);
				g.drawString("Stem Length: " + currentGenes.maxStemLength, 14, 270);
				g.drawString("Stems per Seed: " + currentGenes.numberOfSeedStems, 14, 310);
				g.drawString("Stems per Leaf: " + currentGenes.numberOfLeafStems, 14, 350);
				g.drawString("Chance leaf has Stems: " + currentGenes.chanceOfGrowingStems, 14, 390);
				g.drawString("Maximum stem Number: " + currentGenes.maxStems, 14, 430);
				g.drawString("Stem Angle Variation: " + currentGenes.stemAngleVariation, 14, 470);
				g.drawString("Energy to Seed: " + currentGenes.seedEnergy, 14, 510);
				g.drawString("Leaf Transparency: " + ((255f - currentGenes.colour.getAlpha()) / 2.55f) + " %", 14, 550);

				if (mouseX > 200 && mouseY > 0 && mouseX < 1000 && mouseY < 600)
					switch (mouseState)
						{
						case SELECT:
							g.drawString("SELECT", Math.round(mouseX), Math.round(mouseY));
							break;
						case PLANT:
							g.drawString("PLANT", Math.round(mouseX), Math.round(mouseY));
							break;
						case KILL:
							g.drawString("KILL", Math.round(mouseX), Math.round(mouseY));
							break;
						case GETGENES:
							g.drawString("GETGENES", Math.round(mouseX), Math.round(mouseY));
							break;
						case RECOLOUR:
							g.drawString("RE-COLOUR", Math.round(mouseX), Math.round(mouseY));
							;
							break;
						}
			}

		public final void addPlant(Plant seedling)
			{
				int spacing = seedling.x < 600 ? plantSpacing : plantSpacing2;

				for (Plant plant : plants)
					if (seedling.x < plant.x + spacing && seedling.x > plant.x - spacing)
						return;
					else if (seedling.x < 200 || seedling.x > 1000)
						return;

				for (Plant plant : plantsToAdd)
					if (plant.genes.maxAge > 10)
						{
							if (seedling.x < plant.x + spacing && seedling.x > plant.x - spacing)
								return;
							else if (seedling.x < 200 || seedling.x > 1000)
								return;
						}

				plantsToAdd.add(seedling);
			}

		@Override
		protected void mousePressed(MouseEvent event)
			{
				switch (mouseState)
					{
					case SELECT:
						for (Plant p : plants)
							for (Leaf l : p.leaves)
								if (l.containsPoint(event.getX(), event.getY()))
									selectedPlant = p;
						break;
					case PLANT:
						addPlant(new Plant(new Plant(new Plant(null, event.getX(), event.getY(), currentGenes.seedEnergy, currentGenes), event.getX(),
								event.getY(), currentGenes.seedEnergy, currentGenes), event.getX(), event.getY(), currentGenes.seedEnergy, currentGenes));
						break;
					case KILL:
						for (Plant p : plants)
							for (Leaf l : p.leaves)
								if (l.containsPoint(event.getX(), event.getY()))
									p.exists = false;
						break;
					case GETGENES:
						for (Plant p : plants)
							for (Leaf l : p.leaves)
								if (l.containsPoint(event.getX(), event.getY()))
									currentGenes = p.genes;
						setGeneSliders();
						break;
					case RECOLOUR:
						for (Plant p : plants)
							for (Leaf l : p.leaves)
								if (l.containsPoint(event.getX(), event.getY()))
									{
										Color c = Tools.randColour();
										p.genes.colour = new Color(c.getRed(), c.getGreen(), c.getBlue(), p.genes.colour.getAlpha());
									}
						break;
					}
			}

		@Override
		protected void mouseReleased(MouseEvent event)
			{
			}

		@Override
		protected void mouseDragged(MouseEvent event)
			{
				mouseX = event.getX();
				mouseY = event.getY();
			}

		@Override
		protected void mouseMoved(MouseEvent event)
			{
				mouseX = event.getX();
				mouseY = event.getY();
			}

		@Override
		protected void mouseWheelMoved(MouseWheelEvent event)
			{
			}

		@Override
		protected void actionPerformed(ActionEvent event)
			{
				if (event.getSource() == viewPhotonsButton)
					viewLight = !viewLight;

				else if (event.getSource() == resetButton)
					{
						for (Plant p : plantsToAdd)
							p.exists = false;
						for (Plant p : plants)
							p.exists = false;
						refresh();
					}

				else if (event.getSource() == reColourButton)
					{
						if (selectedPlant != null)
							{
								Color c = Tools.randColour();
								selectedPlant.genes.colour = new Color(c.getRed(), c.getGreen(), c.getBlue(), selectedPlant.genes.colour.getAlpha());
							}
					}

				else if (event.getSource() == reColourRelativesButton)
					{
						if (selectedPlant != null)
							{
								Color c = Tools.randColour();
								selectedPlant.genes.colour = new Color(c.getRed(), c.getGreen(), c.getBlue(), selectedPlant.genes.colour.getAlpha());

								for (Plant p : plants)
									if (isRelated(p))
										p.genes.colour = selectedPlant.genes.colour;
							}
					}

				else if (event.getSource() == randomiseColourButton)
					for (Plant p : plants)
						{
							Color c = Tools.randColour();
							p.genes.colour = new Color(c.getRed(), c.getGreen(), c.getBlue(), p.genes.colour.getAlpha());
						}

				else if (event.getSource() == getGenesButton)
					{
						if (selectedPlant != null)
							{
								currentGenes = selectedPlant.genes;
								setGeneSliders();
							}
					}

				else if (event.getSource() == mouseSelectButton)
					mouseState = SELECT;
				else if (event.getSource() == mousePlantButton)
					mouseState = PLANT;
				else if (event.getSource() == mouseKillButton)
					mouseState = KILL;
				else if (event.getSource() == mouseGetGenesButton)
					mouseState = GETGENES;
				else if (event.getSource() == reColourSelectButton)
					mouseState = RECOLOUR;
			}

		private final boolean isRelated(Plant p)
			{
				// if is parent or grandParent of selected plant
				if (p == selectedPlant.parent || p == selectedPlant.parent.parent)
					return true;

				// if is child or sibling or Aunt/Uncle of selected plant
				if (p.parent == selectedPlant || p.parent == selectedPlant.parent || p.parent == selectedPlant.parent.parent)
					return true;

				// if is great grandChild, or niece/nephew or cousin of selected
				// plant
				if (p.parent.parent == selectedPlant || p.parent.parent == selectedPlant.parent || p.parent.parent == selectedPlant.parent.parent)
					return true;

				return false;
			}

		private final void setGeneSliders()
			{
				Genes g = currentGenes;

				maxAgeSlider.setSliderPercent(0, (g.maxAge - 3) / 79.97f);
				stemLengthSlider.setSliderPercent(0, (g.maxStemLength - 10) / 1.9f);
				stemsPerSeedSlider.setSliderPercent(0, (g.numberOfSeedStems - 1) / 0.14f);
				stemsPerLeafSlider.setSliderPercent(0, g.numberOfLeafStems / 0.15f);
				chanceGrowingStemsSlider.setSliderPercent(0, g.chanceOfGrowingStems);
				maxStemsSlider.setSliderPercent(0, (g.maxStems - 1) / 0.29f);
				stemAngleVarSlider.setSliderPercent(0, g.stemAngleVariation);
				seedEnergySlider.setSliderPercent(0, g.seedEnergy / 5);
				alphaValueSlider.setSliderPercent(0, (255 - g.colour.getAlpha()) / 2.55f);
			}

		@Override
		protected void keyPressed(KeyEvent event)
			{
			}

		@Override
		protected void keyReleased(KeyEvent event)
			{
			}

		@Override
		protected void keyTyped(KeyEvent event)
			{
			}

		@Override
		protected void mouseClicked(MouseEvent event)
			{
			}

		@Override
		protected void mouseEntered(MouseEvent event)
			{
			}

		@Override
		protected void mouseExited(MouseEvent event)
			{
			}

		@Override
		protected void programGainedFocus(WindowEvent event)
			{
			}

		@Override
		protected void programLostFocus(WindowEvent event)
			{
			}

		@Override
		protected void frameResized(ComponentEvent event)
			{
			}

		@Override
		public void tScrollBarScrolled(TScrollEvent event)
			{
				// LEFT
				if (event.getSource() == photonIntensitySlider)
					lightIntensity = (int) (photonIntensitySlider.getSliderPercent(0) * 0.08);
				else if (event.getSource() == photonEnergySlider)
					lightEnergy = (int) ((photonEnergySlider.getSliderPercent(0) * 0.49) + 1);
				else if (event.getSource() == plantSpacingSlider)
					plantSpacing = (int) ((plantSpacingSlider.getSliderPercent(0) * 0.15) + 5);
				else if (event.getSource() == UVIntensitySlider)
					UVIntensity = UVIntensitySlider.getSliderPercent(0);
				else if (event.getSource() == UVDamageSlider)
					UVDamage = UVDamageSlider.getSliderPercent(0);
				// RIGHT
				else if (event.getSource() == photonIntensitySlider2)
					lightIntensity2 = (int) (photonIntensitySlider2.getSliderPercent(0) * 0.08);
				else if (event.getSource() == photonEnergySlider2)
					lightEnergy2 = (int) ((photonEnergySlider2.getSliderPercent(0) * 0.49) + 1);
				else if (event.getSource() == plantSpacingSlider2)
					plantSpacing2 = (int) ((plantSpacingSlider2.getSliderPercent(0) * 0.15) + 5);
				else if (event.getSource() == UVIntensitySlider2)
					UVIntensity2 = UVIntensitySlider2.getSliderPercent(0);
				else if (event.getSource() == UVDamageSlider2)
					UVDamage2 = UVDamageSlider2.getSliderPercent(0);
				// GENES
				else if (event.getSource() == maxAgeSlider)
					currentGenes.maxAge = (int) (maxAgeSlider.getSliderPercent(0) * 79.97f) + 3;
				else if (event.getSource() == stemLengthSlider)
					currentGenes.maxStemLength = (stemLengthSlider.getSliderPercent(0) * 1.9f) + 10;
				else if (event.getSource() == stemsPerSeedSlider)
					currentGenes.numberOfSeedStems = (stemsPerSeedSlider.getSliderPercent(0) * 0.14f) + 1;
				else if (event.getSource() == stemsPerLeafSlider)
					currentGenes.numberOfLeafStems = stemsPerLeafSlider.getSliderPercent(0) * 0.15f;
				else if (event.getSource() == chanceGrowingStemsSlider)
					currentGenes.chanceOfGrowingStems = chanceGrowingStemsSlider.getSliderPercent(0);
				else if (event.getSource() == maxStemsSlider)
					currentGenes.maxStems = (maxStemsSlider.getSliderPercent(0) * 0.29f) + 1;
				else if (event.getSource() == stemAngleVarSlider)
					currentGenes.stemAngleVariation = stemAngleVarSlider.getSliderPercent(0);
				else if (event.getSource() == seedEnergySlider)
					currentGenes.seedEnergy = seedEnergySlider.getSliderPercent(0) * 5;
				else if (event.getSource() == alphaValueSlider)
					{
						Color c = currentGenes.colour;
						currentGenes.colour = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (255 - (alphaValueSlider.getSliderPercent(0) * 2.55f)));
					}
			}
	}
