package PlantSim;

import TroysCode.Tools;

public class Genes
	{
		private final int MINPERCENT = 0;
		private final int MAXPERCENT = 100;
		private final int PERCENTRANGE = 100;
		
		
		public float geneStability = 50f;
		// Gene variation 0 - 1
		public float var = 0.5f;

		public int maxAge = 2000;
		private final int ageVar = 300;

		public float seedEnergy = 250;
		private final float seedEnergyVar = 30;
		public float seedSpread = 50;
		private final float seedSpreadVar = 15;
		public float numberOfSeedStems = 1.5f;
		private final float numSeedStemVar = 3;

		public float maxStems = 1;
		private final float maxStemsVar = 2;
		public float numberOfLeafStems = 3;
		private final float numLeafStemsVar = 2;
		public float chanceOfGrowingStems = 100f;
		private final float chanceGrowingStemsVar = 10;
		public float maxStemLength = 40;
		private final float maxStemLengthVar = 8;
		public float stemGrowIncrement = 2f;
		private final float stemGrowIncrementVar = 0.1f;
		public float stemAngleVariation = 0f;
		private final float stamAngleVar = 15;

		public int leafEnergyThreshold = 100;
		private final float leafEnergyThresholdVar = 20;
		public int leafEnergyToPlant = 1;
		private final float leafEnergyToPlantVar = 0.1f;

		public Genes(Plant parent)
			{
				if (parent != null)
					{
						maxAge = parent.genes.maxAge;

						seedEnergy = parent.genes.seedEnergy;
						seedSpread = parent.genes.seedSpread;

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
				mutate();
				checkGenes();
			}

		private final void mutate()
			{
				if (geneStability > Tools.randPercent())
					{
						maxAge += Tools.randFloat(-var * ageVar, var * ageVar);

						seedEnergy += Tools.randFloat(-var * seedEnergyVar, var * seedEnergyVar);
						seedSpread += Tools.randFloat(-var * seedSpreadVar, var * seedSpreadVar);

						numberOfSeedStems += Tools.randFloat(-var * numSeedStemVar, var * numSeedStemVar);
						numberOfLeafStems += Tools.randFloat(-var * numLeafStemsVar, var * numLeafStemsVar);

						maxStems += Tools.randFloat(-var * maxStemsVar, var * maxStemsVar);
						chanceOfGrowingStems += Tools.randFloat(-var * PERCENTRANGE, var * PERCENTRANGE);
						maxStemLength += Tools.randFloat(-var * maxStemLengthVar, var * maxStemLengthVar);
						stemGrowIncrement += Tools.randFloat(-var * stemGrowIncrementVar, var * stemGrowIncrementVar);
						stemAngleVariation += Tools.randFloat(-var * PERCENTRANGE, var * PERCENTRANGE);

						leafEnergyThreshold += Tools.randFloat(-var * leafEnergyThresholdVar, var * leafEnergyThresholdVar);
						leafEnergyToPlant += Tools.randFloat(-var * leafEnergyToPlantVar, var * leafEnergyToPlantVar);

						geneStability += Tools.randFloat(-var * PERCENTRANGE, var * PERCENTRANGE);
						var += Tools.randFloat(-var * var, var * var);
					}
			}

		private final void checkGenes()
			{
				if (seedSpread < 10)
					seedSpread = 10;

				if (numberOfSeedStems < 1 || maxStems < 1)
					maxAge = 120;

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

				if (geneStability < MINPERCENT)
					geneStability = MINPERCENT;

				if (geneStability > MAXPERCENT)
					geneStability = MAXPERCENT;

				if (var < 0)
					var = 0;

				if (var > 0.5f)
					var = 0.5f;
			}
	}
