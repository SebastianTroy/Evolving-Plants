package EvolvingPlants;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import TroysCode.Tools;
import TroysCode.hub;

public class Plant extends PlantPart
	{
		public Plant parent;

		public int age = 0;

		protected Genes genes;
		protected Seed seed;

		public boolean selected = false;
		public int numGerminatedOffspring = 0;

		protected int numberOfStemsLeft;

		public ArrayList<Leaf> leaves = new ArrayList<Leaf>();

		Color leafColour;
		Color seedColour;

		public Plant(Plant parentPlant, float x, float y)
			{
				// null because thisPlant != parentPlant (it's == this)
				super(null, x, y);
				parent = parentPlant;
				thisPlant = this;

				genes = new Genes(parentPlant, this);

				leafColour = genes.leafColour;
				seedColour = genes.seedColour;

				seed = new Seed(this, x, y);
				seed.energy = genes.seedEnergy;

				numberOfStemsLeft = (int) genes.maxStems;
			}

		public Plant(Plant parentPlant, float x, float y, Genes genes)
			{
				super(null , x, y);
				parent = this;
				thisPlant = this;

				this.genes = genes;

				leafColour = genes.leafColour;
				seedColour = genes.seedColour;

				seed = new Seed(this, x, y);
				seed.energy = genes.seedEnergy;

				numberOfStemsLeft = (int) genes.maxStems;
			}

		public final void tick()
			{
				if (age >= genes.maxAge)
					{
						if (hub.world.viewDeathAnimation)
							{
								seed.germinated = false;
								if (getDarker())
									{
										boolean leavesFallen = true;
										for (Leaf l : leaves)
											if (l.y < 612)
												{
													l.y += 1.5;
													if (l.x < 600)
														l.x += Tools.randDouble(-1.75, 1.75) + (hub.world.leftWindFactor / (l.y / 400));
													else
														l.x += Tools.randDouble(-1.75, 1.75) + (hub.world.rightWindFactor / (l.y / 400));
													leavesFallen = false;
												}

										if (leavesFallen)
											exists = false;
									}
							}
						else
							exists = false;
					}
				else
					{
						if (seed.germinated)
							age++;
						seed.tick();
					}
			}

		public void render(Graphics g)
			{
				seed.render(g);
			}

		private final boolean getDarker()
			{
				int red = leafColour.getRed();
				int green = leafColour.getGreen();
				int blue = leafColour.getBlue();
				int alpha = leafColour.getAlpha();

				leafColour = Tools.checkAlphaColour(red - 3, green - 3, blue - 3, alpha + 3);
				seedColour = Tools.checkAlphaColour(red - 3, green - 3, blue - 3, alpha + 3);

				return (red == 0 && green == 0 && blue == 0);
			}
	}
