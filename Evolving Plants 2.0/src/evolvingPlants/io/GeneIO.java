package evolvingPlants.io;

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
import evolvingPlants.Hub;
import evolvingPlants.simulation.Genes;
import evolvingPlants.simulation.Simulation;

public class GeneIO
	{
		private String saveDirectory = System.getProperty("user.home") + "//Evolving Plants//Genes";

		private final BufferedImage saveImage = Hub.loadImage("save.jpeg");

		public GeneIO()
			{
				File directory = new File(saveDirectory);

				if (!directory.exists())
					{
						directory.mkdirs();
					}
			}

		public final void addGenesToMenu()
			{
				TMenu geneMenu = Hub.simWindow.savedGenesMenu;

				geneMenu.clearTComponents();

				createDefaultGenesFile();
				File[] files = new File(saveDirectory).listFiles();

				for (File file : files)
					{
						geneMenu.add(newButton(file), false);
					}
			}

		public void saveGenes(String geneName)
			{
				Genes genes = Hub.simWindow.sim.currentGenes;
				BufferedWriter out = null;

				try
					{
						File geneFile = new File(saveDirectory + "//" + geneName + ".txt");

						while (geneFile.exists())
							{
								geneName = new String(geneName + "-");
								geneFile = new File(saveDirectory + "//" + geneName + ".txt");
							}

						out = new BufferedWriter(new FileWriter(geneFile, false));
						out.write(genes.getGenes());
						out.newLine();
						out.write("Seed Size=" + genes.seedEnergy);
						out.newLine();
						out.write("Leaf Red=" + genes.leafColour.getRed());
						out.newLine();
						out.write("Leaf Green=" + genes.leafColour.getGreen());
						out.newLine();
						out.write("Leaf Blue=" + genes.leafColour.getBlue());
						out.newLine();
						out.write("Remember that the leaf colour represents Light NOT absorbed by the plant.");
						out.newLine();
						out.write("White (0, 0, 0), means no light absorbed. Black (255, 255, 255), means all light absorbed");
						out.newLine();
						out.write("The optimum absorbance is 200 units of light, this can be a mix of the three colours");

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

		public void loadGenes(String geneName)
			{
				BufferedReader in = null;
				Simulation sim = Hub.simWindow.sim;

				Color leafColour;

				try
					{
						File geneFile = new File(saveDirectory + "//" + geneName);

						in = new BufferedReader(new FileReader(geneFile));

						sim.currentGenes = new Genes(readStringFromLine(in.readLine()), readValueFromLine(in.readLine()), (int) readValueFromLine(in.readLine()),
								(int) readValueFromLine(in.readLine()), (int) readValueFromLine(in.readLine()));
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

		private final void createDefaultGenesFile()
			{

				// TODO have gene files within .jar and extract to folder when
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
						File genesFile = new File(saveDirectory + "//default" + ".txt");

						out = new BufferedWriter(new FileWriter(genesFile, false));
						out.write("Instructions=");
						for (int i = 0; i < 45; i++)
							out.write('|');
						out.newLine();
						out.write("Seed Size=100");
						out.newLine();
						out.write("Leaf Red=175");
						out.newLine();
						out.write("Leaf Green=175");
						out.newLine();
						out.write("Leaf Blue=175");
						out.newLine();
						out.write("Remember that the leaf colour represents Light NOT absorbed by the plant.");
						out.newLine();
						out.write("White (0, 0, 0), means no light absorbed. Black (255, 255, 255), means all light absorbed");
						out.newLine();
						out.write("The optimum absorbance is 200 units of light, this can be a mix of the three colours");
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

		private final String readStringFromLine(String text)
			{
				return text.substring(text.indexOf('=') + 1, text.length());
			}

		private final double readValueFromLine(String text)
			{
				return Double.parseDouble(text.substring(text.indexOf('=') + 1, text.length()));
			}

		private final GeneSaveButton newButton(File file)
			{
				BufferedImage im = new BufferedImage(175, 45, BufferedImage.TYPE_INT_ARGB);
				Graphics g = im.getGraphics();

				g.drawImage(saveImage, 5, 0, null);
				g.setColor(new Color(1, 1, 1, 1));
				g.fillRect(0, 0, 175, 45);
				g.setColor(Color.BLACK);
				g.drawString(file.getName(), 51, 25);
				g.dispose();

				return new GeneSaveButton(im, file);
			}

		private class GeneSaveButton extends TButton
			{
				private File file;

				public GeneSaveButton(BufferedImage image, File file)
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
										if (Hub.simWindow.loadGenesButton.isChecked())
											loadGenes(file.getName());
										else if (Hub.simWindow.deleteGenesButton.isChecked()
												&& JOptionPane.showConfirmDialog(Hub.simWindow.getObserver(), "Are you sure you want to delete this gene?", "Delete Confirmation",
														JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
											{
												file.delete();
												addGenesToMenu();
											}
									}
								active = down = false;
							}
					}
			}

	}