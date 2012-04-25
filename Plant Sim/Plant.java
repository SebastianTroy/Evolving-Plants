package EvolvingPlants;

import java.awt.Graphics;
import java.util.ArrayList;

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

		public Plant(Plant parentPlant, float x, float y, float energy)
			{
				// null because thisPlant != parentPlant (it's == this)
				super(null, x, y);
				parent = parentPlant;
				thisPlant = this;

				genes = new Genes(parentPlant, this);

				seed = new Seed(this, x, y, genes.germinationTime);
				seed.energy = energy;

				numberOfStemsLeft = (int) genes.maxStems;
			}
		
		public Plant(Plant parentPlant, float x, float y, float energy, Genes genes)
			{
				// null because thisPlant != parentPlant (it's == this)
				super(null, x, y);
				parent = parentPlant;
				thisPlant = this;

				this.genes = genes;;

				seed = new Seed(this, x, y, genes.germinationTime);
				seed.energy = energy;

				numberOfStemsLeft = (int) genes.maxStems;
			}

		public final void tick()
			{
				seed.tick();
				age++;
				if (age >= genes.maxAge)
					exists = false;
			}

		public void render(Graphics g)
			{
				seed.render(g);
//				g.setColor(Color.BLACK);
//				g.drawString("" + energy, Math.round(x), Math.round(y));
			}
	}
