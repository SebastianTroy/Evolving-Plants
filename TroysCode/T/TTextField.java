package TroysCode.T;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import javax.swing.event.EventListenerList;

import TroysCode.hub;

//TODO this is work in progress, and highly ambitious!

/**
 * <strong> THIS CLASS IS NOT FINISHED DO NOT USE</strong>
 */
public class TTextField extends TComponent implements Serializable
	{
		private static final long serialVersionUID = 1L;

		private Font font = setFont();

		private String text = "";
		private TDimension textBounds = new TDimension();

		public TTextField(double x, double y, double width, double height)
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
				tComponentContainer.getParent().removeMouseListener(this);
				tComponentContainer.getParent().removeMouseMotionListener(this);

				listenerList = new EventListenerList();
				tComponentContainer = null;
			}

		@Override
		public void render(Graphics g)
			{
				g.setColor(Color.WHITE);
				g.fillRect((int) Math.round(x), (int) Math.round(y), (int) Math.round(width), (int) Math.round(height));

				BufferedImage img = new BufferedImage((int) Math.round(width), (int) Math.round(height), BufferedImage.TYPE_INT_ARGB);
				Graphics textBox = img.getGraphics();
				// textBox.drawString(text, x, y);
				textBox.dispose();
				g.drawImage(img, (int) Math.round(x), (int) Math.round(y), hub.renderer);
			}

		/**
		 * This method allows the user to set the {@link Font}, font size and
		 * font style for the {@link TButton}.
		 * 
		 * @param font
		 *            - the {@link Font} to be used for the <code>label</code>.
		 */
		public final void setFont(Font font)
			{
				this.font = font;
				setTextBounds();
				sizeTTextFieldToLabel();
			}

		/**
		 * This method allows the {@link Font}'s typeface to be changed, while
		 * not changing the size or style of the {@link Font}.
		 * 
		 * @param fontName
		 *            - The new {@link Font}'s name, the old {@link Font}'s size
		 *            and style are maintained.
		 */
		public final void setFontFont(String fontName)
			{
				this.font = new Font(fontName, this.font.getStyle(), this.font.getSize());
				setTextBounds();
				sizeTTextFieldToLabel();
			}

		/**
		 * This method allows the {@link Font}'s size to be changed, while not
		 * changing the typeface or style of the {@link Font}.
		 * 
		 * @param size
		 *            - The size to set the <code>label</code> to.
		 */
		public final void setFontSize(int size)
			{
				this.font = new Font(font.toString(), font.getStyle(), size);
				setTextBounds();
				sizeTTextFieldToLabel();
			}

		/**
		 * This method allows the {@link Font}'s style to be changed, while not
		 * changing the typeface or size of the {@link Font}.
		 * 
		 * @param size
		 *            - The font style to set the <code>label</code> to. E.G.
		 *            <code>Font.BOLD</code>.
		 */
		public final void setFontStyle(int style)
			{
				this.font = new Font(font.toString(), style, font.getSize());
				setTextBounds();
				sizeTTextFieldToLabel();
			}

		/**
		 * This method is only called when an instance of the class is created,
		 * it is needed because the only way I can find to get the computers
		 * default font without performing lots of checks is to let Java do the
		 * hard work and use a graphics object, which is given a default font by
		 * java.
		 */
		private final Font setFont()
			{
				Graphics g = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).getGraphics();
				Font font = new Font(g.getFont().toString(), 0, 12);
				g.dispose();

				/*
				 * NOTE I don't use ""return g.getFont()"" because then the
				 * graphics is not explicitly disposed of, which is bad
				 * practice.
				 */

				return font;
			}

		/**
		 * This method allows the computer to calculate the physical size of the
		 * label, no matter what font is being used. The information is stored
		 * in a {@link Dimension} called <code>labelBounds</code>.
		 */
		private final void setTextBounds()
			{
				Graphics g = new BufferedImage(hub.frame.startWidth, hub.frame.startHeight, BufferedImage.TYPE_INT_ARGB).getGraphics();
				g.setFont(font);

				textBounds.setWidth(g.getFontMetrics().getStringBounds(text, g).getWidth());
				textBounds.setHeight(g.getFontMetrics().getAscent() + g.getFontMetrics().getDescent());

				g.dispose();
			}

//		/**
//		 * The baseline is the line on which text is written, however letters
//		 * often have tails below the line, and different {@link Font}s have
//		 * different tail lengths. This length is called the 'descent' and is
//		 * needed in order to calculate the physical {@link Dimension}s of the
//		 * <code>label</code>, and therefore used to position the
//		 * <code>label</code> in the center of the {@link TButton}.
//		 * 
//		 * @return - the distance from the baseline of a letter (e.g. 'j') to
//		 *         the tip of it's tail.
//		 */
//		private final int getFontDescent()
//			{
//				Graphics g = new BufferedImage(hub.frame.startWidth, hub.frame.startHeight, BufferedImage.TYPE_INT_ARGB).getGraphics();
//				g.setFont(font);
//
//				return (int) g.getFontMetrics().getDescent();
//			}

		/**
		 * This method sizes the {@link TButton} to the size of its label, but
		 * only <code>if (fitToLabel == true && image == null)</code>.
		 */
		private final void sizeTTextFieldToLabel()
			{
				this.width = textBounds.getWidth() + 6;
				this.height = textBounds.getHeight() + 6;
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
		public void actionPerformed(ActionEvent e)
			{
			}
	}
