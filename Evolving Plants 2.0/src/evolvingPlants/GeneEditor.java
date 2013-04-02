package evolvingPlants;

import java.awt.Color;
import java.awt.Graphics;

import tCode.RenderableObject;

public class GeneEditor extends RenderableObject
	{
		// Plant variables
		private double plantX = 700, plantY = 500;
		private Color leafColour = new Color(175, 175, 175);

		@Override
		protected void initiate()
			{
				// TODO Auto-generated method stub

			}

		@Override
		public void tick(double secondsPassed)
			{
				// TODO Auto-generated method stub

			}

		@Override
		protected void render(Graphics g)
			{
				g.setColor(Color.CYAN);
				g.fillRect(0, 0, 700, 500);
				g.setColor(Color.DARK_GRAY);
				g.fillRect(0, 500, Hub.canvasWidth, 100);
				g.setColor(Color.WHITE);
				g.fillRect(700, 0, 500, 500);
			}
/*
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
									switch (genes.nextInstruction(true))
										{
											case Genes.ADD_NODE:
												currentNode.addNode(new Node(currentNode));
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

						private final void render(Graphics g, int simX)
							{
								if (!isLeaf)
									for (Node n : daughterNodes)
										n.render(g, simX);
								else
									{
										int leafSize = (int) getLeafSize();
										int x = x - (leafSize / 2);
										int y = getY() - (leafSize / 2);

										g.setColor(leafColour);
										g.fillOval(x, y, leafSize, leafSize);
										g.setColor(Color.BLACK);
										g.drawOval(x, y, leafSize, leafSize);
									}
								g.setColor(Color.BLACK);
								g.drawLine(x, getY(), (int) (parentNode.getX() + simX), parentNode.getY());
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
							}

						private final void setShadow()
							{
								if (daughterNodes.size() > 0)
									for (Node n : daughterNodes)
										n.setShadow();
								else if (isLeaf)
									Hub.simWindow.sim.addShadow(x, y, leafSize, shadowColour);
							}

						private final void removeShadow()
							{
								if (daughterNodes.size() > 0)
									for (Node n : daughterNodes)
										n.removeShadow();
								else if (isLeaf)
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
			}*/
	}