package evolvingPlants.simulation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import tools.ColTools;

import evolvingPlants.Hub;

public class LightFilter
	{
		public int x, y, width;
		private Color filterColour;
		private BufferedImage filterColourMap;

		public LightFilter(int x, int y, int width, Color filterColour)
			{
				this.x = x;
				this.y = y;
				this.width = width;
				this.filterColour = filterColour;

				filterColourMap = new BufferedImage(width, 4, BufferedImage.TYPE_INT_ARGB);
				Graphics g = filterColourMap.getGraphics();
				g.setColor(filterColour);
				g.fillRect(0, 0, width, 4);
				g.dispose();
			}

		public LightFilter(int x, int y, BufferedImage filterColourMap)
			{
				this.x = x;
				this.y = y;
				this.filterColourMap = filterColourMap;
				this.width = filterColourMap.getWidth();
			}

		public final void render(Graphics2D g, int simX)
			{
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
	}