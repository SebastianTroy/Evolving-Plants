package PlantSim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import TroysCode.RenderableObject;
import TroysCode.Tools;
import TroysCode.hub;
import TroysCode.T.TButton;
import TroysCode.T.TScrollEvent;
import TroysCode.T.TSlider;

public class World extends RenderableObject
	{
		private static final long serialVersionUID = 1L;

		private ArrayList<Plant> plantsToAdd = new ArrayList<Plant>(20);
		private ArrayList<LightBeam> lightBeams = new ArrayList<LightBeam>(100);
		private ArrayList<Plant> plants = new ArrayList<Plant>(25);

		private boolean viewLight = true;

		private int lightIntensity = 8;
		private int lightEnergy = 15;
		private int plantSpacing = 12;

		private final TButton viewLightButton = new TButton(850, 0, "View Light? on/off");

		private final TSlider lightIntensitySlider = new TSlider(810, 40, TSlider.HORIZONTAL, 180, 1);
		private final TSlider lightEnergySlider = new TSlider(810, 80, TSlider.HORIZONTAL, 180, 1);
		private final TSlider plantSpacingSlider = new TSlider(810, 120, TSlider.HORIZONTAL, 180, 1);

		private Plant selectedPlant = new Plant(null, 0, 0, 0);

		public World()
			{
			}

		@Override
		protected void initiate()
			{
				addTComponent(viewLightButton);

				addTComponent(lightIntensitySlider);
				addTComponent(lightEnergySlider);
				addTComponent(plantSpacingSlider);
			}

		@Override
		protected void refresh()
			{
				// add 10 equally spaced new plants
				for (int i = 0; i < 10; i++)
					plants.add(new Plant(null, (i * 80) + 40, 500, 250));
			}

		@Override
		protected void tick()
			{
				for (int i = 0; i < lightIntensity; i++)
					{
						lightBeams.add(new LightBeam(Tools.randInt(0, 800), lightEnergy));
					}

				// Remove dead lightBeams
				LightBeam[] lightBeamsCopy = new LightBeam[lightBeams.size()];
				lightBeams.toArray(lightBeamsCopy);
				for (LightBeam e : lightBeamsCopy)
					if (e.alive == false)
						lightBeams.remove(e);

				// Remove dead plants
				Plant[] plantsCopy = new Plant[plants.size()];
				plants.toArray(plantsCopy);
				for (Plant e : plantsCopy)
					if (e.alive == false)
						plants.remove(e);

				// Add new seedlings to Array
				for (Plant p : plantsToAdd)
					plants.add(p);
				plantsToAdd.clear();

				// Process entities
				for (Entity e : lightBeams)
					e.tick();
				for (Entity e : plants)
					e.tick();

				// Check if leaves contain light beams
				for (Plant plant : plants)
					for (LightBeam light : lightBeams)
						for (Leaf leaf : plant.leaves)
							if (light.alive && leaf.containsPoint(light.x, light.y))
								{
									leaf.containsLight(light);
									light.alive = false;
								}
			}

		@Override
		protected void renderObject(Graphics g)
			{
				g.setColor(new Color(100, 100, 255));
				g.fillRect(0, 0, 800, 600);

				if (viewLight)
					for (Entity e : lightBeams)
						e.render(g);

				for (Entity p : plants)
					p.render(g);

				g.setColor(Color.GRAY);
				g.fillRect(800, 0, 200, 400);

				g.setColor(Color.BLACK);
				g.drawString("Light Intensity: " + lightIntensity, 820, 40);
				g.drawString("Light Energy: " + lightEnergy, 820, 80);
				g.drawString("Plant Spacing: " + plantSpacing, 820, 120);
				
				g.setColor(Color.WHITE);
				g.fillRect(800, 400, 200, 200);
				
				g.setColor(Color.BLACK);
				g.drawString("Selected Plant:", 810, 417);
				g.drawString("Ticks left: " +  (selectedPlant.genes.maxAge - selectedPlant.age), 810, 440);
				g.drawString("Stem height: " + selectedPlant.genes.maxStemLength, 810, 460);
				g.drawString("Max stem Number: " + selectedPlant.genes.maxStems, 810, 480);
				g.drawString("% chance of mutation: " + selectedPlant.genes.geneStability, 810, 500);
				g.drawString("Variability: " + selectedPlant.genes.var, 810, 520);
				g.drawString("Energy for seeds: " + selectedPlant.energy, 810, 540);
				g.drawString("Energy per seed: " + selectedPlant.genes.seedEnergy, 810, 560);
			}

		public final void addPlant(Plant seedling)
			{
				for (Plant plant : plants)
					if (seedling.x < plant.x + plantSpacing && seedling.x > plant.x - plantSpacing)
						return;
					else if (seedling.x < 0 || seedling.x > 800)
						return;

				plantsToAdd.add(seedling);
			}

		@Override
		protected void mousePressed(MouseEvent event)
			{
				if (event.getButton() == MouseEvent.BUTTON1)
					{
						for (Plant p : plants)
							for (Leaf l : p.leaves)
								if (l.containsPoint(event.getX(), event.getY()))
									selectedPlant = p;
					}
				else
					addPlant(new Plant(null, event.getX(), event.getY(), 250));
			}

		@Override
		protected void mouseReleased(MouseEvent event)
			{
			}

		@Override
		protected void mouseDragged(MouseEvent event)
			{
			}

		@Override
		protected void mouseMoved(MouseEvent event)
			{
			}

		@Override
		protected void mouseWheelMoved(MouseWheelEvent event)
			{
			}

		@Override
		protected void actionPerformed(ActionEvent event)
			{
				if (event.getSource() == viewLightButton)
					viewLight = !viewLight;
			}

		@Override
		protected void keyPressed(KeyEvent event)
			{
			}

		@Override
		protected void keyReleased(KeyEvent event)
			{
			}

		@Override
		protected void keyTyped(KeyEvent event)
			{
			}

		@Override
		protected void mouseClicked(MouseEvent event)
			{
			}

		@Override
		protected void mouseEntered(MouseEvent event)
			{
			}

		@Override
		protected void mouseExited(MouseEvent event)
			{
			}

		@Override
		protected void programGainedFocus(WindowEvent event)
			{
			}

		@Override
		protected void programLostFocus(WindowEvent event)
			{
			}

		@Override
		protected void frameResized(ComponentEvent event)
			{
			}

		@Override
		public void tScrollBarScrolled(TScrollEvent event)
			{
				if (event.getSource() == lightIntensitySlider)
					lightIntensity = (int) ((lightIntensitySlider.getSliderPercent(0) * 0.14) + 1);
				else if (event.getSource() == lightEnergySlider)
					lightEnergy = (int) ((lightEnergySlider.getSliderPercent(0) * 0.28) + 2);
				else if (event.getSource() == plantSpacingSlider)
					plantSpacing = (int) ((plantSpacingSlider.getSliderPercent(0) * 0.15) + 5);
			}

	}
