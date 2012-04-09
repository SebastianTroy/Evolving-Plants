package PlantSim;

import java.awt.Color;
import java.awt.Graphics;

import TroysCode.Tools;

public class Stem extends PlantPart
	{
		private Leaf leaf;

		private float tipX;
		private float tipY;

		private float growX;
		private float growY;

		public Stem(Plant thisPlant, float x, float y)
			{
				super(thisPlant, x, y);
				tipX = x;
				tipY = y;

				calculateGrowthPath();

				leaf = new Leaf(thisPlant, tipX, tipY);
			}

		private final void calculateGrowthPath()
			{
				float maxGrowAngle = thisPlant.genes.stemGrowIncrement * (thisPlant.genes.stemAngleVariation / 100f);
				growX = Tools.randFloat(-maxGrowAngle, maxGrowAngle);
				while (Tools.getVectorLength(0, 0, growX, growY) < thisPlant.genes.stemGrowIncrement)
					growY += 0.05f;
			}

		private final int totatStemLength()
			{
				return (int) Tools.getVectorLength(x, y, tipX, tipY);
			}

		protected final void grow(PlantPart energyFrom)
			{
				tipY -= growY;
				tipX += growX;
				leaf.grow(growX, growY);
				energyFrom.energy -= thisPlant.genes.stemGrowIncrement;
			}

		protected final void move(float xMod, float yMod)
			{
				tipX += xMod;
				tipY -= yMod;
				x += xMod;
				y -= yMod;
				leaf.grow(xMod, yMod);
			}

		@Override
		public final void tick()
			{
				if (totatStemLength() < thisPlant.genes.maxStemLength)
					{
						if (energy > thisPlant.genes.stemGrowIncrement)
							grow(this);
						else if (leaf.energy > thisPlant.genes.stemGrowIncrement)
							grow(leaf);
					}
				else if (energy > thisPlant.genes.stemGrowIncrement)
					{
						leaf.energy++;
						energy--;
					}

				leaf.tick();
			}

		@Override
		public final void render(Graphics g)
			{
				g.setColor(Color.BLACK);
				g.drawLine(Math.round(x), Math.round(y), Math.round(tipX), Math.round(tipY));

				leaf.render(g);

				g.setColor(thisPlant.genes.colour);
				g.fillOval(Math.round(leaf.x - 12.5f), Math.round(leaf.y - 12.5f), 25, 25);
			}
	}
