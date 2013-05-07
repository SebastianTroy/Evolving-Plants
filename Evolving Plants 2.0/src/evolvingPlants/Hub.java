package evolvingPlants;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import tCode.TCode;
import evolvingPlants.io.GeneIO;
import evolvingPlants.io.SimPresetIO;

public class Hub extends TCode
	{
		public static final GeneIO geneIO = new GeneIO();
		public static final SimPresetIO presetIO = new SimPresetIO();

		public static final MainMenu menu = new MainMenu();
		public static SimulationWindow simWindow;
		public static final GeneEditor editor = new GeneEditor();

		public Hub(int width, int height, boolean framed, boolean resizable)
			{
				super(width, height, framed, resizable);
				frame.icons.add(loadImage("icon.png"));
				programName = "Evolving Plants 2.0";
				versionNumber = "0.2_1";
				DEBUG = true;
				begin(menu);
			}

		public static void main(String[] args)
			{
				new Hub(1200, 600, true, false);
			}

		public static BufferedImage loadImage(String name)
			{
				try
					{
						return ImageIO.read(Hub.class.getResource("/" + name));
					}
				catch (Exception e)
					{
						e.printStackTrace();
						return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
					}
			}
	}