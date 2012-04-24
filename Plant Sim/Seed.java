package EvolvingPlants;

import java.awt.Color;
import java.awt.Graphics;

import TroysCode.Tools;
import TroysCode.hub;

public class Seed extends PlantPart
	{
		private Stem[] stems = null;

		private int germinationTime;
		public boolean germinated = false;

		double xMod = 0;

		public Seed(Plant thisPlant, float x, float y, int germinationTime)
			{
				super(thisPlant, x, y);
				this.germinationTime = germinationTime;

				xMod = Tools.randDouble(-thisPlant.genes.seedSpread, thisPlant.genes.seedSpread);
			}

		@Override
		public void tick()
			{
				if (!germinated)
					{
						if (y < hub.frame.getHeight() - 30)
							{
								y += 2;
								thisPlant.y += 2;

								double var = Tools.randDouble(-xMod, xMod) / 2;
								x += xMod + var;
								thisPlant.x += xMod + var;
							}
						else if (germinationTime > 0)
							{
								germinationTime--;
							}
						else if (stems == null)
							{
								germinate();
							}
					}
				else
					{
						for (Stem s : stems)
							{
								s.tick();
								if (energy > 0)
									{
										energy--;
										s.energy++;
									}
							}
					}
			}

		@Override
		public void render(Graphics g)
			{
				if (stems != null)
					for (Stem s : stems)
						s.render(g);

				g.setColor(thisPlant.genes.seedColour);
				int seedSize = (int) (energy / 15);
				g.fillOval(Math.round(x - (seedSize / 2)), Math.round(y - (seedSize / 2)), seedSize, seedSize);
			}

		public void germinate()
			{
				if (thisPlant.genes.germinate && hub.world.isSpaceToGerminate(thisPlant))
					{
						germinated = true;
						thisPlant.parent.numGerminatedOffspring++;

						int numStems = (int) Math.min(thisPlant.numberOfStemsLeft, thisPlant.genes.numberOfSeedStems);
						stems = new Stem[numStems];
						for (int i = 0; i < numStems; i++)
							stems[i] = new Stem(thisPlant, x, y);
						thisPlant.numberOfStemsLeft -= numStems;
					}
				else
					thisPlant.exists = false;
			}
	}
