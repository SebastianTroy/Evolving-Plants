package TroysCode.T;

import java.util.EventListener;

public interface TScrollListener extends EventListener
	{
		public abstract void tScrollBarScrolled(TScrollEvent event);
	}
