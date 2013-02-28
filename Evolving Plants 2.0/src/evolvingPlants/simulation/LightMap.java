package evolvingPlants.simulation;

import java.awt.Color;
import java.awt.image.BufferedImage;

import tools.ColTools;

public class LightMap
	{
		private static final int RED = 0;
		private static final int GREEN = 1;
		private static final int BLUE = 2;

		private int[][][] lightData;
		private int[] baseLightColours = { 255, 255, 255 };

		private int width, height, depth = 3;

		public LightMap(int width, int height)
			{
				this.width = width;
				this.height = height;

				lightData = new int[width][height][3];

				for (int x = 0; x < width; x++)
					for (int y = 0; y < height; y++)
						for (int c = 0; c < depth; c++)
							lightData[x][y][c] = baseLightColours[c];
			}

		public void setRedLight(double newRed)
			{
				int difference = (int) newRed - baseLightColours[RED];

				for (int x = 0; x < width; x++)
					for (int y = 0; y < height; y++)
						lightData[x][y][RED] += difference;

				baseLightColours[RED] = (int) newRed;
			}

		public void setGreenLight(double newGreen)
			{
				int difference = (int) newGreen - baseLightColours[GREEN];

				for (int x = 0; x < width; x++)
					for (int y = 0; y < height; y++)
						lightData[x][y][GREEN] += difference;

				baseLightColours[GREEN] = (int) newGreen;
			}

		public void setBlueLight(double newBlue)
			{
				int difference = (int) newBlue - baseLightColours[BLUE];

				for (int x = 0; x < width; x++)
					for (int y = 0; y < height; y++)
						lightData[x][y][BLUE] += difference;

				baseLightColours[BLUE] = (int) newBlue;
			}

		public final void addShadow(int shadowX, int shadowY, int shadowWidth, Color shadowColour)
			{
				int[] shadowCols = new int[3];
				shadowCols[RED] = shadowColour.getRed();
				shadowCols[GREEN] = shadowColour.getGreen();
				shadowCols[BLUE] = shadowColour.getBlue();

				if (shadowX + shadowWidth > width)
					shadowWidth = width - shadowX;

				for (int x = Math.max(0, shadowX); x < shadowX + shadowWidth; x++)
					for (int y = shadowY; y < height; y++)
						for (int c = 0; c < depth; c++)
							lightData[x][y][c] -= shadowCols[c];
			}

		public final void removeShadow(int shadowX, int shadowY, int shadowWidth, Color shadowColour)
			{
				int[] shadowCols = new int[3];
				shadowCols[RED] = shadowColour.getRed();
				shadowCols[GREEN] = shadowColour.getGreen();
				shadowCols[BLUE] = shadowColour.getBlue();

				if (shadowX + shadowWidth > width)
					shadowWidth = width - shadowX;

				for (int x = Math.max(0, shadowX); x < shadowX + shadowWidth; x++)
					for (int y = shadowY; y < height; y++)
						for (int c = 0; c < depth; c++)
							lightData[x][y][c] += shadowCols[c];
			}

		public final BufferedImage getLightMap(BufferedImage image, int xPos)
			{
				for (int x = xPos; x < xPos + image.getWidth() && x < width; x++)
					for (int y = 0; y < image.getHeight(); y++)
						image.setRGB(x - xPos, y, ColTools.checkColour(lightData[x][y][RED], lightData[x][y][GREEN], lightData[x][y][BLUE]).getRGB());

				return image;
			}

		public int[] getLightMinusShadowAt(int x, int y, Color shadowColor)
			{
				int[] lightCols = { 0, 0, 0 };

				if (x > 0 && x < width && y > 0 && y < height)
					{
						lightCols[RED] = lightData[x][y][RED] + shadowColor.getRed();
						lightCols[GREEN] = lightData[x][y][GREEN] + shadowColor.getGreen();
						lightCols[BLUE] = lightData[x][y][BLUE] + shadowColor.getBlue();
					}

				return lightCols;
			}
	}