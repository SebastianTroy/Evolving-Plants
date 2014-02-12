package evolvingPlants.io;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import javax.swing.JOptionPane;

import tComponents.components.TButton;
import tComponents.components.TMenu;
import evolvingPlants.Main;
import evolvingPlants.SimulationWindow;

public class SimPresetIO
	{
		private String saveDirectory = System.getProperty("user.home") + "//Evolving Plants//Presets";

		private final BufferedImage saveImage = Main.loadImage("save.jpeg");

		public SimPresetIO()
			{
				File directory = new File(saveDirectory);

				if (!directory.exists())
					{
						directory.mkdirs();
					}
				new FileWatcher();
			}

		public final void addPresetsToMenu()
			{
				TMenu presetMenu = Main.simWindow.savedPresetsMenu;

				presetMenu.clearTComponents();

				createDefaultPresetFile();
				File[] files = new File(saveDirectory).listFiles();

				for (File file : files)
					{
						presetMenu.add(newButton(file), false);
					}
			}

		public final void openFolder()
			{
				File file = new File(saveDirectory);
				Desktop desktop = Desktop.getDesktop();
				try
					{
						desktop.open(file);
					}
				catch (IOException e)
					{
						e.printStackTrace();
					}
			}

		public void savePreset(String presetName)
			{
				SimulationWindow sim = Main.simWindow;
				BufferedWriter out = null;

				try
					{
						File presetFile = new File(saveDirectory + "//" + presetName + ".txt");

						while (presetFile.exists())
							{
								presetName = new String(presetName + "-");
								presetFile = new File(saveDirectory + "//" + presetName + ".txt");
							}

						out = new BufferedWriter(new FileWriter(presetFile, false));
						out.write("Plants:");
						out.newLine();
						out.write("LeafSize= " + sim.leafSizeSlider.getValue());
						out.newLine();
						out.write("StalkLength= " + sim.stalkLengthSlider.getValue());
						out.newLine();
						out.write("MutationChance= " + sim.mutantOffspringSlider.getValue());
						out.newLine();
						out.write("DNADamage= " + sim.dnaDamageSlider.getValue());
						out.newLine();
						out.newLine();
						out.write("Light:");
						out.newLine();
						out.write("RedLightIntensity= " + sim.redLightSlider.getValue());
						out.newLine();
						out.write("GreenLightIntensity= " + sim.greenLightSlider.getValue());
						out.newLine();
						out.write("BlueLightIntensity= " + sim.blueLightSlider.getValue());
						out.newLine();
						out.write("LeafTransparency= " + sim.leafOpacitySlider.getValue());
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
				SimulationWindow sim = Main.simWindow;

				try
					{
						sim.sim.pause();
						
						File presetFile = new File(saveDirectory + "//" + presetName);

						in = new BufferedReader(new FileReader(presetFile));
						in.readLine();// Plants
						sim.plantOptionsMenu.setX(-200);
						/**/sim.add(sim.plantOptionsMenu);
						sim.leafSizeSlider.setValue(readValueFromLine(in.readLine()));
						sim.stalkLengthSlider.setValue(readValueFromLine(in.readLine()));
						sim.mutantOffspringSlider.setValue(readValueFromLine(in.readLine()));
						sim.dnaDamageSlider.setValue(readValueFromLine(in.readLine()));
						/**/sim.remove(sim.plantOptionsMenu);
						in.readLine();// /n
						in.readLine();// Light
						sim.lightOptionsMenu.setX(-200);
						/**/sim.add(sim.lightOptionsMenu);
						sim.redLightSlider.setValue(readValueFromLine(in.readLine()));
						sim.greenLightSlider.setValue(readValueFromLine(in.readLine()));
						sim.blueLightSlider.setValue(readValueFromLine(in.readLine()));
						sim.leafOpacitySlider.setValue(readValueFromLine(in.readLine()));
						/**/sim.remove(sim.lightOptionsMenu);
						
						sim.sim.unpause();
					}
				catch (Exception e)
					{
						if (presetName == "default.txt")
							createDefaultPresetFile();

						loadPreset("default.txt");
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
						
						sim.sim.unpause();
					}
			}

		private final double readValueFromLine(String text)
			{
				return Double.parseDouble(text.substring(text.indexOf('=') + 1, text.length()));
			}

		private final void createDefaultPresetFile()
			{

				// TODO have preset files within .jar and extract to folder when
				// missing.
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
						out.write("MutationChance= 35.0");
						out.newLine();
						out.write("DNADamage= 4.0");
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
						out.write("LeafTransparency= 0.0");
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

		private class FileWatcher extends Thread
			{
				Path this_dir = new File(saveDirectory).toPath();

				private FileWatcher()
					{
						start();
					}

				@Override
				public final void run()
					{
						while (true)
							try
								{
									WatchService watcher = this_dir.getFileSystem().newWatchService();
									this_dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);

									WatchKey watckKey = watcher.take();

									List<WatchEvent<?>> events = watckKey.pollEvents();
									if (!events.isEmpty())
										addPresetsToMenu();
								}
							catch (Exception e)
								{
									System.out.println(e.toString());
								}
					}
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
										if (Main.simWindow.loadPresetButton.isChecked())
											loadPreset(file.getName());
										else if (Main.simWindow.deletePresetButton.isChecked()
												&& JOptionPane.showConfirmDialog(Main.simWindow.getObserver(), "Are you sure you want to delete this preset?", "Delete Confirmation",
														JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
											file.delete();
									}
								active = down = false;
							}
					}
			}
	}