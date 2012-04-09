package TroysCode.T;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import TroysCode.hub;

public class TScrollBar extends TComponent implements Serializable, MouseListener, MouseMotionListener, MouseWheelListener, ActionListener
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

		/*
		 * This class creates a simple scroll bar. The look of it can be
		 * modified by editing the the texture.png file located in the
		 * images\default file.
		 */

		/*
		 * The length of the TScrollBar includes the arrow TButtons at either
		 * end
		 */
		private float length = 0;

		/*
		 * This byte is used to check if the TScrollbar is vertical or
		 * horizontal. This difference is important for rendering and also for
		 * making sure the correct arrow TButtons are used.
		 */
		private byte orientation;

		public static final byte HORIZONTAL = 0;
		public static final byte VERTICAL = 1;

		private Color background = Color.LIGHT_GRAY;

		/*
		 * Only three of these TButtons will ever be used for a particular
		 * instance of a TScrollBar. The ones used depend on the isVertical
		 * boolean above.
		 */
		private TButton up;
		private TButton down;
		private TButton left;
		private TButton right;
		private TButton slider;

		/*
		 * This Constructor allows you to set the position of the top left of
		 * the TScrollBar, it also allows you to set the isVertical boolean and
		 * the total length for the TScrollBar. Default texture images are used
		 * for the TButtons unless the constructor below is used to specify an
		 * array of images which can be used instead.
		 */
		public TScrollBar(float x, float y, byte orientation, float length)
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
			}

		public TScrollBar(float x, float y, byte orientation, float length, BufferedImage[] images)
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
			}

		public final float getSliderPercent()
			{
				float scrollPercent = 0f;

				if (orientation == VERTICAL)
					scrollPercent = ((slider.y - (y + 25)) / (length - 75f)) * 100f;
				else if (orientation == HORIZONTAL)
					scrollPercent = ((slider.x - (x + 25)) / (length - 75f)) * 100f;

				return scrollPercent;
			}

		private final void checkSliderPosition()
			{
				if (orientation == VERTICAL)
					{
						if (slider.y < y + 25)
							slider.y = y + 25;
						else if (slider.y > y + length - 50)
							slider.y = y + length - 50;
					}
				else if (orientation == HORIZONTAL)
					{
						if (slider.x < x + 25)
							slider.x = x + 25;
						else if (slider.x > x + length - 50)
							slider.x = x + length - 50;
					}
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
						tComponentContainer.getParent().addMouseMotionListener(this);
						tComponentContainer.getParent().addMouseWheelListener(this);
					}
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
						g.fillRect(Math.round(x), Math.round(y), 25, Math.round(length));
						up.render(g);
						down.render(g);
						slider.render(g);
					}
				else if (orientation == HORIZONTAL)
					{
						g.setColor(background);
						g.fillRect(Math.round(x), Math.round(y), Math.round(length), 25);
						left.render(g);
						right.render(g);
						slider.render(g);
					}
			}

		/*
		 * All of the methods which are used to move the TScrollBar around have
		 * to be overridden, this is because the positions of each of the
		 * TButtons have to be updated too.
		 */
		@Override
		public final void setX(float x)
			{
				float diffX = x - this.x;
				this.moveX(diffX);
			}

		@Override
		public final void setY(float y)
			{
				float diffY = y - this.y;
				this.moveY(diffY);
			}

		@Override
		public final void setPosition(float x, float y)
			{
				float diffX = x - this.x;
				float diffY = y - this.y;
				this.movePosition(diffX, diffY);
			}

		@Override
		public final void moveX(float x)
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
		public final void moveY(float y)
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
		public final void movePosition(float x, float y)
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
		public final void setLength(float length)
			{
				length = length < 76 ? length = 76 : length;

				if (orientation == VERTICAL)
					{
						up.setPosition(x, y);
						down.setPosition(x, y + (length - 25f));
						slider.setY(y + (getSliderPercent() * length / 100f) - 12.5f);
					}
				else if (orientation == HORIZONTAL)
					{
						left.setPosition(x, y);
						right.setPosition(x + (length - 25f), y);
						slider.setX(x + (getSliderPercent() * length / 100f) - 12.5f);
					}
				/*
				 * If the length < 76 change this.length and length to 76.
				 * (Lengths of 75 and less show unusual behavior)
				 */
				this.length = length;
			}

		/**
		 * @deprecated The width of the {@link TSlider} is set according to its
		 *             <code>length</code> and <code>orientation</code>. Use
		 *             <code>setLength()</code> instead.
		 */
		@Override
		public final void setWidth(float width)
			{
			}

		/**
		 * @deprecated The heigt of the {@link TSlider} is set according to its
		 *             <code>length</code> and <code>orientation</code>. Use
		 *             <code>setLength()</code> instead.
		 */
		@Override
		public final void setHeight(float height)
			{
			}

		/**
		 * @deprecated The dimensions of the {@link TSlider} are set according
		 *             to its <code>length</code> and <code>orientation</code>.
		 *             Use <code>setLength()</code> instead.
		 */
		@Override
		public final void setDimensions(float width, float height)
			{
			}

		/**
		 * This method sets the colour of the {@link TSlider}s bar.
		 * 
		 * @param colour
		 *            - the colour that the bar will be set to.
		 */
		public final void setBackgroundColour(Color colour)
			{
				background = colour;
			}

		/*
		 * This method passes on any MouseEvents onto the TButtons so they can
		 * process them.
		 */
		@Override
		public final void mousePressed(MouseEvent me)
			{
				if (me.getButton() == 1)
					{
						if (orientation == VERTICAL)
							{
								up.mousePressed(me);
								down.mousePressed(me);
							}
						else if (orientation == VERTICAL)
							{
								left.mousePressed(me);
								right.mousePressed(me);
							}
						slider.mousePressed(me);
					}
			}

		/*
		 * If the slider is active (i.e. the mouse is being held down over it),
		 * this method calculates the ""scrollPercent"" then moves the slider to
		 * it's new position. It does it this way round (i.e. instead of moving
		 * the slider and then calculating the scrollPercentage) so it can make
		 * sure the slider is never moved outside it's range.
		 */
		@Override
		public final void mouseDragged(MouseEvent me)
			{
				if (slider.inUse)
					if (slider.inUse)
						if (orientation == VERTICAL)
							{
								slider.y = me.getY() - 12.5f;
								checkSliderPosition();
								sendTScrollEvent(new TScrollEvent(this, TScrollEvent.TSCROLLBARSCROLLED, getSliderPercent()));
							}
						else if (orientation == HORIZONTAL)
							{
								slider.x = me.getX() - 12.5f;
								checkSliderPosition();
								sendTScrollEvent(new TScrollEvent(this, TScrollEvent.TSCROLLBARSCROLLED, getSliderPercent()));
							}
			}

		/*
		 * This method passes on any MouseEvents onto the TButtons so they can
		 * process them.
		 */
		@Override
		public final void mouseReleased(MouseEvent me)
			{
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

		/*
		 * This method allows scrolling using a mouse wheel.
		 */
		@Override
		public final void mouseWheelMoved(MouseWheelEvent event)
			{
				if (tComponentContainer.mostAppropriateScrollBar(event) == this)
					{
						if (orientation == VERTICAL)
							{
								slider.moveY(event.getWheelRotation());
								checkSliderPosition();
								sendTScrollEvent(new TScrollEvent(this, TScrollEvent.TSCROLLBARSCROLLED, getSliderPercent()));
							}
						else if (orientation == HORIZONTAL)
							{
								slider.moveX(event.getWheelRotation());
								checkSliderPosition();
								sendTScrollEvent(new TScrollEvent(this, TScrollEvent.TSCROLLBARSCROLLED, getSliderPercent()));
							}
					}
			}

		/*
		 * This method picks up any ActionEvents produced by the arrow TButtons
		 * and then adjusts the slider position.
		 */
		@Override
		public final void actionPerformed(ActionEvent event)
			{
				if (event.getSource() == up)
					slider.moveY(-(length - 75f) / 100f);
				else if (event.getSource() == down)
					slider.moveY((length - 75f) / 100f);
				else if (event.getSource() == left)
					slider.moveX(-(length - 75f) / 100f);
				else if (event.getSource() == right)
					slider.moveX((length - 75f) / 100f);

				checkSliderPosition();
				sendTScrollEvent(new TScrollEvent(this, TScrollEvent.TSCROLLBARSCROLLED, getSliderPercent()));
			}

		/*
		 * This method can be used to set
		 */
		public final void setScrollPercent(float scrollPercent)
			{
				if (orientation == VERTICAL)
					{
						slider.setY(((scrollPercent * (length - 75f)) / 100f) + 12.5f);
						checkSliderPosition();
						sendTScrollEvent(new TScrollEvent(this, TScrollEvent.TSCROLLBARSCROLLED, getSliderPercent()));
					}
				else if (orientation == HORIZONTAL)
					{
						slider.setX(((scrollPercent * (length - 75f)) / 100f) + 12.5f);
						checkSliderPosition();
						sendTScrollEvent(new TScrollEvent(this, TScrollEvent.TSCROLLBARSCROLLED, getSliderPercent()));
					}
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

	}