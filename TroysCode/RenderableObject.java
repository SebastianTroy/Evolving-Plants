package TroysCode;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;

import TroysCode.T.TComponentContainer;

/**
 * An abstract class. Daughter Classes which inherit from this one can be added
 * to the {@link Renderer} and therefore render to the canvas and pick up the
 * user input detected in {@link InputListener}.
 * 
 * @author Sebastian Troy
 */
public abstract class RenderableObject extends TComponentContainer
	{
		private static final long serialVersionUID = 1L;

		/**
		 * This boolean is set to <code>true</code> once the
		 * {@link RenderableObject} has been initiated.
		 */
		public boolean initiated = false;

		public RenderableObject()
			{
			}
		
		public final void tInitiate()
			{
				addActionListener(hub.input);
				setParent(hub.renderer);
				
				initiate();
			}

		/**
		 * This method is called by the main loop in the {@link Renderer}, it is
		 * only called if the <code>tTick()</code> method has been called since
		 * the last render.
		 * 
		 * @param g
		 *            - The graphics object which is being drawn onto.
		 */
		public final void tRenderObject(Graphics g)
			{
				renderObject(g);

				renderTComponents(g);
			}

		/**
		 * This method switches the current {@link RenderableOnject} in the
		 * {@link Renderer} to one of your choice.
		 * 
		 * @param object
		 *            - The {@link RenderableObject} you want to switch to.
		 */
		protected final void changeRenderableObject(RenderableObject object)
			{
				hub.renderer.changeRenderableObject(object);
			}

		/**
		 * This method is called when the {@link RenderableObject} is added to
		 * the {@link Renderer} for the first time.
		 */
		protected abstract void initiate();

		/**
		 * This method is called by the {@link Renderer} each time it makes a
		 * {@link RenderableObject} current.
		 */
		protected abstract void refresh();

		/**
		 * This method is called from the main loop in the {@link Renderer}
		 * Class. The time interval between calls has been set by the loop,
		 * however if you modify the loop you may wish to pass a timePassed
		 * variable into the method.
		 */
		protected abstract void tick();

		/**
		 * This method is called by the main loop in the {@link Renderer}, it is
		 * only called if the <code>tick()</code> method has been called since
		 * the last render.
		 * 
		 * @param graphics
		 *            - The graphics object which is being drawn onto.
		 */
		protected abstract void renderObject(Graphics graphics);

		/**
		 * This method is called whenever the {@link InputListener} Class
		 * detects a mouse button is pressed down.
		 * 
		 * @param event
		 *            - The {@link MouseEvent} detected by the
		 *            {@link InputListener}
		 */
		protected abstract void mousePressed(MouseEvent event);

		/**
		 * This method is called whenever the {@link InputListener} Class
		 * detects a pressed mouse button is released.
		 * 
		 * @param event
		 *            - The {@link MouseEvent} detected by the
		 *            {@link InputListener}
		 */
		protected abstract void mouseReleased(MouseEvent event);

		/**
		 * This method is called whenever the {@link InputListener} Class
		 * detects the mouse is moved while a mouse button is pressed down.
		 * 
		 * @param event
		 *            - The {@link MouseEvent} detected by the
		 *            {@link InputListener}
		 */
		protected abstract void mouseDragged(MouseEvent event);

		/**
		 * This method is called whenever the {@link InputListener} Class
		 * detects the mouse has been moved.
		 * 
		 * @param event
		 *            - The {@link MouseEvent} detected by the
		 *            {@link InputListener}
		 */
		protected abstract void mouseMoved(MouseEvent event);

		/**
		 * This method is called whenever the {@link InputListener} Class
		 * detects that the mouse wheel (if present) has been scrolled.
		 * 
		 * @param event
		 *            - The {@link MouseEvent} detected by the
		 *            {@link InputListener}
		 */
		protected abstract void mouseWheelMoved(MouseWheelEvent event);

		/**
		 * This method is called whenever the {@link InputListener} Class
		 * detects that an {@link ActionEvent} has been fired. {@link JButtons}s
		 * and {@link TButtons}s fire such events, among other things.
		 * 
		 * @param event
		 *            - The {@link ActionEvent} detected by the
		 *            {@link InputListener}
		 */
		protected abstract void actionPerformed(ActionEvent event);

		/**
		 * This method is called whenever the {@link InputListener} Class
		 * detects that a key has been pressed down.
		 * 
		 * @param event
		 *            - The {@link KeyEvent} detected by the
		 *            {@link InputListener}
		 */
		protected abstract void keyPressed(KeyEvent event);

		/**
		 * This method is called whenever the {@link InputListener} Class
		 * detects that a key, which has previously been pressed down, has been
		 * released.
		 * 
		 * @param event
		 *            - The {@link KeyEvent} detected by the
		 *            {@link InputListener}
		 */
		protected abstract void keyReleased(KeyEvent event);

		/**
		 * This method is called whenever the {@link InputListener} Class
		 * detects that a key has been pressed down and released in quick
		 * succession.
		 * 
		 * @param event
		 *            - The {@link KeyEvent} detected by the
		 *            {@link InputListener}
		 */
		protected abstract void keyTyped(KeyEvent event);

		/**
		 * This method is called whenever the {@link InputListener} Class
		 * detects that a mouse button has been pressed down and released in
		 * quick succession.
		 * 
		 * @param event
		 *            - The {@link MouseEvent} detected by the
		 *            {@link InputListener}
		 */
		protected abstract void mouseClicked(MouseEvent event);

		/**
		 * This method is called whenever the {@link InputListener} Class
		 * detects that the mouse point has entered the program's {@link Frame}.
		 * 
		 * @param event
		 *            - The {@link MouseEvent} detected by the
		 *            {@link InputListener}
		 */
		protected abstract void mouseEntered(MouseEvent event);

		/**
		 * This method is called whenever the {@link InputListener} Class
		 * detects that the mouse point has exited the program's {@link Frame}.
		 * 
		 * @param event
		 *            - The {@link MouseEvent} detected by the
		 *            {@link InputListener}
		 */
		protected abstract void mouseExited(MouseEvent event);

		/**
		 * This method is called whenever the {@link InputListener} Class
		 * detects that the program's {@link Frame} has gained the user's focus.
		 * 
		 * @param event
		 *            - The {@link WindowEvent} detected by the
		 *            {@link InputListener}
		 */
		protected abstract void programGainedFocus(WindowEvent event);

		/**
		 * This method is called whenever the {@link InputListener} Class
		 * detects that the program's {@link Frame} has lost the user's focus.
		 * 
		 * @param event
		 *            - The {@link WindowEvent} detected by the
		 *            {@link InputListener}
		 */
		protected abstract void programLostFocus(WindowEvent event);

		/**
		 * This method is called whenever the {@link Frame} Class detects that
		 * the program's {@link Frame} has been resized.
		 * 
		 * @param event
		 *            - The {@link ComponentEvent} detected by the
		 *            {@link InputListener}
		 */
		protected abstract void frameResized(ComponentEvent event);
	}
