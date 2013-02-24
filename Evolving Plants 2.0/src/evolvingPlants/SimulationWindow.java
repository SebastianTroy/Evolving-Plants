package evolvingPlants;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import tCode.RenderableObject;
import tComponents.components.TScrollBar;
import tComponents.utils.events.TScrollEvent;
import evolvingPlants.simulation.Simulation;

public class SimulationWindow extends RenderableObject
	{
		public Simulation sim;
		private final TScrollBar simulationScroller = new TScrollBar(200, 0, 800, 800, TScrollBar.HORIZONTAL, new Rectangle(200, 0, 800, 550));

		public SimulationWindow()
			{}

		@Override
		protected void initiate()
			{
				simulationScroller.setY(Hub.canvasHeight - 20);
				simulationScroller.setMaxScrollDistance(sim.simWidth);

				if (sim.simWidth > 800)
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