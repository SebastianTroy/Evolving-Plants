package EvolvingPlants;

import java.awt.Color;
import java.awt.Graphics;

import TroysCode.Tools;
import TroysCode.hub;

public class Dust extends Entity
	{
		
		public Dust(float x, float y)
			{
				this.x = x;
				this.y = y;
			}

		@Override
		public void tick()
			{
				y += 2;
				if (x < 600)
					x += (hub.world.leftWindFactor / (y / 200)) + Tools.randFloat(-0.75f, 0.75f);
				else
					x += (hub.world.rightWindFactor / (y / 200)) + Tools.randFloat(-0.75f, 0.75f);
				
				if (y > 600)
					exists = false;
			}

		@Override
		public void render(Graphics g)
			{
				g.setColor(Color.GREEN);
				g.fillRect((int) Math.round(x), (int) Math.round(y), 3, 3);
			}

	}
