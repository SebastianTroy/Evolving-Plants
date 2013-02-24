package evolvingPlants;

import java.awt.Color;
import java.awt.Graphics;

import tCode.RenderableObject;
import tComponents.components.TButton;
import tComponents.components.TMenu;
import tComponents.components.TSlider;
import tComponents.utils.events.TActionEvent;
import tComponents.utils.events.TScrollEvent;
import evolvingPlants.simulation.Simulation;

public class MainMenu extends RenderableObject
	{
		private TMenu mainMenu;
		private TButton newSimButton = new TButton(0, 0, 0, 0, "New Simulation");
		private TButton resumeSimButton = new TButton(0, 0, 0, 0, "Resume Simulation");
		private TButton editorButton = new TButton(0, 0, 0, 0, "DNA Editor");

		private TMenu newSimMenu;
		private TSlider simWidthSlider = new TSlider(1200, 150, 500, TSlider.HORIZONTAL);
		private TButton startSimButton = new TButton(1450, 400, 300, 75, "Start Simulation");

		// New Simulation Variables
		private int simWidth = 800;

		@Override
		protected void initiate()
			{
				mainMenu = new TMenu(200, 0, 600, Hub.canvasHeight, TMenu.VERTICAL);
				newSimMenu = new TMenu(1200, 0, 500, Hub.canvasHeight, TMenu.VERTICAL);

				addTComponent(mainMenu);
				mainMenu.addTComponent(newSimButton);
				mainMenu.addTComponent(resumeSimButton);
				mainMenu.addTComponent(editorButton);

				simWidthSlider.setRange(800, 2000);
				newSimMenu.addTComponent(simWidthSlider);
				newSimMenu.addTComponent(startSimButton);
				addTComponent(newSimMenu);
			}

		@Override
		public void tick(double secondsPassed)
			{}

		@Override
		protected void render(Graphics g)
			{
				g.setColor(Color.YELLOW);
				g.fillRect(0, 0, 1200, 600);
			}

		@Override
		public void tActionEvent(TActionEvent event)
			{
				if (event.getSource() == newSimButton)
					{
						mainMenu.setX(0);
						newSimMenu.setX(640);
						simWidth = (int) simWidthSlider.getSliderValue(0);
					}
				else if (event.getSource() == resumeSimButton)
					{
						if (Hub.simWindow.sim != null)
							changeRenderableObject(Hub.simWindow);
					}
				else if (event.getSource() == startSimButton)
					{
						Hub.simWindow.sim = new Simulation(simWidth);
						changeRenderableObject(Hub.simWindow);
					}
				else if (event.getSource() == editorButton)
					{
						// changeRenderableObject(Hub.editorWindow);
					}
			}

		@Override
		public void tScrollEvent(TScrollEvent event)
			{
				if (event.getSource() == simWidthSlider)
					{
						simWidth = (int) event.getScrollValue();
					}
			}
	}