package TroysCode;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import TroysCode.T.TButton;
import TroysCode.T.TComponent;
import TroysCode.T.TScrollBar;
import TroysCode.T.TSlider;

/**
 * The {@link Images} Class loads from file and holds images in the form of
 * {@link BufferedImage}'s
 * <p>
 * Declare and load any images you wish to use, in your program, in this class.
 * 
 * @author Sebastian Troy
 */
public class Images
	{
		/**
		 * the directory for the images folder
		 */
		private static final String imageDir = "/images";
		/**
		 * directory for the texture folder, change this in 'hub'!
		 */
		private static final String textureDir = imageDir + "/" + hub.textureFolderName;

		/*
		 * These images declarations are used for Troy's Code textures,
		 */
		// #########################################################################
		public ArrayList<BufferedImage> icons = new ArrayList<BufferedImage>();// ##
		public BufferedImage[] tButtonIcons = new BufferedImage[2];// ////////////##
		public BufferedImage[] tScrollBarIcons = new BufferedImage[6];// /////////##
		// #########################################################################
		
		/*
		 * Place your own Image declarations below here.
		 */

		/**
		 * This method loads images specified by you for the program, see the
		 * tutorial on the wiki for more info:
		 * <p>
		 * https ://github.com/SebastianTroy/Troy-s-Code/wiki/_pages
		 * 
		 * @throws IOException
		 */
		private final void loadImages() throws IOException
			{
			}

		/**
		 * When an instance of the {@link Images} class is created, it
		 * automatically loads all of the Images needed for the program
		 */
		public Images()
			{
				reLoadImages();
			}

		/**
		 * This method can be called to completely reload the images used for
		 * the program.
		 * <p>
		 * This may be desirable if the <code> textureFolderName </code> in
		 * {@link hub} is edited.
		 */
		public final void reLoadImages()
			{
				try
					{
						loadImages();
						loadTroysCodeImages();
					}
				catch (IOException IOe)
					{
						Tools.errorWindow(IOe, "IO exception at loadImages");
					}
			}

		/*
		 * This method has been made seperate simply to keep these statements
		 * out of the way. Any modification to these images should be done by
		 * editing the image files only!
		 */
		/**
		 * This method loads images from the texture file indicated in the
		 * {@link hub}. This method does not need to be modified in order to
		 * change the {@link TComponent} textures.
		 * 
		 * @throws IOException
		 */
		private final void loadTroysCodeImages() throws IOException
			{
				icons.add(ImageIO.read(Images.class.getResource(textureDir + "/titleBar.png")));
				icons.add(ImageIO.read(Images.class.getResource(textureDir + "/taskBar.png")));

				BufferedImage textureSheet = ImageIO.read(Images.class.getResource(textureDir + "/defaultTexture.png"));

				tButtonIcons[TButton.TBUTTON] = getCroppedImage(textureSheet, 100, 0, 100, 40);

				tScrollBarIcons[TScrollBar.UPBUTTON] = getCroppedImage(textureSheet, 50, 0, 25, 25);
				tScrollBarIcons[TScrollBar.DOWNBUTTON] = getCroppedImage(textureSheet, 75, 0, 25, 25);
				tScrollBarIcons[TScrollBar.LEFTBUTTON] = getCroppedImage(textureSheet, 0, 0, 25, 25);
				tScrollBarIcons[TScrollBar.RIGHTBUTTON] = getCroppedImage(textureSheet, 25, 0, 25, 25);
				tScrollBarIcons[TScrollBar.SCROLLBUTTON] = getCroppedImage(textureSheet, 0, 25, 25, 25);
				tScrollBarIcons[TSlider.SLIDERBUTTON] = getCroppedImage(textureSheet, 25, 25, 25, 25);
			}

		/**
		 * The method returns a cropped copy of a {@link BufferedImage}. You can
		 * use this method to obtain many small {@link BufferedImage}'s from a
		 * single large one, allowing one image loaded in the
		 * <code>loadImages()</code> method to become many smaller ones.
		 * <p>
		 * NOTE: The sourceImage is NOT lost in the process!
		 * 
		 * @param sourceImage
		 *            - The {@link BufferedImage} containing an image you wish
		 *            to copy.
		 * @param x
		 *            - The x coordinate for the top boundary of the crop.
		 * @param y
		 *            - The y coordinate for the top left boundary of the crop.
		 * @param width
		 *            - The width of the area you wish to obtain.
		 * @param height
		 *            - The height of the area you wish to obtain.
		 * @return
		 */
		private static BufferedImage getCroppedImage(BufferedImage sourceImage, int x, int y, int width, int height)
			{
				GraphicsEnvironment graphEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
				BufferedImage croppedImage = null;

				try
					{
						GraphicsDevice screen = graphEnv.getDefaultScreenDevice();
						GraphicsConfiguration gc = screen.getDefaultConfiguration();
						croppedImage = gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
					}
				catch (Exception e)
					{
						Tools.errorWindow(e, "crop, in Images");
					}

				if (croppedImage == null)
					{
						croppedImage = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);// BufferedImage.TYPE_INT_ARGB);
					}

				int[] pixels = new int[width * height];
				sourceImage.getRGB(x, y, width, height, pixels, 0, width);
				croppedImage.setRGB(0, 0, width, height, pixels, 0, width);

				return croppedImage;
			}

	}
