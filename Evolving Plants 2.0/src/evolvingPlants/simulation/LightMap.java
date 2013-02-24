package evolvingPlants.simulation;

import java.awt.Color;
import java.awt.image.BufferedImage;

import evolvingPlants.Hub;

import tools.ColTools;

public class LightMap
	{
		private static final int RED = 0;
		private static final int GREEN = 1;
		private static final int BLUE = 2;

		private int[][][] colourData;

		private int width, height, depth = 3;

		public LightMap(int width, int height)
			{
				this.width = width;
				this.height = height;

				colourData = new int[width][height][3];

				for (int x = 0; x < width; x++)
					for (int y = 0; y < height; y++)
						for (int c = 0; c < depth; c++)
							colourData[x][y][c] = 255;
			}

		public final void addShadow(int shadowX, int shadowY, int shadowWidth, Color colour)
			{
				int[] shadowCols = new int[3];
				shadowCols[RED] = (int) ((255 - colour.getRed()) * Hub.simWindow.sim.leafOpacity);
				shadowCols[GREEN] = (int) ((255 - colour.getGreen()) * Hub.simWindow.sim.leafOpacity);
				shadowCols[BLUE] = (int) ((255 - colour.getBlue()) * Hub.simWindow.sim.leafOpacity);

				if (shadowX + shadowWidth > width)
					shadowWidth = width - shadowX;

				for (int x = Math.max(0, shadowX); x < shadowX + shadowWidth; x++)
					for (int y = shadowY; y < height; y++)
						for (int c = 0; c < depth; c++)
							colourData[x][y][c] -= shadowCols[c];
			}

		public final void removeShadow(int shadowX, int shadowY, int shadowWidth, Color colour)
			{
				int[] shadowCols = new int[3];
				shadowCols[RED] = (int) ((255 - colour.getRed()) * Hub.simWindow.sim.leafOpacity);
				shadowCols[GREEN] = (int) ((255 - colour.getGreen()) * Hub.simWindow.sim.leafOpacity);
				shadowCols[BLUE] = (int) ((255 - colour.getBlue()) * Hub.simWindow.sim.leafOpacity);

				if (shadowX + shadowWidth > width)
					shadowWidth = width - shadowX;

				for (int x = Math.max(0, shadowX); x < shadowX + shadowWidth; x++)
					for (int y = shadowY; y < height; y++)
						for (int c = 0; c < depth; c++)
							colourData[x][y][c] += shadowCols[c];
			}

		public final BufferedImage getLightMap(BufferedImage image, int xPos)
			{
				for (int x = xPos; x < xPos + image.getWidth() && x < width; x++)
					for (int y = 0; y < image.getHeight(); y++)
						image.setRGB(x, y, ColTools.checkColour(colourData[x][y][RED], colourData[x][y][GREEN], colourData[x][y][BLUE]).getRGB());

				return image;
			}

		public int[] getLightAt(int x, int y, Color leafColour)
			{
				int[] lightCols = { 0, 0, 0 };

				if (x > 0 && x < width && y > 0 && y < height)
					{
						lightCols[RED] = colourData[x][y][RED] + leafColour.getRed();
						lightCols[GREEN] = colourData[x][y][GREEN] + leafColour.getGreen();
						lightCols[BLUE] = colourData[x][y][BLUE] + leafColour.getBlue();
					}

				return lightCols;
			}
	}