package evolvingPlants;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
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
import evolvingPlants.simulation.Genes;

public class GeneEditor extends RenderableObject
	{
		private String saveDirectory = System.getProperty("user.home") + "//Evolving Plants//Genes";

		// Plant variables
		private int plantX = 950, plantY = 500, leafSize = 14;
		private Color leafColour = new Color(175, 175, 175), oppositeColour = new Color(80, 80, 80), lightColour = Color.WHITE;
		private NodeTree examplePlant = new NodeTree(new Genes(" ", 100, 175, 175, 175));
		private NodeTree.Node lastSelectedNode;
		private double height = 0, lean = 0, energyGained = 200, metabolism = 0.1;

		public final TMenu plantOptionsMenu = new TMenu(450, 0, 250, 500, TMenu.VERTICAL);
		public final TSlider seedSizeSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider redLeafSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider greenLeafSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider blueLeafSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider redLightSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider greenLightSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider blueLightSlider = new TSlider(TSlider.HORIZONTAL);

		private TTextField geneEditorField;
		private final TTextField saveNameField = new TTextField(10, 540, 300, 20, "Save Name Here");

		private final TMenu saveLoadMenu = new TMenu(320, 533, 500, 37, TMenu.HORIZONTAL);
		private final TButton saveGenesButton = new TButton("Save Genes");
		private final TButton loadGenesButton = new TButton("Load Genes");
		private final TButton openGenesFolderButton = new TButton("Open Genes Folder");
		private final TButton mainMenuButton = new TButton("Main Menu");

		private final TMenu instructionMenu = new TMenu(0, 0, 400, 500, TMenu.VERTICAL);

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
				seedSizeSlider.setSliderImage(0, Hub.loadImage("seed.png"));
				redLeafSlider.setSliderImage(0, Hub.loadImage("redLeaf.png"));
				greenLeafSlider.setSliderImage(0, Hub.loadImage("greenLeaf.png"));
				blueLeafSlider.setSliderImage(0, Hub.loadImage("blueLeaf.png"));
				redLightSlider.setRange(0, 255);
				greenLightSlider.setRange(0, 255);
				blueLightSlider.setRange(0, 255);
				redLightSlider.setSliderImage(0, Hub.loadImage("redSun.png"));
				greenLightSlider.setSliderImage(0, Hub.loadImage("greenSun.png"));
				blueLightSlider.setSliderImage(0, Hub.loadImage("blueSun.png"));

				TLabel plantOptionsLabel = new TLabel("Plant Options");
				plantOptionsLabel.setFontSize(15);
				plantOptionsLabel.setBackgroundColour(new Color(0, 200, 200));
				plantOptionsMenu.add(plantOptionsLabel, false);
				plantOptionsMenu.add(seedSizeSlider);
				plantOptionsMenu.add(redLeafSlider);
				plantOptionsMenu.add(greenLeafSlider);
				plantOptionsMenu.add(blueLeafSlider);

				TLabel simOptionsLabel = new TLabel("Light Options");
				simOptionsLabel.setFontSize(15);
				simOptionsLabel.setBackgroundColour(new Color(0, 200, 200));
				plantOptionsMenu.add(simOptionsLabel, false);
				plantOptionsMenu.add(redLightSlider);
				plantOptionsMenu.add(greenLightSlider);
				plantOptionsMenu.add(blueLightSlider);

				redLeafSlider.setValue(175.0);
				greenLeafSlider.setValue(175.0);
				blueLeafSlider.setValue(175.0);

				redLightSlider.setValue(255);
				greenLightSlider.setValue(255);
				blueLightSlider.setValue(255);

				add(plantOptionsMenu);

				saveLoadMenu.setBorderSize(0);
				saveLoadMenu.add(saveGenesButton);
				saveLoadMenu.add(loadGenesButton);
				saveLoadMenu.add(openGenesFolderButton);
				saveLoadMenu.add(mainMenuButton);

				add(saveLoadMenu);

				geneEditorField = new TTextField(10, 510, Hub.canvasWidth - 20, 20, "Insert genetic code here");
				add(geneEditorField);
				add(saveNameField);

				add(instructionMenu);
				instructionMenu.add(getInstructionLabel("Genetic Instructions:"), false);
				instructionMenu.add(getInstructionLabel("Grow up instruction = ^"), false);
				instructionMenu.add(getInstructionLabel("Grow down instruction = v"), false);
				instructionMenu.add(getInstructionLabel("Grow left instruction = <"), false);
				instructionMenu.add(getInstructionLabel("Grow right instruction = >"), false);
				instructionMenu.add(getInstructionLabel("Create new node = N"), false);
				instructionMenu.add(getInstructionLabel("Toggle if node can seed = S"), false);
				instructionMenu.add(getInstructionLabel("Climb up node tree = +"), false);
				instructionMenu.add(getInstructionLabel("Climb down node tree = -"), false);
				instructionMenu.add(getInstructionLabel("Stop reading instructions = |"), false);
			}

		@Override
		public void tick(double secondsPassed)
			{}

		@Override
		protected void render(Graphics2D g)
			{
				g.setColor(Color.CYAN);
				g.fillRect(0, 0, 700, 500);
				g.setColor(Color.DARK_GRAY);
				g.fillRect(0, 500, Hub.canvasWidth, 100);
				g.setColor(lightColour);
				g.fillRect(700, 0, 500, 500);

				g.setColor(Color.BLACK);
				g.drawString((Math.abs(lean) / height > 0.6) ? "Plant not Viable, too much lean!" : "Plant Viable", 710, 30);
				g.drawString("Energy/Sec: " + energyGained, 520, 400);
				g.drawString("Metabolism: " + metabolism, 520, 415);

				examplePlant.baseNode.render(g);
			}

		private final TLabel getInstructionLabel(String labelText)
			{
				TLabel label = new TLabel(labelText);
				label.setHeight(30);

				return label;
			}

		private final void updateExamplePlant()
			{
				metabolism = 0;
				examplePlant = new NodeTree(new Genes(geneEditorField.getText(), seedSizeSlider.getValue(), (int) redLeafSlider.getValue(), (int) greenLeafSlider.getValue(),
						(int) blueLeafSlider.getValue()));
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
		public final void keyReleased(KeyEvent e)
			{
				height = 0;
				lean = 0;
				updateExamplePlant();
			}

		@Override
		public final void tActionEvent(TActionEvent e)
			{
				Object eventSource = e.getSource();

				if (eventSource == saveGenesButton)
					{
						Hub.geneIO.saveGenes(
								new Genes(geneEditorField.getText(), seedSizeSlider.getValue(), (int) redLeafSlider.getValue(), (int) greenLeafSlider.getValue(), (int) blueLeafSlider.getValue()),
								saveNameField.getText());
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
										geneEditorField.setText(readStringFromLine(in.readLine()));
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
					Hub.geneIO.openFolder();
				else if (eventSource == mainMenuButton)
					changeRenderableObject(Hub.menu);
			}

		@Override
		public final void tScrollEvent(TScrollEvent e)
			{
				leafColour = new Color((int) redLeafSlider.getValue(), (int) greenLeafSlider.getValue(), (int) blueLeafSlider.getValue());
				oppositeColour = new Color(255 - (int) redLeafSlider.getValue(), 255 - (int) greenLeafSlider.getValue(), 255 - (int) blueLeafSlider.getValue());
				lightColour = new Color((int) redLightSlider.getValue(), (int) greenLightSlider.getValue(), (int) blueLightSlider.getValue());

				energyGained = 0;
				energyGained += Math.max(0, (lightColour.getRed() - leafColour.getRed()));
				energyGained += Math.max(0, (lightColour.getGreen() - leafColour.getGreen()));
				energyGained += Math.max(0, (lightColour.getBlue() - leafColour.getBlue()));

				if (energyGained > 200)
					energyGained = Math.max(0, 200 - (energyGained - 200));
			}

		private class NodeTree
			{
				private ArrayList<Point> leafLocations = new ArrayList<Point>(5);

				private Node baseNode = new Node(plantX, plantY);

				private NodeTree(Genes genes)
					{
						boolean finishedReading = false;
						baseNode.growUp();
						Node currentNode = baseNode;

						while (!finishedReading)
							{
								metabolism += 0.1;
								switch (genes.nextInstruction())
									{
										case Genes.ADD_NODE:
											Node newNode = new Node(currentNode);
											currentNode.addNode(newNode);
											currentNode = newNode;
											currentNode.growUp();
											break;
										case Genes.CLIMB_NODE_TREE:
											currentNode = currentNode.getDaughterNode();
											break;
										case Genes.DESCEND_NODE_TREE:
											currentNode = currentNode.getParentNode();
											break;
										case Genes.NODE_CAN_SEED:
											currentNode.canSeed = !currentNode.canSeed;
											break;
										case Genes.GROW_UP:
											metabolism++;
											currentNode.growUp();
											break;
										case Genes.GROW_LEFT:
											currentNode.growLeft();
											break;
										case Genes.GROW_RIGHT:
											currentNode.growRight();
											break;
										case Genes.GROW_DOWN:
											metabolism += 0.5;
											currentNode.growDown();
											break;
										case Genes.SKIP:
											metabolism += 0.75;
											break;
										case Genes.END_ALL:
											lastSelectedNode = currentNode;
											finishedReading = true;
									}
							}

						baseNode.calculateLean();
						baseNode.setLeaves();
						baseNode.parentNode = new Node(plantX, plantY);
					}

				private class Node
					{
						private static final double StalkLength = 20;

						private double x, y;

						private boolean isLeaf = false, canSeed = true;

						private Node parentNode;
						private ArrayList<Node> daughterNodes = new ArrayList<Node>();

						private Node(double x, double y)
							{
								this.x = x;
								this.y = y;
								parentNode = this;
							}

						private Node(Node parent)
							{
								x = parent.x;
								y = parent.y;
								parentNode = parent;
							}

						private final void render(Graphics g)
							{
								if (!daughterNodes.isEmpty())
									for (Node n : daughterNodes)
										n.render(g);
									{
										int x = (int) this.x - (leafSize / 2);
										int y = getY() - (leafSize / 2);

										if (isLeaf)
											{
												g.setColor(leafColour);
												g.fillOval(x, y, leafSize, leafSize);
												g.setColor(Color.BLACK);
												g.drawOval(x, y, leafSize, leafSize);
												if (canSeed)
													{
														g.setColor(oppositeColour);
														g.fillOval(x + leafSize / 4, y + leafSize / 4, leafSize / 2, leafSize / 2);
													}
											}
										if (this == lastSelectedNode)
											{
												g.setColor(Color.BLACK);
												g.drawOval(x - 3, y - 3, leafSize + 6, leafSize + 6);
											}
									}

								g.setColor(Color.BLACK);
								g.drawLine((int) x, getY(), (int) (parentNode.getX()), parentNode.getY());
							}

						private final void growUp()
							{
								y -= (int) StalkLength;
								for (Node n : daughterNodes)
									n.growUp();

								if (height < plantY - y)
									height = plantY - y;
							}

						private final void growDown()
							{
								if (y < plantY - StalkLength)
									{
										y += (int) StalkLength;
										for (Node n : daughterNodes)
											n.growDown();
									}
							}

						private final void growLeft()
							{
								x -= (int) StalkLength;
								for (Node n : daughterNodes)
									n.growLeft();
							}

						private final void growRight()
							{
								x += (int) StalkLength;
								for (Node n : daughterNodes)
									n.growRight();
							}

						private final void addNode(Node newNode)
							{
								daughterNodes.add(newNode);
							}

						private final Node getParentNode()
							{
								return parentNode;
							}

						private final Node getDaughterNode()
							{
								if (daughterNodes.size() > 0)
									return daughterNodes.get(daughterNodes.size() - 1);
								else
									return this;
							}

						private final void calculateLean()
							{
								lean -= plantX - x;
								for (Node n : daughterNodes)
									n.calculateLean();
							}

						private final void setLeaves()
							{
								if (daughterNodes.size() > 0)
									for (Node n : daughterNodes)
										n.setLeaves();
								else
									{
										isLeaf = true;
										for (Point p : leafLocations)
											if (p.x == (int) x && p.y == (int) y)
												isLeaf = false;
										if (isLeaf)
											leafLocations.add(new Point((int) x, (int) y));
									}
							}

						final int getX()
							{
								return (int) x;
							}

						final int getY()
							{
								return (int) y;
							}
					}
			}
	}