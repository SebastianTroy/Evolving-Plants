package EvolvingPlants;

import java.awt.Color;

import TroysCode.Tools;
import TroysCode.hub;

public class Genes
	{
		private final int MINPERCENT = 0;
		private final int MAXPERCENT = 100;

		public Color colour = Tools.randAlphaColour();
		public Color seedColour = Tools.randColour();

		public int maxAge = Tools.randInt(1500, 2500);
		private final int ageVar = 300;

		public float seedEnergy = Tools.randInt(200, 300);
		private final float seedEnergyVar = 30;
		public float seedSpread = Tools.randFloat(1.0f, 1.7f);
		private final float seedSpreadVar = 0.25f;
		public float numberOfSeedStems = Tools.randFloat(1.0f, 2.0f);
		private final float numSeedStemVar = 3;
		public int germinationTime = Tools.randInt(0, 40);
		private final float germinationTimeVar = 20;

		public float maxStems = Tools.randInt(1, 3);
		private final float maxStemsVar = 2;
		public float numberOfLeafStems = Tools.randInt(1, 2);
		private final float numLeafStemsVar = 2;
		public float chanceOfGrowingStems = (float) Tools.randPercent();
		private final float chanceGrowingStemsVar = 10;
		public float maxStemLength = Tools.randInt(25, 50);
		private final float maxStemLengthVar = 8;
		public float stemGrowIncrement = 2f;
		private final float stemGrowIncrementVar = 0.1f;
		public float stemAngleVariation = Tools.randFloat(0, 30);
		private final float stamAngleVar = 15;

		public int leafEnergyThreshold = Tools.randInt(50, 150);
		private final float leafEnergyThresholdVar = 20;
		public float leafEnergyToPlant = Tools.randFloat(0.5f, 3.5f);
		private final float leafEnergyToPlantVar = 0.1f;

		protected boolean germinate = true;

		public Genes(Plant parent, Plant thisPlant)
			{
				if (parent != null)
					{
						colour = parent.genes.colour;
						seedColour = parent.genes.seedColour;

						maxAge = parent.genes.maxAge;

						seedEnergy = parent.genes.seedEnergy;
						seedSpread = parent.genes.seedSpread;
						germinationTime = parent.genes.germinationTime;

						numberOfSeedStems = parent.genes.numberOfSeedStems;
						numberOfLeafStems = parent.genes.numberOfLeafStems;

						maxStems = parent.genes.maxStems;
						chanceOfGrowingStems = parent.genes.chanceOfGrowingStems;
						maxStemLength = parent.genes.maxStemLength;
						stemGrowIncrement = parent.genes.stemGrowIncrement;
						stemAngleVariation = parent.genes.stemAngleVariation;

						leafEnergyThreshold = parent.genes.leafEnergyThreshold;
						leafEnergyToPlant = parent.genes.leafEnergyToPlant;
					}
				mutate(thisPlant);
				checkGenes();
			}

		private final void mutate(Plant thisPlant)
			{
				float UVIntensity = 50f;
				float variability = 50f;

				if (thisPlant != null)
					{
						UVIntensity = thisPlant.x < 600 ? hub.world.UVIntensity : hub.world.UVIntensity2;
						variability = thisPlant.x < 600 ? hub.world.UVDamage : hub.world.UVDamage2;
					}

				if (UVIntensity > Tools.randFloat(MINPERCENT, MAXPERCENT))
					{
						float var = variability / 100f;

						mutateColour(thisPlant);

						maxAge += Tools.randFloat(-var * ageVar, var * ageVar);

						seedEnergy += Tools.randFloat(-var * seedEnergyVar, var * seedEnergyVar);
						seedSpread += Tools.randFloat(-var * seedSpreadVar, var * seedSpreadVar);
						germinationTime += (int) (Tools.randFloat(-var * germinationTimeVar, var * germinationTimeVar));

						numberOfSeedStems += Tools.randFloat(-var * numSeedStemVar, var * numSeedStemVar);
						numberOfLeafStems += Tools.randFloat(-var * numLeafStemsVar, var * numLeafStemsVar);

						maxStems += Tools.randFloat(-var * maxStemsVar, var * maxStemsVar);
						chanceOfGrowingStems += Tools.randFloat(-var * chanceGrowingStemsVar, var * chanceGrowingStemsVar);
						maxStemLength += Tools.randFloat(-var * maxStemLengthVar, var * maxStemLengthVar);
						stemGrowIncrement += Tools.randFloat(-var * stemGrowIncrementVar, var * stemGrowIncrementVar);
						stemAngleVariation += Tools.randFloat(-var * stamAngleVar, var * stamAngleVar);

						leafEnergyThreshold += Tools.randFloat(-var * leafEnergyThresholdVar, var * leafEnergyThresholdVar);
						leafEnergyToPlant += Tools.randFloat(-var * leafEnergyToPlantVar, var * leafEnergyToPlantVar);
					}
			}

		private final void checkGenes()
			{
				if (germinationTime < 0)
					seedSpread = 0;

				if (numberOfSeedStems < 1 || maxStems < 1 || leafEnergyToPlant < 1 || seedEnergy < 0.1f)
					germinate = false;

				if (numberOfLeafStems < 0)
					numberOfLeafStems = 0;

				if (chanceOfGrowingStems < MINPERCENT)
					chanceOfGrowingStems = MINPERCENT;

				if (chanceOfGrowingStems > MAXPERCENT)
					chanceOfGrowingStems = MAXPERCENT;

				if (maxStemLength < 1)
					maxStemLength = 1;

				if (stemAngleVariation < MINPERCENT)
					stemAngleVariation = MINPERCENT;

				if (stemAngleVariation > MAXPERCENT)
					stemAngleVariation = MAXPERCENT;

				if (leafEnergyToPlant > 30)
					leafEnergyToPlant = 30;
			}

		private final void mutateColour(Plant thisPlant)
			{
				float var = 5f;

				if (thisPlant != null)
					var = thisPlant.x < 600 ? hub.world.UVDamage / 2.5f : hub.world.UVDamage2 / 2.5f;

				int alpha = (int) (colour.getAlpha() + Tools.randFloat(-var, var));
				int red = (int) (colour.getRed() + Tools.randFloat(-var, var));
				int green = (int) (colour.getGreen() + Tools.randFloat(-var, var));
				int blue = (int) (colour.getBlue() + Tools.randFloat(-var, var));

				colour = checkColour(red, green, blue, alpha);

				var *= 2;
				
				alpha = 255;
				red = (int) (seedColour.getRed() + Tools.randFloat(-var, var));
				green = (int) (seedColour.getGreen() + Tools.randFloat(-var, var));
				blue = (int) (seedColour.getBlue() + Tools.randFloat(-var, var));

				seedColour = checkColour(red, green, blue, alpha);
			}
		
		private final Color checkColour(int red, int green, int blue, int alpha)
			{				
				if (alpha < 0)
					alpha = 0;
				else if (alpha > 255)
					alpha = 255;

				if (red < 0)
					red = 0;
				else if (red > 255)
					red = 255;

				if (green < 0)
					green = 0;
				else if (green > 255)
					green = 255;

				if (blue < 0)
					blue = 0;
				else if (blue > 255)
					blue = 255;
				
				return new Color(red, green, blue, alpha);
			}
	}
