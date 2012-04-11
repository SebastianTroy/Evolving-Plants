package PlantSim;

import java.awt.Color;
import java.awt.Graphics;

public class Photon extends Entity
	{
		public Photon(int x, int energy)
			{
				this.x = x;
				this.energy = energy;
			}

		@Override
		public void tick()
			{
				y += 5;
				if (y > 560)
					exists = false;
			}

		@Override
		public void render(Graphics g)
			{
				g.setColor(Color.YELLOW);
				g.drawLine(Math.round(x), Math.round(y), Math.round(x), Math.round(y));
			}
	}
