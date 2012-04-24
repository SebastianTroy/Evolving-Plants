package EvolvingPlants;

import java.awt.Graphics;

public class PlantPart extends Entity
	{
		protected Plant thisPlant;
				
		public PlantPart(Plant thisPlant, float x, float y)
			{
				this.thisPlant = thisPlant;
				this.x = x;
				this.y = y;
			}

		@Override
		public void tick()
			{
			}

		@Override
		public void render(Graphics g)
			{
			}
	}
