package TroysCode.T;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import javax.swing.event.EventListenerList;

import TroysCode.hub;

public class TSlider extends TComponent implements Serializable
	{
		private static final long serialVersionUID = 1L;

		/**
		 * This constant represents the location of an image in the Tcomponents
		 * texture array
		 */
		public static final byte SLIDERBUTTON = 5;

		/*
		 * This class creates a simple scroll bar. The look of it can be
		 * modified by editing the the texture.png file located in the
		 * images\default file.
		 */

		/*
		 * The length of the TScrollBar includes the arrow TButtons at either
		 * end
		 */
		private double length = 0;

		/*
		 * This byte is used to check if the TScrollbar is vertical or
		 * horizontal. This difference is important for rendering and also for
		 * making sure the correct arrow TButtons are used.
		 */
		private byte orientation;

		public static final byte HORIZONTAL = 0;
		public static final byte VERTICAL = 1;

		private Color background = Color.LIGHT_GRAY;

		TButton slider;

		/*
		 * This Constructor allows you to set the position of the top left of
		 * the TScrollBar, it also allows you to set the isVertical boolean and
		 * the total length for the TScrollBar. Default texture images are used
		 * for the TButtons unless the constructor below is used to specify an
		 * array of images which can be used instead.
		 */
		public TSlider(double x, double y, byte orientation, double length)
			{
				super(x, y, 0, 0);
				if (orientation == VERTICAL || orientation == HORIZONTAL)
					this.orientation = orientation;
				else
					this.orientation = VERTICAL;
				/*
				 * if length < 76 set length and this.length to 76, TScrollBars
				 * with lengths of 75 or less act strangely.
				 */
				this.length = length < 76 ? length = 76 : length;

				/*
				 * The code below creates the instances of the TButtons needed
				 * for the TScrollBar, using the default texture images.
				 */
				if (orientation == VERTICAL)
					{
						slider = new TButton(x, y + (length / 2) - 12.5, hub.images.tScrollBarIcons[SLIDERBUTTON]);
						width = 25;
						height = length;
					}
				else if (orientation == HORIZONTAL)
					{
						slider = new TButton(x + (length / 2) - 12.5, y, hub.images.tScrollBarIcons[SLIDERBUTTON]);
						width = length;
						height = 25;
					}
			}

		public TSlider(double x, double y, byte orientation, double length, BufferedImage image)
			{
				super(x, y, 0, 0);

				if (orientation == VERTICAL || orientation == HORIZONTAL)
					this.orientation = orientation;
				else
					this.orientation = VERTICAL;
				this.length = length;

				/*
				 * The code below creates the instances of the TButtons needed
				 * for the TScrollBar, using the array of images provided
				 * through the constructor, however there is a check to ensure
				 * the images are compatable first. If they are not it will
				 * default to the default texture.
				 */
				if (image.getWidth() > 25 || image.getHeight() > 25)
					image = hub.images.tScrollBarIcons[SLIDERBUTTON];

				if (orientation == VERTICAL)
					{
						slider = new TButton(x, y + (length / 2) - 12.5, image);
						width = 25;
						height = length;
					}
				else if (orientation == HORIZONTAL)
					{
						slider = new TButton(x + (length / 2) - 12.5, y, image);
						width = length;
						height = 25;
					}
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

				listenerList = new EventListenerList();
				tComponentContainer = null;
			}

		/*
		 * The rendering of the TScrollBar is simple, with a slight variation if
		 * it is vertical or horizontal. Most of the rendering is taken care of
		 * by the TButton class.
		 */
		@Override
		public final void render(Graphics g)
			{
				if (orientation == VERTICAL)
					{
						g.setColor(background);
						g.fillRect((int) Math.round(x + 11), (int) Math.round(y), 3, (int) Math.round(length));
					}
				else if (orientation == HORIZONTAL)
					{
						g.setColor(background);
						g.fillRect((int) Math.round(x), (int) Math.round(y + 11), (int) Math.round(length), 3);
					}

				slider.render(g);
			}

		/*
		 * All of the methods which are used to move the TScrollBar around have
		 * to be overridden, this is because the positions of each of the
		 * TButtons have to be updated too.
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
				slider.moveX(x);
			}

		@Override
		public final void moveY(double y)
			{
				this.y += y;
				slider.moveY(y);
			}

		@Override
		public final void movePosition(double x, double y)
			{
				this.x += x;
				this.y += y;

				slider.movePosition(x, y);
			}

		/*
		 * This method allows the length of the TScrollBar to be changed.
		 */
		public final void setLength(double length)
			{
				/*
				 * If the length < 26 change this.length and length to 26.
				 * (Lengths of 26 and less show unusual behavior)
				 */
				length = length < 76 ? length = 76 : length;

				if (orientation == VERTICAL)
					{
						slider.moveY((getSliderPercent() / 100.0) * (length - this.length));
					}
				else if (orientation == HORIZONTAL)
					{
						slider.moveX((getSliderPercent() / 100.0) * (length - this.length));
					}
				this.length = length;
			}

		/**
		 * @deprecated The width of the {@link TSlider} is set according to its
		 *             <code>length</code> and <code>orientation</code>.
		 */
		@Override
		public final void setWidth(double width)
			{
			}

		/**
		 * @deprecated The heigt of the {@link TSlider} is set according to its
		 *             <code>length</code> and <code>orientation</code>.
		 */
		@Override
		public final void setHeight(double height)
			{
			}

		/**
		 * @deprecated The dimensions of the {@link TSlider} are set according
		 *             to its <code>length</code> and <code>orientation</code>.
		 */
		@Override
		public final void setDimensions(double width, double height)
			{
			}

		/**
		 * This method passes on any {@link MouseEvent}s onto this slider's
		 * {@link TButton}s so they can process them.
		 */
		@Override
		public final void mousePressed(MouseEvent me)
			{
				if (me.getButton() == 1)
					{
						if (containsPoint(me.getPoint()))
							inUse = true;

						slider.mousePressed(me);
					}
			}

		/**
		 * If the slider button is <code>inUse</code> (i.e. the mouse is being
		 * held down within its bounds), this method calculates the
		 * <code>scrollPercent</code> then moves the slider to it's new
		 * position.
		 */
		@Override
		public final void mouseDragged(MouseEvent me)
			{
				if (slider.inUse)
					if (orientation == VERTICAL)
						{
							slider.y = me.getY() - 12.5f;
							if (slider.y < y)
								slider.y = y;
							else if (slider.y > y + length - 25)
								slider.y = y + length - 25;
							sendTScrollEvent(new TScrollEvent(this, TScrollEvent.TSCROLLBARSCROLLED, getSliderPercent(), 0));
						}
					else if (orientation == HORIZONTAL)
						{
							slider.x = me.getX() - 12.5f;
							if (slider.x < x)
								slider.x = x;
							else if (slider.x > x + length - 25)
								slider.x = x + length - 25;
							sendTScrollEvent(new TScrollEvent(this, TScrollEvent.TSCROLLBARSCROLLED, getSliderPercent(), 0));
						}
			}

		/**
		 * This method passes on any {@link MouseEvent}s onto this slider's
		 * {@link TButton}s so they can process them.
		 */
		@Override
		public final void mouseReleased(MouseEvent me)
			{
				if (me.getButton() == 1)
					{
						inUse = false;

						slider.mouseReleased(me);
					}
			}

		public final double getSliderPercent()
			{
					double scrollPercent = 0f;

				if (orientation == VERTICAL)
					scrollPercent = ((slider.y - y) / (length - 25f)) * 100f;
				else if (orientation == HORIZONTAL)
					scrollPercent = ((slider.x - x) / (length - 25f)) * 100f;

				return scrollPercent;
			}

		public final void setSliderPercent(double sliderPercent)
			{
				if (sliderPercent < 0)
					sliderPercent = 0;
				else if (sliderPercent > 100)
					sliderPercent = 100;

				if (orientation == VERTICAL)
					slider.setY(((length - 25f) * (sliderPercent / 100f)) + y);
				else if (orientation == HORIZONTAL)
					slider.setX(((length - 25f) * (sliderPercent / 100f)) + x);

				sendTScrollEvent(new TScrollEvent(this, TScrollEvent.TSCROLLBARSCROLLED, getSliderPercent(), 0));
			}

		@Override
		public void mouseMoved(MouseEvent paramMouseEvent)
			{
			}

		@Override
		public void mouseClicked(MouseEvent paramMouseEvent)
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
		public void keyTyped(KeyEvent e)
			{
			}

		@Override
		public void keyPressed(KeyEvent e)
			{
			}

		@Override
		public void keyReleased(KeyEvent e)
			{
			}

		@Override
		public void actionPerformed(ActionEvent e)
			{
			}
	}