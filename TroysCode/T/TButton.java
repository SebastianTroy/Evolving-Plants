package TroysCode.T;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import javax.swing.event.EventListenerList;

import TroysCode.InputListener;
import TroysCode.RenderableObject;
import TroysCode.SerializableBufferedImage;
import TroysCode.hub;

/**
 * An implementation of a "push" button.
 * <p>
 * {@link TButton}s are the most simple type of {@link TComponent}. They can
 * either use the default button image and have a {@link String} set as their
 * <code>label</code>, or an image can be set which replaces the default image,
 * and the <code>label</code>.
 * <p>
 * When a {@link TButton} has been clicked on, it fires an {@link ActionEvent}.
 * 
 * @author Sebastian Troy
 */
public class TButton extends TComponent implements Serializable
	{
		private static final long serialVersionUID = 1L;

		/**
		 * This constant represents the location of an image in the Tcomponents
		 * texture array
		 */
		public static final byte TBUTTON = 0;
		/**
		 * This constant represents the location of an image in the Tcomponents
		 * texture array
		 */
		public static final byte BUTTONDOWN = 1;

		/**
		 * This {@link SerializableBufferedImage} can be used to represet this
		 * {@link TButton}. This means the {@link TButton} changes to the size
		 * of the image, and the <code>label</code> is hidden.
		 * <p>
		 * To load a {@link BufferedImage} check out the
		 * {@link TroysCode.Images} class.
		 */
		private final SerializableBufferedImage image = new SerializableBufferedImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
		private boolean reDraw = true;

		/**
		 * If the {@link TButton} is <code>active</code>, this boolean is used
		 * to keep track of whether the mouse is still within the
		 * {@link TButton}'s bounds.
		 * <p>
		 * If the {@link TButton} is not <code>active</code> this boolean is
		 * <code>false</code>.
		 */
		private boolean mouseOver = false;

		/**
		 * The <code>label</code> uses this {@link Font}.
		 */
		private Font font = setFont();

		/**
		 * This {@link String} can be used to display a message on the
		 * {@link TButton}.
		 * <p>
		 * If the {@link String} is too big to fit inside the {@link TButton} it
		 * is displayed to the side of the {@link TButton} instead. The
		 * {@link TButton} can be set to change size to fit the message instead.
		 * Use <code>setFitToLabel(true)</code>.
		 * <p>
		 * If the {@link TButton}'s image has been set, the mesage is not shown.
		 */
		private String label = "=";

		/**
		 * The <code>label</code> can be bigger than the actual {@link TButton}.
		 * If this happens the {@link TButton} can be re-sized, or the
		 * <code>label</code> is rendered to the right of the {@link TButton}.
		 * This {@link Dimension} is used to keep track of the size of the
		 * <code>label</code>.
		 */
		private TDimension labelBounds = new TDimension();

		/**
		 * The <code>label</code> can be bigger than the actual {@link TButton}.
		 * If this boolean is true happens the {@link TButton} is re-sized, if
		 * it is false, the <code>label</code> is rendered to the right of the
		 * {@link TButton}.
		 */
		private boolean fitToLabel = false;

		/**
		 * The standard {@link TComponent} constructor.
		 * 
		 * @param x
		 *            - the x position of the {@link TCompoment} within the
		 *            program's frame.
		 * @param y
		 *            - the y position of the {@link TCompoment} within the
		 *            program's frame.
		 * @param width
		 *            - the width of the {@link TCompoment} in pixels.
		 * @param height
		 *            - the height of the {@link TCompoment} in pixels.
		 */
		public TButton(double x, double y, double width, double height)
			{
				super(x, y, width, height);
			}

		/**
		 * This constructor automatically sizes the {@link TButton} to the size
		 * of the <code>label</code>.
		 * 
		 * @param x
		 *            - the x position of the {@link TCompoment} within the
		 *            program's frame.
		 * @param y
		 *            - the y position of the {@link TCompoment} within the
		 *            program's frame.
		 * @param label
		 *            - a {@link String} which is rendered onto the
		 *            {@link TButton}.
		 */
		public TButton(double x, double y, String label)
			{
				super(x, y, 10, 10);

				setLabel(label);
				setLabelBounds();
				fitToLabel = true;
				sizeTButtonToLabel();
				reDraw = true;
			}

		/**
		 * This constructor is the same as the standard constructor, except it
		 * also sets the {@link TButton}'s <code>label</code>. The
		 * {@link TButton} does not re-size to fit the <code>label</code> unless
		 * otherwise specified.
		 * 
		 * @param x
		 *            - the x position of the {@link TCompoment} within the
		 *            program's frame.
		 * @param y
		 *            - the y position of the {@link TCompoment} within the
		 *            program's frame.
		 * @param width
		 *            - the width of the {@link TCompoment} in pixels.
		 * @param height
		 *            - the height of the {@link TCompoment} in pixels.
		 * 
		 * @param label
		 *            - a {@link String} which is rendered onto the
		 *            {@link TButton}.
		 */
		public TButton(double x, double y, double width, double height, String label)
			{
				super(x, y, width, height);

				setLabel(label);
				setLabelBounds();
				sizeTButtonToLabel();
			}

		/**
		 * This constructor is the same as the standard constructor, except it
		 * also sets the {@link TButton}'s <code>label</code>, and the
		 * {@link Font} that the <code>label</code> will use. The
		 * {@link TButton} does not re-size to fit the <code>label</code> unless
		 * otherwise specified.
		 * 
		 * @param x
		 *            - the x position of the {@link TCompoment} within the
		 *            program's frame.
		 * @param y
		 *            - the y position of the {@link TCompoment} within the
		 *            program's frame.
		 * @param width
		 *            - the width of the {@link TCompoment} in pixels.
		 * @param height
		 *            - the height of the {@link TCompoment} in pixels.
		 * 
		 * @param label
		 *            - a {@link String} which is rendered onto the
		 *            {@link TButton}.
		 */
		public TButton(double x, double y, double width, double height, String label, Font font)
			{
				super(x, y, width, height);

				setLabel(label);
				setFont(font);
				setLabelBounds();
				sizeTButtonToLabel();
			}

		/**
		 * This constructor uses the width and height of the <code>image</code>
		 * passed in to set the size of the {@link TButton}.
		 * 
		 * @param x
		 *            - the x position of the {@link TCompoment} within the
		 *            program's frame.
		 * @param y
		 *            - the y position of the {@link TCompoment} within the
		 *            program's frame.
		 * @param image
		 *            - the {@link BufferedImage} to be used as the
		 *            {@link TButton}.
		 */
		public TButton(double x, double y, BufferedImage image)
			{
				super(x, y, image.getWidth(), image.getHeight());

				setImage(image);
				reDraw = false;
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
						tComponentContainer.getParent().addMouseMotionListener(this);
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

				tComponentContainer = null;
				listenerList = new EventListenerList();
			}

		/**
		 * If no image is set for this {@link TButton} an image from the
		 * <code>defaultTexture.png</code> is used, the <code>label</code> is
		 * then rendered over the top of it.
		 * <p>
		 * If an image is set, then only the image is rendered.
		 */
		@Override
		public final void render(Graphics g)
			{
				if (reDraw)
					{
						reRenderTButton();
					}

				// This code draws the image
				g.drawImage(image.get(), (int) Math.round(x), (int) Math.round(y), (int) Math.round(width), (int) Math.round(height), hub.renderer);

				/*
				 * if the mouse is over this TButton, it covers it with a
				 * translucent rectangle, this darkens the image when it is
				 * being clicked on
				 */
				if (this.mouseOver)
					{
						g.setColor(new Color(50, 50, 50, 50));
						g.fillRect((int) Math.round(x), (int) Math.round(y), (int) Math.round(width), (int) Math.round(height));
					}
			}

		/**
		 * If the mouse is within the bounds of the {@link TButton},the
		 * <code>inUse</code> and <code>mouseOver</code> booleans are set to
		 * true.
		 */
		@Override
		public final void mousePressed(MouseEvent me)
			{
				if (!me.isConsumed() && me.getButton() == MouseEvent.BUTTON1)
					{
						// Checks to see if the mouse point is within the
						// TButton's area
						if (containsPoint(me.getPoint()))
							{
								this.inUse = true;
								this.mouseOver = true;
								me.consume();
							}
					}
			}

		/**
		 * If the {@link TButton} is <code>inUse</code>, this method checks to
		 * see if the mouse is still within the bounds of the {@link TButton}
		 */
		@Override
		public final void mouseDragged(MouseEvent me)
			{
				if (this.inUse)
					{
						if (containsPoint(me.getPoint()))
							this.mouseOver = true;
						else
							this.mouseOver = false;
					}
			}

		/**
		 * This method is called when a mouse button is released, if the mouse
		 * is still within the bounds of the {@link TButton} it sends an
		 * {@link ActionEvent} to the {@link InputListener} which in turn passes
		 * it onto the current {@link RenderableObject}. the <code>inUse</code>
		 * and <code>mouseOver</code> booleans are set to false.
		 */
		@Override
		public final void mouseReleased(MouseEvent me)
			{
				if (me.getButton() == MouseEvent.BUTTON1)
					{
						if (mouseOver)
							{
								sendActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
							}
						this.inUse = false;
						this.mouseOver = false;
					}
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
				setLabelBounds();
				sizeTButtonToLabel();
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
				setLabelBounds();
				sizeTButtonToLabel();
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
				setLabelBounds();
				sizeTButtonToLabel();
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
				setLabelBounds();
				sizeTButtonToLabel();
			}

		/**
		 * If <code>fitToLabel</code> is true, the {@link TButton}'s size will
		 * be set to match the size of the <code>label</code>.
		 * 
		 * @param fit
		 */
		public final void setFitToLabel(boolean fit)
			{
				this.fitToLabel = fit;
				sizeTButtonToLabel();
				reDraw = true;
			}

		/**
		 * This method sets the {@link TButton}'s <code>label</code>. Then if
		 * <code>fitToLabel</code> is true it re-sizes the {@link TButton}.
		 * 
		 * @param label
		 *            - The {@link String} the {@link TButton}'s
		 *            <code>label</code> will be set to.
		 */
		public final void setLabel(String label)
			{
				this.label = label;
				sizeTButtonToLabel();
				reDraw = true;
			}

		/**
		 * This method allows an image to be set for this {@link TButton}. The
		 * {@link BufferedImage} will represent the {@link TButton} and the
		 * {@link TButton} will be set to the size of the {@link BufferedImage}
		 * used.
		 * 
		 * @param image
		 *            - the image to be used to represent the {@link TButton}.
		 */
		public final void setImage(BufferedImage image)
			{
				this.image.set(image);
				this.width = image.getWidth();
				this.height = image.getHeight();
			}

		/**
		 * This method allows an image to be set for this {@link TButton}. The
		 * {@link BufferedImage} will represent the {@link TButton} and the
		 * {@link BufferedImage} and {@link TButton} will be set to the size of
		 * the parameters entered.
		 * 
		 * @param image
		 *            - the image the {@link TButton} will use.
		 * @param width
		 *            - the width the image will be scaled to.
		 * @param height
		 *            - the height the image will be scaled to.
		 */
		public final void scaleCurrentImage(double width, double height)
			{
				int widthInt = (int) Math.round(width);
				int heightInt = (int) Math.round(height);
				
				BufferedImage scaledImage = new BufferedImage(widthInt, heightInt, BufferedImage.TYPE_INT_ARGB);
				Graphics g = scaledImage.getGraphics();
				g.drawImage(image.get(), 0, 0, widthInt, heightInt, hub.renderer);
				g.dispose();

				this.image.set(scaledImage);
				this.width = widthInt;
				this.height = heightInt;
			}

		/**
		 * This method allows an image to be set for this {@link TButton}. The
		 * {@link BufferedImage} will represent the {@link TButton} and the
		 * {@link BufferedImage} and {@link TButton} will be set to the size of
		 * the parameters entered.
		 * 
		 * @param image
		 *            - the image the {@link TButton} will use.
		 * @param width
		 *            - the width the image will be scaled to.
		 * @param height
		 *            - the height the image will be scaled to.
		 */
		public final void setAndScaleImage(BufferedImage image, int width, int height)
			{
				BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				Graphics g = scaledImage.getGraphics();
				g.drawImage(image, 0, 0, width, height, hub.renderer);
				g.dispose();

				this.image.set(scaledImage);
				this.width = scaledImage.getWidth();
				this.height = scaledImage.getHeight();
			}

		/**
		 * This method rmoves the image from this {@link TButton}. The
		 * {@link TButton} is then re-sized to fit its label.
		 */
		public final void removeImage()
			{
				setLabelBounds();
				fitToLabel = true;
				reRenderTButton();
				reDraw = true;
			}

		/**
		 * This method rmoves the image from this {@link TButton}. The
		 * {@link TButton} is then re-sized to the parameters passed into the
		 * method.
		 * 
		 * 
		 * @param width
		 *            - the desired width of the {@link TButton}.
		 * @param height
		 *            - the desired height of the {@link TButton}.
		 */
		public final void removeImage(float width, float height)
			{
				setLabelBounds();
				setDimensions(width, height);
				reDraw = true;
			}

		/**
		 * This method allows the computer to calculate the physical size of the
		 * label, no matter what font is being used. The information is stored
		 * in a {@link Dimension} called <code>labelBounds</code>.
		 */
		private final void setLabelBounds()
			{
				Graphics g = new BufferedImage(hub.frame.startWidth, hub.frame.startHeight, BufferedImage.TYPE_INT_ARGB).getGraphics();
				g.setFont(font);

				labelBounds.setWidth(g.getFontMetrics().getStringBounds(label, g).getWidth());
				labelBounds.setHeight(g.getFontMetrics().getAscent() + g.getFontMetrics().getDescent());

				g.dispose();
			}

		/**
		 * The baseline is the line on which text is written, however letters
		 * often have tails below the line, and different {@link Font}s have
		 * different tail lengths. This length is called the 'descent' and is
		 * needed in order to calculate the physical {@link Dimension}s of the
		 * <code>label</code>, and therefore used to position the
		 * <code>label</code> in the center of the {@link TButton}.
		 * 
		 * @return - the distance from the baseline of a letter (e.g. 'j') to
		 *         the tip of it's tail.
		 */
		private final int getFontDescent()
			{
				Graphics g = new BufferedImage(hub.frame.startWidth, hub.frame.startHeight, BufferedImage.TYPE_INT_ARGB).getGraphics();
				g.setFont(font);

				return (int) g.getFontMetrics().getDescent();
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
		 * This method sizes the {@link TButton} to the size of its label, but
		 * only <code>if (fitToLabel == true)</code>.
		 */
		private final void sizeTButtonToLabel()
			{
				if (fitToLabel == true)
					{
						this.width = labelBounds.getWidth() + 6;
						this.height = labelBounds.getHeight() + 6;
					}
			}

		private final void reRenderTButton()
			{
				BufferedImage newImage = new BufferedImage((int) Math.round(width), (int) Math.round(height), BufferedImage.TYPE_INT_ARGB);

				Graphics g = newImage.getGraphics();

				g.drawImage(hub.images.tButtonIcons[TBUTTON], 0, 0, (int) Math.round(width), (int) Math.round(height), hub.renderer);

				// This code centers the label for the button, either
				// centered or to the right of the button if the label
				// does not fit inside the TButton.
				g.setColor(Color.BLACK);
				g.setFont(font);
				if (labelBounds.getWidth() + 6 > width || labelBounds.getHeight() + 6 > height)
					g.drawString(label, (int) Math.round(width + 3), (int) Math.round((height - ((height - labelBounds.getHeight()) / 2)) - getFontDescent()));
				else
					g.drawString(label, (int) Math.round(((width - labelBounds.getWidth()) / 2)),
							(int) Math.round((height - ((height - labelBounds.getHeight()) / 2)) - getFontDescent()));

				g.dispose();

				setImage(newImage);
				reDraw = false;
			}

		/**
		 * This method is not used by this class.
		 */
		@Override
		public void mouseMoved(MouseEvent paramMouseEvent)
			{
			}

		/**
		 * This method is not used by this class.
		 */
		@Override
		public void mouseClicked(MouseEvent paramMouseEvent)
			{
			}

		/**
		 * This method is not used by this class.
		 */
		@Override
		public void mouseEntered(MouseEvent paramMouseEvent)
			{
			}

		/**
		 * This method is not used by this class.
		 */
		@Override
		public void mouseExited(MouseEvent paramMouseEvent)
			{
			}

		/**
		 * This method is not used by this class.
		 */
		@Override
		public void keyTyped(KeyEvent e)
			{
			}

		/**
		 * This method is not used by this class.
		 */
		@Override
		public void keyPressed(KeyEvent e)
			{
			}

		/**
		 * This method is not used by this class.
		 */
		@Override
		public void keyReleased(KeyEvent e)
			{
			}

		/**
		 * This method is not used by this class.
		 */
		@Override
		public void mouseWheelMoved(MouseWheelEvent e)
			{
			}

		/**
		 * This method is not used by this class.
		 */
		@Override
		public void actionPerformed(ActionEvent e)
			{
			}
	}