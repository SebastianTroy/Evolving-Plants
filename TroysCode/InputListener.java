package TroysCode;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

/**
 * This Class listens for user input, e.g. mouse clicks and key presses, and
 * passes the input it recieved to the current {@link RenderableObject}.
 * 
 * @author Sebastian Troy
 */
public class InputListener implements ActionListener, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, WindowFocusListener // ,ComponentListener
	{
		/*
		 * Each of these methods simply passes all detected events to the
		 * current "renderable object", this means that only the currently
		 * focused class recieves events, also it means that all eventlisteners
		 * are held in this single class. Any statements added to these methods
		 * would occour regardless of which renderableObject is current, however
		 * I tend to keep this class simple and put the specific methods in the
		 * RenderableObject Class.
		 */

		@Override
		public final void mousePressed(MouseEvent event)
			{
				hub.renderer.getRenderableObject().mousePressed(event);
			}

		@Override
		public final void mouseReleased(MouseEvent event)
			{
				hub.renderer.getRenderableObject().mouseReleased(event);
			}

		@Override
		public final void mouseClicked(MouseEvent event)
			{
				hub.renderer.getRenderableObject().mouseClicked(event);
			}

		@Override
		public final void mouseDragged(MouseEvent event)
			{
				hub.renderer.getRenderableObject().mouseDragged(event);
			}

		@Override
		public final void mouseMoved(MouseEvent event)
			{
				hub.renderer.getRenderableObject().mouseMoved(event);
			}

		@Override
		public final void mouseEntered(MouseEvent event)
			{
				hub.renderer.getRenderableObject().mouseEntered(event);
			}

		@Override
		public final void mouseExited(MouseEvent event)
			{
				hub.renderer.getRenderableObject().mouseExited(event);
			}

		@Override
		public final void mouseWheelMoved(MouseWheelEvent event)
			{
				hub.renderer.getRenderableObject().mouseWheelMoved(event);
			}

		@Override
		public final void keyPressed(KeyEvent event)
			{
				/*
				 * This allows developer to quickly check the keycode for any
				 * key to use, simply hold the ALT key and press another
				 */
				if (hub.DEBUG)
					/*
					 * If the alt key is down, and the key being prerssed is not
					 * alt, send the keyCode to an infoBox.
					 */
					if (event.isAltDown() && event.getKeyCode() != 18)
						Tools.infoBox(event.getKeyChar() + " = " + event.getKeyCode() + "   - (keycode for ALT key = 18)", "class: InputReciever");

				/*
				 * This allows you to exit the program without the mouse or a
				 * framed window.
				 */

				if (event.getKeyCode() == 27) // 'Esc' key
					{
						Tools.exitWindow("Do you want to Quit?");
					}

				hub.renderer.getRenderableObject().keyPressed(event);
			}

		@Override
		public final void keyReleased(KeyEvent event)
			{
				hub.renderer.getRenderableObject().keyReleased(event);
			}

		@Override
		public final void keyTyped(KeyEvent event)
			{
				hub.renderer.getRenderableObject().keyTyped(event);
			}

		@Override
		public final void actionPerformed(ActionEvent event)
			{
				hub.renderer.getRenderableObject().actionPerformed(event);
			}

		@Override
		public void windowGainedFocus(WindowEvent event)
			{
				hub.renderer.focused = true;
				hub.renderer.getRenderableObject().programGainedFocus(event);
			}

		@Override
		public void windowLostFocus(WindowEvent event)
			{
				hub.renderer.focused = true;
				hub.renderer.getRenderableObject().programLostFocus(event);
			}

	}
