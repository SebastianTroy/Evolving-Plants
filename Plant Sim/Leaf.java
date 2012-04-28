package EvolvingPlants;

import java.awt.Color;
import java.awt.Graphics;

import TroysCode.Tools;
import TroysCode.hub;

public class Leaf extends PlantPart
	{
		private Stem[] stems;

		private boolean notGrowingStems = false;

		private double seedEnergy = 0;

		private final int ENERGY_THRESHOLD = 25;

		public Leaf(Plant thisPlant, float tipX, float tipY)
			{
				super(thisPlant, tipX, tipY);
				this.thisPlant = thisPlant;

				this.x = tipX;
				this.y = tipY;

				thisPlant.leaves.add(this);
			}

		protected final void grow(double growX, double growY)
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
					for (Stem s : stems)
						s.tick();

				else if (!notGrowingStems && energy > ENERGY_THRESHOLD && thisPlant.numberOfStemsLeft > 0)
					growStems();

				if (energy > ENERGY_THRESHOLD)
					{
						seedEnergy += 3;
						energy -= 3;
						if (seedEnergy > thisPlant.genes.seedEnergy)
							{
								hub.world.addPlant(new Plant(thisPlant, x, y));
								energy -= thisPlant.genes.seedEnergy;
								seedEnergy = 0;
							}
					}
			}

		@Override
		public void render(Graphics g)
			{
				if (stems != null)
					for (Stem s : stems)
						s.render(g);

				g.setColor(thisPlant.leafColour);
				g.fillOval(Math.round(x - 12.5f), Math.round(y - 12.5f), 25, 25);

				g.setColor(thisPlant.selected ? Color.WHITE : Color.BLACK);
				g.drawOval(Math.round(x - 12.5f), Math.round(y - 12.5f), 25, 25);

				if (hub.world.viewSeeds)
					{
						g.setColor(thisPlant.seedColour);
						int seedSize = (int) (seedEnergy / 15);
						g.fillOval(Math.round(x - (seedSize / 2)), Math.round(y - (seedSize / 2)), seedSize, seedSize);
					}
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
				if (Tools.getVectorLength(x, y, photon.x, photon.y) < 12.5)
					{
						float energyGainedFromLight = photon.energy * (thisPlant.genes.leafColour.getAlpha() / 255f);

						energy += energyGainedFromLight;
						photon.energy -= energyGainedFromLight;
						if (photon.energy < 1)
							photon.exists = false;
					}
			}
	}
