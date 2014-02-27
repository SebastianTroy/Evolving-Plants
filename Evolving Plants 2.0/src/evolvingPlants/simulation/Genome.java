package evolvingPlants.simulation;

import java.awt.Color;
import java.util.LinkedList;

import tools.ColTools;
import tools.RandTools;
import evolvingPlants.Main;

/**
 * This class represents a plant's genetics
 * 
 * @author Sebastian Troy
 */
public class Genome
	{
		// Gene constants
		public static final int MAX_RECURSIONS = 5;
		public static final int NUM_GENES = 4;
		public static final int GENE_LENGTH = 10;
		public static final int MAX_UNPACKED_GENE_LENGTH = 100;

		// Genetic Instructions
		public static final char GROW = '^';
		public static final char ROTATE_LEFT = '<';
		public static final char ROTATE_RIGHT = '>';
		public static final char TOGGLE_LEAF = 'l';
		public static final char START_NODE = '{';
		public static final char END_NODE = '}';
		public static final char NOTHING = '~';

		public final char[/* Genes */][/* Instructions */] genes = new char[NUM_GENES][GENE_LENGTH];

		private boolean genesUnpacked = false;
		private final LinkedList<Character> unpackedGenome = new LinkedList<Character>();

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		public Color leafColour;
		public double seedEnergy, /* energy to seeds */baseNodeSize, taper /* the proportion by which the plant gets smaller each branch */;

		public Genome()
			{
				for (int geneNum = 0; geneNum < NUM_GENES; geneNum++)
					for (int geneIndex = 0; geneIndex < GENE_LENGTH; geneIndex++)
						genes[geneNum][geneIndex] = NOTHING;

				genes[0][0] = GROW;
				genes[0][1] = TOGGLE_LEAF;

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
				// Make this.genes a hard copy of parentGenes.genes
				for (int geneNum = 0; geneNum < NUM_GENES; geneNum++)
					for (int geneIndex = 0; geneIndex < GENE_LENGTH; geneIndex++)
						genes[geneNum][geneIndex] = new Character(parentGenes.genes[geneNum][geneIndex]);

				// Copy the other gene variables too
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

				LinkedList<Character> copyOfUnpackedGenome = new LinkedList<Character>();
				for (Character c : unpackedGenome)
					copyOfUnpackedGenome.addLast(new Character(c));

				return copyOfUnpackedGenome;
			}

		public final void printGenome()
			{
				for (int geneNum = 0; geneNum < NUM_GENES; geneNum++)
					{
						System.out.print("Gene " + (char) (65 + geneNum) + ": ( " + genes[geneNum][0]);
						for (int geneIndex = 1; geneIndex < GENE_LENGTH; geneIndex++)
							System.out.print(", " + genes[geneNum][geneIndex]);
						System.out.print(" )");
						System.out.println();
					}

				System.out.println(getUnpackedGenome().toString());
				System.out.println();
			}

		private final void unpackGenes()
			{
				// Construct the entire genome by unpacking A (the first gene)
				for (int index = 0; index < GENE_LENGTH; index++)
					unpackedGenome.add(genes[0][index]);

				// Keep track of whether we have finished unpacking the genes.
				boolean completed;

				// For as many recursions as are allowed
				for (int recursions = 0; recursions < MAX_RECURSIONS && unpackedGenome.size() < MAX_UNPACKED_GENE_LENGTH; recursions++)
					{
						// Assume this will be the last run for unpacking the genome (we can correct ourselves alter)
						completed = true;

						// Work through the genome and unpack any capital letters
						for (int i = 0; i < unpackedGenome.size(); i++)
							{
								char c = unpackedGenome.get(i);

								// // Don't bother adding any NOTHING instructions
								// if (c == NOTHING)
								// {
								// completed = false;
								// break;
								// }

								// If the character is upper case (i.e. can be unpacked)
								if (Character.isUpperCase(c))
									{
										// Remove the letter so it isn't unpacked again
										unpackedGenome.remove(i);

										// If the letter represents one of the other genes
										if (c - 65 < NUM_GENES)
											{
												// Add the letters from that gene to the genome
												for (int index = 0; index < GENE_LENGTH; index++)
													unpackedGenome.add(i + index, genes[c - 65][index]);
												i += GENE_LENGTH;
											}

										// We'll need to go through again in case we've unpacked more unpackable characters from that gene
										completed = false;
									}
							}

						// if there is nothing left to unpack, finish early
						if (completed)
							recursions = MAX_RECURSIONS;
					}

				// This code checked that START_NODE & END_NODE brackets matched up in pairs
				int numStartNodes = 0;
				int numEndNodes = 0;

				// Go through the unpacked genes and remove any surplus unpackables
				for (int i = 0; i < unpackedGenome.size();)
					{
						Character c = unpackedGenome.get(i);
						if (Character.isUpperCase(c))
							// Add the next character to our string of genes
							unpackedGenome.remove(i);
						else if (c == START_NODE)
							{
								numStartNodes++;
								i++;
							}
						else if (c == END_NODE)
							{
								numEndNodes++;
								// If inviable, stop checking the genes
								if (numEndNodes > numStartNodes)
									i = unpackedGenome.size();
								i++;
							}
						else
							i++;
					}

				// If the brackets don't balance out, break the genes
				if (numStartNodes != numEndNodes)
					{
						unpackedGenome.clear();
					}

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
						int red = leafColour.getRed() + RandTools.getInt(-10, 10);
						int green = leafColour.getGreen() + RandTools.getInt(-10, 10);
						int blue = leafColour.getBlue() + RandTools.getInt(-10, 10);
						leafColour = ColTools.checkColour(red, green, blue);
					}
				// Mutate numerical genes
				seedEnergy += RandTools.getDouble(-2, 2);
				baseNodeSize += RandTools.getDouble(-0.01, 0.01);
				taper += RandTools.getDouble(-0.008, 0.008);
				if (taper < 0)
					taper = 0;

				// For a user specified value
				for (int i = 0; i < Main.simWindow.dnaDamageSlider.getValue(); i++)
					// Modify a random character in the genome
					genes[RandTools.getInt(0, NUM_GENES - 1)][RandTools.getInt(0, GENE_LENGTH - 1)] = getRandomCharacter();
			}

		private final char getRandomCharacter()
			{
				// Mostly return NOTHING
				if (RandTools.randPercent() > 40)
					return NOTHING;

				// Add one of each of the rest of the letters
				char[] letters = new char[6 + NUM_GENES];
				letters[0] = GROW;
				letters[1] = ROTATE_LEFT;
				letters[2] = ROTATE_RIGHT;
				letters[3] = TOGGLE_LEAF;
				letters[4] = START_NODE;
				letters[5] = END_NODE;

				// Add a capitol letter to represent each of the genes
				for (int i = 6; i < letters.length; i++)
					letters[i] = new Character((char) (65 + (i - 6)));

				// return a random letter from our list of possible characters
				return letters[RandTools.getInt(0, letters.length - 1)];

			}
	}