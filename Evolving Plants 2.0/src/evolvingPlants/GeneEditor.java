package evolvingPlants;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import tCode.RenderableObject;
import tComponents.components.TButton;
import tComponents.components.TLabel;
import tComponents.components.TMenu;
import tComponents.components.TSlider;
import tComponents.components.TTextField;
import tComponents.utils.events.TActionEvent;
import tComponents.utils.events.TScrollEvent;
import evolvingPlants.simulation.Genome;
import evolvingPlants.simulation.LightMap;
import evolvingPlants.simulation.Plant;

public class GeneEditor extends RenderableObject
	{
		private String saveDirectory = System.getProperty("user.home") + "//Evolving Plants//Genes";

		// Plant variables
		private int plantX = 250;
		static int plantY = 500;

		LightMap lightMap = new LightMap(500, 500);
		private BufferedImage lightMapImage = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
		private double height = 0, lean = 0, energyGained = 200;

		public final TMenu plantOptionsMenu = new TMenu(450, 0, 250, 500, TMenu.VERTICAL);
		public final TSlider seedSizeSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider redLeafSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider greenLeafSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider blueLeafSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider leafOpacitySlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider lightSlider = new TSlider(TSlider.HORIZONTAL);
		public final TButton refreshPlantButton = new TButton("refresh Plant")
			{
				@Override
				public final void pressed()
					{
						updateExamplePlant();
					}
			};

		private TMenu geneFieldsMenu = new TMenu(0, 490, 1200, 50, TMenu.HORIZONTAL);
		private ArrayList<TTextField> geneFields = new ArrayList<TTextField>();
		private final TTextField saveNameField = new TTextField(10, 540, 300, 20, "Save Name Here");

		private final TMenu saveLoadMenu = new TMenu(320, 533, 500, 37, TMenu.HORIZONTAL);
		private final TButton saveGenesButton = new TButton("Save Genes");
		private final TButton loadGenesButton = new TButton("Load Genes");
		private final TButton openGenesFolderButton = new TButton("Open Genes Folder");
		private final TButton mainMenuButton = new TButton("Main Menu");

		private final TMenu instructionMenu = new TMenu(0, 0, 400, 500, TMenu.VERTICAL);

		private Genome exampleGenes;
		private Plant examplePlant;

		@Override
		protected void initiate()
			{
				File directory = new File(saveDirectory);

				if (!directory.exists())
					{
						directory.mkdirs();
					}

				seedSizeSlider.setRange(0, 1000);
				redLeafSlider.setRange(0, 255);
				greenLeafSlider.setRange(0, 255);
				blueLeafSlider.setRange(0, 255);
				seedSizeSlider.setSliderImage(0, Main.loadImage("seed.png"));
				redLeafSlider.setSliderImage(0, Main.loadImage("redLeaf.png"));
				greenLeafSlider.setSliderImage(0, Main.loadImage("greenLeaf.png"));
				blueLeafSlider.setSliderImage(0, Main.loadImage("blueLeaf.png"));
				lightSlider.setRange(0, 255);
				lightSlider.setSliderImage(0, Main.loadImage("sun.png"));

				TLabel plantOptionsLabel = new TLabel("Plant Options");
				plantOptionsLabel.setFontSize(15);
				plantOptionsLabel.setBackgroundColour(new Color(0, 200, 200));
				plantOptionsMenu.add(plantOptionsLabel, false);
				plantOptionsMenu.add(seedSizeSlider);
				plantOptionsMenu.add(redLeafSlider);
				plantOptionsMenu.add(greenLeafSlider);
				plantOptionsMenu.add(blueLeafSlider);

				plantOptionsMenu.add(new TLabel("Leaf Transparency"), false);
				leafOpacitySlider.setRange(0, 255);
				leafOpacitySlider.setValue(75);
				plantOptionsMenu.add(leafOpacitySlider);

				TLabel simOptionsLabel = new TLabel("Light Intensity");
				simOptionsLabel.setFontSize(15);
				simOptionsLabel.setBackgroundColour(new Color(0, 200, 200));
				plantOptionsMenu.add(simOptionsLabel, false);
				plantOptionsMenu.add(lightSlider);

				plantOptionsMenu.add(refreshPlantButton);

				redLeafSlider.setValue(175.0);
				greenLeafSlider.setValue(175.0);
				blueLeafSlider.setValue(175.0);

				lightSlider.setValue(255);

				add(plantOptionsMenu);

				saveLoadMenu.setBorderSize(0);
				saveLoadMenu.add(saveGenesButton);
				saveLoadMenu.add(loadGenesButton);
				saveLoadMenu.add(openGenesFolderButton);
				saveLoadMenu.add(mainMenuButton);

				add(saveLoadMenu);

				geneFieldsMenu.setTComponentAlignment(TMenu.ALIGN_START);
				geneFieldsMenu.setBorderSize(5);

				for (int i = 0; i < Genome.NUM_GENES; i++)
					{
						TTextField t = new TTextField(0, 0, 90, 20, Genome.GENE_LENGTH);

						TLabel l = new TLabel("Gene" + (char) (i + 65));
						l.setBackgroundColour(new Color(24, 157, 181));
						geneFieldsMenu.add(l, false);
						geneFieldsMenu.add(t, false);
						geneFields.add(t);
					}

				add(geneFieldsMenu);
				add(saveNameField);

				instructionMenu.add(getInstructionLabel("Genetic Instructions:"), false);
				instructionMenu.add(getInstructionLabel("Grow = " + Genome.GROW), false);
				instructionMenu.add(getInstructionLabel("Rotate left = " + Genome.ROTATE_LEFT), false);
				instructionMenu.add(getInstructionLabel("Rotate right = " + Genome.ROTATE_RIGHT), false);
				instructionMenu.add(getInstructionLabel("Start Node = " + Genome.START_NODE), false);
				instructionMenu.add(getInstructionLabel("End Node = " + Genome.END_NODE), false);
				instructionMenu.add(getInstructionLabel("Toggle leaf = " + Genome.TOGGLE_LEAF), false);
				instructionMenu.add(getInstructionLabel("Nothing = " + Genome.NOTHING), false);
				add(instructionMenu);

				lightMap.getLightImage(lightMapImage, 0);

				updateExamplePlant();
			}

		@Override
		public void tick(double secondsPassed)
			{
				examplePlant.tick();
				energyGained = examplePlant.plantEnergy;
				examplePlant.plantEnergy = Double.MIN_VALUE;
				lightMap.getLightImage(lightMapImage, 0);
			}

		@Override
		protected void render(Graphics2D g)
			{
				g.setColor(Color.CYAN);
				g.fillRect(0, 0, 700, 500);
				g.setColor(Color.DARK_GRAY);
				g.fillRect(0, 500, Main.canvasWidth, 100);
				g.drawImage(lightMapImage, 700, 0, getObserver());

				g.setColor(Color.BLACK);
				g.drawString(exampleGenes.getUnpackedGenome().size() == 0 ? "Plant not Viable, You need a " + Genome.END_NODE + " for each " + Genome.START_NODE : "Plant Viable", 710, 30);
				g.drawString("Energy/Tick: " + energyGained, 520, 424);

				g.drawString("UnpackedGenes: " + exampleGenes.getUnpackedGenome(), 5, 480);

				examplePlant.render(g, 700);
			}

		private final TLabel getInstructionLabel(String labelText)
			{
				TLabel label = new TLabel(labelText);
				label.setHeight(30);

				return label;
			}

		private final void updateExamplePlant()
			{
				if (examplePlant != null)
					examplePlant.kill();

				setGenome();

				examplePlant = new Plant(exampleGenes, plantX);
				examplePlant.leafOpacity = (int) leafOpacitySlider.getValue();
				examplePlant.metabolismIncreasePerTick = 0;

				exampleGenes.seedEnergy = Double.POSITIVE_INFINITY;
			}

		private final void setGenome()
			{
				exampleGenes = new Genome();

				for (int geneNum = 0; geneNum < Genome.NUM_GENES; geneNum++)
					{
						String geneString = geneFields.get(geneNum).getText();
						while (geneString.length() < Genome.GENE_LENGTH)
							geneString += Genome.NOTHING;

						for (int geneIndex = 0; geneIndex < Genome.GENE_LENGTH; geneIndex++)
							{
								char charAtIndex = geneString.charAt(geneIndex);
								switch (charAtIndex)
									{
										case (Genome.GROW):
											exampleGenes.genes[geneNum][geneIndex] = Genome.GROW;
											break;
										case (Genome.ROTATE_LEFT):
											exampleGenes.genes[geneNum][geneIndex] = Genome.ROTATE_LEFT;
											break;
										case (Genome.ROTATE_RIGHT):
											exampleGenes.genes[geneNum][geneIndex] = Genome.ROTATE_RIGHT;
											break;
										case (Genome.TOGGLE_LEAF):
											exampleGenes.genes[geneNum][geneIndex] = Genome.TOGGLE_LEAF;
											break;
										case (Genome.START_NODE):
											exampleGenes.genes[geneNum][geneIndex] = Genome.START_NODE;
											break;
										case (Genome.END_NODE):
											exampleGenes.genes[geneNum][geneIndex] = Genome.END_NODE;
											break;
										default:
											if (charAtIndex - 65 < Genome.NUM_GENES && (charAtIndex - 65 > 0))
													exampleGenes.genes[geneNum][geneIndex] = charAtIndex;
											else
												exampleGenes.genes[geneNum][geneIndex] = Genome.NOTHING;
									}								
							}					
					}

				exampleGenes.leafColour = new Color((int) redLeafSlider.getValue(), (int) greenLeafSlider.getValue(), (int) blueLeafSlider.getValue());
				exampleGenes.seedEnergy = seedSizeSlider.getValue();
			}

		private final String readStringFromLine(String text)
			{
				return text.substring(text.indexOf('=') + 1, text.length());
			}

		private final double readValueFromLine(String text)
			{
				return Double.parseDouble(text.substring(text.indexOf('=') + 1, text.length()));
			}

		@Override
		public final void tActionEvent(TActionEvent e)
			{
				Object eventSource = e.getSource();

				if (eventSource == saveGenesButton)
					{
						// TODO update GeneIO and save genes
						// Main.geneIO.saveGenes(
						// new RecursiveGenes(geneEditorField.getText(), seedSizeSlider.getValue(), (int) redLeafSlider.getValue(), (int)
						// greenLeafSlider.getValue(), (int) blueLeafSlider.getValue()),
						// saveNameField.getText());
					}
				else if (eventSource == loadGenesButton)
					{
						JFileChooser chooser = new JFileChooser(saveDirectory);
						int returnVal = chooser.showOpenDialog(getObserver());
						if (returnVal == JFileChooser.APPROVE_OPTION)
							{
								BufferedReader in = null;
								try
									{
										File geneFile = new File(saveDirectory + "//" + chooser.getSelectedFile().getName());

										in = new BufferedReader(new FileReader(geneFile));

										saveNameField.setText(chooser.getSelectedFile().getName().substring(0, chooser.getSelectedFile().getName().length() - 4));
										// geneEditorField.setText(readStringFromLine(in.readLine()));
										seedSizeSlider.setValue(readValueFromLine(in.readLine()));
										redLeafSlider.setValue(readValueFromLine(in.readLine()));
										greenLeafSlider.setValue(readValueFromLine(in.readLine()));
										blueLeafSlider.setValue(readValueFromLine(in.readLine()));
									}
								catch (Exception ex)
									{}
								finally
									{
										try
											{
												in.close();
											}
										catch (IOException ex)
											{
												ex.printStackTrace();
											}
									}
								updateExamplePlant();
							}

					}
				else if (eventSource == openGenesFolderButton)
					Main.geneIO.openFolder();
				else if (eventSource == mainMenuButton)
					changeRenderableObject(Main.menu);
			}

		@Override
		public final void tScrollEvent(TScrollEvent e)
			{
				exampleGenes.leafColour = new Color((int) redLeafSlider.getValue(), (int) greenLeafSlider.getValue(), (int) blueLeafSlider.getValue());
				lightMap.setLight(lightSlider.getValue());

				examplePlant.refreshShadows((int) leafOpacitySlider.getValue());
				examplePlant.leafOpacity = (int) leafOpacitySlider.getValue();

				lightMap.getLightImage(lightMapImage, 0);
			}
	}