package TroysCode.T;

import java.awt.AWTEvent;

public class TScrollEvent extends AWTEvent
	{
	private static final long serialVersionUID = 1L;

	public static final int TSCROLLBARSCROLLED = 1001;
	
	public int sliderIndex;
	public double scrollPercent;
	
	public TScrollEvent(Object source, int id, double scrollPercent, int sliderIndex)
		{
			super(source, id);
			this.scrollPercent = scrollPercent;
			this.sliderIndex = sliderIndex;
		}

	}
