package PlantSim;

import java.awt.Graphics;
import java.util.ArrayList;

import TroysCode.Tools;
import TroysCode.hub;

public class Plant extends PlantPart
	{
		public Plant parent;
		
		public int age = 0;

		protected Genes genes;
		private Seed seed;

		protected int numberOfStemsLeft;

		public ArrayList<Leaf> leaves = new ArrayList<Leaf>();

		public Plant(Plant parentPlant, float x, float y, float energy)
			{
				// null because thisPlant != parentPlant (it's == this)
				super(null, x, y);
				parent = parentPlant;
				thisPlant = this;

				genes = new Genes(parentPlant, this);

				seed = new Seed(this, x, y);
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

				seed = new Seed(this, x, y);
				seed.energy = energy;

				numberOfStemsLeft = (int) genes.maxStems;
			}

		protected final void sendEnergyToPlant(float energy, Leaf leaf)
			{
				this.energy += energy;
				if (this.energy > genes.seedEnergy)
					seed(leaf);
			}

		private final void seed(Leaf leaf)
			{
				float seedX = leaf.x + (Tools.randFloat(-genes.seedSpread, genes.seedSpread));
				hub.world.addPlant(new Plant(this, seedX, leaf.y, genes.seedEnergy));
				energy -= genes.seedEnergy;
			}

		public final void tick()
			{
				seed.tick();
				age++;
				if (age >= genes.maxAge)
					alive = false;
			}

		public void render(Graphics g)
			{
				seed.render(g);
//				g.setColor(Color.BLACK);
//				g.drawString("" + energy, Math.round(x), Math.round(y));
			}
	}
