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
		private Color leafColour = new Color(175, 175, 175);
		private NodeTree examplePlant = new NodeTree(new Genes(" ", 100, 175, 175, 175));
		private double height = 0, lean = 0;

		public final TMenu plantOptionsMenu = new TMenu(450, 0, 250, 500, TMenu.VERTICAL);
		public final TSlider seedSizeSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider redLeafSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider greenLeafSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider blueLeafSlider = new TSlider(TSlider.HORIZONTAL);

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

				TLabel plantOptionsLabel = new TLabel("Plant Options");
				plantOptionsLabel.setFontSize(15);
				plantOptionsLabel.setBackgroundColour(new Color(0, 200, 200));
				plantOptionsMenu.add(plantOptionsLabel, false);
				plantOptionsMenu.add(seedSizeSlider);
				plantOptionsMenu.add(redLeafSlider);
				plantOptionsMenu.add(greenLeafSlider);
				plantOptionsMenu.add(blueLeafSlider);

				redLeafSlider.setValue(175.0);
				greenLeafSlider.setValue(175.0);
				blueLeafSlider.setValue(175.0);

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
				instructionMenu.add(getInstructionLabel("Climb up node tree = +"), false);
				instructionMenu.add(getInstructionLabel("Climb down node tree = -"), false);
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
				g.setColor(Color.WHITE);
				g.fillRect(700, 0, 500, 500);

				g.setColor(Color.BLACK);
				g.drawString((Math.abs(lean) / height > 0.6) ? "Plant not Viable, too much lean!" : "Plant Viable", 710, 30);

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

										saveNameField.setText(chooser.getSelectedFile().getName());
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
			}

		// TODO sort out hoe leaves are decided and rendering of Node trees here
		// and in Plant.java

		private class NodeTree
			{
				private ArrayList<Point> leafLocations = new ArrayList<Point>(5);

				private Node baseNode = new Node(plantX, plantY);

				private NodeTree(Genes genes)
					{
						baseNode.growUp();
						Node currentNode = baseNode;

						while (genes.currentInstruction() != Genes.END_ALL)
							if (currentNode != null)
								{
									switch (genes.nextInstruction(true))
										{
											case Genes.ADD_NODE:
												Node newNode = new Node(currentNode);
												currentNode.addNode(newNode);
												currentNode = newNode;
												break;
											case Genes.CLIMB_NODE_TREE:
												currentNode = currentNode.getDaughterNode();
												break;
											case Genes.DESCEND_NODE_TREE:
												currentNode = currentNode.getParentNode();
												break;
											case Genes.GROW_UP:
												currentNode.growUp();
												break;
											case Genes.GROW_LEFT:
												currentNode.growLeft();
												break;
											case Genes.GROW_RIGHT:
												currentNode.growRight();
												break;
											case Genes.GROW_DOWN:
												currentNode.growDown();
												break;
											case Genes.SKIP:
												break;
											case Genes.END_ALL:
												currentNode = null;
										}
								}
							else
								genes.nextInstruction(false);

						baseNode.calculateLean();
						baseNode.setLeaves();
						baseNode.parentNode = new Node(plantX, plantY);
					}

				private class Node
					{
						private static final double StalkLength = 20;

						private double x, y;

						private boolean isLeaf = false;

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
								if (isLeaf)
									{
										int x = (int) this.x - (leafSize / 2);
										int y = getY() - (leafSize / 2);

										g.setColor(leafColour);
										g.fillOval(x, y, leafSize, leafSize);
										g.setColor(Color.BLACK);
										g.drawOval(x, y, leafSize, leafSize);
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