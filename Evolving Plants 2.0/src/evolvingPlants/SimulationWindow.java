package evolvingPlants;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import tCode.RenderableObject;
import tComponents.components.TButton;
import tComponents.components.TLabel;
import tComponents.components.TMenu;
import tComponents.components.TRadioButton;
import tComponents.components.TScrollBar;
import tComponents.components.TSlider;
import tComponents.components.TTextField;
import tComponents.utils.RadioButtonsCollection;
import tComponents.utils.events.TActionEvent;
import tComponents.utils.events.TScrollEvent;
import evolvingPlants.simulation.Genome;
import evolvingPlants.simulation.Simulation;

public class SimulationWindow extends RenderableObject
	{
		public Simulation sim;
		Rectangle simBounds = new Rectangle(200, 70, 800, 480);
		private final TScrollBar simulationScroller = new TScrollBar(200, 0, 800, 800, TScrollBar.HORIZONTAL, new Rectangle(200, 0, 800, 550));

		final TMenu topMenu = new TMenu(200, 0, 800, 70, TMenu.HORIZONTAL);
		private final TButton plantInteractionsButton = new TButton("Interactions");
		private final TButton plantOptionsButton = new TButton("Plant Options");
		private final TButton lightOptionsButton = new TButton("Light Options");
		private final TButton filterOptionsButton = new TButton("Filter Options");
		private final TButton presetOptionsButton = new TButton("Preset Options");
		private final TButton geneOptionsButton = new TButton("Gene Options");

		TMenu currentLeftSideMenu;
		TMenu currentRightSideMenu;

		TMenu simOptionsMenu;
		public final TSlider ticksPerSecondSlider = new TSlider(TSlider.HORIZONTAL);
		private final TButton stepSimButton = new TButton("Step")
			{
				@Override
				public final void pressed()
					{
						sim.secondsPassed = sim.secondsBetweenTicks;
					}
			};

		private final TButton playPauseSimButton = new TButton("Pause")
			{
				@Override
				public final void pressed()
					{
						sim.paused = !sim.paused;
						this.setLabel(sim.paused ? "Play" : "Pause", true);
					}
			};

		private final TButton resetSimButton = new TButton("Reset Simulation");
		private final TButton mainMenuButton = new TButton("Main Menu");

		TMenu interactionsMenu;
		private RadioButtonsCollection interactionButtons = new RadioButtonsCollection();
		private TRadioButton selectPlantButton = new TRadioButton("Select Plants");
		private TRadioButton plantSeedButton = new TRadioButton("Plant Seeds");
		private TRadioButton getGenesButton = new TRadioButton("Extract Genes");
		private TRadioButton killPlantsButton = new TRadioButton("Kill Plants");
		private TRadioButton moveFilterButton = new TRadioButton("Move Filters");
		private TRadioButton deleteFilterButton = new TRadioButton("Delete Filters");
		public Cursor plantSeedCursor;
		public Cursor getGenesCursor;
		public Cursor killPlantCursor;
		public Cursor moveFilterCursor;
		public Cursor deleteFilterCursor;
		public Cursor currentCursor = getObserver().getCursor();

		public TMenu plantOptionsMenu;
		public final TSlider mutantOffspringSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider dnaDamageSlider = new TSlider(TSlider.HORIZONTAL);
		public final TButton showLightButton = new TButton("Show light");

		public TMenu lightOptionsMenu;
		public final TSlider lightSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider leafOpacitySlider = new TSlider(TSlider.HORIZONTAL);

		public TMenu filterOptionsMenu;
		public final TSlider filterWidthSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider filterOpacitySlider = new TSlider(TSlider.HORIZONTAL);
		private final TButton createFilterButton = new TButton("Create Filter");

		TMenu geneOptionsMenu;
		private final TTextField geneSaveNameField = new TTextField(0, 0, 160, 25, "My Genes");
		private final TButton saveGenesButton = new TButton("Save Current Genes");
		public final TMenu savedGenesMenu = new TMenu(0, 0, 200, 340, TMenu.VERTICAL);
		private RadioButtonsCollection loadDeleteGenesButtons = new RadioButtonsCollection();
		public final TRadioButton loadGenesButton = new TRadioButton("Load Genes");
		public final TRadioButton deleteGenesButton = new TRadioButton("Delete Genes");
		private final TButton openGenesFolderButton = new TButton("Open Folder");

		TMenu presetOptionsMenu;
		private final TTextField presetSaveNameField = new TTextField(0, 0, 160, 25, "My Settings");
		private final TButton savePresetButton = new TButton("Save Current Settings");
		public final TMenu savedPresetsMenu = new TMenu(0, 0, 200, 340, TMenu.VERTICAL);
		private RadioButtonsCollection loadDeletePresetButtons = new RadioButtonsCollection();
		public final TRadioButton loadPresetButton = new TRadioButton("Load Presets");
		public final TRadioButton deletePresetButton = new TRadioButton("Delete Presets");
		private final TButton openPresetsFolderButton = new TButton("Open Folder");

		@Override
		protected void initiate()
			{
				simulationScroller.setY(Main.canvasHeight - 20);
				simulationScroller.setMaxScrollDistance(sim.simWidth);

				if (sim.simWidth > 800)
					add(simulationScroller);
				else
					remove(simulationScroller);

				simOptionsMenu = new TMenu(1000, 0, 200, Main.canvasHeight, TMenu.VERTICAL);
				interactionsMenu = new TMenu(0, 0, 200, Main.canvasHeight, TMenu.VERTICAL);
				plantOptionsMenu = new TMenu(0, 0, 200, Main.canvasHeight, TMenu.VERTICAL);
				lightOptionsMenu = new TMenu(0, 0, 200, Main.canvasHeight, TMenu.VERTICAL);
				filterOptionsMenu = new TMenu(0, 0, 200, Main.canvasHeight, TMenu.VERTICAL);
				geneOptionsMenu = new TMenu(0, 0, 200, Main.canvasHeight, TMenu.VERTICAL);
				presetOptionsMenu = new TMenu(0, 0, 200, Main.canvasHeight, TMenu.VERTICAL);
				topMenu.setBorderSize(5);

				Toolkit k = Toolkit.getDefaultToolkit();
				plantSeedCursor = k.createCustomCursor(Main.loadImage("seedPointer.png"), new Point(0, 0), "seed");
				getGenesCursor = k.createCustomCursor(Main.loadImage("dna.png"), new Point(0, 0), "genes");
				killPlantCursor = k.createCustomCursor(Main.loadImage("skull.png"), new Point(0, 0), "kill");
				deleteFilterCursor = k.createCustomCursor(Main.loadImage("bin.png"), new Point(0, 0), "move");
				moveFilterCursor = k.createCustomCursor(Main.loadImage("fourWayArrow.png"), new Point(0, 0), "delete");

				// Top menu set-up.
				add(topMenu);
				topMenu.add(plantInteractionsButton);
				topMenu.add(plantOptionsButton);
				topMenu.add(lightOptionsButton);
				topMenu.add(filterOptionsButton);
				topMenu.add(geneOptionsButton);
				topMenu.add(presetOptionsButton);

				// PlantInteractions menu set-up. This menu is located on the
				// left
				interactionButtons.add(selectPlantButton);
				interactionButtons.add(plantSeedButton);
				interactionButtons.add(getGenesButton);
				interactionButtons.add(killPlantsButton);
				interactionButtons.add(moveFilterButton);
				interactionButtons.add(deleteFilterButton);
				interactionsMenu.add(selectPlantButton);
				interactionsMenu.add(plantSeedButton);
				interactionsMenu.add(getGenesButton);
				interactionsMenu.add(killPlantsButton);
				interactionsMenu.add(moveFilterButton);
				interactionsMenu.add(deleteFilterButton);

				// SimOptions menu set-up. This menu is located on the right
				ticksPerSecondSlider.setRange(0, 10000);
				simOptionsMenu.add(new TLabel("Ticks per second"), false);
				ticksPerSecondSlider.setValue(15);
				simOptionsMenu.add(ticksPerSecondSlider);
				simOptionsMenu.add(stepSimButton);
				simOptionsMenu.add(playPauseSimButton);
				simOptionsMenu.add(resetSimButton);
				simOptionsMenu.add(mainMenuButton);

				// PlantOptions menu set-up. This menu is located on the left
				TLabel allPlantsLabel = new TLabel("All Plants");
				allPlantsLabel.setFontSize(15);
				allPlantsLabel.setBackgroundColour(new Color(0, 200, 200));
				plantOptionsMenu.add(allPlantsLabel, false);
				plantOptionsMenu.add(new TLabel("Chance of mutant offspring (%)"), false);
				plantOptionsMenu.add(mutantOffspringSlider);
				plantOptionsMenu.add(new TLabel("Damage to mutant DNA (%)"), false);
				dnaDamageSlider.setRange(0, 15);
				plantOptionsMenu.add(dnaDamageSlider);

				// GeneOptionsMenu set-up. This menu is located on the left
				geneOptionsMenu.add(geneSaveNameField, false);
				geneOptionsMenu.add(saveGenesButton);
				geneOptionsMenu.add(savedGenesMenu, false);
				geneOptionsMenu.add(loadGenesButton);
				geneOptionsMenu.add(deleteGenesButton);
				loadDeleteGenesButtons.add(loadGenesButton);
				loadDeleteGenesButtons.add(deleteGenesButton);
				geneOptionsMenu.add(openGenesFolderButton);

				// PresetOptionsMenu set-up. This menu is located on the left
				presetOptionsMenu.add(presetSaveNameField, false);
				presetOptionsMenu.add(savePresetButton);
				presetOptionsMenu.add(savedPresetsMenu, false);
				presetOptionsMenu.add(loadPresetButton);
				presetOptionsMenu.add(deletePresetButton);
				loadDeletePresetButtons.add(loadPresetButton);
				loadDeletePresetButtons.add(deletePresetButton);
				presetOptionsMenu.add(openPresetsFolderButton);

				// LightOptions menu set-up. This menu is located on the left
				TLabel lightOptionsLabel = new TLabel("Sunlight Options");
				lightOptionsLabel.setFontSize(15);
				lightOptionsLabel.setBackgroundColour(new Color(0, 200, 200));
				lightOptionsMenu.add(lightOptionsLabel, false);
				lightOptionsMenu.add(showLightButton);
				lightOptionsMenu.add(new TLabel("Light Intensity"), false);
				lightSlider.setRange(0, 255);
				lightSlider.setSliderImage(0, Main.loadImage("sun.png"));
				lightOptionsMenu.add(lightSlider);
				lightOptionsMenu.add(new TLabel("Leaf Transparency"), false);
				leafOpacitySlider.setRange(0, 255);
				lightOptionsMenu.add(leafOpacitySlider);

				// FilterOptions menu set-up. This menu is located on the left
				TLabel filterOptionsLabel = new TLabel("Light Filter Options");
				filterOptionsLabel.setFontSize(15);
				filterOptionsLabel.setBackgroundColour(new Color(0, 200, 200));
				filterOptionsMenu.add(filterOptionsLabel, false);
				filterOptionsMenu.add(new TLabel("Filter Width"), false);
				filterWidthSlider.setRange(0, sim.simWidth);
				filterOptionsMenu.add(filterWidthSlider);
				filterOpacitySlider.setSliderImage(0, Main.loadImage("filter.png"));
				filterOpacitySlider.setRange(0, 255);
				filterOptionsMenu.add(filterOpacitySlider);
				filterOptionsMenu.add(createFilterButton);

				setLeftMenu(interactionsMenu);
				setRightMenu(simOptionsMenu);

				Main.geneIO.addGenesToMenu();
				// Main.geneIO.loadGenes("default.txt");
				sim.currentGenes = new Genome();

				Main.presetIO.addPresetsToMenu();
				Main.presetIO.loadPreset("default.txt");
			}

		@Override
		public void tick(double secondsPassed)
			{
				sim.tick(secondsPassed);
			}

		@Override
		protected void render(Graphics2D g)
			{
				sim.render(g);
			}

		final void setLeftMenu(TMenu newMenu)
			{
				remove(currentLeftSideMenu);
				currentLeftSideMenu = newMenu;
				currentLeftSideMenu.setLocation(0, 0);
				currentLeftSideMenu.setDimensions(200, Main.canvasHeight);
				add(currentLeftSideMenu);
			}

		private final void setRightMenu(TMenu newMenu)
			{
				remove(currentRightSideMenu);
				currentRightSideMenu = newMenu;
				currentRightSideMenu.setLocation(1000, 0);
				currentRightSideMenu.setDimensions(200, Main.canvasHeight / 2);
				add(currentRightSideMenu);
			}

		@Override
		public final void tActionEvent(TActionEvent e)
			{
				Object eventSource = e.getSource();

				// Important buttons
				if (eventSource == mainMenuButton)
					changeRenderableObject(Main.menu);
				else if (eventSource == resetSimButton)
					sim.reset = true;
				// Change menu's ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				else if (eventSource == plantInteractionsButton)
					setLeftMenu(interactionsMenu);
				else if (eventSource == plantOptionsButton)
					setLeftMenu(plantOptionsMenu);
				else if (eventSource == lightOptionsButton)
					setLeftMenu(lightOptionsMenu);
				else if (eventSource == filterOptionsButton)
					setLeftMenu(filterOptionsMenu);
				else if (eventSource == geneOptionsButton)
					setLeftMenu(geneOptionsMenu);
				else if (eventSource == presetOptionsButton)
					setLeftMenu(presetOptionsMenu);
				// Change Cursor ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				else if (eventSource == selectPlantButton)
					currentCursor = Cursor.getDefaultCursor();
				else if (eventSource == plantSeedButton)
					currentCursor = plantSeedCursor;
				else if (eventSource == getGenesButton)
					currentCursor = getGenesCursor;
				else if (eventSource == killPlantsButton)
					currentCursor = killPlantCursor;
				else if (eventSource == moveFilterButton)
					currentCursor = moveFilterCursor;
				else if (eventSource == deleteFilterButton)
					currentCursor = deleteFilterCursor;
				// Update light
				else if (eventSource == showLightButton)
					{
						if (!sim.showLighting)
							sim.updateLighting();

						sim.showLighting = !sim.showLighting;
						showLightButton.setLabel(sim.showLighting ? "Hide light" : "Show Light", true);
					}
				// Filter options
				else if (eventSource == createFilterButton)
					{
						sim.addFilter = true;
					}
				// Preset options
				else if (eventSource == savePresetButton)
					{
						Main.presetIO.savePreset(presetSaveNameField.getText());
					}
				else if (eventSource == openPresetsFolderButton)
					Main.presetIO.openFolder();
				// Gene options
				else if (eventSource == saveGenesButton)
					{
						Main.geneIO.saveGenes(sim.currentGenes, geneSaveNameField.getText());
					}
				else if (eventSource == openGenesFolderButton)
					Main.geneIO.openFolder();
			}

		@Override
		public final void tScrollEvent(TScrollEvent e)
			{
				Object eventSource = e.getSource();

				// Update simulation position
				if (eventSource == simulationScroller)
					{
						sim.simX = -e.getScrollValue();
						if (sim.showLighting)
							sim.updateLighting();
					}
				// update light
				else if (eventSource == lightSlider && (e.getScrollType() == TScrollEvent.FINAL_VALUE || e.getScrollType() == TScrollEvent.VALUE_SET_INTERNALLY))
					{
						sim.lightMap.setLight(lightSlider.getValue());
						if (sim.showLighting)
							sim.updateLighting();
					}
				else if (eventSource == ticksPerSecondSlider)
					sim.secondsBetweenTicks = 1.0 / ticksPerSecondSlider.getValue();
			}

		@Override
		public void mousePressed(MouseEvent e)
			{
				sim.mousePressed(e);
			}

		@Override
		public void mouseReleased(MouseEvent e)
			{
				sim.mouseReleased(e);
			}

		@Override
		public void mouseMoved(MouseEvent e)
			{
				if (simBounds.contains(e.getPoint()))
					getObserver().setCursor(currentCursor);
				else if (getObserver().getCursor().getType() != Cursor.TEXT_CURSOR)
					getObserver().setCursor(Cursor.getDefaultCursor());
			}

		@Override
		public void mouseDragged(MouseEvent e)
			{
				sim.mouseDragged(e);

				if (simBounds.contains(e.getPoint()))
					getObserver().setCursor(currentCursor);
				else if (getObserver().getCursor().getType() != Cursor.TEXT_CURSOR)
					getObserver().setCursor(Cursor.getDefaultCursor());
			}
	}