package PlantSim;

import java.awt.Graphics;

import TroysCode.Tools;

public class Leaf extends PlantPart
	{
		private Stem[] stems;

		private boolean notGrowingStems = false;

		public Leaf(Plant thisPlant, float tipX, float tipY)
			{
				super(thisPlant, tipX, tipY);
				this.thisPlant = thisPlant;

				this.x = tipX;
				this.y = tipY;

				thisPlant.leaves.add(this);
			}

		protected final void grow(float growX, float growY)
			{
				y -= growY;
				x += growX;
				if (stems != null)
					for (Stem s : stems)
						s.move(growX, growY);
			}

		@Override
		public void tick()
			{
				if (stems != null)
					{
						if (energy > stems.length)
							for (Stem s : stems)
								{
									s.energy++;
									energy--;
								}

						for (Stem s : stems)
							s.tick();
					}

				else if (!notGrowingStems && energy > thisPlant.genes.leafEnergyThreshold && thisPlant.numberOfStemsLeft > 0)
					growStems();

				else if (energy > thisPlant.genes.leafEnergyThreshold)
					{
						if (energy > thisPlant.genes.leafEnergyToPlant)
							{
								thisPlant.sendEnergyToPlant(thisPlant.genes.leafEnergyToPlant, this);
								energy -= thisPlant.genes.leafEnergyToPlant;
							}
					}
			}

		@Override
		public void render(Graphics g)
			{
				if (stems != null)
					for (Stem s : stems)
						s.render(g);
			}

		public final boolean containsPoint(float x, float y)
			{
				return Tools.getVectorLength(this.x, this.y, x, y) < 12 ? true : false;
			}

		private void growStems()
			{
				if (thisPlant.genes.chanceOfGrowingStems > Tools.randPercent())
					{
						int numStems = (int) Math.min(thisPlant.numberOfStemsLeft, thisPlant.genes.numberOfLeafStems);
						if (numStems == 0)
							{
								notGrowingStems = true;
								return;
							}
						stems = new Stem[numStems];
						for (int i = 0; i < numStems; i++)
							stems[i] = new Stem(thisPlant, x, y);
						thisPlant.numberOfStemsLeft -= numStems;
					}
				else
					notGrowingStems = true;
			}

		public void containsPhoton(Photon photon)
			{
				float energyGainedFromLight = photon.energy * (thisPlant.genes.colour.getAlpha() / 255f);

				energy += energyGainedFromLight;
				photon.energy -= energyGainedFromLight;
				if (photon.energy < 1)
					photon.exists = false;
			}
	}
