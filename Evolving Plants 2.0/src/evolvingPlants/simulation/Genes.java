package evolvingPlants.simulation;

import java.awt.Color;
import java.util.LinkedList;

import tools.ColTools;
import tools.RandTools;
import evolvingPlants.Hub;

/**
 * A plant has Genes with a fixed number of instructions on how to grow, these
 * can mutate
 * 
 * @author Sebastian Troy
 */
public class Genes
	{
		public static final char ADD_NODE = 'N';
		public static final char CLIMB_NODE_TREE = '+';
		public static final char DESCEND_NODE_TREE = '-';
		public static final char NODE_CAN_SEED = 'S';
		public static final char GROW_UP = '^';
		public static final char GROW_LEFT = '<';
		public static final char GROW_RIGHT = '>';
		public static final char GROW_DOWN = 'v';
		public static final char SKIP = ' ';
		public static final char END_ALL = '|';

		private int currentInstruction = 0;
		private LinkedList<Character> instructions = new LinkedList<Character>();

		public Color leafColour;
		public Color seedColour = ColTools.randColour();
		public double seedEnergy, seedEnergyTransfer = 50;

		public Genes(String genes, double seedEnergy, int leafRed, int leafGreen, int leafBlue)
			{
				for (int i = 0; i < genes.length(); i++)
					instructions.add(genes.charAt(i));
				this.seedEnergy = seedEnergy;
				leafColour = new Color(leafRed, leafGreen, leafBlue);
			}

		public Genes(Genes parent, boolean mutate)
			{
				leafColour = parent.leafColour;
				seedColour = parent.seedColour;
				seedEnergy = parent.seedEnergy;
				seedEnergyTransfer = parent.seedEnergyTransfer;
				instructions = new LinkedList<Character>();

				instructions = aSexual(parent.instructions);

				if (mutate)
					mutate();
			}

		public Genes(Genes parentOne, Genes parentTwo)
			{
				leafColour = ColTools.interpolateColours(parentOne.leafColour, parentTwo.leafColour);
				seedColour = ColTools.interpolateColours(parentOne.seedColour, parentTwo.seedColour);
				seedEnergy = (parentOne.seedEnergy + parentTwo.seedEnergy) / 2;
				seedEnergyTransfer = (parentOne.seedEnergyTransfer + parentTwo.seedEnergyTransfer) / 2;
				instructions = new LinkedList<Character>();

				// TODO create genes from both parents

				if (areRelated(parentOne.instructions, parentTwo.instructions))
					instructions = sexual(parentOne.instructions, parentTwo.instructions);

				mutate();
			}

		public final int currentInstruction()
			{
				int instruction = END_ALL;
				if (currentInstruction < instructions.size())
					{
						instruction = instructions.get(currentInstruction);
					}

				return instruction;
			}

		public final int nextInstruction(boolean growing)
			{
				int instruction = END_ALL;
				if (currentInstruction < instructions.size())
					{
						instruction = instructions.get(currentInstruction);
						currentInstruction++;
					}

				if (instruction == END_ALL)
					currentInstruction = instructions.size();

				return instruction;
			}

		public final String getGenes()
			{
				char[] genes = new char[instructions.size()];

				for (int i = 0; i < instructions.size(); i++)
					genes[i] = instructions.get(i);

				return new String(genes);
			}

		private final LinkedList<Character> aSexual(LinkedList<Character> parent)
			{
				instructions = new LinkedList<Character>();

				for (int i = 0; i < parent.size(); i++)
					instructions.add(parent.get(i));

				return instructions;
			}

		private final LinkedList<Character> sexual(LinkedList<Character> parentOne, LinkedList<Character> parentTwo)
			{
				instructions = new LinkedList<Character>();

				for (int i = 0; i < parentOne.size() && i < parentTwo.size(); i++)
					instructions.add(RandTools.getBool() ? parentOne.get(i) : parentTwo.get(i));

				return instructions;
			}

		private final void mutate()
			{
				if (RandTools.randPercent() > Hub.simWindow.mutantOffspringSlider.getValue())
					return;

				seedEnergy += RandTools.getDouble(-10, 10);
				seedEnergyTransfer += RandTools.getDouble(-1, 1);

				// mutate seed colour
				int[] seedColours = { seedColour.getRed(), seedColour.getGreen(), seedColour.getBlue() };
				seedColours[RandTools.getInt(0, 2)] += RandTools.getInt(-12, 12);
				seedColour = ColTools.checkColour(seedColours[0], seedColours[1], seedColours[2]);

				// mutate leaf colour
				int[] leafColours = { leafColour.getRed(), leafColour.getGreen(), leafColour.getBlue() };
				leafColours[RandTools.getInt(0, 2)] += RandTools.getInt(-10, 10);
				leafColour = ColTools.checkColour(leafColours[0], leafColours[1], leafColours[2]);

				// Mutate instructions
				for (int i = 0; i < instructions.size(); i++)
					{
						if (RandTools.randPercent() < Hub.simWindow.dnaDamageSlider.getValue())
							{
								// Mutate instruction
								if (RandTools.randPercent() > 6)
									{
										instructions.remove(i);
										instructions.add(i, getRandomInstruction());
									}
								// Insert Extra instruction
								else if (RandTools.randPercent() > 3)
									{
										instructions.add(i, getRandomInstruction());
										i++;
									}
								// else delete instruction
								else
									{
										instructions.remove(i);
										i--;
									}
							}
					}
			}

		private final char getRandomInstruction()
			{
				switch (RandTools.getInt(0, 20))
					{
						case 0:
							return ADD_NODE;
						case 1:
							return CLIMB_NODE_TREE;
						case 2:
							return DESCEND_NODE_TREE;
						case 3:
							return GROW_UP;
						case 4:
							return GROW_LEFT;
						case 5:
							return GROW_RIGHT;
						case 6:
							return GROW_DOWN;
						case 7:
							return NODE_CAN_SEED;
						default:
							return SKIP;
					}
			}

		private final boolean areRelated(LinkedList<Character> parentOneCommands, LinkedList<Character> parentTwoCommands)
			{
				// Unrelated if too many differences in gene sequence
				int differences = 0;
				int acceptableDifference = (int) ((double) parentOneCommands.size() * Hub.simWindow.sim.geneCompatability);
				for (int i = 0; i < parentOneCommands.size(); i++)
					{
						if (parentOneCommands.get(i) != parentTwoCommands.get(i))
							{
								differences++;
								if (differences > acceptableDifference)
									return false;
							}
					}
				return true;
			}
	}