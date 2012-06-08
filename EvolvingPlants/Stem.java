package EvolvingPlants;

import java.awt.Color;
import java.awt.Graphics;

import TroysCode.Tools;
import TroysCode.hub;
import TroysCode.T.TPoint;

public class Stem extends PlantPart
	{
		private Leaf leaf;

		private float tipX;
		private float tipY;

		private double growX;
		private double growY;
		
		private double energyPerGrow;

		public Stem(Plant thisPlant, float x, float y)
			{
				super(thisPlant, x, y);
				tipX = x;
				tipY = y;

				energyPerGrow = thisPlant.genes.stemGrowSpeed * Math.pow(thisPlant.genes.stemGrowSpeed + 0.2, 1.25);
				calculateGrowthPath();

				leaf = new Leaf(thisPlant, tipX, tipY);
			}

		private final void calculateGrowthPath()
			{
				float stemAngleVar = thisPlant.genes.stemAngleVariation;
				double growAngle = Tools.randFloat(-stemAngleVar, stemAngleVar);
				if (x < 600)
					growAngle += (hub.world.leftWindFactor * 10.0) / (y / 200);
				else
					growAngle += (hub.world.rightWindFactor * 10.0) / (y / 200);
				
				TPoint growVector = Tools.getVector(growAngle, thisPlant.genes.stemGrowSpeed);
				
				growX = growVector.getX();
				growY = growVector.getY();
			}

		private final int totatStemLength()
			{
				return (int) Tools.getVectorLength(x, y, tipX, tipY);
			}

		private final void grow(PlantPart energyFrom)
			{
				tipY -= growY;
				tipX += growX;
				leaf.move(growX, growY);
				energyFrom.energy -= energyPerGrow;
			}

		protected final void move(double xMod, double yMod)
			{
				tipX += xMod;
				tipY -= yMod;
				x += xMod;
				y -= yMod;
				leaf.move(xMod, yMod);
			}

		@Override
		public final void tick()
			{
				if (totatStemLength() < thisPlant.genes.maxStemLength)
					{
						if (energy > energyPerGrow)
							grow(this);
						else if (leaf.energy > energyPerGrow)
							grow(leaf);
					}
				else if (energy > 1)
					{
						leaf.energy++;
						energy--;
					}

				leaf.tick();
			}

		@Override
		public final void render(Graphics g)
			{
				g.setColor(thisPlant.selected ? Color.WHITE : Color.BLACK);
				g.drawLine(Math.round(x), Math.round(y), Math.round(tipX), Math.round(tipY));

				leaf.render(g);
			}
	}
