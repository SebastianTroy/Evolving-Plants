package TroysCode.T;

import java.awt.Dimension;
import java.io.Serializable;

// TODO add java docs for this class
public class TDimension implements Serializable
	{
		private static final long serialVersionUID = 1L;

		private double width = 0;
		private double height = 0;

		public TDimension()
			{
			}

		public TDimension(double width, double height)
			{
				this.setWidth(width);
				this.setHeight(height);
			}
		
		public void setSize(double width, double height)
			{
				this.width = width;
				this.height = height;
			}
		
		public void setSize(Dimension dim)
			{
				this.width = dim.width;
				this.height = dim.height;
			}

		public void setWidth(double width)
			{
				this.width = width;
			}

		public void setHeight(double height)
			{
				this.height = height;
			}

		public double getWidth()
			{
				return width;
			}

		public double getHeight()
			{
				return height;
			}
	}
