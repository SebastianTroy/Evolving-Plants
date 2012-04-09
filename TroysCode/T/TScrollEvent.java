package TroysCode.T;

import java.awt.AWTEvent;

public class TScrollEvent extends AWTEvent
	{
	private static final long serialVersionUID = 1L;

	public static final int TSCROLLBARSCROLLED = 1001;
	
	public float scrollPercent;
	
	public TScrollEvent(Object source, int id, float scrollPercent)
		{
			super(source, id);
			this.scrollPercent = scrollPercent;
		}

	}
