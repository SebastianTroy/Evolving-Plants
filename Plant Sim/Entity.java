package EvolvingPlants;

import java.awt.Graphics;

public abstract class Entity
	{
		public float x;
		public float y;
		
		public float energy;
		public boolean exists = true;
		
		public abstract void tick();
		
		public abstract void render(Graphics g);
	}
