package evolvingPlants;

import java.awt.Color;
import java.awt.Graphics;

import evolvingPlants.simulation.Simulation;

import tCode.RenderableObject;
import tComponents.components.TButton;
import tComponents.components.TCollection;
import tComponents.components.TMenu;
import tComponents.components.TSlider;
import tComponents.utils.events.TActionEvent;
import tComponents.utils.events.TScrollEvent;

public class MainMenu extends RenderableObject
	{
		private TMenu mainMenu = new TMenu(300, 25, 600, 525, TMenu.VERTICAL);
		private TButton newSimButton = new TButton(0, 0, 0, 0, "New Simulation");
		private TButton resumeSimButton = new TButton(0, 0, 0, 0, "Resume Simulation");
		private TButton editorButton = new TButton(0, 0, 0, 0, "DNA Editor");

		private TCollection newSimCollection = new TCollection(1200, 0);
		private TSlider simWidthSlider = new TSlider(1200, 150, 500, TSlider.HORIZONTAL);
		private TButton startSimButton = new TButton(1450, 400, 300, 75, "Start Simulation");

		// New Simulation Variables
		private int simWidth = 800;

		@Override
		protected void initiate()
			{
				addTComponent(mainMenu);
				mainMenu.addTComponent(newSimButton, true);
				mainMenu.addTComponent(resumeSimButton, true);
				mainMenu.addTComponent(editorButton, true);

				simWidthSlider.setRange(800, 2000);
				newSimCollection.addTComponent(simWidthSlider);
				newSimCollection.addTComponent(startSimButton);
				addTComponent(newSimCollection);
				
				mainMenu.setX(200);
				newSimCollection.setX(1200);					
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
						newSimCollection.setX(640);
					}
				else if (event.getSource() == resumeSimButton)
					{
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
						simWidth = (int) ((event.getScrollPercent() * 52) + 800);
					}
			}
	}