package evolvingPlants;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
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
import evolvingPlants.simulation.Simulation;

public class SimulationWindow extends RenderableObject
	{
		public Simulation sim;
		Rectangle simBounds = new Rectangle(200, 70, 800, 480);
		private final TScrollBar simulationScroller = new TScrollBar(200, 0, 800, 800, TScrollBar.HORIZONTAL, new Rectangle(200, 0, 800, 550));

		final TMenu topMenu = new TMenu(200, 0, 800, 70, TMenu.HORIZONTAL);
		private final TButton plantInteractionsButton = new TButton("Plant Interactions");
		private final TButton plantOptionsButton = new TButton("Plant Options");
		private final TButton lightOptionsButton = new TButton("Light Options");
		private final TButton presetOptionsButton = new TButton("Preset Options");
		private final TButton geneOptionsButton = new TButton("Gene Options");

		TMenu currentLeftSideMenu;
		TMenu currentRightSideMenu;

		TMenu simOptionsMenu;
		public final TSlider playbackSpeed = new TSlider(TSlider.HORIZONTAL);
		public final TSlider warpSpeedSlider = new TSlider(TSlider.HORIZONTAL);
		private final TButton resetSimButton = new TButton("Reset Simulation");
		private final TButton mainMenuButton = new TButton("Main Menu");

		TMenu plantInteractionsMenu;
		private RadioButtonsCollection plantInteractionButtons = new RadioButtonsCollection();
		private TRadioButton selectPlantButton = new TRadioButton("Select Plants");
		private TRadioButton plantSeedButton = new TRadioButton("Plant Seeds");
		private TRadioButton getGenesButton = new TRadioButton("Extract Genes");
		private TRadioButton killPlantsButton = new TRadioButton("Kill Plants");
		public Cursor plantSeedCursor;
		public Cursor getGenesCursor;
		public Cursor killPlantCursor;
		public Cursor currentCursor = getObserver().getCursor();

		public TMenu plantOptionsMenu;
		public final TSlider mutantOffspringSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider dnaDamageSlider = new TSlider(TSlider.HORIZONTAL);
		public final TButton showLightButton = new TButton("Show light");
		public final TSlider largePlantSizeSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider largePlantSpacingSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider mediumPlantSizeSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider mediumPlantSpacingSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider smallPlantSpacingSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider leafSizeSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider stalkLengthSlider = new TSlider(TSlider.HORIZONTAL);

		public TMenu lightOptionsMenu;
		private final TSlider filterWidthSlider = new TSlider(TSlider.HORIZONTAL);
		private final TSlider filterRedLightSlider = new TSlider(TSlider.HORIZONTAL);
		private final TSlider filterGreenLightSlider = new TSlider(TSlider.HORIZONTAL);
		private final TSlider filterBlueLightSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider redLightSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider greenLightSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider blueLightSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider leafOpacitySlider = new TSlider(TSlider.HORIZONTAL);

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
				simulationScroller.setY(Hub.canvasHeight - 20);
				simulationScroller.setMaxScrollDistance(sim.simWidth);

				if (sim.simWidth > 800)
					add(simulationScroller);
				else
					remove(simulationScroller);

				simOptionsMenu = new TMenu(1000, 0, 200, Hub.canvasHeight, TMenu.VERTICAL);
				plantInteractionsMenu = new TMenu(0, 0, 200, Hub.canvasHeight, TMenu.VERTICAL);
				plantOptionsMenu = new TMenu(0, 0, 200, Hub.canvasHeight, TMenu.VERTICAL);
				lightOptionsMenu = new TMenu(0, 0, 200, Hub.canvasHeight, TMenu.VERTICAL);
				geneOptionsMenu = new TMenu(0, 0, 200, Hub.canvasHeight, TMenu.VERTICAL);
				presetOptionsMenu = new TMenu(0, 0, 200, Hub.canvasHeight, TMenu.VERTICAL);
				topMenu.setBorderSize(5);

				Toolkit k = Toolkit.getDefaultToolkit();
				plantSeedCursor = k.createCustomCursor(Hub.loadImage("seedPointer.png"), new Point(0, 0), "seed");
				getGenesCursor = k.createCustomCursor(Hub.loadImage("dna.png"), new Point(0, 0), "seed");
				killPlantCursor = k.createCustomCursor(Hub.loadImage("skull.png"), new Point(0, 0), "seed");

				// Top menu set-up.
				add(topMenu);
				topMenu.add(plantInteractionsButton);
				topMenu.add(plantOptionsButton);
				topMenu.add(lightOptionsButton);
				topMenu.add(geneOptionsButton);
				topMenu.add(presetOptionsButton);

				// PlantInteractions menu set-up. This menu is located on the
				// left
				plantInteractionButtons.add(selectPlantButton);
				plantInteractionButtons.add(plantSeedButton);
				plantInteractionButtons.add(getGenesButton);
				plantInteractionButtons.add(killPlantsButton);
				plantInteractionsMenu.add(selectPlantButton);
				plantInteractionsMenu.add(plantSeedButton);
				plantInteractionsMenu.add(getGenesButton);
				plantInteractionsMenu.add(killPlantsButton);

				// SimOptions menu set-up. This menu is located on the right
				warpSpeedSlider.setRange(0, 50);
				simOptionsMenu.add(new TLabel("Warp Speed"), false);
				warpSpeedSlider.setValue(10.0);
				simOptionsMenu.add(warpSpeedSlider);
				playbackSpeed.setRange(0, 12);
				simOptionsMenu.add(new TLabel("Playback Speed"), false);
				playbackSpeed.setValue(5.0);
				simOptionsMenu.add(playbackSpeed);
				simOptionsMenu.add(resetSimButton);
				simOptionsMenu.add(mainMenuButton);

				// PlantOptions menu set-up. This menu is located on the left
				TLabel allPlantsLabel = new TLabel("All Plants");
				allPlantsLabel.setFontSize(15);
				allPlantsLabel.setBackgroundColour(new Color(0, 200, 200));
				plantOptionsMenu.add(allPlantsLabel, false);
				plantOptionsMenu.add(new TLabel("Leaf Size"), false);
				leafSizeSlider.setRange(2, 25);
				plantOptionsMenu.add(leafSizeSlider);
				plantOptionsMenu.add(new TLabel("Stalk Length"), false);
				stalkLengthSlider.setRange(15, 100);
				plantOptionsMenu.add(stalkLengthSlider);
				plantOptionsMenu.add(new TLabel("Chance of mutant offspring (%)"), false);
				plantOptionsMenu.add(mutantOffspringSlider);
				plantOptionsMenu.add(new TLabel("Damage to mutant DNA (%)"), false);
				dnaDamageSlider.setRange(0, 15);
				plantOptionsMenu.add(dnaDamageSlider);

				TLabel largePlantsLabel = new TLabel("Large Plants");
				largePlantsLabel.setFontSize(15);
				largePlantsLabel.setBackgroundColour(new Color(0, 200, 200));
				plantOptionsMenu.add(largePlantsLabel, false);
				plantOptionsMenu.add(new TLabel("Plant is large if bigger than:"), false);
				largePlantSizeSlider.setRange(0, Hub.canvasHeight);
				plantOptionsMenu.add(largePlantSizeSlider);
				plantOptionsMenu.add(new TLabel("Large plant spacing"), false);
				largePlantSpacingSlider.setRange(1, 100);
				plantOptionsMenu.add(largePlantSpacingSlider);

				TLabel mediumPlantsLabel = new TLabel("Medium Plants");
				mediumPlantsLabel.setFontSize(15);
				mediumPlantsLabel.setBackgroundColour(new Color(0, 200, 200));
				plantOptionsMenu.add(mediumPlantsLabel, false);
				plantOptionsMenu.add(new TLabel("Plant is medium if bigger than:"), false);
				plantOptionsMenu.add(mediumPlantSizeSlider);
				plantOptionsMenu.add(new TLabel("Medium plant spacing"), false);
				mediumPlantSpacingSlider.setRange(1, 50);
				plantOptionsMenu.add(mediumPlantSpacingSlider);

				TLabel smallPlantsLabel = new TLabel("Small Plants");
				smallPlantsLabel.setFontSize(15);
				smallPlantsLabel.setBackgroundColour(new Color(0, 200, 200));
				plantOptionsMenu.add(smallPlantsLabel, false);
				plantOptionsMenu.add(new TLabel("Small plant spacing"), false);
				smallPlantSpacingSlider.setRange(1, 25);
				plantOptionsMenu.add(smallPlantSpacingSlider);

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
				// TODO just make an addFilterButton and create a generic filter
				// that when clicked on can be edited by a menu on the right.

				// TLabel filterOptionsLabel = new
				// TLabel("Light Filter Options");
				// filterOptionsLabel.setFontSize(15);
				// filterOptionsLabel.setBackgroundColour(new Color(0, 200,
				// 200));
				// lightOptionsMenu.addTComponent(filterOptionsLabel, false);
				// lightOptionsMenu.addTComponent(new TLabel("Filter Width"),
				// false);
				// filterWidthSlider.setRange(0, sim.simWidth);
				// lightOptionsMenu.addTComponent(filterWidthSlider);
				// filterRedLightSlider.setSliderImage(0,
				// Hub.loadImage("redSun.png"));
				// filterGreenLightSlider.setSliderImage(0,
				// Hub.loadImage("greenSun.png"));
				// filterBlueLightSlider.setSliderImage(0,
				// Hub.loadImage("blueSun.png"));
				// lightOptionsMenu.addTComponent(filterRedLightSlider);
				// lightOptionsMenu.addTComponent(filterGreenLightSlider);
				// lightOptionsMenu.addTComponent(filterBlueLightSlider);

				TLabel lightOptionsLabel = new TLabel("Sunlight Options");
				lightOptionsLabel.setFontSize(15);
				lightOptionsLabel.setBackgroundColour(new Color(0, 200, 200));
				lightOptionsMenu.add(lightOptionsLabel, false);
				lightOptionsMenu.add(showLightButton);
				lightOptionsMenu.add(new TLabel("Light Intensity"), false);
				redLightSlider.setRange(0, 255);
				greenLightSlider.setRange(0, 255);
				blueLightSlider.setRange(0, 255);
				redLightSlider.setSliderImage(0, Hub.loadImage("redSun.png"));
				greenLightSlider.setSliderImage(0, Hub.loadImage("greenSun.png"));
				blueLightSlider.setSliderImage(0, Hub.loadImage("blueSun.png"));
				lightOptionsMenu.add(redLightSlider);
				lightOptionsMenu.add(greenLightSlider);
				lightOptionsMenu.add(blueLightSlider);
				lightOptionsMenu.add(new TLabel("Leaf Transparency"), false);
				lightOptionsMenu.add(leafOpacitySlider);

				Hub.geneIO.addGenesToMenu();
				Hub.geneIO.loadGenes("default.txt");

				Hub.presetIO.addPresetsToMenu();

				setLeftMenu(plantInteractionsMenu);
				setRightMenu(simOptionsMenu);

				Hub.presetIO.loadPreset("default.txt");
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
				currentLeftSideMenu.setDimensions(200, Hub.canvasHeight);
				add(currentLeftSideMenu);
			}

		private final void setRightMenu(TMenu newMenu)
			{
				remove(currentRightSideMenu);
				currentRightSideMenu = newMenu;
				currentRightSideMenu.setLocation(1000, 0);
				currentRightSideMenu.setDimensions(200, Hub.canvasHeight / 2);
				add(currentRightSideMenu);
			}

		@Override
		public final void tActionEvent(TActionEvent e)
			{
				Object eventSource = e.getSource();

				// Important buttons
				if (eventSource == mainMenuButton)
					changeRenderableObject(Hub.menu);
				else if (eventSource == resetSimButton)
					sim.reset = true;
				// Change menu's ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				else if (eventSource == plantInteractionsButton)
					setLeftMenu(plantInteractionsMenu);
				else if (eventSource == plantOptionsButton)
					setLeftMenu(plantOptionsMenu);
				else if (eventSource == lightOptionsButton)
					setLeftMenu(lightOptionsMenu);
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
				// Update light
				else if (eventSource == showLightButton)
					{
						if (!sim.showLighting)
							{
								sim.pause();
								warpSpeedSlider.setPercent(0);
								sim.updateLighting();
							}
						else
							sim.unpause();

						sim.showLighting = !sim.showLighting;
						showLightButton.setLabel(sim.showLighting ? "Hide light" : "Show Light", true);
					}
				// Preset options
				else if (eventSource == savePresetButton)
					{
						Hub.presetIO.savePreset(presetSaveNameField.getText());
					}
				else if (eventSource == openPresetsFolderButton)
					Hub.presetIO.openFolder();
				// Gene options
				else if (eventSource == saveGenesButton)
					{
						Hub.geneIO.saveGenes(sim.currentGenes, geneSaveNameField.getText());
						}
				else if (eventSource == openGenesFolderButton)
					Hub.geneIO.openFolder();
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
				// Update plant size category limits
				else if (eventSource == largePlantSizeSlider)
					mediumPlantSizeSlider.setRange(0, (int) largePlantSizeSlider.getValue());
				// update light
				else if (eventSource == redLightSlider && (e.getScrollType() == TScrollEvent.FINAL_VALUE || e.getScrollType() == TScrollEvent.VALUE_SET_INTERNALLY))
					{
						double simSpeed = playbackSpeed.getValue();
						playbackSpeed.setValue(0);
						sim.lightMap.setRedLight(redLightSlider.getValue());
						playbackSpeed.setValue(simSpeed);
					}
				else if (eventSource == greenLightSlider && (e.getScrollType() == TScrollEvent.FINAL_VALUE || e.getScrollType() == TScrollEvent.VALUE_SET_INTERNALLY))
					{
						double simSpeed = playbackSpeed.getValue();
						playbackSpeed.setValue(0);
						sim.lightMap.setGreenLight(greenLightSlider.getValue());
						playbackSpeed.setValue(simSpeed);
					}
				else if (eventSource == blueLightSlider && (e.getScrollType() == TScrollEvent.FINAL_VALUE || e.getScrollType() == TScrollEvent.VALUE_SET_INTERNALLY))
					{
						double simSpeed = playbackSpeed.getValue();
						playbackSpeed.setValue(0);
						sim.lightMap.setBlueLight(blueLightSlider.getValue());
						playbackSpeed.setValue(simSpeed);
					}
			}

		@Override
		public void mousePressed(MouseEvent e)
			{
				sim.mousePressed(e);
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