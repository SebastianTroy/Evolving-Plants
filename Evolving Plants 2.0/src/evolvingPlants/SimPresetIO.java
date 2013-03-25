package evolvingPlants;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;


public class SimPresetIO
	{
		private String saveDirectory = System.getProperty("user.home") + "//Evolving Plants//Presets//";

		public SimPresetIO()
			{
				File directory = new File(System.getProperty("user.home") + "//Evolving Plants//Presets");

				if (!directory.exists())
					{
						directory.mkdirs();
					}
			}

		public void createPreset(String presetName)
			{
				SimulationWindow sim = Hub.simWindow;

				BufferedWriter out = null;

				try
					{
						File presetFile = new File(saveDirectory + presetName + ".txt");

						out = new BufferedWriter(new FileWriter(presetFile, false));
						out.write("All Plants:");
						out.newLine();
						out.write("Leaf Size= ");
						out.write((int) sim.leafSizeSlider.getSliderValue());
						out.newLine();
					}
				catch (Exception e)
					{
						e.printStackTrace();
					}
				finally
					{
						try
							{
								out.flush();
								out.close();
							}
						catch (IOException e)
							{
								e.printStackTrace();
							}
					}
			}
	}