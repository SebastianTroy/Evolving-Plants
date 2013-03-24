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
import tComponents.components.TRadioButton;
import tComponents.components.TScrollBar;
import tComponents.components.TSlider;
import tComponents.utils.RadioButtonsCollection;
import tComponents.utils.events.TActionEvent;
import tComponents.utils.events.TScrollEvent;
import evolvingPlants.simulation.LightMap;
import evolvingPlants.simulation.Simulation;

public class SimulationWindow extends RenderableObject
	{
		public Simulation sim;
		Rectangle simBounds = new Rectangle(200, 70, 800, 480);
		private final TScrollBar simulationScroller = new TScrollBar(200, 0, 800, 800, TScrollBar.HORIZONTAL, new Rectangle(200, 0, 800, 550));

		private final TMenu topMenu = new TMenu(200, 0, 800, 70, TMenu.HORIZONTAL);
		private final TButton plantInteractionsButton = new TButton("Plant Interactions");
		private final TButton plantOptionsButton = new TButton("Plant Options");
		private final TButton lightOptionsButton = new TButton("Light Options");

		private TMenu currentLeftSideMenu;
		private TMenu currentRightSideMenu;

		private TMenu simOptionsMenu;
		public final TSlider playbackSpeed = new TSlider(TSlider.HORIZONTAL);
		private final TButton warpSpeedButton = new TButton("Warp speed [off]");
		private final TButton resetSimButton = new TButton("Reset Simulation");
		private final TButton mainMenuButton = new TButton("Main Menu");

		private TMenu plantInteractionsMenu;
		private RadioButtonsCollection plantInteractionButtons = new RadioButtonsCollection();
		private TRadioButton selectPlantButton = new TRadioButton("Select Plants");
		private TRadioButton plantSeedButton = new TRadioButton("Plant Seeds");
		private TRadioButton getGenesButton = new TRadioButton("Extract Genes");
		private TRadioButton killPlantsButton = new TRadioButton("Kill Plants");
		public Cursor plantSeedCursor;
		public Cursor getGenesCursor;
		public Cursor killPlantCursor;
		public Cursor currentCursor = getObserver().getCursor();

		private TMenu plantOptionsMenu;
		public final TButton showLightButton = new TButton("Show light");
		public final TSlider largePlantSizeSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider largePlantSpacingSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider mediumPlantSizeSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider mediumPlantSpacingSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider smallPlantSpacingSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider leafSizeSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider stalkLengthSlider = new TSlider(TSlider.HORIZONTAL);

		private TMenu lightOptionsMenu;
		public final TSlider redLightSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider greenLightSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider blueLightSlider = new TSlider(TSlider.HORIZONTAL);
		public final TSlider leafOpacitySlider = new TSlider(TSlider.HORIZONTAL);

		@Override
		protected void initiate()
			{
				simulationScroller.setY(Hub.canvasHeight - 20);
				simulationScroller.setMaxScrollDistance(sim.simWidth);

				if (sim.simWidth > 800)
					addTComponent(simulationScroller);

				topMenu.setBorderSize(5);

				Toolkit k = Toolkit.getDefaultToolkit();
				plantSeedCursor = k.createCustomCursor(Hub.loadImage("seed.png"), new Point(0, 0), "seed");
				getGenesCursor = k.createCustomCursor(Hub.loadImage("dna.png"), new Point(0, 0), "seed");
				killPlantCursor = k.createCustomCursor(Hub.loadImage("skull.png"), new Point(0, 0), "seed");

				simOptionsMenu = new TMenu(1000, 0, 200, Hub.canvasHeight, TMenu.VERTICAL);
				plantInteractionsMenu = new TMenu(0, 0, 200, Hub.canvasHeight, TMenu.VERTICAL);
				plantOptionsMenu = new TMenu(0, 0, 200, Hub.canvasHeight, TMenu.VERTICAL);
				lightOptionsMenu = new TMenu(0, 0, 200, Hub.canvasHeight, TMenu.VERTICAL);
				setRightMenu(simOptionsMenu);

				// Top menu set-up.
				addTComponent(topMenu);
				topMenu.addTComponent(plantOptionsButton);
				topMenu.addTComponent(lightOptionsButton);
				topMenu.addTComponent(plantInteractionsButton);

				// SimOptions menu set-up. This menu is located on the right
				simOptionsMenu.addTComponent(warpSpeedButton);
				playbackSpeed.setRange(0, 25);
				simOptionsMenu.addTComponent(new TLabel("Playback Speed"), false);
				simOptionsMenu.addTComponent(playbackSpeed);
				simOptionsMenu.addTComponent(resetSimButton);
				simOptionsMenu.addTComponent(mainMenuButton);

				// PlantOptions menu set-up. This menu is located on the left
				TLabel allPlantsLabel = new TLabel("All Plants");
				allPlantsLabel.setFontSize(14);
				allPlantsLabel.setBackgroundColour(new Color(0, 200, 200));
				plantOptionsMenu.addTComponent(allPlantsLabel, false);
				plantOptionsMenu.addTComponent(new TLabel("Leaf Size"), false);
				leafSizeSlider.setRange(2, 25);
				plantOptionsMenu.addTComponent(leafSizeSlider);
				plantOptionsMenu.addTComponent(new TLabel("Stalk Length"), false);
				stalkLengthSlider.setRange(15, 100);
				plantOptionsMenu.addTComponent(stalkLengthSlider);

				TLabel largePlantsLabel = new TLabel("Large Plants");
				largePlantsLabel.setFontSize(14);
				largePlantsLabel.setBackgroundColour(new Color(0, 200, 200));
				plantOptionsMenu.addTComponent(largePlantsLabel, false);
				plantOptionsMenu.addTComponent(new TLabel("Plant is large if bigger than:"), false);
				largePlantSizeSlider.setRange(0, Hub.canvasHeight);
				plantOptionsMenu.addTComponent(largePlantSizeSlider);
				plantOptionsMenu.addTComponent(new TLabel("Large plant spacing"), false);
				largePlantSpacingSlider.setRange(1, 100);
				plantOptionsMenu.addTComponent(largePlantSpacingSlider);

				TLabel mediumPlantsLabel = new TLabel("Medium Plants");
				mediumPlantsLabel.setFontSize(14);
				mediumPlantsLabel.setBackgroundColour(new Color(0, 200, 200));
				plantOptionsMenu.addTComponent(mediumPlantsLabel, false);
				plantOptionsMenu.addTComponent(new TLabel("Plant is medium if bigger than:"), false);
				plantOptionsMenu.addTComponent(mediumPlantSizeSlider);
				plantOptionsMenu.addTComponent(new TLabel("Medium plant spacing"), false);
				mediumPlantSpacingSlider.setRange(1, 50);
				plantOptionsMenu.addTComponent(mediumPlantSpacingSlider);

				TLabel smallPlantsLabel = new TLabel("Small Plants");
				smallPlantsLabel.setFontSize(14);
				smallPlantsLabel.setBackgroundColour(new Color(0, 200, 200));
				plantOptionsMenu.addTComponent(smallPlantsLabel, false);
				plantOptionsMenu.addTComponent(new TLabel("Small plant spacing"), false);
				smallPlantSpacingSlider.setRange(1, 25);
				plantOptionsMenu.addTComponent(smallPlantSpacingSlider);

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

				// LightOptions menu set-up. This menu is located on the left
				lightOptionsMenu.addTComponent(showLightButton);
				leafOpacitySlider.setRange(0, 1);
				lightOptionsMenu.addTComponent(new TLabel("Light Intensity"), false);
				redLightSlider.setSliderImage(0, Hub.loadImage("redSun.png"));
				redLightSlider.setRange(0, 255);
				redLightSlider.setSliderValue(255);
				lightOptionsMenu.addTComponent(redLightSlider);
				greenLightSlider.setSliderImage(0, Hub.loadImage("greenSun.png"));
				greenLightSlider.setRange(0, 255);
				greenLightSlider.setSliderValue(255);
				lightOptionsMenu.addTComponent(greenLightSlider);
				blueLightSlider.setSliderImage(0, Hub.loadImage("blueSun.png"));
				blueLightSlider.setRange(0, 255);
				blueLightSlider.setSliderValue(255);
				lightOptionsMenu.addTComponent(blueLightSlider);
				lightOptionsMenu.addTComponent(new TLabel("Leaf Transparency"), false);
				lightOptionsMenu.addTComponent(leafOpacitySlider);

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

		private final void setLeftMenu(TMenu newMenu)
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
				// Modify Sim variables
				else if (eventSource == warpSpeedButton)
					{
						sim.warpSpeed = !sim.warpSpeed;
						warpSpeedButton.setLabel("Warp speed " + (sim.warpSpeed ? "[on]" : "[off]"), true);
					}
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

						sim.showLighting = !sim.showLighting;
						showLightButton.setLabel(sim.showLighting ? "Hide light" : "Show Light", true);
					}
			}

		@Override
		public final void tScrollEvent(TScrollEvent e)
			{
				Object eventSource = e.getSource();

				// Update simulation position
				if (eventSource == simulationScroller)
					sim.simX = -e.getScrollValue();
				// Update plant size category limits
				else if (eventSource == largePlantSizeSlider)
					mediumPlantSizeSlider.setRange(0, largePlantSizeSlider.getSliderValue());
				// update light
				else if (eventSource == redLightSlider && e.getScrollType() == TScrollEvent.FINAL_VALUE)
					{
						double simSpeed = playbackSpeed.getSliderValue();
						playbackSpeed.setSliderValue(0);
						sim.lightMap.setRedLight(redLightSlider.getSliderValue());
						playbackSpeed.setSliderValue(simSpeed);
					}
				else if (eventSource == greenLightSlider && e.getScrollType() == TScrollEvent.FINAL_VALUE)
					{
						double simSpeed = playbackSpeed.getSliderValue();
						playbackSpeed.setSliderValue(0);
						sim.lightMap.setGreenLight(greenLightSlider.getSliderValue());
						playbackSpeed.setSliderValue(simSpeed);
					}
				else if (eventSource == blueLightSlider && e.getScrollType() == TScrollEvent.FINAL_VALUE)
					{
						double simSpeed = playbackSpeed.getSliderValue();
						playbackSpeed.setSliderValue(0);
						sim.lightMap.setBlueLight(blueLightSlider.getSliderValue());
						playbackSpeed.setSliderValue(simSpeed);
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
				else
					getObserver().setCursor(Cursor.getDefaultCursor());
			}

		@Override
		public void mouseDragged(MouseEvent e)
			{
				if (simBounds.contains(e.getPoint()))
					getObserver().setCursor(currentCursor);
				else
					getObserver().setCursor(Cursor.getDefaultCursor());
			}

		@Override
		public void keyPressed(KeyEvent e)
			{
				sim.keyPressed(e);
			}
	}