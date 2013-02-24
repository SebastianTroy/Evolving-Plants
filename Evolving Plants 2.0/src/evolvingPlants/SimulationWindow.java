package evolvingPlants;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import evolvingPlants.simulation.Simulation;

import tCode.RenderableObject;
import tComponents.components.TScrollBar;

public class SimulationWindow extends RenderableObject
	{
		public Simulation sim;
		private final TScrollBar simulationScroller = new TScrollBar(200, Hub.canvasHeight - 20, 800, 800, TScrollBar.HORIZONTAL, new Rectangle(200, 0, 800, 550));


		public SimulationWindow(int width)
			{
				sim = new Simulation(width);
			}

		@Override
		protected void initiate()
			{
				addTComponent(simulationScroller);
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
		
		@Override
		public void mousePressed(MouseEvent e)
			{
				sim.mousePressed(e);
			}
	}