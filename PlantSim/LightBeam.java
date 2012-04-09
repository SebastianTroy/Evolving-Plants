package PlantSim;

import java.awt.Color;
import java.awt.Graphics;

import TroysCode.hub;

public class LightBeam extends Entity
	{
		public LightBeam(int x, int energy)
			{
				this.x = x;
				this.energy = energy;
			}

		@Override
		public void tick()
			{
				y += 5;
				if (y > 560)
					alive = false;
			}
		
		@Override
		public void render(Graphics g)
			{
				g.setColor(Color.YELLOW);
				g.drawRect(Math.round(x), Math.round(y), 1, 1);
			}
	}
