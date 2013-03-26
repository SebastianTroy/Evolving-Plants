package evolvingPlants.simulation;

import java.awt.Color;

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
		public static final char GROW_UP = '^';
		public static final char GROW_LEFT = '<';
		public static final char GROW_RIGHT = '>';
		public static final char GROW_DOWN = 'v';
		public static final char SKIP = ' ';
		public static final char END_ALL = '|';

		private int currentInstruction = 0;
		private char[] instructions = { END_ALL };

		Color leafColour = new Color(175, 175, 175);
		Color seedColour = ColTools.randColour();
		public double seedEnergy = 100, seedEnergyTransfer = 15;

		public Genes(int numInstructions)
			{
				if (numInstructions < 1)
					numInstructions = 1;

				instructions = new char[numInstructions];
				for (int i = 0; i < numInstructions; i++)
					instructions[i] = SKIP;
			}

		public Genes(Genes parent, boolean mutate)
			{
				leafColour = parent.leafColour;
				seedColour = parent.seedColour;
				seedEnergy = parent.seedEnergy;
				seedEnergyTransfer = parent.seedEnergyTransfer;
				instructions = new char[parent.instructions.length];

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
				instructions = new char[parentOne.instructions.length];

				if (areRelated(parentOne.instructions, parentTwo.instructions))
					instructions = sexual(parentOne.instructions, parentTwo.instructions);

				mutate();
			}

		public final int currentInstruction()
			{
				int instruction = END_ALL;
				if (currentInstruction < instructions.length)
					{
						instruction = instructions[currentInstruction];
					}

				return instruction;
			}

		public final int nextInstruction(boolean growing)
			{
				int instruction = END_ALL;
				if (currentInstruction < instructions.length)
					{
						instruction = instructions[currentInstruction];
						currentInstruction++;
					}

				if (instruction == END_ALL)
					currentInstruction = instructions.length;

				return instruction;
			}

		public final String getGenes()
			{
				return new String(instructions);
			}

		private final char[] aSexual(char[] parent)
			{
				instructions = new char[parent.length];

				for (int i = 0; i < parent.length; i++)
					instructions[i] = parent[i];

				return instructions;
			}

		private final char[] sexual(char[] parentOne, char[] parentTwo)
			{
				instructions = new char[parentOne.length];

				for (int i = 0; i < parentOne.length; i++)
					instructions[i] = RandTools.getBool() ? parentOne[i] : parentTwo[i];

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
				seedColours[RandTools.getInt(0, 2)] += RandTools.getInt(-6, 6);
				seedColour = ColTools.checkColour(seedColours[0], seedColours[1], seedColours[2]);

				// mutate leaf colour
				int[] leafColours = { leafColour.getRed(), leafColour.getGreen(), leafColour.getBlue() };
				leafColours[RandTools.getInt(0, 2)] += RandTools.getInt(-10, 10);
				leafColour = ColTools.checkColour(leafColours[0], leafColours[1], leafColours[2]);

				// Mutate instructions
				for (int i = 0; i < instructions.length; i++)
					{
						if (RandTools.randPercent() < Hub.simWindow.dnaDamageSlider.getValue())
							instructions[i] = getRandomInstruction();
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
						default:
							return SKIP;
					}
			}

		private final boolean areRelated(char[] parentOneCommands, char[] parentTwoCommands)
			{
				// Unrelated if too many differences in gene sequence
				int differences = 0;
				int acceptableDifference = (int) ((double) parentOneCommands.length * Hub.simWindow.sim.geneCompatability);
				for (int i = 0; i < parentOneCommands.length; i++)
					{
						if (parentOneCommands[i] != parentTwoCommands[i])
							{
								differences++;
								if (differences > acceptableDifference)
									return false;
							}
					}

				return true;
			}
	}