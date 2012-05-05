package TroysCode.T;

import java.awt.Point;
import java.io.Serializable;

/**
 * I made this class because I dislike {@link Point} only working to integer
 * accuracy.
 * 
 * @author Sebastian Troy
 */
public class TPoint implements Serializable
	{
		private static final long serialVersionUID = 1L;

		private double x = 0;
		private double y = 0;

		/**
		 * Creates a new {@link TPoint} with the co-ordinates (0,0).
		 */
		public TPoint()
			{
			}

		/**
		 * Creates a new {@link TPoint} with the co-ordinates (0,0).
		 * 
		 * @param x
		 *            - the x co-ordinate of the {@link TPoint}.
		 * @param y
		 *            - the x co-ordinate of the {@link TPoint}.
		 */
		public TPoint(double x, double y)
			{
				this.x = x;
				this.y = y;
			}

		/**
		 * Creates a new {@link TPoint} from a {@link Point}.
		 * 
		 * @param p
		 *            - The {@link Point} this {@link TPoint} will get its
		 *            co-ordinates from.
		 */
		public TPoint(Point p)
			{
				this.x = p.x;
				this.y = p.y;
			}

		/**
		 * This method sets both of the co-ordinates of the {@link TPoint}.
		 * 
		 * @param x
		 *            - the new x co-ordinate for this {@link TPoint}.
		 * @param y
		 *            - the new y co-ordinate for this {@link TPoint}.
		 */
		public void setLocation(double x, double y)
			{
				this.x = x;
				this.y = y;
			}

		/**
		 * This method sets the co-ordinates of this {@link TPoint} to the same
		 * as another {@link TPoint}.
		 * 
		 * @param point
		 *            - the {@link TPoint} from which the co-ordinates for this
		 *            {@link TPoint} will be taken.
		 */
		public void setLocation(TPoint point)
			{
				this.x = point.getX();
				this.y = point.getY();
			}

		/**
		 * This method converts a {@link Point} into a {@link TPoint}.
		 * 
		 * @param point
		 *            - the {@link Point} to be converted into a {@link TPoint}.
		 */
		public void setLocation(Point point)
			{
				this.x = point.getX();
				this.y = point.getY();
			}

		/**
		 * This method sets the x co-ordinate of the {@link TPoint}.
		 * 
		 * @param x
		 *            - the new x co-ordinate for this {@link TPoint}.
		 */
		public void setX(double x)
			{
				this.x = x;
			}

		/**
		 * This method sets the y co-ordinate of the {@link TPoint}.
		 * 
		 * @param y
		 *            - the new y co-ordinate for this {@link TPoint}.
		 */
		public void setY(double y)
			{
				this.y = y;
			}

		/**
		 * @return - The x co-ordinate for this {@link TPoint}.
		 */
		public double getX()
			{
				return x;
			}

		/**
		 * @return - The y co-ordinate for this {@link TPoint}.
		 */
		public double getY()
			{
				return y;
			}

		/**
		 * Returns a string representation of this {@link TPoint} and its location in the
		 * {@code (x,y)} coordinate space. This method is intended to be used
		 * only for debugging purposes, and the content and format of the
		 * returned string may vary between implementations. The returned string
		 * may be empty but may not be <code>null</code>.
		 * 
		 * @return a {@link String} representation of this point
		 */
		public String toString()
			{
				return getClass().getName() + "[x=" + x + ",y=" + y + "]";
			}
	}
