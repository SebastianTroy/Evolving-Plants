package evolvingPlants.simulation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import tools.RandTools;
import evolvingPlants.Hub;

public class Plant
	{
		public static final int LARGE = 0;
		public static final int MEDIUM = 1;
		public static final int SMALL = 2;

		public int plantX, minX, maxX, leafSize, sizeCategory;
		public static final int plantY = 550;

		private double height = 0, lean = 0, energy, metabolism = 1, fractionGrown = 0;
		private Genes genes;

		private NodeTree nodeTree;

		public boolean alive = true, selected = false;

		private Color shadowColour = new Color(0, 0, 0);
		private boolean shadowsSet = false;

		public Plant(Seed seed)
			{
				plantX = (int) seed.x;
				minX = maxX = plantX;
				genes = seed.genes;
				energy = seed.energy;
				leafSize = (int) Hub.simWindow.leafSizeSlider.getValue();

				nodeTree = new NodeTree(genes);

				minX -= leafSize / 2;
				maxX += leafSize / 2;
				height += leafSize / 2;
			}

		public final void tick(double secondsPassed)
			{
				if (!alive)
					return;

				if (fractionGrown < 1) // Growing
					{
						fractionGrown += (1.75 / metabolism) * secondsPassed;
					}
				else if (fractionGrown > 1) // full grown - only called once
					{
						double leafOpacity = 1.0 - (Hub.simWindow.leafOpacitySlider.getValue() / 100.0);
						int r = (int) ((255 - genes.leafColour.getRed()) * leafOpacity);
						int g = (int) ((255 - genes.leafColour.getGreen()) * leafOpacity);
						int b = (int) ((255 - genes.leafColour.getBlue()) * leafOpacity);
						shadowColour = new Color(r, g, b);
						nodeTree.setShadows();
						shadowsSet = true;
						fractionGrown = 1;
					}
				if (alive && energy > 0) // Alive
					{
						metabolism += secondsPassed * 2;
						energy -= metabolism * secondsPassed;
						nodeTree.baseNode.tick(secondsPassed);
					}
				else
					{
						if (shadowsSet)
							nodeTree.removeShadows();
						alive = false;
					}
			}

		public final void render(Graphics g, int simX)
			{
				if (maxX < -simX + 200 || minX > -simX + 1000)
					return;

				nodeTree.baseNode.render(g, simX);
			}

		public boolean contains(Point p)
			{
				// if nowhere near plant, return false
				if (p.x < minX || p.x > maxX || plantY - p.y > height)
					return false;

				return nodeTree.contains(p);
			}

		public Genes getGenesCopy()
			{
				return new Genes(genes, false);
			}

		public final void kill()
			{
				energy = -9999999;
				metabolism = 999999;
			}

		private double getLeafSize()
			{
				return fractionGrown * leafSize;
			}

		private class NodeTree
			{
				private Node baseNode = new Node(plantX, plantY);

				private NodeTree(Genes genes)
					{
						baseNode.growUp();
						Node currentNode = baseNode;

						while (genes.currentInstruction() != Genes.END_ALL)
							if (currentNode != null)
								{
									// Every instruction ups metabolism
									metabolism += 0.05;
									switch (genes.nextInstruction(true))
										{
											case Genes.ADD_NODE:
												metabolism++;
												currentNode.addNode(new Node(currentNode));
												break;
											case Genes.CLIMB_NODE_TREE:
												currentNode = currentNode.getDaughterNode();
												break;
											case Genes.DESCEND_NODE_TREE:
												currentNode = currentNode.getParentNode();
												break;
											case Genes.GROW_UP:
												metabolism++;
												currentNode.growUp();
												break;
											case Genes.GROW_LEFT:
												metabolism += 0.25;
												currentNode.growLeft();
												break;
											case Genes.GROW_RIGHT:
												metabolism += 0.25;
												currentNode.growRight();
												break;
											case Genes.GROW_DOWN:
												metabolism += 0.1;
												currentNode.growDown();
												break;
											case Genes.SKIP:
												metabolism += 0.20;
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

						if (Math.abs(lean) / height > 0.6)
							alive = false;

						if (height > Hub.simWindow.largePlantSizeSlider.getValue())
							sizeCategory = LARGE;
						else if (height > Hub.simWindow.mediumPlantSizeSlider.getValue())
							sizeCategory = MEDIUM;
						else
							sizeCategory = SMALL;
					}

				private final void setShadows()
					{
						baseNode.setShadow();
					}

				private final void removeShadows()
					{
						baseNode.removeShadow();
					}

				private boolean contains(Point p)
					{
						return baseNode.contains(p);
					}

				private class Node
					{
						private double x, y;

						private boolean isLeaf = false;
						private double seedEnergy = 0;

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

						private final void tick(double secondsPassed)
							{
								if (isLeaf)
									{
										// NOTE: seedlings do not have shadows
										// hence BLACK shadow
										energy += Hub.simWindow.sim.photosynthesizeAt(getX() + RandTools.getDouble((int) leafSize / -2, (int) leafSize / 2), (int) y, genes.leafColour, shadowColour)
												* secondsPassed;

										if (fractionGrown == 1)
											{
												if (energy > genes.seedEnergyTransfer * secondsPassed)
													{
														seedEnergy += genes.seedEnergyTransfer * secondsPassed;
														energy -= genes.seedEnergyTransfer * secondsPassed;
													}
												if (seedEnergy > genes.seedEnergy)
													{
														/*
														 * make seed twice as
														 * energetically
														 * expensive
														 */
														energy -= seedEnergy;
														seedEnergy = 0;
														Hub.simWindow.sim.addSeed(getX(), getY(), genes, genes.seedEnergy, sizeCategory);
													}
											}
									}
								else
									for (Node n : daughterNodes)
										n.tick(secondsPassed);
							}

						private final void render(Graphics g, int simX)
							{
								int apparentX = (int) (getX() + simX);

								g.setColor(selected ? Color.LIGHT_GRAY : Color.BLACK);
								g.drawLine(apparentX, getY(), (int) (parentNode.getX() + simX), parentNode.getY());

								if (!daughterNodes.isEmpty())
									for (Node n : daughterNodes)
										n.render(g, simX);
								else if (isLeaf)
									{
										int leafSize = (int) getLeafSize();
										int x = apparentX - (leafSize / 2);
										int y = getY() - (leafSize / 2);

										g.setColor(genes.leafColour);
										g.fillOval(x, y, leafSize, leafSize);
										g.setColor(selected ? Color.LIGHT_GRAY : Color.BLACK);
										g.drawOval(x, y, leafSize, leafSize);
									}
							}

						private boolean contains(Point p)
							{
								if (!isLeaf)
									{
										for (Node n : daughterNodes)
											if (n.contains(p))
												return true;
									}
								else
									{
										Point2D leaf = new Point(getX(), getY());
										if (leaf.distance(p) < (leafSize / 2))
											return true;

									}
								return false;
							}

						private final void growUp()
							{
								y -= (int) Hub.simWindow.stalkLengthSlider.getValue();
								if (height < plantY - y)
									height = plantY - y;
								for (Node n : daughterNodes)
									n.growUp();
							}

						private final void growDown()
							{
								if (y < plantY - Hub.simWindow.stalkLengthSlider.getValue())
									{
										y += (int) Hub.simWindow.stalkLengthSlider.getValue();
										for (Node n : daughterNodes)
											n.growDown();
									}
							}

						private final void growLeft()
							{
								x -= (int) Hub.simWindow.stalkLengthSlider.getValue();
								for (Node n : daughterNodes)
									n.growLeft();
								if ((int) x < minX)
									minX = (int) (x - ((int) leafSize / 2));
							}

						private final void growRight()
							{
								x += (int) Hub.simWindow.stalkLengthSlider.getValue();
								for (Node n : daughterNodes)
									n.growRight();
								if ((int) x > maxX)
									maxX = (int) (x + ((int) leafSize / 2));
							}

						private final void addNode(Node newNode)
							{
								daughterNodes.add(newNode);
								newNode.growUp();
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
								else if (parentNode == this || (parentNode.x != x && parentNode.y != y))
									isLeaf = true;
								else if (!parentNode.isLeaf)
									parentNode.isLeaf = true;
							}

						private final void setShadow()
							{
								if (daughterNodes.size() > 0)
									for (Node n : daughterNodes)
										n.setShadow();
								
								if (isLeaf)
									Hub.simWindow.sim.addShadow(x, y, leafSize, shadowColour);
							}

						private final void removeShadow()
							{
								if (daughterNodes.size() > 0)
									for (Node n : daughterNodes)
										n.removeShadow();
								
								if (isLeaf)
									Hub.simWindow.sim.removeShadow(x, y, leafSize, shadowColour);
							}

						final int getX()
							{
								if (fractionGrown < 1)
									return (int) (plantX + ((x - plantX) * fractionGrown));

								return (int) x;
							}

						final int getY()
							{
								if (fractionGrown < 1)
									return (int) (plantY + ((y - plantY) * fractionGrown));

								return (int) y;
							}
					}
			}
	}