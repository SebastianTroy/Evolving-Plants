package TroysCode;

import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * This class extends {@link JFrame}, it is responsible for initialising the
 * frame at the beginning of the program.
 * 
 * @author Sebastian Troy
 */
public class Frame extends JFrame
	{
		private static final long serialVersionUID = 1L;

		/**
		 * The width the program frame is initialised to
		 */
		public int startWidth = 1200;
		/**
		 * The height the program frame is initialised to
		 */
		public int startHeight = 600;

		public Frame()
			{
			}

		/**
		 * This method is called from the {@link hub} once all of the vital
		 * classes have been initialised.
		 * <p>
		 * Any modifications you wish to do to the frame should be made here.
		 */
		public final void startFrame()
			{
				// This sets the title, first the name stated in the hub, then
				// it adds the game version and finally the DEBUG status
				setTitle(hub.programName + ",  version " + hub.versionNumber + "   |   Powered by http://troyscode.blogspot.com   |   "
						+ (hub.DEBUG ? "[DEBUG MODE]" : ""));

				// this sets the images used on the task bar and the title bar
				setIconImages(hub.images.icons);

				// this sets the size of the frame
				setSize(startWidth, startHeight);

				// We want to stop the window closing as soon as exit is
				// selected, this way we can add a confirmation "dialouge box"
				setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

				// Detects WindowEvents
				addWindowListener(new WindowAdapter()
					{
						// Open an exit dialogue box if the user tries to exit
						public void windowClosing(WindowEvent event)
							{
								Tools.exitWindow("Are you sure you want to Exit?");
							}
					});

				// Detects ComponentEvents
				addComponentListener(new ComponentListener()
					{

						@Override
						public void componentResized(ComponentEvent event)
							{
								hub.renderer.getRenderableObject().frameResized(event);
							}

						@Override
						public void componentMoved(ComponentEvent event)
							{
							}

						@Override
						public void componentShown(ComponentEvent event)
							{
							}

						@Override
						public void componentHidden(ComponentEvent event)
							{
							}

					});

				/*
				 * This means the user cannot re-size the window while the
				 * program is running, change to boolean to true if you
				 */
				setResizable(false);

				/*
				 * This is just the frame, to actually put something visual
				 * iside it we want to add a canvas, so we can draw onto it. The
				 * ""Renderer"" class is the canvas so we add it here.
				 */
				add(hub.renderer);

				// The toolkit allows us to center our window on our screen
				Toolkit toolkit = Toolkit.getDefaultToolkit();

				int x = (int) (toolkit.getScreenSize().getWidth() / 2) - (startWidth / 2);
				int y = (int) (toolkit.getScreenSize().getHeight() / 2) - (startHeight / 2);
				this.setLocation(x, y);

				// finally (and most importantly) we make the JFrame visible
				this.setVisible(true);
			}
	}