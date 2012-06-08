package EvolvingPlants;

import java.awt.Color;

import TroysCode.Tools;
import TroysCode.hub;

public class Genes
	{
		private final int MINPERCENT = 0;
		private final int MAXPERCENT = 100;

		public Color leafColour = Tools.randAlphaColour();
		public Color seedColour = Tools.randColour();

		public int maxAge = Tools.randInt(1500, 2500);
		private final int ageVar = 300;

		public float seedEnergy = Tools.randInt(200, 300);
		private final float seedEnergyVar = 30;
		public float seedSpread = Tools.randFloat(1.0f, 1.7f);
		private final float seedSpreadVar = 0.25f;
		public float numberOfSeedStems = Tools.randFloat(1.0f, 2.0f);
		private final float numSeedStemVar = 3;

		public float maxStems = Tools.randFloat(1, 3);
		private final float maxStemsVar = 2;
		public float numberOfLeafStems = Tools.randFloat(0, 2);
		private final float numLeafStemsVar = 2;
		public float chanceOfGrowingStems = (float) Tools.randPercent();
		private final float chanceGrowingStemsVar = 10;
		public float maxStemLength = Tools.randInt(25, 50);
		private final float maxStemLengthVar = 8;
		public float stemAngleVariation = Tools.randFloat(0, 20);
		private final float stamAngleVar = 15;

		public double stemGrowSpeed = Tools.randDouble(0.01, 3.0);
		private final double stemGrowSpeedVar = 0.08;

		protected boolean germinate = true;

		public Genes(Plant parent, Plant thisPlant)
			{
				if (parent != null)
					{
						leafColour = parent.genes.leafColour;
						seedColour = parent.genes.seedColour;

						maxAge = parent.genes.maxAge;

						seedEnergy = parent.genes.seedEnergy;
						seedSpread = parent.genes.seedSpread;

						numberOfSeedStems = parent.genes.numberOfSeedStems;
						numberOfLeafStems = parent.genes.numberOfLeafStems;

						maxStems = parent.genes.maxStems;
						chanceOfGrowingStems = parent.genes.chanceOfGrowingStems;
						maxStemLength = parent.genes.maxStemLength;
						stemAngleVariation = parent.genes.stemAngleVariation;

						stemGrowSpeed = parent.genes.stemGrowSpeed;
					}
				mutate(thisPlant);
				checkGenes();
			}

		private final void mutate(Plant thisPlant)
			{
				float UVIntensity = 0f;
				float var = 0f;

				if (thisPlant != null)
					{
						UVIntensity = thisPlant.x < 600 ? hub.world.UVIntensity : hub.world.UVIntensity2;
						var = thisPlant.x < 600 ? hub.world.UVDamage : hub.world.UVDamage2;
					}

				if (UVIntensity > Tools.randFloat(MINPERCENT, MAXPERCENT))
					{
						var /= 100f;

						mutateColour(thisPlant);

						maxAge += Tools.randFloat(-var * ageVar, var * ageVar);

						seedEnergy += Tools.randFloat(-var * seedEnergyVar, var * seedEnergyVar);
						seedSpread += Tools.randFloat(-var * seedSpreadVar, var * seedSpreadVar);

						numberOfSeedStems += Tools.randFloat(-var * numSeedStemVar, var * numSeedStemVar);
						numberOfLeafStems += Tools.randFloat(-var * numLeafStemsVar, var * numLeafStemsVar);

						maxStems += Tools.randFloat(-var * maxStemsVar, var * maxStemsVar);
						chanceOfGrowingStems += Tools.randFloat(-var * chanceGrowingStemsVar, var * chanceGrowingStemsVar);
						maxStemLength += Tools.randFloat(-var * maxStemLengthVar, var * maxStemLengthVar);
						stemAngleVariation += Tools.randFloat(-var * stamAngleVar, var * stamAngleVar);

						stemGrowSpeed += Tools.randDouble(-var * stemGrowSpeedVar, var * stemGrowSpeedVar);
					}
			}

		private final void checkGenes()
			{
				if (numberOfSeedStems < 1 || maxStems < 1 || seedEnergy < 0.1f || stemGrowSpeed <= 0)
					germinate = false;

				if (numberOfLeafStems < 0)
					numberOfLeafStems = 0;

				if (stemAngleVariation < 0)
					stemAngleVariation = 0;

				if (chanceOfGrowingStems < MINPERCENT)
					chanceOfGrowingStems = MINPERCENT;

				if (chanceOfGrowingStems > MAXPERCENT)
					chanceOfGrowingStems = MAXPERCENT;
			}

		private final void mutateColour(Plant thisPlant)
			{
				float var = 5f;

				if (thisPlant != null)
					var = thisPlant.x < 600 ? hub.world.UVDamage / 3.5f : hub.world.UVDamage2 / 3.5f;

				int alpha = (int) (leafColour.getAlpha() + Tools.randFloat(-var, var));
				int red = (int) (leafColour.getRed() + Tools.randFloat(-var, var));
				int green = (int) (leafColour.getGreen() + Tools.randFloat(-var, var));
				int blue = (int) (leafColour.getBlue() + Tools.randFloat(-var, var));

				leafColour = Tools.checkAlphaColour(red, green, blue, alpha);

				var *= 2.5;

				red = (int) (seedColour.getRed() + Tools.randFloat(-var, var));
				green = (int) (seedColour.getGreen() + Tools.randFloat(-var, var));
				blue = (int) (seedColour.getBlue() + Tools.randFloat(-var, var));

				seedColour = Tools.checkColour(red, green, blue);
			}
	}
