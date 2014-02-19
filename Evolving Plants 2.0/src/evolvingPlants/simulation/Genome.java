package evolvingPlants.simulation;

import java.awt.Color;
import java.util.LinkedList;

import evolvingPlants.Main;

import tools.ColTools;
import tools.RandTools;

/**
 * This class represents a plant's genetics
 * 
 * @author Sebastian Troy
 */
public class Genome
	{
		private static final int MAX_RECURSIONS = 10;
		private static final int MAX_GENES = 5;

		public static final char GROW = 'a';
		public static final char ROTATE_LEFT = 'b';
		public static final char ROTATE_RIGHT = 'c';
		public static final char TOGGLE_LEAF = 'd';
		public static final char START_NODE = '{';
		public static final char END_NODE = '}';
		public static final char NOTHING = '~';

		@SuppressWarnings("unchecked")
		private final LinkedList<Character>[] genes = new LinkedList[MAX_GENES];

		private boolean genesUnpacked = false;
		private final LinkedList<Character> unpackedGenome = new LinkedList<Character>();

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		Color leafColour;
		double seedEnergy, /* energy to seeds */baseNodeSize, taper /* the proportion by which the plant gets smaller each branch */;

		public Genome()
			{
				for (int i = 0; i < MAX_GENES; i++)
					{
						genes[i] = new LinkedList<Character>();
						for (int num = 0; num < 10; num++)
							genes[i].add(NOTHING);

					}

				genes[0].add(GROW);
				genes[0].add(TOGGLE_LEAF);
				genes[0].add('B');
				genes[1].add('C');
				genes[2].add('D');

				leafColour = ColTools.randColour();
				seedEnergy = 70;
				baseNodeSize = 1;
				taper = 0.1;
			}

		public Genome(Genome parentGenes)
			{
				this(parentGenes, true);
			}

		public Genome(Genome parentGenes, boolean mutate)
			{
				// For each set of genes (or as many allowed by the MAX_UNPACKABLES variable)
				for (int i = 0; i < MAX_GENES || i < parentGenes.genes.length; i++)
					{
						this.genes[i] = new LinkedList<Character>();
						for (int charIndex = 0; charIndex < parentGenes.genes[i].size(); charIndex++)
							this.genes[i].addLast(new Character(parentGenes.genes[i].get(charIndex)));
					}

				// Get other gene variables too
				leafColour = parentGenes.leafColour;
				seedEnergy = parentGenes.seedEnergy;
				baseNodeSize = parentGenes.baseNodeSize;
				taper = parentGenes.taper;

				if (mutate)
					mutate();
			}

		public final LinkedList<Character> getUnpackedGenome()
			{
				if (!genesUnpacked)
					unpackGenes();
				return unpackedGenome;
			}

		private final void unpackGenes()
			{
				// Construct the entire genome by unpacking A (the first gene)
				unpackedGenome.addAll(genes[0]);

				boolean completed;

				// For as many recursions as are allowed
				for (int recursions = 0; recursions < MAX_RECURSIONS; recursions++)
					{
						// Assume this will be the last run for unpacking the genome (we can correct ourselves alter)
						completed = true;

						// Work through the genome backwards to avoid getting stuck in a recursive unpacking loop
						for (int i = unpackedGenome.size() - 1; i >= 0; i--)
							{
								char c = unpackedGenome.get(i);
								// If the character is unpackable
								if (Character.isUpperCase(c))
									{
										// Remove the capital letter so it isn't unpacked again
										unpackedGenome.remove(i);

										// If the capital letter is a valid unpackable letter
										if (c - 65 < MAX_GENES)
											{
												unpackedGenome.add(START_NODE);
												// Add the letters that the Capital letter represented to the genome
												unpackedGenome.addAll(i, genes[c - 65]);
												unpackedGenome.add(END_NODE);
											}
										// We'll need to go through again in case we've unpacked more unpackable characters
										completed = false;
									}
							}

						// if there is nothing left to unpack, finish early
						if (completed)
							recursions = MAX_RECURSIONS;
					}

				// This code checked that START_NODE & END_NODE brackets matched up in pairs
				// int numStartNodes = 0;
				// int numEndNodes = 0;
				//
				// // Go through the unpacked genes and remove any surplus unpackables
				// for (int i = 0; i < unpackedGenome.size();)
				// {
				// Character c = unpackedGenome.get(i);
				// if (Character.isUpperCase(c))
				// // Add the next character to our string of genes
				// unpackedGenome.remove(i);
				// else if (c == START_NODE)
				// {
				// numStartNodes++;
				// i++;
				// }
				// else if (c == END_NODE)
				// {
				// numEndNodes++;
				// // If inviable, stop checking the genes
				// if (numEndNodes > numStartNodes)
				// i = unpackedGenome.size();
				// i++;
				// }
				// else
				// i++;
				// }
				//
				// // If the brackets don't balance out, break the genes
				// if (numStartNodes != numEndNodes)
				// unpackedGenome.clear();

				// Recored that the genes have been unpacked
				genesUnpacked = true;
			}

		private final void mutate()
			{
				// If a random % is higher than the chance of being a mutated offspring, return.
				if (RandTools.randPercent() > Main.simWindow.mutantOffspringSlider.getPercent())
					return;

				// Mutate leaf colour
				if (RandTools.randPercent() > 50)
					{
						int red = leafColour.getRed() + RandTools.getInt(-5, 5);
						int green = leafColour.getGreen() + RandTools.getInt(-5, 5);
						int blue = leafColour.getBlue() + RandTools.getInt(-5, 5);
						leafColour = ColTools.checkColour(red, green, blue);
					}
				// Mutate numerical genes
				seedEnergy += RandTools.getDouble(-2, 2);
				baseNodeSize += RandTools.getDouble(-0.01, 0.01);
				taper += RandTools.getDouble(-0.008, 0.008);
				if (taper < 0)
					taper = 0;

				// Mutate structural genes

				for (int i = RandTools.getInt(1, (int) Main.simWindow.dnaDamageSlider.getValue()); i > 0; i--)
					{
						int geneToModify = RandTools.getInt(0, genes.length - 1);
						int mutationIndex = RandTools.getInt(0, genes[geneToModify].size() - 1);
						switch (RandTools.getInt(0, 10))
							{
							// Mutate a character
								case 1:
									if (genes[geneToModify].size() > 0)
										genes[geneToModify].remove(mutationIndex);
									genes[geneToModify].add(mutationIndex, getRandomCharacter());
									break;
								// Add a character
								case 2:
									genes[geneToModify].add(mutationIndex, getRandomCharacter());
									break;
								// Remove a character
								case 3:
									if (genes[geneToModify].size() > 0)
										genes[geneToModify].remove(mutationIndex);
									break;
							}
					}
			}

		private final Character getRandomCharacter()
			{
				if (RandTools.randPercent() > 40)
					return NOTHING;

				Character[] letters = new Character[6 + (MAX_GENES * 2)];
				letters[0] = GROW;
				letters[1] = ROTATE_LEFT;
				letters[2] = ROTATE_RIGHT;
				letters[3] = TOGGLE_LEAF;
				letters[4] = START_NODE;
				letters[5] = END_NODE;

				// Add the characters that represent each gene
				for (int i = 6; i < letters.length; i += 2)
					{
						letters[i] = new Character((char) (65 + i));
						letters[i + 1] = new Character((char) (65 + i));
					}
				return letters[RandTools.getInt(0, letters.length - 1)];

			}
	}