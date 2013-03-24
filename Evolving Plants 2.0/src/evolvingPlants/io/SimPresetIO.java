package evolvingPlants.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class SimPresetIO
	{
		public void createPreset(String presetName)
			{
				File presetFile = new File("Saves/" + presetName + ".txt");
				presetFile.mkdirs();
				FileOutputStream fos;
				ObjectOutputStream out;
				try
					{
						fos = new FileOutputStream("Saves/" + presetName + ".txt");
						out = new ObjectOutputStream(fos);

						out.writeObject("All Plants:/n");
						out.writeObject("Leaf Size");

					}
				catch (Exception e)
					{
						e.printStackTrace();
					}
			}
	}