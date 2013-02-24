package evolvingPlants.simulation;

import java.awt.Color;
import java.awt.Graphics;

import tools.RandTools;
import evolvingPlants.Hub;

public class Seed
	{
		double x, y, energy, distance = -1, speed;
		public boolean exists = true, falling = true;
		Genes genes;

		public Seed(double x, double y, Genes parent, double energy)
			{
				this.x = x;
				this.y = y;
				this.energy = energy;
				genes = new Genes(parent);
			}

		public Seed(double x, double y, Genes parentOne, Genes parentTwo, double energy)
			{
				this.x = x;
				this.y = y;
				this.energy = energy;
				genes = new Genes(parentOne, parentTwo);
			}

		public final void tick(double secondsPassed)
			{
				if (falling)
					{
						if (distance < 0)
							{
								distance = RandTools.getDouble(0, 15);
								speed = RandTools.getDouble(2, 7);
								speed *= RandTools.getBool() ? -1.0 : 1.0;
							}
						else
							{
								distance -= Math.abs(speed) * secondsPassed;
								x += speed * (distance * secondsPassed);
							}

						y += 90 * secondsPassed;
					}
				if (exists && y > Plant.plantY)
					if (getX() < 0 || getX() > Hub.simWindow.sim.simWidth)
						exists = false;
					else
						{
							falling = false;
							boolean canGrow = Hub.simWindow.sim.isSpaceAt(getX());

							if (canGrow)
								{
									Hub.simWindow.sim.addPlant(new Plant(this));
									exists = false;
								}
							else
								{
									energy -= 5 * secondsPassed;

									if (energy < 0)
										exists = false;
								}
						}
			}

		public final void render(Graphics g, int simX)
			{
				if (x < -simX + 200 || x > -simX + 1000)
					return;

				int apparentX = (int) (getX() + simX);
				
				g.setColor(Color.BLACK);
				g.fillOval(apparentX, (int) y, (int) (energy / 10), (int) (energy / 10));
			}

		public final int getX()
			{
				return (int) (x - (energy / 10));
			}

	}