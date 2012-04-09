//package TroysCode.T;
//
//import java.awt.Graphics;
//import java.awt.event.ActionEvent;
//import java.awt.event.ComponentEvent;
//import java.awt.event.KeyEvent;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseWheelEvent;
//import java.awt.image.BufferedImage;
//import java.util.ArrayList;
//
//import TroysCode.hub;
//
//public class TPanel extends TComponent implements Serializable
//	{
//		private static final long serialVersionUID = 1L;
//
//		/*
//		 * This class has been designed purely to make moving collections of
//		 * buttons and text boxes easier, it will not catch events like JPanels,
//		 * or have any of it's extended features, it does however allow the
//		 * setting of a background image.
//		 */
//
//		/*
//		 * This image (if present) will form the background and set the size for
//		 * the TPanel
//		 */
//		private BufferedImage background = null;
//
//		/*
//		 * These constructors assume that if you specify a size, no image is
//		 * being used, and if you specify an image then you want to use the
//		 * images bounds as the TPanel size.
//		 */
//		public TPanel(float x, float y, float width, float height)
//			{
//				super(x, y, width, height);
//			}
//
//		public TPanel(float x, float y, BufferedImage background)
//			{
//				super(x, y, background.getWidth(), background.getHeight());
//			}
//
//
//		/*
//		 * If there is a background this draws it, and all it's embedded
//		 * components
//		 */
//		@Override
//		public final void render(Graphics g)
//			{
//				if (background != null)
//					g.drawImage(background, Math.round(x), Math.round(y), hub.renderer);
//
//				for (TComponent tc : getTComponents())
//					tc.render(g);
//			}
//
//		/*
//		 * The following move and set position methods allow the movement of all
//		 * the TComponents in unison, this is what this class is mainly intended
//		 * for.
//		 */
//
//		@Override
//		public final void setX(float x)
//			{
//				float diffX = x - this.x;
//				this.moveX(diffX);
//			}
//
//		@Override
//		public final void setY(float y)
//			{
//				float diffY = y - this.y;
//				this.moveY(diffY);
//			}
//
//		@Override
//		public final void setPosition(float x, float y)
//			{
//				float diffX = x - this.x;
//				float diffY = y - this.y;
//				this.movePosition(diffX, diffY);
//			}
//
//		@Override
//		public final void moveX(float x)
//			{
//				this.x += x;
//				for (TComponent tc : getTComponents())
//					tc.moveX(x);
//			}
//
//		@Override
//		public final void moveY(float y)
//			{
//				this.y += y;
//				for (TComponent tc : getTComponents())
//					tc.moveX(y);
//			}
//
//		@Override
//		public final void movePosition(float x, float y)
//			{
//				this.x += x;
//				this.y += y;
//				for (TComponent tc : getTComponents())
//					tc.movePosition(x, y);
//
//			}
//
//		/*
//		 * The following methods pass on mouse events and key events onto any
//		 * TComponents it contains
//		 */
//
//		@Override
//		public final void mousePressed(MouseEvent me)
//			{
//				for (TComponent tc : getTComponents())
//					tc.mousePressed(me);
//			}
//
//		@Override
//		public final void mouseReleased(MouseEvent me)
//			{
//				for (TComponent tc : getTComponents())
//					tc.mouseReleased(me);
//			}
//
//		@Override
//		public final void mouseDragged(MouseEvent me)
//			{
//				for (TComponent tc : getTComponents())
//					tc.mouseDragged(me);
//			}
//
//		@Override
//		public final void mouseWheelMoved(MouseWheelEvent me)
//			{
//				for (TComponent tc : getTComponents())
//					tc.mouseDragged(me);
//			}
//
//		@Override
//		public final void actionPerformed(ActionEvent ae)
//			{
//				for (TComponent tc : getTComponents())
//					tc.actionPerformed(ae);
//			}
//
//		@Override
//		public final void keyPressed(KeyEvent ke)
//			{
//				for (TComponent tc : getTComponents())
//					tc.keyPressed(ke);
//			}
//
//		@Override
//		public final void keyReleased(KeyEvent ke)
//			{
//				for (TComponent tc : getTComponents())
//					tc.keyReleased(ke);
//			}
//
//		@Override
//		public final void keyTyped(KeyEvent ke)
//			{
//				for (TComponent tc : getTComponents())
//					tc.keyTyped(ke);
//			}
//
//		/*
//		 * These method is not used
//		 */
//		@Override
//		public final void mouseMoved(MouseEvent me)
//			{
//			}
//
//		@Override
//		public final void mouseClicked(MouseEvent e)
//			{
//			}
//
//		@Override
//		public final void mouseEntered(MouseEvent e)
//			{
//			}
//
//		@Override
//		public final void mouseExited(MouseEvent e)
//			{
//			}
//
//		@Override
//		public final void componentResized(ComponentEvent e)
//			{
//			}
//
//		@Override
//		public final void componentMoved(ComponentEvent e)
//			{
//			}
//
//		@Override
//		public final void componentShown(ComponentEvent e)
//			{
//			}
//
//		@Override
//		public final void componentHidden(ComponentEvent e)
//			{
//			}
//	}
