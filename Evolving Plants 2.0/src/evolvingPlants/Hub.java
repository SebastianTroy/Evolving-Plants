package evolvingPlants;

import tCode.TCode;

public class Hub extends TCode
	{
		public static MainMenu menu = new MainMenu();
		public static SimulationWindow simWindow = new SimulationWindow();

		public Hub(int width, int height, boolean framed, boolean resizable)
			{
				super(width, height, framed, resizable);
				DEBUG = true;
				begin(menu);
			}

		public static void main(String[] args)
			{
				new Hub(1200, 600, true, false);
			}
	}