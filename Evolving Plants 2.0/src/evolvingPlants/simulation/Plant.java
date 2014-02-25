package evolvingPlants.simulation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;

import tools.NumTools;
import tools.RandTools;
import evolvingPlants.Main;

public class Plant
	{
		public int plantX, minX, maxX, leafOpacity;
		public static final int plantY = 550, leafSize = 15;

		private static final double MIN_NODE_SIZE = 0.1;

		private double height = 0, /* lean = 0, */plantEnergy, metabolism = 0;

		private Genome genes;

		private Node baseNode = new Node(null);

		public boolean alive = true, selected = false;

		public Plant(Genome parentGenes, int x)
			{
				plantX = x;
				minX = maxX = plantX;
				genes = parentGenes;
				plantEnergy = genes.seedEnergy - 1;
				leafOpacity = (int) Main.simWindow.leafOpacitySlider.getValue();

				baseNode.extractNodeInstructions(parentGenes.getUnpackedGenome());
				baseNode.calculateShape();
				// baseNode.calculateLean();
				baseNode.calculateBounds();
				baseNode.nodeEnergy++;

				minX -= leafSize / 2;
				maxX += leafSize / 2;
				height += leafSize / 2;

			}

		public final void tick()
			{
				if (!alive)
					return;

				// Remove the metabolism value from plant energy
				plantEnergy -= metabolism;
				// Increase metabolism with age
				metabolism += 0.0005;

				baseNode.tick();

				if (plantEnergy < 0 /* || Math.abs(lean) > 50 */)
					{
						if (alive)
							kill();
					}
				else if (plantEnergy > genes.seedEnergy)
					{
						plantEnergy -= genes.seedEnergy;

						if (RandTools.randPercent() > 80)
							Main.simWindow.sim.plantsToAdd.addFirst(new Plant(new Genome(genes, true), RandTools.getInt(minX - 30, maxX + 30)));
					}
			}

		public final void render(Graphics g, int simX)
			{
				if (maxX < -simX + 200 || minX > -simX + 1000)
					return;

				baseNode.render(g, simX);
			}

		public boolean contains(Point p)
			{
				// if nowhere near plant, return false
				if (p.x < minX || p.x > maxX || plantY - p.y > height)
					return false;

				return baseNode.contains(p);
			}

		public Genome getGenesCopy()
			{
				return new Genome(genes, false);
			}

		public final void kill()
			{
				if (alive)
					{
						baseNode.removeAllShadows();
						alive = false;
					}
			}

		/**
		 * Each {@link Plant} is made up of {@link Node}s arranged in a tree. Each {@link Node} takes care of itself, how grown it is, its shadow and also holds
		 * references to any daughter {@link Node}s it has.
		 * 
		 * @author Sebastian Troy
		 */
		private class Node
			{
				private Node parentNode;
				private ArrayList<Node> daughterNodes = new ArrayList<Node>();

				/*
				 * Proportion grown is initially set to 1 (full) so that the bounds of the plant can be calculated, it is set back to 0 once this has been done.
				 */
				private double proportionGrown = 1, maxSize, stemLength = 10, stemAngle = 180 /* UP */, nodeEnergy = 0;
				private int relativeX, relativeY, finalX, finalY;

				private boolean fullGrown = false;

				private boolean isLeaf = false;

				private Node(Node parentNode)
					{
						this.parentNode = parentNode;
						maxSize = 1;
					}

				private final void tick()
					{
						// if fully grown (a node should be fully grown the majority of the plants lifetime)
						if (proportionGrown == maxSize)
							{
								// If is a leaf
								if (isLeaf)
									{
										nodeEnergy += (Main.simWindow.sim.lightMap.getLightMinusShadowAt(getX() + RandTools.getInt((getLeafSize() / -2) + 1, (getLeafSize() / 2) - 1), getY(),
												leafOpacity) * proportionGrown) / 100;
									}

								// Pass this Node's energy onto daughter Nodes
								if (daughterNodes.size() > 0)
									for (Node n : daughterNodes)
										n.nodeEnergy += nodeEnergy / daughterNodes.size();
								// If no daughter nodes, give energy to plant
								else
									plantEnergy += nodeEnergy;

								// reset energy value because it hs been redistributed
								nodeEnergy = 0;

								// Process daughter nodes (after energy has been passed to them)
								for (Node n : daughterNodes)
									n.tick();
							}
						// if growing
						else if (proportionGrown < maxSize)
							{
								// If is a leaf
								if (isLeaf)
									{
										nodeEnergy += (Main.simWindow.sim.lightMap.getLightAt(getX() + RandTools.getInt((getLeafSize() / -2) + 1, (getLeafSize() / 2) - 1), getY()) * proportionGrown) / 255.0;
									}

								proportionGrown += nodeEnergy / stemLength;
								nodeEnergy = 0;

								// if fully or over-grown, only ever called once
								if (proportionGrown >= maxSize)
									{
										// Recuperate any excess energy used to grow too large
										nodeEnergy += (proportionGrown - maxSize) * stemLength;
										// Set size to the correct size
										proportionGrown = maxSize;
										// Assuming all Nodes below this one are fully grown
										finalX = getX();
										finalY = getY();
										fullGrown = true;
										setNodeShadow();
									}
							}
					}

				private final void render(Graphics g, int simX)
					{
						int parentX = (int) (parentNode == null ? plantX + simX : (parentNode.getX() + simX));
						int parentY = (int) (parentNode == null ? plantY : parentNode.getY());

						int apparentX = (int) (getX() + simX);

						// Draw the stem of the node
						g.setColor(selected ? Color.LIGHT_GRAY : Color.BLACK);
						g.drawLine(apparentX, getY(), parentX, parentY);

						// Only draw daughter nodes when fully grown (they wont have grown until that point)
						if (fullGrown)
							for (Node n : daughterNodes)
								n.render(g, simX);

						if (isLeaf)
							{
								int leafSize = (int) getLeafSize();
								int x = apparentX - (leafSize / 2);
								int y = getY() - (leafSize / 2);

								// Fill the leaf with colour
								g.setColor(genes.leafColour);
								g.fillOval(x, y, leafSize, leafSize);
								// outline the leaf
								g.setColor(selected ? Color.LIGHT_GRAY : Color.BLACK);
								g.drawOval(x, y, leafSize, leafSize);
							}
					}

				private final int getX()
					{
						if (fullGrown)
							return finalX;
						else
							{
								if (parentNode != null)
									return (int) (parentNode.getX() + (relativeX * proportionGrown));
								else
									return (int) (plantX + (relativeX * proportionGrown));
							}
					}

				private final int getY()
					{
						if (fullGrown)
							return finalY;
						else
							{
								if (parentNode != null)
									return (int) (parentNode.getY() + (relativeY * proportionGrown));
								else
									return (int) (plantY + (relativeY * proportionGrown));
							}
					}

				private final int getLeafSize()
					{
						return (int) (leafSize * proportionGrown);
					}

				private final boolean contains(Point p)
					{
						if (isLeaf && NumTools.distance(p.x, p.y, getX(), getY()) < getLeafSize())
							return true;

						for (Node n : daughterNodes)
							if (n.contains(p))
								return true;

						return false;
					}

				/**
				 * Shadows are set on an individual node basis as a node may be fully grown before the entire plant is.
				 */
				private final void setNodeShadow()
					{
						if (isLeaf)
							Main.simWindow.sim.addShadow(finalX, finalY, getLeafSize(), leafOpacity);
					}

				/**
				 * If this is called then the pant is dead, any shadow ever cast by this plant must be removed from the simulation.
				 */
				private final void removeAllShadows()
					{
						for (Node n : daughterNodes)
							n.removeAllShadows();

						// if is a leaf and has had its shadow previously set
						if (isLeaf && proportionGrown == maxSize)
							Main.simWindow.sim.removeShadow(finalX, finalY, getLeafSize(), leafOpacity);
					}

				private final void calculateBounds()
					{
						for (Node n : daughterNodes)
							n.calculateBounds();

						if (height < plantY - getY())
							height = plantY - getY();

						if (getX() < minX)
							minX = getX() - (getLeafSize() / 2);

						if (getX() > maxX)
							maxX = getX() + (getLeafSize() / 2);

						// Once our final position has been used to calculate the bounds of the plant, set our proportion grown to 0
						proportionGrown = 0;
					}

				// private final void calculateLean()
				// {
				// lean -= (plantX - getX()) * maxSize;
				// for (Node n : daughterNodes)
				// n.calculateLean();
				// }

				private final LinkedList<Character> extractNodeInstructions(LinkedList<Character> allPlantInstructions)
					{
						// Store a list of all instructions specifically for this Node.
						LinkedList<Character> nodeInstructions = new LinkedList<Character>();

						char c;

						// While there are still instructions to process
						while (allPlantInstructions.size() > 0)
							{
								// Remove the first instruction to process
								c = allPlantInstructions.pop();

								// If the node is big enough to be part of the final plant
								if (maxSize > 0.2)
									{
										// If it signifies the start of a new node
										if (c == Genome.START_NODE)
											{
												// Create a new node
												Node n = new Node(this);
												// Tell the Node how big it will be
												n.maxSize = maxSize - genes.taper;

												// Pass on the instructions and get in return the instructions, minus all instructions relevant to that node
												allPlantInstructions = n.extractNodeInstructions(allPlantInstructions);

												// If the leaf is big enough AND the daughter Node is a leaf or has branches which eventually terminate in
												// leaves
												if (n.maxSize > 0.1 && (n.isLeaf || n.daughterNodes.size() > 0))
													// Add the daughter node to our list to keep track of it
													daughterNodes.add(n);
											}
										// If it signifies the end of this node
										else if (c == Genome.END_NODE)
											// stop processing instructions
											break;
										else
											nodeInstructions.addFirst(c);
									}
							}

						// For each instruction
						for (int i = 0; i < nodeInstructions.size(); i++)
							{
								switch (nodeInstructions.get(i))
									{
										case (Genome.GROW):
											stemLength += 10;
											break;
										case (Genome.ROTATE_LEFT):
											stemAngle += 10;
											break;
										case (Genome.ROTATE_RIGHT):
											stemAngle -= 10;
										case (Genome.TOGGLE_LEAF):
											isLeaf = !isLeaf;
											break;
									}
							}

						// Pass on any rotation to any daughter Nodes
						rotate(stemAngle - 180);

						// Return any remaining instructions so the parent Node can keep processing them
						return allPlantInstructions;
					}

				private final void rotate(double angle)
					{
						stemAngle += angle;
						for (Node n : daughterNodes)
							n.rotate(angle);
					}

				/**
				 * Takes {@link Node#stemAngle} and {@link Node#stemLength} and converts that into the {@link Node}s' position relative to
				 * {@link Node#parentNode}.
				 */
				private final void calculateShape()
					{
						double[] vector = NumTools.getVector(stemAngle);
						relativeX = (int) ((vector[0] * stemLength) + RandTools.getDouble(-2, 2));
						relativeY = (int) ((vector[1] * stemLength) + RandTools.getDouble(-2, 2));

						if (maxSize < MIN_NODE_SIZE)
							daughterNodes.clear();
						for (Node n : daughterNodes)
							{
								n.maxSize = maxSize - genes.taper;
								n.calculateShape();
							}
					}
			}
	}