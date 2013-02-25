package evolvingPlants;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import tCode.RenderableObject;
import tComponents.components.TButton;
import tComponents.components.TLabel;
import tComponents.components.TMenu;
import tComponents.components.TScrollBar;
import tComponents.components.TSlider;
import tComponents.utils.events.TActionEvent;
import tComponents.utils.events.TScrollEvent;
import evolvingPlants.simulation.Simulation;

public class SimulationWindow extends RenderableObject
	{
		public Simulation sim;
		private final TScrollBar simulationScroller = new TScrollBar(200, 0, 800, 800, TScrollBar.HORIZONTAL, new Rectangle(200, 0, 800, 550));

		private final TMenu topMenu = new TMenu(200, 0, 800, 70, TMenu.HORIZONTAL);
		private final TButton plantInteractionsButton = new TButton("Plant Interactions");
		private final TButton plantOptionsButton = new TButton("Plant Options");
		private final TButton lightOptionsButton = new TButton("Light Options");

		private TMenu currentLeftSideMenu;
		private TMenu currentRightSideMenu;

		private TMenu simOptionsMenu;
		public final TSlider playbackSpeed = new TSlider(TSlider.HORIZONTAL);
		private final TButton resetSimButton = new TButton("Reset Simulation");
		public final TButton mainMenuButton = new TButton("Main Menu");

		private TMenu plantInteractionsMenu;
		// private TRadioButtons plantInteractionButtons = new
		// TRadioButtons(TRadioButtons.VERTICAL);
		Cursor plantSeedCursor;
		Cursor getGenesCursor;
		Cursor killPlantCursor;

		private TMenu plantOptionsMenu;
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

				// SimOptions menu set-up.
				playbackSpeed.setRange(0, 10);
				simOptionsMenu.addTComponent(new TLabel("Playback Speed"), false);
				simOptionsMenu.addTComponent(playbackSpeed);
				simOptionsMenu.addTComponent(resetSimButton);
				simOptionsMenu.addTComponent(mainMenuButton);

				// PlantOptions menu set-up
				TLabel allPlantsLabel = new TLabel("All Plants");
				allPlantsLabel.setFontSize(14);
				allPlantsLabel.setBackgroundColour(new Color(0, 200, 200));
				plantOptionsMenu.addTComponent(allPlantsLabel, false);
				plantOptionsMenu.addTComponent(new TLabel("Leaf Size"), false);
				leafSizeSlider.setRange(2, 25);
				plantOptionsMenu.addTComponent(leafSizeSlider);
				plantOptionsMenu.addTComponent(new TLabel("Stalk Length"), false);
				stalkLengthSlider.setRange(5, 100);
				plantOptionsMenu.addTComponent(stalkLengthSlider);

				TLabel largePlantsLabel = new TLabel("Large Plants");
				largePlantsLabel.setFontSize(14);
				largePlantsLabel.setBackgroundColour(new Color(0, 200, 200));
				plantOptionsMenu.addTComponent(largePlantsLabel, false);
				plantOptionsMenu.addTComponent(new TLabel("Plant is large if bigger than:"), false);
				plantOptionsMenu.addTComponent(largePlantSizeSlider);
				plantOptionsMenu.addTComponent(new TLabel("Large plant spacing"), false);
				largePlantSpacingSlider.setRange(1, 25);
				plantOptionsMenu.addTComponent(largePlantSpacingSlider);

				TLabel mediumPlantsLabel = new TLabel("Medium Plants");
				mediumPlantsLabel.setFontSize(14);
				mediumPlantsLabel.setBackgroundColour(new Color(0, 200, 200));
				plantOptionsMenu.addTComponent(mediumPlantsLabel, false);
				plantOptionsMenu.addTComponent(new TLabel("Plant is medium if bigger than:"), false);
				plantOptionsMenu.addTComponent(mediumPlantSizeSlider);
				plantOptionsMenu.addTComponent(new TLabel("Medium plant spacing"), false);
				mediumPlantSpacingSlider.setRange(1, 25);
				plantOptionsMenu.addTComponent(mediumPlantSpacingSlider);

				TLabel smallPlantsLabel = new TLabel("Small Plants");
				smallPlantsLabel.setFontSize(14);
				smallPlantsLabel.setBackgroundColour(new Color(0, 200, 200));
				plantOptionsMenu.addTComponent(smallPlantsLabel, false);
				plantOptionsMenu.addTComponent(new TLabel("Small plant spacing"), false);
				smallPlantSpacingSlider.setRange(1, 25);
				plantOptionsMenu.addTComponent(smallPlantSpacingSlider);

				// LightOptions menu set-up
				leafOpacitySlider.setRange(0, 1);
				lightOptionsMenu.addTComponent(new TLabel("Light Intensity"), false);
				redLightSlider.setSliderImage(0, Hub.loadImage("redSun.png"));
				lightOptionsMenu.addTComponent(redLightSlider);
				greenLightSlider.setSliderImage(0, Hub.loadImage("greenSun.png"));
				lightOptionsMenu.addTComponent(greenLightSlider);
				blueLightSlider.setSliderImage(0, Hub.loadImage("blueSun.png"));
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
				// Change menu's
				if (e.getSource() == plantInteractionsButton)
					setLeftMenu(plantInteractionsMenu);
				else if (e.getSource() == plantOptionsButton)
					setLeftMenu(plantOptionsMenu);
				else if (e.getSource() == lightOptionsButton)
					setLeftMenu(lightOptionsMenu);
				// Change Cursor
				// else if (e.getSource())

			}

		@Override
		public final void tScrollEvent(TScrollEvent e)
			{
				if (e.getSource() == simulationScroller)
					{
						sim.simX = -e.getScrollValue();
					}
			}

		@Override
		public void mousePressed(MouseEvent e)
			{
				sim.mousePressed(e);
			}

		@Override
		public void keyPressed(KeyEvent e)
			{
				sim.keyPressed(e);
			}
	}