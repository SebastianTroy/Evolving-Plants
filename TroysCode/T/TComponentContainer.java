package TroysCode.T;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.event.EventListenerList;

import TroysCode.InputListener;
import TroysCode.Tools;

/**
 * This class allows any classes which extend it to have {@link TComponent}s
 * added to them.
 * 
 * @author Sebastian Troy
 */
public abstract class TComponentContainer implements Serializable, MouseWheelListener, TScrollListener
	{
		private static final long serialVersionUID = 1L;

		/**
		 * This {@link Component} is set at the parent to this
		 * {@link TComponentContainer}. It is then set as the parent of any
		 * {@link TComponent}s which are added. Any {@link java.awt.AWTEvent}s
		 * picked up by the parent are passed onto any {@link TComponent}s
		 * inside this {@link TComponentContainer}.
		 */
		private Component parentComponent;

		/**
		 * This {@link ArrayList} holds any {@link TComponent}'s added to the
		 * {@link TComponentContainer}.
		 */
		private volatile ArrayList<TComponent> tComponents = new ArrayList<TComponent>();

		/**
		 * This {@link EventListenerList} is used to hold
		 * {@link ActionListeners}. When a {@link TComponent} is added to this
		 * {@link TComponentContainer} any {@link ActionListener}s it contains
		 * are automatically added to the new {@link TComponent}.
		 * <p>
		 * When a {@link TComponent} is used it will then fire an
		 * {@link ActionEvent} to each of the {@link ActionListener}s in this
		 * list.
		 */
		private EventListenerList eventListeners = new EventListenerList();

		public TComponentContainer()
			{
			}

		/**
		 * This method is used to set the parent {@link Component} for this
		 * {@link TComponentContainer}. It also sets the parent for any
		 * {@link TComponent}s it contains.
		 * <p>
		 * This allows {@link MouseEvent}s and {@link KeyEvent}s to be picked up
		 * by the {@link TComponent}s
		 * <p>
		 * <strong>Note that the parent can only be set once!</strong>
		 * 
		 * @param parentComponent
		 */
		public synchronized final void setParent(Component parentComponent)
			{
				if (this.parentComponent == null)
					{
						this.parentComponent = parentComponent;
						for (TComponent component : tComponents)
							component.setTComponentContainer(this);
					}
			}

		/**
		 * @return This {@link TComponentContainer}s parent {@link Component}.
		 */
		public final Component getParent()
			{
				return parentComponent;
			}

		/**
		 * This method adds an {@link ActionListener} to this classes
		 * {@link EventListenerList}. The {@link ActionListener}s in this class
		 * are automatically added to any {@link TComponent}s it holds.
		 * 
		 * @param listener
		 *            - The listener to be added to this class and all of the
		 *            {@link TComponents} within it.
		 */
		protected synchronized final void addActionListener(ActionListener listener)
			{
				eventListeners.add(ActionListener.class, listener);
				for (TComponent tComponent : tComponents)
					{
						tComponent.addActionListener(listener);
					}
			}

		/**
		 * Adds a {@link TComponent} to the {@link components} array. This
		 * method stops duplicate entries. It also sets the {@link TComponent}'s
		 * parent {@link Component}, this allows it to pick up events caught by
		 * the {@link Component}.
		 * 
		 * @param component
		 *            - The {@link TComponent} you wish to add.
		 */
		protected synchronized final void addTComponent(TComponent component)
			{
				if (component != null && !getTComponents().contains(component))
					{
						/*
						 * Only initiates the TComponent if it has not been
						 * initiated already and this class has had it's own
						 * parent set.
						 */
						if (component.tComponentContainer != this && parentComponent != null)
							{
								if (component.tComponentContainer != null)
									component.tComponentContainer.removeTComponent(component);

								ActionListener[] listeners = getEventListeners();
								for (ActionListener listener : listeners)
									component.addActionListener(listener);

								if (component.getClass() == TScrollBar.class || component.getClass() == TSlider.class || component.getClass() == TMultiSlider.class)
									component.addTScrollListener(this);

								component.setTComponentContainer(this);
							}
						tComponents.add(component);
					}
			}

		/**
		 * Removes a {@link TComponent} from the {@link components} array.
		 * 
		 * @param component
		 *            - The {@link TComponent} you wish to remove.
		 */
		protected synchronized final void removeTComponent(TComponent component)
			{
				component.removedFromTComponentContainer();
				tComponents.remove(component);
			}

		/**
		 * @return an array of {@link ActionListener}s which are being used by
		 *         this class.
		 */
		protected final ActionListener[] getEventListeners()
			{
				return eventListeners.getListeners(ActionListener.class);
			}

		private synchronized final ArrayList<TComponent> getTComponents()
			{
				TComponent[] components = new TComponent[tComponents.size()];
				tComponents.toArray(components);
				ArrayList<TComponent> components2 = new ArrayList<TComponent>();
				for (TComponent c : components)
					components2.add(c);
				return components2;
			}
		
		/**
		 * This method draws all of the {@link TComponents} which have been
		 * added to this {@link TComponentContainer}
		 * 
		 * @param g
		 *            - the graphics object the {@link TComponents} will be
		 *            drawn onto.
		 */
		protected synchronized final void renderTComponents(Graphics g)
			{
				for (TComponent tComponent : tComponents)
					{
						tComponent.render(g);
					}
			}

		/**
		 * This method is called whenever a {@link TScrollEvent} occours.
		 */
		@Override
		public abstract void tScrollBarScrolled(TScrollEvent event);

		/**
		 * When there is a {@link MouseWheelEvent} this method searches through
		 * the <code>tComponents</code> held by this {@link TComponentContainer}
		 * and finds the {@link TScrollBar} it thinks the event should be sent
		 * to. This is either the closets one, or if the event is over a
		 * {@link TComponent} which itself contains a {@link TScrollBar}.
		 * 
		 * @param event
		 *            - the {@link MouseWheelEvent} to be sent to a
		 *            {@link TScrollBar}.
		 * 
		 * @return The {@link TScrollBar} which the method has chosen to send
		 *         the {@link MouseWheelEvent} to.
		 */
		@Override
		public final void mouseWheelMoved(MouseWheelEvent e)
			{
				mouseWheelScrolled(e);

				// TODO make this mess better!
				TScrollBar scrollBar = null;
				double distanceToScrollBar = Double.MAX_VALUE;

				for (TComponent c : tComponents)
					{
						// If c is a TScrollBar
						if (c.getClass() == TScrollBar.class)
							{
								/*
								 * If ""nearestTScrollBar"" is null OR c is
								 * closer to the mouse then
								 * ""nearestTScrollBar"" set
								 * ""nearestTScrollBar"" to c.
								 */
								double distanceToC = Tools.getVectorLength(c.getNearestPoint(new TPoint(e.getPoint())), new TPoint(e.getPoint()));
								if (distanceToC < distanceToScrollBar)
									{
										scrollBar = (TScrollBar) c;
										distanceToScrollBar = distanceToC;
									}
							}
						/*
						 * If the mouse is over a TPanel send the event to the
						 * TPanel and stop searching for the nearest TScrollBar
						 */
						else if (c.getClass() == TMenu.class)
							{
								if (c.containsPoint(e.getPoint()))
									{
										distanceToScrollBar = 0;
										TMenu m = (TMenu) c;
										scrollBar = m.scrollBar;
									}
							}
					}
				scrollBar.mouseWheelMoved(e);
			}

		/**
		 * This method is called whenever the {@link InputListener} Class
		 * detects that the mouse wheel (if present) has been scrolled.
		 * 
		 * @param event
		 *            - The {@link MouseEvent} detected by the
		 *            {@link InputListener}
		 */
		protected abstract void mouseWheelScrolled(MouseWheelEvent event);
	}
