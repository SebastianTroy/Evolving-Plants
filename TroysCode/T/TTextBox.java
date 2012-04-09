package TroysCode.T;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;

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
		 * @param parent
		 *            - the {@link TComponentContainer} to which this
		 *            {@link TComponent} has been added.
		 */
		public final void setTComponentContainer(TComponentContainer parent)
			{
				if (tComponentContainer == null)
					{
						tComponentContainer = parent;
						tComponentContainer.getParent().addMouseListener(this);
						tComponentContainer.getParent().addKeyListener(this);
					}
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
		public void render(Graphics g)
			{
			}
	}
