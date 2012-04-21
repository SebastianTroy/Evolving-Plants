package TroysCode.T;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.Serializable;
import java.util.ArrayList;

import TroysCode.hub;

/**
 * This class is designed to allow the moving of multiple {@link TComponent}s at
 * one time easy.
 * 
 * @author Sebastian Troy
 */
public class TCollection extends TComponent implements Serializable
	{
		private static final long serialVersionUID = 1L;

		/**
		 * An {@link ArrayList} of all the {@link TComponent}s within this
		 * TCollection.
		 */
		private volatile ArrayList<TComponent> components = new ArrayList<TComponent>();

		/**
		 * The position of the {@link TCollection}. When the position of this
		 * {@link TCollection} is updated all of the positions of the
		 * {@link TComponent}s it contains are updated too.
		 * 
		 * @param x
		 *            - this x position of this {@link TCollection}.
		 * @param y
		 *            - this y position of this {@link TCollection}.
		 */
		public TCollection(double x, double y)
			{
				super(x, y, 0, 0);
			}

		/**
		 * The position of the {@link TCollection}. When the position of this
		 * {@link TCollection} is updated all of the positions of the
		 * {@link TComponent}s it contains are updated too.
		 * 
		 * @param x
		 *            - this x position of this {@link TCollection}.
		 * @param y
		 *            - this y position of this {@link TCollection}.
		 */
		public TCollection(double x, double y, double width, double height)
			{
				super(x, y, width, height);
			}

		/**
		 * This method tells the {@link TComponent} which
		 * {@link TComponentContainer} it has been added to. It then updates the
		 * {@link TComponentContainer} for each of the {@link TComponent}s it
		 * contains.
		 * 
		 * @param componentContainer
		 *            - the {@link TComponentContainer} to which this
		 *            {@link TComponent} has been added.
		 */
		protected final void setTComponentContainer(TComponentContainer componentContainer)
			{
				if (tComponentContainer != componentContainer)
					{
						if (tComponentContainer != null)
							{
								for (TComponent c : getTComponents())
									tComponentContainer.removeTComponent(c);
							}
						tComponentContainer = componentContainer;
					}

				for (TComponent c : getTComponents())
					tComponentContainer.addTComponent(c);
			}

		@Override
		protected final void removedFromTComponentContainer()
			{
				for (TComponent c : getTComponents())
					tComponentContainer.removeTComponent(c);

				tComponentContainer = null;
			}

		public synchronized final void addTComponent(TComponent component)
			{
				if (!components.contains(component))
					{
						components.add(component);
						if (tComponentContainer != null)
							tComponentContainer.addTComponent(component);
					}
			}

		public synchronized final void removeTComponent(TComponent component)
			{
				components.remove(component);
				if (tComponentContainer != null)
					tComponentContainer.removeTComponent(component);
			}

		private synchronized final TComponent[] getTComponents()
			{
				TComponent[] tComponents = new TComponent[components.size()];
				components.toArray(tComponents);
				return tComponents;
			}

		/*
		 * If there is a background this draws it, and all it's embedded
		 * components
		 */
		@Override
		public final void render(Graphics g)
			{
				if (hub.DEBUG)
					g.drawRect((int) Math.round(x), (int) Math.round(y), (int) Math.round(width), (int) Math.round(height));

				if (backgroundColour != null)
					{
						g.setColor(backgroundColour);
						g.fillRect((int) Math.round(x), (int) Math.round(y), (int) Math.round(width), (int) Math.round(height));
					}
				for (TComponent tc : getTComponents())
					tc.render(g);
			}

		/*
		 * The following move and set position methods allow the movement of all
		 * the TComponents in unison, this is what this class is mainly intended
		 * for.
		 */

		@Override
		public final void setX(double x)
			{
				double diffX = x - this.x;
				this.moveX(diffX);
			}

		@Override
		public final void setY(double y)
			{
				double diffY = y - this.y;
				this.moveY(diffY);
			}

		@Override
		public final void setPosition(double x, double y)
			{
				double diffX = x - this.x;
				double diffY = y - this.y;
				this.movePosition(diffX, diffY);
			}

		@Override
		public final void moveX(double x)
			{
				this.x += x;
				for (TComponent tc : getTComponents())
					tc.moveX(x);
			}

		@Override
		public final void moveY(double y)
			{
				this.y += y;
				for (TComponent tc : getTComponents())
					tc.moveX(y);
			}

		@Override
		public final void movePosition(double x, double y)
			{
				this.x += x;
				this.y += y;
				for (TComponent tc : getTComponents())
					tc.movePosition(x, y);

			}

		@Override
		public void keyTyped(KeyEvent e)
			{
				// TODO Auto-generated method stub
				
			}

		@Override
		public void keyPressed(KeyEvent e)
			{
				// TODO Auto-generated method stub
				
			}

		@Override
		public void keyReleased(KeyEvent e)
			{
				// TODO Auto-generated method stub
				
			}

		@Override
		public void mouseDragged(MouseEvent e)
			{
				// TODO Auto-generated method stub
				
			}

		@Override
		public void mouseMoved(MouseEvent e)
			{
				// TODO Auto-generated method stub
				
			}

		@Override
		public void mouseClicked(MouseEvent e)
			{
				// TODO Auto-generated method stub
				
			}

		@Override
		public void mousePressed(MouseEvent e)
			{
				// TODO Auto-generated method stub
				
			}

		@Override
		public void mouseReleased(MouseEvent e)
			{
				// TODO Auto-generated method stub
				
			}

		@Override
		public void mouseEntered(MouseEvent e)
			{
				// TODO Auto-generated method stub
				
			}

		@Override
		public void mouseExited(MouseEvent e)
			{
				// TODO Auto-generated method stub
				
			}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e)
			{
				// TODO Auto-generated method stub
				
			}

		@Override
		public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				
			}
	}