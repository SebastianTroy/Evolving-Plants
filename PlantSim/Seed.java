package PlantSim;

import java.awt.Color;
import java.awt.Graphics;

import TroysCode.hub;

public class Seed extends PlantPart
	{
		private Stem[] stems = null;

		public Seed(Plant thisPlant, float x, float y)
			{
				super(thisPlant, x, y);
			}

		@Override
		public void tick()
			{
				if (y < hub.frame.getHeight() - 30)
					{
						y += 2;
						thisPlant.y += 2;
					}
				else if (stems == null)
					germinate();
				else
					for (Stem s : stems)
						{
							s.tick();
							if (energy > 0)
								{
									energy--;
									s.energy++;
								}
						}
			}

		@Override
		public void render(Graphics g)
			{
				if (stems != null)
					for (Stem s : stems)
						s.render(g);

				g.setColor(Color.LIGHT_GRAY);
				int seedSize = (int) (energy / 15);
				g.fillOval(Math.round(x - (seedSize / 2)), Math.round(y - (seedSize / 2)), seedSize, seedSize);
			}

		public void germinate()
			{
				int numStems = (int) Math.min(thisPlant.numberOfStemsLeft, thisPlant.genes.numberOfSeedStems);
				stems = new Stem[numStems];
				for (int i = 0; i < numStems; i++)
					stems[i] = new Stem(thisPlant, x, y);
				thisPlant.numberOfStemsLeft -= numStems;
			}
	}
