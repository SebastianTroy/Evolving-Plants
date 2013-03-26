package evolvingPlants;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import tCode.RenderableObject;
import tComponents.components.TButton;
import tComponents.components.TLabel;
import tComponents.components.TMenu;
import tComponents.components.TNumberField;
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

		TMenu plantOptionsMenu;
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

		TMenu lightOptionsMenu;
		private final TSlider filterWidthSlider = new TSlider(TSlider.HORIZONTAL);
		private final TSlider filterRedLightSlider = new TSlider(TSlider.HORIZONTAL);
		private final TSlider filterGreenLightSlider = new TSlider(TSlider.HORIZONTAL);
		private final TSlider filterBlueLightSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider redLightSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider greenLightSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider blueLightSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider leafOpacitySlider = new TSlider(TSlider.HORIZONTAL);

		TMenu geneOptionsMenu;
		// TODO add gene options

		TMenu presetOptionsMenu;
		private final TTextField saveNameField = new TTextField(0, 0, 160, 25, "My Settings");
		private final TButton savePresetButton = new TButton("Save Current Settings");
		public final TMenu savedPresetsMenu = new TMenu(0, 0, 200, 350, TMenu.VERTICAL);
		private RadioButtonsCollection loadDeletePresetButtons = new RadioButtonsCollection();
		public final TRadioButton loadPresetButton = new TRadioButton("Load Presets");
		public final TRadioButton deletePresetButton = new TRadioButton("Delete Presets");

		@Override
		protected void initiate()
			{
				simulationScroller.setY(Hub.canvasHeight - 20);
				simulationScroller.setMaxScrollDistance(sim.simWidth);

				if (sim.simWidth > 800)
					addTComponent(simulationScroller);

				simOptionsMenu = new TMenu(1000, 0, 200, Hub.canvasHeight, TMenu.VERTICAL);
				plantInteractionsMenu = new TMenu(0, 0, 200, Hub.canvasHeight, TMenu.VERTICAL);
				plantOptionsMenu = new TMenu(0, 0, 200, Hub.canvasHeight, TMenu.VERTICAL);
				lightOptionsMenu = new TMenu(0, 0, 200, Hub.canvasHeight, TMenu.VERTICAL);
				geneOptionsMenu = new TMenu(0, 0, 200, Hub.canvasHeight, TMenu.VERTICAL);
				presetOptionsMenu = new TMenu(0, 0, 200, Hub.canvasHeight, TMenu.VERTICAL);

				topMenu.setBorderSize(5);

				Toolkit k = Toolkit.getDefaultToolkit();
				plantSeedCursor = k.createCustomCursor(Hub.loadImage("seed.png"), new Point(0, 0), "seed");
				getGenesCursor = k.createCustomCursor(Hub.loadImage("dna.png"), new Point(0, 0), "seed");
				killPlantCursor = k.createCustomCursor(Hub.loadImage("skull.png"), new Point(0, 0), "seed");

				// Top menu set-up.
				addTComponent(topMenu);
				topMenu.addTComponent(plantInteractionsButton);
				topMenu.addTComponent(plantOptionsButton);
				topMenu.addTComponent(lightOptionsButton);
				topMenu.addTComponent(geneOptionsButton);
				topMenu.addTComponent(presetOptionsButton);

				// PlantInteractions menu set-up. This menu is located on the
				// left
				plantInteractionButtons.addRadioButton(selectPlantButton);
				plantInteractionButtons.addRadioButton(plantSeedButton);
				plantInteractionButtons.addRadioButton(getGenesButton);
				plantInteractionButtons.addRadioButton(killPlantsButton);
				plantInteractionsMenu.addTComponent(selectPlantButton);
				plantInteractionsMenu.addTComponent(plantSeedButton);
				plantInteractionsMenu.addTComponent(getGenesButton);
				plantInteractionsMenu.addTComponent(killPlantsButton);

				// SimOptions menu set-up. This menu is located on the right
				warpSpeedSlider.setRange(0, 50);
				simOptionsMenu.addTComponent(new TLabel("Warp Speed"), false);
				simOptionsMenu.addTComponent(warpSpeedSlider);
				playbackSpeed.setRange(0, 12);
				simOptionsMenu.addTComponent(new TLabel("Playback Speed"), false);
				simOptionsMenu.addTComponent(playbackSpeed);
				simOptionsMenu.addTComponent(resetSimButton);
				simOptionsMenu.addTComponent(mainMenuButton);

				// PlantOptions menu set-up. This menu is located on the left
				TLabel allPlantsLabel = new TLabel("All Plants");
				allPlantsLabel.setFontSize(15);
				allPlantsLabel.setBackgroundColour(new Color(0, 200, 200));
				plantOptionsMenu.addTComponent(allPlantsLabel, false);
				plantOptionsMenu.addTComponent(new TLabel("Leaf Size"), false);
				leafSizeSlider.setRange(2, 25);
				plantOptionsMenu.addTComponent(leafSizeSlider);
				plantOptionsMenu.addTComponent(new TLabel("Stalk Length"), false);
				stalkLengthSlider.setRange(15, 100);
				plantOptionsMenu.addTComponent(stalkLengthSlider);
				plantOptionsMenu.addTComponent(new TLabel("Chance of mutant offspring (%)"), false);
				plantOptionsMenu.addTComponent(mutantOffspringSlider);
				plantOptionsMenu.addTComponent(new TLabel("Damage to mutant DNA (%)"), false);
				dnaDamageSlider.setRange(0, 15);
				plantOptionsMenu.addTComponent(dnaDamageSlider);

				TLabel largePlantsLabel = new TLabel("Large Plants");
				largePlantsLabel.setFontSize(15);
				largePlantsLabel.setBackgroundColour(new Color(0, 200, 200));
				plantOptionsMenu.addTComponent(largePlantsLabel, false);
				plantOptionsMenu.addTComponent(new TLabel("Plant is large if bigger than:"), false);
				largePlantSizeSlider.setRange(0, Hub.canvasHeight);
				plantOptionsMenu.addTComponent(largePlantSizeSlider);
				plantOptionsMenu.addTComponent(new TLabel("Large plant spacing"), false);
				largePlantSpacingSlider.setRange(1, 100);
				plantOptionsMenu.addTComponent(largePlantSpacingSlider);

				TLabel mediumPlantsLabel = new TLabel("Medium Plants");
				mediumPlantsLabel.setFontSize(15);
				mediumPlantsLabel.setBackgroundColour(new Color(0, 200, 200));
				plantOptionsMenu.addTComponent(mediumPlantsLabel, false);
				plantOptionsMenu.addTComponent(new TLabel("Plant is medium if bigger than:"), false);
				plantOptionsMenu.addTComponent(mediumPlantSizeSlider);
				plantOptionsMenu.addTComponent(new TLabel("Medium plant spacing"), false);
				mediumPlantSpacingSlider.setRange(1, 50);
				plantOptionsMenu.addTComponent(mediumPlantSpacingSlider);

				TLabel smallPlantsLabel = new TLabel("Small Plants");
				smallPlantsLabel.setFontSize(15);
				smallPlantsLabel.setBackgroundColour(new Color(0, 200, 200));
				plantOptionsMenu.addTComponent(smallPlantsLabel, false);
				plantOptionsMenu.addTComponent(new TLabel("Small plant spacing"), false);
				smallPlantSpacingSlider.setRange(1, 25);
				plantOptionsMenu.addTComponent(smallPlantSpacingSlider);

				// GeneOptionsMenu set-up. This menu is located on the left
				// TODO place gene interaction components here

				// PresetOptionsMenu set-up. This menu is located on the left
				presetOptionsMenu.addTComponent(saveNameField, false);
				presetOptionsMenu.addTComponent(savePresetButton);
				presetOptionsMenu.addTComponent(savedPresetsMenu, false);
				Hub.presetIO.addPresetsToMenu();
				presetOptionsMenu.addTComponent(loadPresetButton);
				presetOptionsMenu.addTComponent(deletePresetButton);
				loadDeletePresetButtons.addRadioButton(loadPresetButton);
				loadDeletePresetButtons.addRadioButton(deletePresetButton);

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
				lightOptionsMenu.addTComponent(lightOptionsLabel, false);
				lightOptionsMenu.addTComponent(showLightButton);
				leafOpacitySlider.setRange(0, 1);
				lightOptionsMenu.addTComponent(new TLabel("Light Intensity"), false);
				redLightSlider.setRange(0, 255);
				greenLightSlider.setRange(0, 255);
				blueLightSlider.setRange(0, 255);
				redLightSlider.setSliderImage(0, Hub.loadImage("redSun.png"));
				greenLightSlider.setSliderImage(0, Hub.loadImage("greenSun.png"));
				blueLightSlider.setSliderImage(0, Hub.loadImage("blueSun.png"));
				lightOptionsMenu.addTComponent(redLightSlider);
				lightOptionsMenu.addTComponent(greenLightSlider);
				lightOptionsMenu.addTComponent(blueLightSlider);
				lightOptionsMenu.addTComponent(new TLabel("Leaf Transparency"), false);
				lightOptionsMenu.addTComponent(leafOpacitySlider);

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
		protected void render(Graphics g)
			{
				sim.render(g);
			}

		final void setLeftMenu(TMenu newMenu)
			{
				removeTComponent(currentLeftSideMenu);
				currentLeftSideMenu = newMenu;
				currentLeftSideMenu.setLocation(0, 0);
				currentLeftSideMenu.setDimensions(200, Hub.canvasHeight);
				addTComponent(currentLeftSideMenu);
			}

		private final void setRightMenu(TMenu newMenu)
			{
				removeTComponent(currentRightSideMenu);
				currentRightSideMenu = newMenu;
				currentRightSideMenu.setLocation(1000, 0);
				currentRightSideMenu.setDimensions(200, Hub.canvasHeight / 2);
				addTComponent(currentRightSideMenu);
			}

		@Override
		public final void tActionEvent(TActionEvent e)
			{
				Object eventSource = e.getSource();

				// Important buttons
				if (eventSource == mainMenuButton)
					changeRenderableObject(Hub.menu);
				else if (eventSource == resetSimButton)
					sim = new Simulation((int) sim.simWidth);
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
								sim.oldWarpSpeed = warpSpeedSlider.getValue();
								warpSpeedSlider.setPercent(0);
								sim.updateLighting();
							}
						else
							warpSpeedSlider.setValue(sim.oldWarpSpeed);

						sim.showLighting = !sim.showLighting;
						showLightButton.setLabel(sim.showLighting ? "Hide light" : "Show Light", true);
					}
				// Preset options
				else if (eventSource == savePresetButton)
					{
						Hub.presetIO.createPreset(saveNameField.getText());
						Hub.presetIO.addPresetsToMenu();
					}
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

		@Override
		public void keyPressed(KeyEvent e)
			{
				sim.keyPressed(e);
			}
	}