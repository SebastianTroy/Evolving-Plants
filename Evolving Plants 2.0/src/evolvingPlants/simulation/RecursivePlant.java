package evolvingPlants.simulation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;

import tools.NumTools;
import tools.RandTools;
import evolvingPlants.Main;

public class RecursivePlant
	{
		public int plantX, minX, maxX, leafSize, leafOpacity;
		public static final int plantY = 550;

		private static final double MIN_NODE_SIZE = 0.1;

		private double height = 0, lean = 0, plantEnergy, metabolism = 1;

		private RecursiveGenes genes;

		private Node baseNode = new Node(null);

		public boolean alive = true, selected = false;

		public RecursivePlant(RecursiveGenes parentGenes, int x)
			{
				plantX = x;
				minX = maxX = plantX;
				genes = parentGenes;
				plantEnergy = genes.seedEnergy;
				leafSize = (int) Main.simWindow.leafSizeSlider.getValue();
				leafOpacity = (int) Main.simWindow.leafOpacitySlider.getValue();

				baseNode.extractNodeInstructions(parentGenes.getGenes());
				baseNode.calculateShape();
				baseNode.calculateBounds();
				baseNode.calculateLean();
				// TODO work out if plant leans too far...

				minX -= leafSize / 2;
				maxX += leafSize / 2;
				height += leafSize / 2;

			}

		public final void tick()
			{
				if (!alive)
					{
						System.out.println("DEAD PLANT NOT REMOVED");
						return;
					}

				// TODO
				/*
				 * pass energy from one fully grown node to the next, if no daughter nodes pass energy to plant, use energy to grow a node, more energy =
				 * quicker growth, once a plant has enough energy to make a seed, seed, get rid of seed, simply place a new plant at desired location and wait
				 * for space.
				 * 
				 * When a plant germinates, it has a set amount of energy passed to it from its parent, this goes down each tick, at a faster rate as the plant
				 * ages, only once the plant is fully grown does this number start going up, so it must grow fully before its seed energy runs out in order to
				 * survive. If the energy goes over a threshold, the plant will drop a seed and pass that amount of energy onto its seed.
				 */

				// 1 energy from the plant's metabolism is passed to the base Node for growth
				baseNode.nodeEnergy++; 
				// Remove the metabolism value from plant energy
				plantEnergy -= metabolism;
				// Increase metabolism with age
				metabolism += 0.01; // TODO work out a decent value for this
				
				baseNode.tick();

				if (plantEnergy < 0)
					{
						if (alive)
							kill();
					}
				else if (plantEnergy > genes.seedEnergy)
					{
						plantEnergy -= genes.seedEnergy;
						Main.simWindow.sim.plantsToAdd.add(new RecursivePlant(new RecursiveGenes(genes, true), RandTools.getInt(minX - 40, maxX + 40)));
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

		public RecursiveGenes getGenesCopy()
			{
				return new RecursiveGenes(genes, false);
			}

		public final void kill()
			{
				baseNode.removeAllShadows();
				alive = false;
			}

		/**
		 * Each {@link RecursivePlant} is made up of {@link Node}s arranged in a tree. Each {@link Node} takes care of itself, how grown it is, its shadow and
		 * also holds references to any daughter {@link Node}s it has.
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
										plantEnergy += Main.simWindow.sim.lightMap.getLightMinusShadowAt(getX() + RandTools.getInt(getLeafSize() / -2, getLeafSize() / 2), getY(), leafOpacity)
												* proportionGrown;
										System.out.println(Main.simWindow.sim.lightMap.getLightMinusShadowAt(getX() + RandTools.getInt(getLeafSize() / -2, getLeafSize() / 2), getY(), leafOpacity));
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
								proportionGrown += nodeEnergy / stemLength;
								nodeEnergy = 0;

								// if over-grown
								if (proportionGrown > maxSize)
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
									return (int) (parentNode.getX() + (relativeY * proportionGrown));
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
						// If is a terminating node (aka a leaf)
						if (daughterNodes.size() == 0)
							return NumTools.distance(p.x, p.y, getX(), getY()) < leafSize;
						else
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
							Main.simWindow.sim.addShadow(finalX, finalY, leafSize, leafOpacity);
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
							Main.simWindow.sim.removeShadow(finalX, finalY, leafSize, leafOpacity);
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

				// TODO needed? check while growing and make plants that suddenly exceed it topple over? (maybe too much...)
				private final void calculateLean()
					{
						lean -= plantX - getX();
						for (Node n : daughterNodes)
							n.calculateLean();
					}

				private final LinkedList<Character> extractNodeInstructions(LinkedList<Character> allPlantInstructions)
					{
						// Store a list of all instructions specifically for this Node.
						LinkedList<Character> nodeInstructions = new LinkedList<Character>();

						// While there are still instructions to process
						while (allPlantInstructions.size() > 0)
							{
								// Remove the first instruction to process
								Character c = allPlantInstructions.pop();
								// If it signifies the start of a new node
								if (c == RecursiveGenes.START_NODE)
									{
										// Create a new node
										Node n = new Node(this);
										// Pass on the instructions and get in return the instructions, minus all instructions relevant to that node
										allPlantInstructions = n.extractNodeInstructions(allPlantInstructions);
										// Add the daughter node to our list to keep track of it
										daughterNodes.add(n);
									}
								// If it signifies the end of this node
								else if (c == RecursiveGenes.END_NODE)
									// stop processing instructions
									break;
								else
									nodeInstructions.addFirst(c);
							}

						// For each instruction
						for (int i = 0; i < nodeInstructions.size(); i++)
							{
								char c = nodeInstructions.get(i);
								switch (c)
									{
										case (RecursiveGenes.GROW):
											stemLength += 10;
											break;
										case (RecursiveGenes.ROTATE_LEFT):
											stemAngle -= 18;
											break;
										case (RecursiveGenes.ROTATE_RIGHT):
											stemAngle += 18;
										case (RecursiveGenes.TOGGLE_LEAF):
											isLeaf = !isLeaf;
											break;
									}
							}

						// Pass on any rotation to any daughter Nodes
						for (Node n : daughterNodes)
							n.stemAngle += stemAngle - 180;

						// Return any remaining instructions so the parent Node can keep processing them
						return allPlantInstructions;
					}

				private final void calculateShape()
					{
						double[] vector = NumTools.getVector(stemAngle);
						relativeX = (int) (vector[0] * stemLength);
						relativeY = (int) (vector[1] * stemLength);

						if (maxSize < MIN_NODE_SIZE)
							daughterNodes.clear();
						for (Node n : daughterNodes)
							{
								n.calculateShape();
								n.maxSize -= genes.taper;
							}
					}
			}
	}