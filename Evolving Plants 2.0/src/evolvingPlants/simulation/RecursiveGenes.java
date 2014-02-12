package evolvingPlants.simulation;

import java.util.LinkedList;

/**
 * This class represents a plant's genetics
 * 
 * @author Sebastian Troy
 */
public class RecursiveGenes
	{
		private static final int MAX_RECURSIONS = 10;
		private static final int MAX_UNPACKABLES = 5;

		public static final Character GROW = 'a';
		public static final Character ROTATE_LEFT = 'b';
		public static final Character ROTATE_RIGHT = 'c';
		public static final Character START_NODE = '{';
		public static final Character END_NODE = '}';
		public static final Character NOTHING = '_';

		@SuppressWarnings("unchecked")
		private final LinkedList<Character>[] genes = new LinkedList[MAX_UNPACKABLES];

		private boolean genesUnpacked = false;
		private final String unpackedGenes = "";

		public RecursiveGenes()
			{
				for (int i = 0; i < MAX_UNPACKABLES; i++)
					{
						genes[i] = new LinkedList<Character>();
						genes[i].addLast(NOTHING);
					}

				genes[0].addFirst(GROW);
			}

		public RecursiveGenes(LinkedList<Character>[] parentGenes)
			{
				// For each set of genes (or as many allowed by the MAX_UNPACKABLES variable)
				for (int i = 0; i < MAX_UNPACKABLES || i < parentGenes.length; i++)
					{
						this.genes[i] = new LinkedList<Character>();
						for (int i2 = 0; i2 < parentGenes[i].size(); i2++)
							this.genes[i].addLast(parentGenes[i2].pop());
					}
			}

		public final String getGenes()
			{
				if (!genesUnpacked)
					unpackGenes();
				return unpackedGenes;
			}

		private final void unpackGenes()
			{
				// Construct the entire genome by unpacking A (the first gene)
				LinkedList<Character> unpackedGenes = new LinkedList<Character>();
				unpackedGenes.addAll(genes[0]);

				boolean completed;

				// For as many recursions as are allowed
				for (int recursions = 0; recursions < MAX_RECURSIONS; recursions++)
					{
						// Assume this will be the last run for unpacking the genome (we can correct ourselves alter)
						completed = true;

						// Work through the genome backwards to avoid getting stuck in a recursive unpacking loop
						for (int i = unpackedGenes.size() - 1; i >= 0; i++)
							{
								char c = unpackedGenes.get(i);
								// If the character is unpackable
								if (Character.isUpperCase(c))
									{
										// Remove the capital letter so it isn't unpacked again
										unpackedGenes.remove(i);

										// If the capital letter is a valid unpackable letter
										if (c - 65 < MAX_UNPACKABLES)
											// Add the letters that the Capital letter represented to the genome
											unpackedGenes.addAll(i, genes[c - 65]);

										// We'll need to go through again in case we've unpacked more unpackable characters
										completed = false;
									}
							}

						// if there is nothing left to unpack, finish early
						if (completed)
							recursions = MAX_RECURSIONS;
					}

				// Go through the unpacked genes and remove any surplus unpackables
				for (int i = 0; i < unpackedGenes.size(); i++)
					{
						char c = unpackedGenes.get(i);
						if (!Character.isUpperCase(c))
							// Add the next character to our string of genes
							this.unpackedGenes.concat(Character.toString(c));
					}
				
				// Recored that the genes have been unpacked
				genesUnpacked = true;
			}
	}