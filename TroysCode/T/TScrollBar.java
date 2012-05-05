package TroysCode.T;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import javax.swing.event.EventListenerList;

import TroysCode.hub;

/**
 * This class creates a simple scroll bar, the bar measures where it is on the
 * scrollbar as a percentage, 0% meaning it is at the beginning of the
 * scrollbar, 100% indicating it is at the end, 50% in the middle ect.
 * <p>
 * To use the {@link TScrollBar}
 * <p>
 * This class throws a {@link TScrollEvent} which is picked up by the
 * {@link TComponentContainer} it is in.
 * 
 * @author Sebastian Troy
 */
public class TScrollBar extends TComponent implements Serializable
	{
		private static final long serialVersionUID = 1L;

		/**
		 * This constant represents the location of an image in the Tcomponents
		 * texture array
		 */
		public static final byte UPBUTTON = 0;
		/**
		 * This constant represents the location of an image in the Tcomponents
		 * texture array
		 */
		public static final byte DOWNBUTTON = 1;
		/**
		 * This constant represents the location of an image in the Tcomponents
		 * texture array
		 */
		public static final byte LEFTBUTTON = 2;
		/**
		 * This constant represents the location of an image in the Tcomponents
		 * texture array
		 */
		public static final byte RIGHTBUTTON = 3;
		/**
		 * This constant represents the location of an image in the Tcomponents
		 * texture array
		 */
		public static final byte SCROLLBUTTON = 4;

		/**
		 * The length of the {@link TScrollBar} includes the arrow
		 * {@link TButton}s at either end
		 */
		private double length = 0;

		/**
		 * This byte represents the orientation of the {@link TScrollBar}. It
		 * can either be <code>VERTICAL</code> or <code>HORIZONTAL</code>.
		 */
		protected byte orientation;

		/**
		 * This constant represents an orientation that a {@link TScrollBar} can
		 * have.
		 */
		public static final byte HORIZONTAL = 0;
		/**
		 * This constant represents an orientation that a {@link TScrollBar} can
		 * have.
		 */
		public static final byte VERTICAL = 1;

		/*
		 * Only three of these TButtons will ever be used for a particular
		 * instance of a TScrollBar. The ones used depend on the isVertical
		 * boolean above.
		 */
		private TButton up;
		private TButton down;
		private TButton left;
		private TButton right;
		protected TButton slider = null;

		// TODO general fixes for sliderSize variability
		private double sliderSize = 25;
		private double scrollDistance = 100;
		private Point lastMouseLocation = null;

		/**
		 * This Constructor allows you to set the position of the top left of
		 * the TScrollBar, it also allows you to set the isVertical boolean and
		 * the total length for the TScrollBar. Default texture images are used
		 * for the TButtons unless the constructor below is used to specify an
		 * array of images which can be used instead.
		 * 
		 * @param x
		 *            - The {@link TScrollBar}'s x position within the program's
		 *            frame.
		 * @param y
		 *            - The {@link TScrollBar}'s y position within the program's
		 *            frame.
		 * @param orientation
		 *            - The orientation of the {@link TScrollBar}. Either
		 *            <code>VERTICAL</code> or <code>HORIZONTAL</code>.
		 * @param length
		 *            - The total length of the {@link TScrollBar}.
		 */
		public TScrollBar(double x, double y, byte orientation, double length)
			{
				// width and height set later
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
				scrollDistance = length * 100;

				/*
				 * The code below creates the instances of the TButtons needed
				 * for the TScrollBar, using the default texture images.
				 */
				if (orientation == VERTICAL)
					{
						width = 25;
						height = length;
						up = new TButton(x, y, hub.images.tScrollBarIcons[UPBUTTON]);
						up.addActionListener(this);
						down = new TButton(x, y + (length - 25f), hub.images.tScrollBarIcons[DOWNBUTTON]);
						down.addActionListener(this);
						slider = new TButton(x, y + 25, hub.images.tScrollBarIcons[SCROLLBUTTON]);
					}
				else if (orientation == HORIZONTAL)
					{
						width = length;
						height = 25;
						left = new TButton(x, y, hub.images.tScrollBarIcons[LEFTBUTTON]);
						left.addActionListener(this);
						right = new TButton(x + (length - 25f), y, hub.images.tScrollBarIcons[RIGHTBUTTON]);
						right.addActionListener(this);
						slider = new TButton(x + 25, y, hub.images.tScrollBarIcons[SCROLLBUTTON]);
					}
				backgroundColour = Color.LIGHT_GRAY;
			}

		public TScrollBar(double x, double y, byte orientation, double length, BufferedImage[] images)
			{
				super(x, y, 0, 0);

				if (orientation == VERTICAL || orientation == HORIZONTAL)
					this.orientation = orientation;
				else
					this.orientation = VERTICAL;
				this.length = length;

				/*
				 * if length < 76 set length and this.length to 76, TScrollBars
				 * with lengths of 75 or less act strangely.
				 */
				this.length = length < 76 ? length = 76 : length;
				scrollDistance = length * 100;

				/*
				 * The code below creates the instances of the TButtons needed
				 * for the TScrollBar, using the array of images provided
				 * through the constructor, however there is a check to ensure
				 * the images are compatable first. If they are not it will
				 * default to the default texture.
				 */
				if (images == null || images.length != hub.images.tScrollBarIcons.length)
					images = hub.images.tScrollBarIcons;

				if (orientation == VERTICAL)
					{
						width = 25;
						height = length;
						up = new TButton(x, y, images[UPBUTTON]);
						up.addActionListener(this);
						down = new TButton(x, y + (length - 25f), images[DOWNBUTTON]);
						down.addActionListener(this);
						slider = new TButton(x, y + 25, images[SCROLLBUTTON]);
					}
				else if (orientation == HORIZONTAL)
					{
						width = length;
						height = 25;
						left = new TButton(x, y, images[LEFTBUTTON]);
						left.addActionListener(this);
						right = new TButton(x + (length - 25f), y, images[RIGHTBUTTON]);
						right.addActionListener(this);
						slider = new TButton(x + 25, y, images[SCROLLBUTTON]);
					}
				backgroundColour = Color.LIGHT_GRAY;
			}

		public final double getScrollPercent()
			{
				double scrollPercent = 0f;

				if (orientation == VERTICAL)
					scrollPercent = ((slider.y - (y + 25)) / (length - (50 + sliderSize))) * 100f;
				else if (orientation == HORIZONTAL)
					scrollPercent = ((slider.x - (x + 25)) / (length - (50 + sliderSize))) * 100f;

				return scrollPercent;
			}

		protected final void checkSliderPosition()
			{
				if (orientation == VERTICAL)
					{
						if (slider.y < y + 25)
							slider.y = y + 25;
						else if (slider.y > y + length - (25 + sliderSize))
							slider.y = y + length - (25 + sliderSize);
					}
				else if (orientation == HORIZONTAL)
					{
						if (slider.x < x + 25)
							slider.x = x + 25;
						else if (slider.x > x + length - (25 + sliderSize))
							slider.x = x + length - (25 + sliderSize);
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
				if (tComponentContainer.getParent() != null)
					{
						tComponentContainer.getParent().removeMouseListener(this);
						tComponentContainer.getParent().removeMouseMotionListener(this);
					}

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
						g.setColor(backgroundColour);
						g.fillRect((int) Math.round(x), (int) Math.round(y), 25, (int) Math.round(length));
						up.render(g);
						down.render(g);
						slider.render(g);
					}
				else if (orientation == HORIZONTAL)
					{
						g.setColor(backgroundColour);
						g.fillRect((int) Math.round(x), (int) Math.round(y), (int) Math.round(length), 25);
						left.render(g);
						right.render(g);
						slider.render(g);
					}
			}

		/**
		 * @param distance
		 *            - distance, in pixels, that the object the
		 *            {@link TScrollBar} is scrolling actually moves.
		 */
		public final void setScrollDistance(double distance)
			{
				distance = distance < 0 ? 0 : distance;
				scrollDistance = distance;
				adjustSliderSize();
			}

		private final void adjustSliderSize()
			{
				if (scrollDistance > length)
					sliderSize = (length - 50.0) * (length / scrollDistance);
				else
					sliderSize = length - 50.0;

				if (sliderSize < 20)
					sliderSize = 20;

				if (orientation == VERTICAL)
					slider.scaleCurrentImage(25, sliderSize);
				else if (orientation == HORIZONTAL)
					slider.scaleCurrentImage(sliderSize, 25);
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

				if (orientation == VERTICAL)
					{
						up.moveX(x);
						down.moveX(x);
					}
				else if (orientation == HORIZONTAL)
					{
						left.moveX(x);
						right.moveX(x);
					}
			}

		@Override
		public final void moveY(double y)
			{
				this.y += y;

				slider.moveY(y);

				if (orientation == VERTICAL)
					{
						up.moveY(y);
						down.moveY(y);
					}
				else if (orientation == HORIZONTAL)
					{
						left.moveY(y);
						right.moveY(y);
					}
			}

		@Override
		public final void movePosition(double x, double y)
			{
				this.x += x;
				this.y += y;

				slider.movePosition(x, y);

				if (orientation == VERTICAL)
					{
						up.movePosition(x, y);
						down.movePosition(x, y);
					}
				else if (orientation == HORIZONTAL)
					{
						left.movePosition(x, y);
						right.movePosition(x, y);
					}
			}

		/*
		 * This method allows the length of the TScrollBar to be changed.
		 */
		public final void setLength(double length)
			{
				/*
				 * If the length < 76 change this.length and length to 76.
				 * (Lengths of 75 and less show unusual behavior)
				 */
				length = length < 76 ? length = 76 : length;

				if (orientation == VERTICAL)
					{
						up.setPosition(x, y);
						down.setPosition(x, y + (length - 25));
						slider.moveY((getScrollPercent() / 100.0) * (length - this.length));
					}
				else if (orientation == HORIZONTAL)
					{
						left.setPosition(x, y);
						right.setPosition(x + (length - 25f), y);
						slider.moveX((getScrollPercent() / 100.0) * (length - this.length));
					}
				this.length = length;
			}

		/**
		 * @deprecated The width of the {@link TSlider} is set according to its
		 *             <code>length</code> and <code>orientation</code>. Use
		 *             <code>setLength()</code> instead.
		 */
		@Override
		public final void setWidth(double width)
			{
			}

		/**
		 * @deprecated The heigt of the {@link TSlider} is set according to its
		 *             <code>length</code> and <code>orientation</code>. Use
		 *             <code>setLength()</code> instead.
		 */
		@Override
		public final void setHeight(double height)
			{
			}

		/**
		 * @deprecated The dimensions of the {@link TSlider} are set according
		 *             to its <code>length</code> and <code>orientation</code>.
		 *             Use <code>setLength()</code> instead.
		 */
		@Override
		public final void setDimensions(double width, double height)
			{
			}

		/**
		 * This method passes on any {@link MouseEvent}s onto this scrollbar's
		 * {@link TButton}s so they can process them.
		 */
		@Override
		public final void mousePressed(MouseEvent me)
			{
				if (me.getButton() == 1)
					{
						if (containsPoint(me.getPoint()))
							{
								inUse = true;
								lastMouseLocation = me.getPoint();
							}

						if (orientation == VERTICAL)
							{
								up.mousePressed(me);
								down.mousePressed(me);
							}
						else if (orientation == HORIZONTAL)
							{
								left.mousePressed(me);
								right.mousePressed(me);
							}
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
							slider.moveY(me.getY() - lastMouseLocation.getY());
							lastMouseLocation = me.getPoint();
							checkSliderPosition();
							sendTScrollEvent(new TScrollEvent(this, TScrollEvent.TSCROLLBARSCROLLED, getScrollPercent(), 0));
						}
					else if (orientation == HORIZONTAL)
						{
							slider.moveX(me.getX() - lastMouseLocation.getX());
							lastMouseLocation = me.getPoint();
							checkSliderPosition();
							sendTScrollEvent(new TScrollEvent(this, TScrollEvent.TSCROLLBARSCROLLED, getScrollPercent(), 0));
						}
			}

		/**
		 * This method passes on any {@link MouseEvent}s onto this scrollbar's
		 * {@link TButton}s so they can process them.
		 */
		@Override
		public final void mouseReleased(MouseEvent me)
			{
				if (me.getButton() == 1)
					{
						inUse = false;

						if (orientation == VERTICAL)
							{
								up.mouseReleased(me);
								down.mouseReleased(me);
							}
						else if (orientation == HORIZONTAL)
							{
								left.mouseReleased(me);
								right.mouseReleased(me);
							}
						slider.mouseReleased(me);
					}
			}

		/**
		 * If this {@link TScrollBar} is the {@link TComponentContainer}
		 * .mostAppropriateScrollBar(), this {@link TScrollBar} is scrolled
		 * according to the distance the mouse wheel has been scrolled.
		 */
		public void mouseWheelMoved(MouseWheelEvent event)
			{
				if (orientation == VERTICAL)
					{
						slider.moveY((event.getWheelRotation() * 2.0) * (((length - 50) - sliderSize) / 100.0));
						checkSliderPosition();
						sendTScrollEvent(new TScrollEvent(this, TScrollEvent.TSCROLLBARSCROLLED, getScrollPercent(), 0));
					}
				else if (orientation == HORIZONTAL)
					{
						slider.moveX((event.getWheelRotation() * 2.0) * (((length - 50) - sliderSize) / 100.0));
						checkSliderPosition();
						sendTScrollEvent(new TScrollEvent(this, TScrollEvent.TSCROLLBARSCROLLED, getScrollPercent(), 0));
					}
			}

		/**
		 * This method picks up any {@link ActionEvent}s produced by this
		 * {@link TScrollBar}'s arrow buttons, and then adjusts the slider
		 * position accordingly.
		 */
		@Override
		public final void actionPerformed(ActionEvent event)
			{
				if (event.getSource() == up)
					slider.moveY(-((length - 50) - sliderSize) / 100.0);
				else if (event.getSource() == down)
					slider.moveY(((length - 50) - sliderSize) / 100.0);
				else if (event.getSource() == left)
					slider.moveX(-((length - 50) - sliderSize) / 100.0);
				else if (event.getSource() == right)
					slider.moveX(((length - 50) - sliderSize) / 100.0);

				checkSliderPosition();
				sendTScrollEvent(new TScrollEvent(this, TScrollEvent.TSCROLLBARSCROLLED, getScrollPercent(), 0));
			}

		/**
		 * This method can be used to set the <code>scrollPercent</code> of this
		 * {@link TScrollBar}.
		 */
		public final void setScrollPercent(double scrollPercent)
			{
				if (orientation == VERTICAL)
					{
						slider.setY((y + 25) + ((scrollPercent / 100.0) * ((length - 50.0) - sliderSize)));
						checkSliderPosition();
						sendTScrollEvent(new TScrollEvent(this, TScrollEvent.TSCROLLBARSCROLLED, getScrollPercent(), 0));
					}
				else if (orientation == HORIZONTAL)
					{
						slider.setX((x + 25) + ((scrollPercent / 100.0) * ((length - 50.0) - sliderSize)));
						checkSliderPosition();
						sendTScrollEvent(new TScrollEvent(this, TScrollEvent.TSCROLLBARSCROLLED, getScrollPercent(), 0));
					}
			}

		/**
		 * Not used by this class.
		 */
		@Override
		public void mouseMoved(MouseEvent paramMouseEvent)
			{
			}

		/**
		 * Not used by this class.
		 */
		@Override
		public void mouseClicked(MouseEvent paramMouseEvent)
			{
			}

		/**
		 * Not used by this class.
		 */
		@Override
		public void mouseEntered(MouseEvent paramMouseEvent)
			{
			}

		/**
		 * Not used by this class.
		 */
		@Override
		public void mouseExited(MouseEvent paramMouseEvent)
			{
			}

		/**
		 * Not used by this class.
		 */
		@Override
		public void keyTyped(KeyEvent e)
			{
			}

		/**
		 * Not used by this class.
		 */
		@Override
		public void keyPressed(KeyEvent e)
			{
			}

		/**
		 * Not used by this class.
		 */
		@Override
		public void keyReleased(KeyEvent e)
			{
			}

	}