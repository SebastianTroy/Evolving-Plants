package TroysCode.T;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.event.EventListenerList;

import TroysCode.SerializableBufferedImage;
import TroysCode.hub;

/**
 * This class holds a number of {@link TButtons}, in a similar way to the
 * {@link TCollection} class, however this class can also take care of the size
 * and positioning of the {@link TButtons}.
 * 
 * @author Sebastian Troy
 */
public class TMenu extends TComponent implements TScrollListener
	{
		private static final long serialVersionUID = 1L;

		public static final byte HORIZONTAL = 0;
		public static final byte VERTICAL = 1;

		private ArrayList<TButton> tButtons = new ArrayList<TButton>();
		private ArrayList<Boolean> resizeButtonArray = new ArrayList<Boolean>();

		private SerializableBufferedImage image;

		TScrollBar scrollBar;
		private boolean usingScrollBar = false;
		private int scrollModifier = 0;

		private byte orientation;
		private double borderSize = 10;
		private double buttonSpacing = 10;
		private double minButtonWidth = 0;
		private double minButtonHeight = 0;

		private double totalMenuLength = 0;

		public TMenu(double x, double y, double width, double height, byte orientation)
			{
				super(x, y, width, height);
				this.orientation = orientation;

				if (orientation == HORIZONTAL)
					scrollBar = new TScrollBar(x, y + (height - 25), TScrollBar.HORIZONTAL, width);
				else if (orientation == VERTICAL)
					scrollBar = new TScrollBar(x + (width - 25), y, TScrollBar.VERTICAL, height);

				scrollBar.addTScrollListener(this);

				backgroundColour = new Color(0, 0, 0, 0);

				image = new SerializableBufferedImage(new BufferedImage((int) Math.round(width), (int) Math.round(height), BufferedImage.TYPE_INT_ARGB));
			}

		/**
		 * This method tells the {@link TComponent} which
		 * {@link TComponentContainer} it has been added to. It then updates the
		 * {@link TComponentContainer} for each of the {@link TComponent}s it
		 * contains.
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

								if (usingScrollBar == true)
									tComponentContainer.removeTComponent(scrollBar);

								for (TButton b : tButtons)
									b.listenerList = new EventListenerList();
							}
						tComponentContainer = componentContainer;
						tComponentContainer.getParent().addMouseListener(this);
						tComponentContainer.getParent().addMouseMotionListener(this);

						ActionListener[] listeners = tComponentContainer.getEventListeners();
						for (ActionListener listener : listeners)
							for (TButton b : tButtons)
								b.addActionListener(listener);

						if (usingScrollBar == true)
							tComponentContainer.addTComponent(scrollBar);
					}
			}

		@Override
		protected final void removedFromTComponentContainer()
			{
				for (TButton b : tButtons)
					b.listenerList = new EventListenerList();

				tComponentContainer.removeTComponent(scrollBar);

				tComponentContainer = null;
			}

		public synchronized final void addTButton(TButton button, boolean resizeButton)
			{
				if (!tButtons.contains(button))
					{
						if (button.tComponentContainer != null)
							button.tComponentContainer.removeTComponent(button);

						if (tComponentContainer != null)
							{
								ActionListener[] listeners = tComponentContainer.getEventListeners();
								for (ActionListener listener : listeners)
									button.addActionListener(listener);
							}

						tButtons.add(button);
						resizeButtonArray.add(resizeButton);

						setMinimumButtonBounds();
						setMenuLayout();
						setButtonPositions();
					}
			}

		public synchronized final void removeTButton(TButton button)
			{
				tButtons.remove(button);

				setMinimumButtonBounds();
				setMenuLayout();
				setButtonPositions();
			}

		private synchronized final TButton[] getTButtons()
			{
				TButton[] tButtons = new TButton[this.tButtons.size()];
				this.tButtons.toArray(tButtons);
				return tButtons;
			}

		private final void setMinimumButtonBounds()
			{
				for (int i = 0; i < tButtons.size(); i++)
					{
						TButton b = tButtons.get(i);

						if (resizeButtonArray.get(i))
							if (orientation == HORIZONTAL)
								{
									if (b.width > minButtonWidth)
										minButtonWidth = b.width;

									b.height = height - (2 * borderSize);
								}
							else if (orientation == VERTICAL)
								{
									if (b.height > minButtonHeight)
										minButtonHeight = b.height;

									b.width = width - (2 * borderSize);
								}
					}
				calculateTotalMenuLength();
			}

		private final void setMenuLayout()
			{
				if (orientation == HORIZONTAL)
					{
						double buttonHeight = Math.max(minButtonHeight, (height - (2 * borderSize)) - (usingScrollBar ? 25 : 0));
						double buttonWidth = Math.max(buttonHeight * (0.8 / 4.0), minButtonWidth);

						double cumulativeWidth = borderSize;

						for (int i = 0; i < tButtons.size(); i++)
							{
								TButton b = tButtons.get(i);

								if (resizeButtonArray.get(i))
									{
										b.setFitToLabel(false);
										b.setHeight(buttonHeight);
										b.setWidth(buttonWidth);
									}
								b.setY(((height - (usingScrollBar ? 25 : 0)) - b.height) / 2);

								cumulativeWidth += b.width + buttonSpacing;
							}
					}

				else if (orientation == VERTICAL)
					{
						double buttonWidth = Math.max(minButtonWidth, (width - (2 * borderSize)) - (usingScrollBar ? 25 : 0));
						double buttonHeight = Math.max(buttonWidth * (0.8 / 4.0), minButtonHeight);

						double cumulativeHeight = borderSize;

						for (int i = 0; i < tButtons.size(); i++)
							{
								TButton b = tButtons.get(i);

								if (resizeButtonArray.get(i))
									{
										b.setFitToLabel(false);
										b.setHeight(buttonHeight);
										b.setWidth(buttonWidth);
									}

								b.setX(((width - (usingScrollBar ? 25 : 0)) - b.width) / 2);

								cumulativeHeight += b.height + buttonSpacing;
							}
					}
			}

		private final void calculateTotalMenuLength()
			{
				if (orientation == HORIZONTAL)
					{
						double buttonHeight = Math.max(minButtonHeight, height - (2 * borderSize));
						double buttonWidth = Math.max(buttonHeight * (0.8 / 4.0), minButtonWidth);

						double cumulativeWidth = borderSize;

						for (int i = 0; i < tButtons.size(); i++)
							{
								if (!resizeButtonArray.get(i))
									{
										TButton b = tButtons.get(i);
										cumulativeWidth += b.width + buttonSpacing;
									}
								else
									{
										cumulativeWidth += buttonWidth + buttonSpacing;
									}
							}

						totalMenuLength = cumulativeWidth - buttonSpacing + borderSize;

						usingScrollBar = totalMenuLength > this.width;
					}

				else if (orientation == VERTICAL)
					{
						double buttonWidth = Math.max(minButtonWidth, width - (2 * borderSize));
						double buttonHeight = Math.max(buttonWidth * (0.8 / 4.0), minButtonHeight);

						double cumulativeHeight = borderSize;

						for (int i = 0; i < tButtons.size(); i++)
							{
								if (!resizeButtonArray.get(i))
									{
										TButton b = tButtons.get(i);
										cumulativeHeight += b.height + buttonSpacing;
									}
								else
									{
										cumulativeHeight += buttonHeight + buttonSpacing;
									}
							}

						totalMenuLength = cumulativeHeight - buttonSpacing + borderSize;

						usingScrollBar = totalMenuLength > this.height;
					}
				scrollBar.setScrollDistance(totalMenuLength);

				setMenuLayout();

				if (usingScrollBar == true && tComponentContainer != null)
					tComponentContainer.addTComponent(scrollBar);
				if (usingScrollBar == false && scrollBar.tComponentContainer != null)
					tComponentContainer.removeTComponent(scrollBar);
			}

		private final void setButtonPositions()
			{
				if (orientation == HORIZONTAL)
					{
						double cumulativeWidth = borderSize;

						for (int i = 0; i < tButtons.size(); i++)
							{
								TButton b = tButtons.get(i);

								b.setX(cumulativeWidth - (usingScrollBar ? scrollModifier : 0));

								cumulativeWidth += b.width + buttonSpacing;
							}

					}

				else if (orientation == VERTICAL)
					{
						double cumulativeHeight = borderSize;

						for (int i = 0; i < tButtons.size(); i++)
							{
								TButton b = tButtons.get(i);

								b.setY(cumulativeHeight - (usingScrollBar ? scrollModifier : 0));

								cumulativeHeight += b.height + buttonSpacing;
							}
					}
			}

		/*
		 * If there is a background this draws it, and all it's embedded
		 * components
		 */
		@Override
		public final void render(Graphics g)
			{
				if (hub.DEBUG)
					{
						g.setColor(Color.DARK_GRAY);
						g.drawRect((int) Math.round(x), (int) Math.round(y), (int) Math.round(width), (int) Math.round(height));
					}

				Graphics2D g2d = (Graphics2D) image.get().getGraphics();

				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
				g2d.fill(new Rectangle2D.Double(0, 0, width, height));
				g2d.dispose();

				Graphics menuGraphics = image.get().getGraphics();

				menuGraphics.setColor(backgroundColour);
				if (orientation == VERTICAL)
					menuGraphics.fillRect(0, 0, (int) Math.round(width - (usingScrollBar ? 25 : 0)), (int) Math.round(height));
				else if (orientation == HORIZONTAL)
					menuGraphics.fillRect(0, 0, (int) Math.round(width), (int) Math.round(height - (usingScrollBar ? 25 : 0)));

				for (TButton button : getTButtons())
					button.render(menuGraphics);

				menuGraphics.dispose();

				g.drawImage(image.get(), (int) Math.round(x), (int) Math.round(y), hub.renderer);
			}

		/*
		 * The following move and set position methods allow the movement of all
		 * the TComponents in unison, this is what this class is mainly intended
		 * for.
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
		public final void setWidth(double width)
			{
				this.width = width;

				setMinimumButtonBounds();
				setMenuLayout();
				setButtonPositions();

				if (usingScrollBar && orientation == HORIZONTAL)
					scrollBar.setLength(width);

				image = new SerializableBufferedImage(new BufferedImage((int) Math.round(width), (int) Math.round(height), BufferedImage.TYPE_INT_ARGB));
			}

		@Override
		public final void setHeight(double height)
			{
				this.height = height;

				setMinimumButtonBounds();
				setMenuLayout();
				setButtonPositions();

				if (usingScrollBar && orientation == VERTICAL)
					scrollBar.setLength(height);

				image = new SerializableBufferedImage(new BufferedImage((int) Math.round(width), (int) Math.round(height), BufferedImage.TYPE_INT_ARGB));
			}

		public final void setButtonSpacing(double spacing)
			{
				buttonSpacing = spacing;
				setMenuLayout();
				setButtonPositions();
			}

		public final void setBorderSize(double border)
			{
				borderSize = border;
				setMenuLayout();
				setButtonPositions();
			}

		@Override
		public final void moveX(double x)
			{
				this.x += x;

				scrollBar.moveX(x);
			}

		@Override
		public final void moveY(double y)
			{
				this.y += y;

				scrollBar.moveY(y);
			}

		@Override
		public final void movePosition(double x, double y)
			{
				this.x += x;
				this.y += y;

				scrollBar.movePosition(x, y);
			}

		/**
		 * This method passes this kind of {@link Event} onto each of the
		 * {@link TComponent}s that this {@link TCollection} contains.
		 */
		@Override
		public final void mousePressed(MouseEvent m)
			{
				if (containsPoint(m.getPoint()))
					{
						MouseEvent m2 = new MouseEvent(m.getComponent(), m.getID(), m.getWhen(), m.getModifiers(), (int) (m.getX() - Math.round(x)),
								(int) (m.getY() - Math.round(y)), m.getClickCount(), m.isPopupTrigger());

						for (TButton tc : getTButtons())
							tc.mousePressed(m2);
					}
			}

		/**
		 * This method passes this kind of {@link Event} onto each of the
		 * {@link TComponent}s that this {@link TCollection} contains.
		 */
		@Override
		public final void mouseReleased(MouseEvent m)
			{
				MouseEvent m2 = new MouseEvent(m.getComponent(), m.getID(), m.getWhen(), m.getModifiers(), (int) (m.getX() - Math.round(x)),
						(int) (m.getY() - Math.round(y)), m.getClickCount(), m.isPopupTrigger());

				for (TButton tc : getTButtons())
					tc.mouseReleased(m2);
			}

		/**
		 * This method passes this kind of {@link Event} onto each of the
		 * {@link TComponent}s that this {@link TCollection} contains.
		 */
		@Override
		public final void mouseDragged(MouseEvent m)
			{
				MouseEvent m2 = new MouseEvent(m.getComponent(), m.getID(), m.getWhen(), m.getModifiers(), (int) (m.getX() - Math.round(x)),
						(int) (m.getY() - Math.round(y)), m.getClickCount(), m.isPopupTrigger());

				for (TButton tc : getTButtons())
					tc.mouseDragged(m2);
			}

		@Override
		public void tScrollBarScrolled(TScrollEvent event)
			{
				if (usingScrollBar)
					{
						scrollModifier = (int) ((scrollBar.getScrollPercent() / 100.0) * (totalMenuLength - (orientation == VERTICAL ? height : width)));
						setButtonPositions();
					}
			}

		public final void mouseWheelMoved(MouseWheelEvent event)
			{
				if (usingScrollBar)
					scrollBar.mouseWheelMoved(event);
			}

		@Override
		public final void actionPerformed(ActionEvent ae)
			{
			}

		@Override
		public void mouseMoved(MouseEvent me)
			{
			}

		@Override
		public final void mouseClicked(MouseEvent me)
			{
			}

		@Override
		public final void mouseEntered(MouseEvent me)
			{
			}

		@Override
		public final void mouseExited(MouseEvent me)
			{
			}

		@Override
		public final void keyPressed(KeyEvent ke)
			{
			}

		@Override
		public final void keyReleased(KeyEvent ke)
			{
			}

		@Override
		public final void keyTyped(KeyEvent ke)
			{
			}
	}
