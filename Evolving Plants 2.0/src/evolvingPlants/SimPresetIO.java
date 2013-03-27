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

import javax.swing.JOptionPane;

import tComponents.components.TButton;
import tComponents.components.TMenu;

public class SimPresetIO
	{
		private String saveDirectory = System.getProperty("user.home") + "//Evolving Plants//Presets";

		private final BufferedImage saveImage = Hub.loadImage("save.jpeg");

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
						presetMenu.add(newButton(file), false);
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
						out.write(sim.leafSizeSlider.getValue() + "");
						out.newLine();
						out.write("StalkLength= ");
						out.write(sim.stalkLengthSlider.getValue() + "");
						out.newLine();
						out.write("MutationChance= ");
						out.write(sim.mutantOffspringSlider.getValue() + "");
						out.newLine();
						out.write("DNADamage= ");
						out.write(sim.dnaDamageSlider.getValue() + "");
						out.newLine();
						out.write("LargePlantSize= ");
						out.write(sim.largePlantSizeSlider.getValue() + "");
						out.newLine();
						out.write("LargePlantSpacing= ");
						out.write(sim.largePlantSpacingSlider.getValue() + "");
						out.newLine();
						out.write("MediumPlantSize= ");
						out.write(sim.mediumPlantSizeSlider.getValue() + "");
						out.newLine();
						out.write("MediumPlantSpacing= ");
						out.write(sim.mediumPlantSpacingSlider.getValue() + "");
						out.newLine();
						out.write("SmallPlantSpacing= ");
						out.write(sim.smallPlantSpacingSlider.getValue() + "");
						out.newLine();
						out.newLine();
						out.write("Light:");
						out.newLine();
						out.write("RedLightIntensity= ");
						out.write(sim.redLightSlider.getValue() + "");
						out.newLine();
						out.write("GreenLightIntensity= ");
						out.write(sim.greenLightSlider.getValue() + "");
						out.newLine();
						out.write("BlueLightIntensity= ");
						out.write(sim.blueLightSlider.getValue() + "");
						out.newLine();
						out.write(sim.leafOpacitySlider.getValue() + "");
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
						/**/sim.add(sim.plantOptionsMenu);
						sim.leafSizeSlider.setValue(readValueFromLine(in.readLine()));
						sim.stalkLengthSlider.setValue(readValueFromLine(in.readLine()));
						sim.mutantOffspringSlider.setValue(readValueFromLine(in.readLine()));
						sim.dnaDamageSlider.setValue(readValueFromLine(in.readLine()));
						sim.largePlantSizeSlider.setValue(readValueFromLine(in.readLine()));
						sim.largePlantSpacingSlider.setValue(readValueFromLine(in.readLine()));
						sim.mediumPlantSizeSlider.setValue(readValueFromLine(in.readLine()));
						sim.mediumPlantSpacingSlider.setValue(readValueFromLine(in.readLine()));
						sim.smallPlantSpacingSlider.setValue(readValueFromLine(in.readLine()));
						/**/sim.remove(sim.plantOptionsMenu);
						in.readLine();// /n
						in.readLine();// Light
						/**/sim.add(sim.lightOptionsMenu);
						sim.redLightSlider.setValue(readValueFromLine(in.readLine()));
						sim.greenLightSlider.setValue(readValueFromLine(in.readLine()));
						sim.blueLightSlider.setValue(readValueFromLine(in.readLine()));
						sim.leafOpacitySlider.setValue(readValueFromLine(in.readLine()));
						/**/sim.remove(sim.lightOptionsMenu);
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

				//TODO have preset files within .jar and extract to folder when missing.
				// URL inputUrl =
				// getClass().getResource("/absolute/path/of/source/in/jar/file");
				// File dest = new File("/path/to/destination/file");
				// FileUtils.copyURLToFile(inputUrl, dest);

				if (new File(saveDirectory + "default.txt").exists())
					return;

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
						out.write("MutationChance= 25.0");
						out.newLine();
						out.write("DNADamage= 2.0");
						out.newLine();
						out.write("LargePlantSize= 170.0");
						out.newLine();
						out.write("LargePlantSpacing= 65.0");
						out.newLine();
						out.write("MediumPlantSize= 30.0");
						out.newLine();
						out.write("MediumPlantSpacing= 25.0");
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
						out.newLine();
						out.write("LeafTransparency= 1.0");
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
				g.setColor(new Color(1, 1, 1, 1));
				g.fillRect(0, 0, 175, 45);
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
										else if (Hub.simWindow.deletePresetButton.isChecked()
												&& JOptionPane.showConfirmDialog(Hub.simWindow.getObserver(), "Are you sure you want to delete this preset?", "Delete Confirmation",
														JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
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