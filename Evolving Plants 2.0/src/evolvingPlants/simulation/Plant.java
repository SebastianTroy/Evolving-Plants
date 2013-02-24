package evolvingPlants.simulation;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import tools.RandTools;
import evolvingPlants.Hub;

public class Plant
	{
		public int plantX, minX, maxX;
		public static final int plantY = 550;

		private double energy, metabolism = 1, age = 0, fractionGrown = 0;
		private Genes genes;

		private NodeTree nodeTree;

		public boolean alive = true;

		private Color shadowColour = new Color(0, 0, 0);
		private boolean shadowsSet = false;

		public Plant(Seed seed)
			{
				plantX = (int) seed.getX();
				minX = maxX = plantX;
				genes = seed.genes;
				energy = seed.energy;

				nodeTree = new NodeTree(genes);

				int r = (int) ((255 - genes.leafColour.getRed()) * Hub.simWindow.sim.leafOpacity);
				int g = (int) ((255 - genes.leafColour.getGreen()) * Hub.simWindow.sim.leafOpacity);
				int b = (int) ((255 - genes.leafColour.getBlue()) * Hub.simWindow.sim.leafOpacity);
				shadowColour = new Color(r, g, b);
			}

		public final void tick(double secondsPassed)
			{
				if (fractionGrown < 1) // Growing
					{
						fractionGrown += (1 / metabolism) * secondsPassed;
					}
				else if (fractionGrown > 1)
					{
						nodeTree.setShadows();
						shadowsSet = true;
						fractionGrown = 1;
					}
				if (alive && energy > 0) // Alive
					{
						age += secondsPassed;
						metabolism += age / 100;
						energy -= metabolism * secondsPassed;
						nodeTree.baseNode.tick(secondsPassed);
					}
				else if (alive)
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

		private class NodeTree
			{
				private Node baseNode = new Node(plantX, plantY);

				private NodeTree(Genes genes)
					{
						baseNode.y -= Hub.simWindow.sim.stalkLength;
						Node currentNode = baseNode;

						while (genes.currentInstruction() != Genes.END_ALL)
							if (currentNode != null)
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
										case Genes.END_ALL:
											currentNode = null;
									}
							else
								genes.nextInstruction(false);

						baseNode.setLeaves();
						baseNode.parentNode = new Node(plantX, plantY);
					}

				private final void setShadows()
					{
						baseNode.setShadow();
					}

				private final void removeShadows()
					{
						baseNode.removeShadow();
					}

				private class Node
					{
						private double x, y;

						private boolean isLeaf = false;
						private boolean growingSeed = false;
						private double seedEnergy = 0, seedDelay = RandTools.getDouble(0.5, 1.75);

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
										energy += Hub.simWindow.sim.photosynthesizeAt(getX() + RandTools.getDouble(Hub.simWindow.sim.leafSize / -2, Hub.simWindow.sim.leafSize / 2), (int) y,
												genes.leafColour, (fractionGrown < 1 ? Color.BLACK : shadowColour));

										if (fractionGrown == 1)
											{
												if (!growingSeed)
													seedDelay -= secondsPassed;
												else
													{
														if (energy > genes.seedEnergyTransfer * secondsPassed)
															{
																seedEnergy += genes.seedEnergyTransfer * secondsPassed;
																energy -= genes.seedEnergyTransfer * secondsPassed;
															}
														if (seedEnergy > genes.seedEnergy)
															{
																growingSeed = false;
																seedEnergy = 0;
																Hub.simWindow.sim.addSeed(getX(), getY(), genes, genes.seedEnergy);
															}
													}
												if (seedDelay < 0)
													growingSeed = true;
											}
									}
								else
									for (Node n : daughterNodes)
										n.tick(secondsPassed);
							}

						private final void render(Graphics g, int simX)
							{
								int apparentX = (int) (getX() + simX);

								if (!isLeaf)
									for (Node n : daughterNodes)
										n.render(g, simX);
								else
									{
										int leafSize = (int) (fractionGrown * Hub.simWindow.sim.leafSize);
										int x = apparentX - (leafSize / 2);
										int y = getY() - (leafSize / 2);

										g.setColor(genes.leafColour);
										g.fillOval(x, y, leafSize, leafSize);
										g.setColor(Color.BLACK);
										g.drawOval(x, y, leafSize, leafSize);
									}
								g.setColor(Color.BLACK);
								g.drawLine(apparentX, getY(), (int) (parentNode.getX() + simX), parentNode.getY());
							}

						private final void growUp()
							{
								y -= Hub.simWindow.sim.stalkLength;
								for (Node n : daughterNodes)
									n.growUp();
							}

						private final void growLeft()
							{
								x -= Hub.simWindow.sim.stalkLength;
								for (Node n : daughterNodes)
									n.growLeft();
								if ((int) x < minX)
									minX = (int) (x - (Hub.simWindow.sim.leafSize / 2));
							}

						private final void growRight()
							{
								x += Hub.simWindow.sim.stalkLength;
								for (Node n : daughterNodes)
									n.growRight();
								if ((int) x > maxX)
									maxX = (int) (x + (Hub.simWindow.sim.leafSize / 2));
							}

						private final void addNode(Node newNode)
							{
								daughterNodes.add(newNode);
								newNode.y -= Hub.simWindow.sim.stalkLength;
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

						private final void setLeaves()
							{
								if (daughterNodes.size() > 0)
									for (Node n : daughterNodes)
										n.setLeaves();
								else
									isLeaf = true;
							}

						private final void setShadow()
							{
								if (daughterNodes.size() > 0)
									for (Node n : daughterNodes)
										n.setShadow();
								else
									Hub.simWindow.sim.addShadow(x, y, shadowColour);
							}

						private final void removeShadow()
							{
								if (daughterNodes.size() > 0)
									for (Node n : daughterNodes)
										n.removeShadow();
								else
									Hub.simWindow.sim.removeShadow(x, y, shadowColour);
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