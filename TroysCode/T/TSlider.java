package TroysCode.T;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;

import TroysCode.Tools;
import TroysCode.hub;

public class TSlider extends TComponent implements Serializable, MouseListener, MouseMotionListener
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
		private boolean showIndex = true;

		private ArrayList<TButton> sliders = new ArrayList<TButton>(5);

		/*
		 * This Constructor allows you to set the position of the top left of
		 * the TScrollBar, it also allows you to set the isVertical boolean and
		 * the total length for the TScrollBar. Default texture images are used
		 * for the TButtons unless the constructor below is used to specify an
		 * array of images which can be used instead.
		 */
		public TSlider(float x, float y, byte orientation, float length, int numberOfSliders)
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
						for (int i = 0; i < numberOfSliders; i++)
							sliders.add(new TButton(x, y + ((length / (numberOfSliders + 1)) * (i + 1) - 12.5f), hub.images.tScrollBarIcons[SLIDERBUTTON]));
						width = 25;
						height = length;
					}
				else if (orientation == HORIZONTAL)
					{
						for (int i = 0; i < numberOfSliders; i++)
							sliders.add(new TButton(x + ((length / (numberOfSliders + 1)) * (i + 1) - 12.5f), y, hub.images.tScrollBarIcons[SLIDERBUTTON]));
						width = length;
						height = 25;
					}
			}

		public TSlider(float x, float y, byte orientation, float length, int numberOfSliders, BufferedImage[] images)
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
						for (int i = 0; i < numberOfSliders; i++)
							{
								sliders.add(new TButton(x, y + (i * (length / (numberOfSliders) + 1)), hub.images.tScrollBarIcons[SLIDERBUTTON]));
							}
						width = 25;
						height = length;
					}
				else if (orientation == HORIZONTAL)
					{
						for (int i = 0; i < numberOfSliders; i++)
							sliders.add(new TButton(x + ((length / (numberOfSliders + 1)) * (i + 1) - 12.5f), y, hub.images.tScrollBarIcons[SLIDERBUTTON]));
						width = length;
						height = 25;
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
						g.fillRect(Math.round(x + 11), Math.round(y), 3, Math.round(length));
					}
				else if (orientation == HORIZONTAL)
					{
						g.setColor(background);
						g.fillRect(Math.round(x), Math.round(y + 11), Math.round(length), 3);
					}
				int sliderIndex = 0;
				for (TButton slider : sliders)
					{
						slider.render(g);
						if (showIndex)
							{
								g.setColor(Color.BLACK);
								g.setFont(new Font(g.getFont().toString(), Font.ITALIC, 12));
								g.drawString("" + sliderIndex, Math.round(slider.x + 8), Math.round(slider.y + 17));
								sliderIndex++;
							}
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

				for (TButton slider : sliders)
					slider.moveX(x);
			}

		@Override
		public final void moveY(float y)
			{
				this.y += y;
				for (TButton slider : sliders)
					slider.moveY(y);
			}

		@Override
		public final void movePosition(float x, float y)
			{
				this.x += x;
				this.y += y;

				for (TButton slider : sliders)
					slider.movePosition(x, y);
			}

		/*
		 * This method allows the length of the TScrollBar to be changed.
		 */
		public final void setLength(float length)
			{
				length = length < 76 ? length = 76 : length;

				int sliderIndex = 0;
				for (TButton slider : sliders)
					{
						if (orientation == VERTICAL)
							{
								slider.setY((y + (getSliderPercent(sliderIndex) * length / 100f)) - 12.5f);
							}
						else if (orientation == HORIZONTAL)
							{
								slider.setX((x + (getSliderPercent(sliderIndex) * length / 100f)) - 12.5f);
							}
						sliderIndex++;
					}
				/*
				 * If the length < 76 change this.length and length to 76.
				 * (Lengths of 75 and less show unusual behavior)
				 */
				this.length = length;
//
//				for (TButton slider : sliders)
//					if (orientation == VERTICAL)
//						{
//							float scrollPercent = ((y - slider.getY()) / this.length) * 100f;
//
//							slider.setPosition(x, y + (scrollPercent * length));
//						}
//					else if (orientation == HORIZONTAL)
//						{
//							float scrollPercent = ((x - slider.getX()) / this.length) * 100f;
//
//							slider.setPosition(x + (scrollPercent * length), y);
//						}
//
//				/*
//				 * If the length < 26 change this.length and length to 26.
//				 * (Lengths of 26 and less show unusual behavior)
//				 */
//				this.length = length < 26 ? length = 26 : length;
			}

		/**
		 * @deprecated The width of the {@link TSlider} is set according to its
		 *             <code>length</code> and <code>orientation</code>.
		 */
		@Override
		public final void setWidth(float width)
			{
			}

		/**
		 * @deprecated The heigt of the {@link TSlider} is set according to its
		 *             <code>length</code> and <code>orientation</code>.
		 */
		@Override
		public final void setHeight(float height)
			{
			}

		/**
		 * @deprecated The dimensions of the {@link TSlider} are set according
		 *             to its <code>length</code> and <code>orientation</code>.
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
					for (TButton slider : sliders)
						slider.mousePressed(me);
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
				int sliderIndex = 0;
				for (TButton slider : sliders)
					{
						if (slider.inUse)
							if (orientation == VERTICAL)
								{
									slider.y = me.getY() - 12.5f;
									if (slider.y < y)
										slider.y = y;
									else if (slider.y > y + length - 25)
										slider.y = y + length - 25;
									sendTScrollEvent(new TScrollEvent(this, TScrollEvent.TSCROLLBARSCROLLED, getSliderPercent(sliderIndex)));
								}
							else if (orientation == HORIZONTAL)
								{
									slider.x = me.getX() - 12.5f;
									if (slider.x < x)
										slider.x = x;
									else if (slider.x > x + length - 25)
										slider.x = x + length - 25;
									sendTScrollEvent(new TScrollEvent(this, TScrollEvent.TSCROLLBARSCROLLED, getSliderPercent(sliderIndex)));
								}
						sliderIndex++;
					}
			}

		/*
		 * This method passes on any MouseEvents onto the TButtons so they can
		 * process them.
		 */
		@Override
		public final void mouseReleased(MouseEvent me)
			{
				for (TButton slider : sliders)
					slider.mouseReleased(me);
			}

		public final float getSliderPercent(int sliderIndex)
			{
				// Check sliderIndex is within array bounds,
				// if not create exception.
				if (sliderIndex < 0 || sliderIndex >= sliders.size())
					{
						Tools.errorWindow(new IndexOutOfBoundsException(
								"The index you passed into the 'getSliderPercent()' method is out of the bounds of the array! you passed in: " + sliderIndex
										+ ". The range is from 0 to " + (sliders.size() - 1)), "Exception thrown in TSlder Class");
						return 0f;
					}

				float scrollPercent = 0f;

				if (orientation == VERTICAL)
					scrollPercent = ((sliders.get(sliderIndex).y - y) / (length - 25f)) * 100f;
				else if (orientation == HORIZONTAL)
					scrollPercent = ((sliders.get(sliderIndex).x - x) / (length - 25f)) * 100f;

				return scrollPercent;
			}

		public final void setSliderPercent(int sliderIndex, float sliderPercent)
			{
				// Check sliderIndex is within array bounds,
				// if not create exception.
				if (sliderIndex < 0 || sliderIndex >= sliders.size())
					{
						Tools.errorWindow(new IndexOutOfBoundsException(
								"The index you passed into the 'getSliderPercent()' method is out of the bounds of the array! you passed in: " + sliderIndex
										+ ". The range is from 0 to " + (sliders.size() - 1)), "Exception thrown in TSlder Class");
					}

				if (orientation == VERTICAL)
					sliders.get(sliderIndex).setY(((length - 25f) * (sliderPercent / 100f)) + y);
				else if (orientation == HORIZONTAL)
					sliders.get(sliderIndex).setX(((length - 25f) * (sliderPercent / 100f)) + x);
			}

		public final void setShowIndexNumbers(boolean show)
			{
				showIndex = show;
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