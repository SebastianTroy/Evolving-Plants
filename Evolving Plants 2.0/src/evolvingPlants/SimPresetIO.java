package evolvingPlants;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import tCode.RenderableObject;
import tComponents.components.TButton;
import tComponents.components.TMenu;
import tComponents.utils.events.TActionEvent;

public class SimPresetIO
	{
		private String saveDirectory = System.getProperty("user.home") + "//Evolving Plants//Presets";

		private final BufferedImage saveImage = Hub.loadImage("save.jpeg");
		private final BufferedImage binImage = Hub.loadImage("bin.png");

		public SimPresetIO()
			{
				File directory = new File(saveDirectory);

				if (!directory.exists())
					{
						directory.mkdirs();
					}
			}

		public final void addPresetsToMenu()
			{
				TMenu presetMenu = Hub.simWindow.savedPresetsMenu;
				
				presetMenu.clearTComponents();

				createDefaultPresetFile();
				File[] files = new File(saveDirectory).listFiles();

				for (File file : files)
					{
						presetMenu.addTComponent(newButton(file), false);
					}
			}

		public void createPreset(String presetName)
			{
				SimulationWindow sim = Hub.simWindow;
				BufferedWriter out = null;

				try
					{
						File presetFile = new File(saveDirectory + "//" + presetName + ".txt");
						
						if (presetFile.exists())
							presetFile = new File(saveDirectory + "//" + presetName + "-.txt");
						
						out = new BufferedWriter(new FileWriter(presetFile, false));
						out.write("Plants:");
						out.newLine();
						out.write("LeafSize= ");
						out.write(sim.leafSizeSlider.getSliderValue() + "");
						out.newLine();
						out.write("StalkLength= ");
						out.write(sim.stalkLengthSlider.getSliderValue() + "");
						out.newLine();
						out.write("LargePlantSize= ");
						out.write(sim.largePlantSizeSlider.getSliderValue() + "");
						out.newLine();
						out.write("LargePlantSpacing= ");
						out.write(sim.largePlantSpacingSlider.getSliderValue() + "");
						out.newLine();
						out.write("MediumPlantSize= ");
						out.write(sim.mediumPlantSizeSlider.getSliderValue() + "");
						out.newLine();
						out.write("MediumPlantSpacing= ");
						out.write(sim.mediumPlantSpacingSlider.getSliderValue() + "");
						out.newLine();
						out.write("SmallPlantSpacing= ");
						out.write(sim.smallPlantSpacingSlider.getSliderValue() + "");
						out.newLine();
						out.newLine();
						out.write("Light:");
						out.newLine();
						out.write("RedLightIntensity= ");
						out.write(sim.redLightSlider.getSliderValue() + "");
						out.newLine();
						out.write("GreenLightIntensity= ");
						out.write(sim.greenLightSlider.getSliderValue() + "");
						out.newLine();
						out.write("BlueLightIntensity= ");
						out.write(sim.blueLightSlider.getSliderValue() + "");
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

		public void loadPreset(String presetName)
			{
				BufferedReader in = null;
				SimulationWindow sim = Hub.simWindow;

				try
					{
						File presetFile = new File(saveDirectory + "//" + presetName);

						in = new BufferedReader(new FileReader(presetFile));
						in.readLine();// Plants
						sim.leafSizeSlider.setSliderValue(readValueFromLine(in.readLine()));
						sim.stalkLengthSlider.setSliderValue(readValueFromLine(in.readLine()));
						sim.largePlantSizeSlider.setSliderValue(readValueFromLine(in.readLine()));
						sim.largePlantSpacingSlider.setSliderValue(readValueFromLine(in.readLine()));
						sim.mediumPlantSizeSlider.setSliderValue(readValueFromLine(in.readLine()));
						sim.mediumPlantSpacingSlider.setSliderValue(readValueFromLine(in.readLine()));
						sim.smallPlantSpacingSlider.setSliderValue(readValueFromLine(in.readLine()));
						in.readLine();// /n
						in.readLine();// Light
						sim.redLightSlider.setSliderValue(readValueFromLine(in.readLine()));
						sim.greenLightSlider.setSliderValue(readValueFromLine(in.readLine()));
						sim.blueLightSlider.setSliderValue(readValueFromLine(in.readLine()));
					}
				catch (Exception e)
					{
						e.printStackTrace();
					}
				finally
					{
						try
							{
								in.close();
							}
						catch (IOException e)
							{
								e.printStackTrace();
							}
					}
			}

		private final double readValueFromLine(String text)
			{
				return Double.parseDouble(text.substring(text.indexOf('=') + 1, text.length()));
			}

		private final void createDefaultPresetFile()
			{
				BufferedWriter out = null;

				try
					{
						File presetFile = new File(saveDirectory + "//default" + ".txt");

						out = new BufferedWriter(new FileWriter(presetFile, false));

						out.write("Plants:");
						out.newLine();
						out.write("LeafSize= 14.0");
						out.newLine();
						out.write("StalkLength= 20.0");
						out.newLine();
						out.write("LargePlantSize= 250.0");
						out.newLine();
						out.write("LargePlantSpacing= 75.0");
						out.newLine();
						out.write("MediumPlantSize= 22.0");
						out.newLine();
						out.write("MediumPlantSpacing= 35.0");
						out.newLine();
						out.write("SmallPlantSpacing= 5.0");
						out.newLine();
						out.newLine();
						out.write("Light:");
						out.newLine();
						out.write("RedLightIntensity= 255.0");
						out.newLine();
						out.write("GreenLightIntensity= 255.0");
						out.newLine();
						out.write("BlueLightIntensity= 255.0");
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

		private final PresetSaveButton newButton(File file)
			{
				BufferedImage im = new BufferedImage(175, 45, BufferedImage.TYPE_INT_ARGB);
				Graphics g = im.getGraphics();

				g.drawImage(saveImage, 5, 0, null);
				g.setColor(Color.BLACK);
				g.drawString(file.getName(), 51, 25);
				g.dispose();

				return new PresetSaveButton(im, file);
			}

		private class PresetSaveButton extends TButton
			{
				private File file;

				public PresetSaveButton(BufferedImage image, File file)
					{
						super(0, 0, image);
						this.file = file;
					}

				@Override
				public void mouseReleased(MouseEvent e)
					{
						if (e.getButton() == MouseEvent.BUTTON1)
							{
								if (down)
									{
										if (Hub.simWindow.loadPresetButton.isChecked())
											loadPreset(file.getName());
										else if (Hub.simWindow.deletePresetButton.isChecked())
											{
												file.delete();
												addPresetsToMenu();
											}
									}
								active = down = false;
							}
					}
			}
	}