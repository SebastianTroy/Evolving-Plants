package evolvingPlants.simulation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import evolvingPlants.Hub;

public class LightFilter
	{
		int x, y, width;
		Color shadowColour;
		private BufferedImage filterColourMap;
		
		public Point movedTo = null;

		public boolean exists = true;

		public LightFilter(int x, int y, int width, Color filterColour)
			{
				this.x = x;
				this.y = y;
				this.width = width;

				int red = (int) (255 - filterColour.getRed());
				int green = (int) (255 - filterColour.getGreen());
				int blue = (int) (255 - filterColour.getBlue());
				shadowColour = new Color(red, green, blue);

				filterColourMap = new BufferedImage(width, 4, BufferedImage.TYPE_INT_ARGB);
				Graphics g = filterColourMap.getGraphics();
				g.setColor(filterColour);
				g.fillRect(0, 0, width, 4);
				g.dispose();
			}

		// public LightFilter(int x, int y, BufferedImage filterColourMap)
		// {
		// this.x = x;
		// this.y = y;
		// this.filterColourMap = filterColourMap;
		// this.width = filterColourMap.getWidth();
		// }

		public final void render(Graphics2D g, int simX)
			{
				int x = movedTo == null ? this.x : movedTo.x;
				int y = movedTo == null ? this.y : movedTo.y;
				
				if (x + width < -simX + 200 || x > -simX + 1000)
					return;

				g.drawImage(filterColourMap, x + simX, y, Hub.simWindow.getObserver());
				g.setColor(Color.LIGHT_GRAY);
				g.fillOval(x - 5 + simX, y - 5, 10, 10);
				g.fillOval(x + width - 5 + simX, y - 5, 10, 10);
				g.setColor(Color.BLACK);
				g.drawOval(x - 5 + simX, y - 5, 10, 10);
				g.drawOval(x + width - 5 + simX, y - 5, 10, 10);
			}

		public final boolean containsPoint(Point p)
			{
				if (Point.distance(p.x, p.y, x, y) < 11)
					return true;
				else if (Point.distance(p.x, p.y, x + width, y) < 11)
					return true;

				return false;
			}

		public final void moving(Point p)
			{
				if (p.x > Hub.simWindow.sim.simWidth || p.x + width< 0 || p.y > 550 || p.y < 0)
					{
						exists = false;
						return;
					}
				else
					movedTo = p;
			}
		
		public final void moved()
			{
				x = movedTo.x;
				y = movedTo.y;
				
				movedTo = null;
			}
	}