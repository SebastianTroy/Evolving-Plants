package TroysCode.T;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.io.Serializable;

import javax.swing.event.EventListenerList;

public class TTextBox extends TComponent implements Serializable, MouseListener, KeyListener
	{
		private static final long serialVersionUID = 1L;

		public TTextBox(float x, float y, float width, float height)
			{
				super(x, y, width, height);
			}

		/**
		 * This method tells the {@link TComponent} which
		 * {@link TComponentContainer} it has been added to.
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
								tComponentContainer.getParent().removeMouseListener(this);
								tComponentContainer.getParent().removeMouseMotionListener(this);
								tComponentContainer.getParent().removeMouseWheelListener(this);
							}
						tComponentContainer = componentContainer;
						tComponentContainer.getParent().addMouseListener(this);
						tComponentContainer.getParent().addKeyListener(this);
					}
			}

		/**
		 * This method is called whenevet this {@link TComponent} is removed
		 * from a {@link TComponentContainer}.
		 */
		@Override
		protected final void removedFromTComponentContainer()
			{
				listenerList = new EventListenerList();
				tComponentContainer = null;
			}

		@Override
		public void render(Graphics g)
			{
			}

		@Override
		public void keyTyped(KeyEvent paramKeyEvent)
			{
			}

		@Override
		public void keyPressed(KeyEvent paramKeyEvent)
			{
			}

		@Override
		public void keyReleased(KeyEvent paramKeyEvent)
			{
			}

		@Override
		public void mouseClicked(MouseEvent paramMouseEvent)
			{
			}

		@Override
		public void mousePressed(MouseEvent paramMouseEvent)
			{
			}

		@Override
		public void mouseReleased(MouseEvent paramMouseEvent)
			{
			}

		@Override
		public void mouseEntered(MouseEvent paramMouseEvent)
			{
			}

		@Override
		public void mouseExited(MouseEvent paramMouseEvent)
			{
			}

		@Override
		public void mouseDragged(MouseEvent e)
			{
			}

		@Override
		public void mouseMoved(MouseEvent e)
			{
			}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e)
			{
			}

		@Override
		public void actionPerformed(ActionEvent e)
			{
			}
	}
