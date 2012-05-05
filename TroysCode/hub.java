package TroysCode;

import EvolvingPlants.World;

/**
 * The {@link hub} Class Contains the main method.
 * <p>
 * This Class also contains the instances of {@link Frame}, {@link Renderer},
 * {@link InputReciever} and {@link Images}, which are required for Troy's Code
 * to work.
 * 
 * @author Sebastian Troy
 */
public class hub
	{
		/*
		 * Using lowercase letters in a class name is not usually done! I know
		 * this but I am also a little lazy... The ""hub"" is so named because
		 * like a bycicle wheel it is the center of the entire program. Each
		 * spoke of the wheel is a class file, to reach ANY other class in the
		 * program you can reach it through the hub, e.g. by calling
		 * ""hub.game.player.level.increaseLevel()"". You will end up writing
		 * hub so many times that it will be significantly easier than writing
		 * ""Main"" each time!
		 */

		/**
		 * This is added to the title of the program's frame in the
		 * {@link Frame} Class.
		 */
		public static final String programName = "Evolving Plants - A-Sexual Reproduction";
		/**
		 * This is added to the title of the program's frame in the
		 * {@link Frame} Class. It can also be used to track compatability due
		 * to version differences.
		 */
		public static final String versionNumber = "1.1_00";
		/**
		 * This is the name of the folder containing the images used by Troy's
		 * Code. It must contain all of the default texture files, however their
		 * contents may be modified.
		 */
		public static String textureFolderName = "default";
		/**
		 * This is the DEBUG status, this should be false for a release version.
		 * <p>
		 * You can include code within <code> if (DEBUG) </code> blocks when
		 * developing for testing purposes.
		 */
		public static final boolean DEBUG = false;

		/*
		 * These classes are the "vital" classes, essential to the way the
		 * program runs.
		 */
		/**
		 * This holds all of the images used in the program.
		 */
		public static final Images images = new Images();
		/**
		 * This is the window inside which the program is running.
		 */
		public static final Frame frame = new Frame();
		/**
		 * This recieves all of the user input.
		 */
		public static final InputListener input = new InputListener();
		/**
		 * This holds the main loop and regulates the speed at which the program updates.
		 */
		public static final Renderer renderer = new Renderer();

		/*
		 * This is where you can add your own classes. They must all extend the
		 * renderableObject class, these can then be quickly and easily switched
		 * between, e.g. to go to the instance of the Example.java class:
		 * ""hub.renderer.changeRenderableObject(hub.mainMenu)""
		 */
		public static final World world = new World();

		/**
		 * The main method, the very first method to be called when the program
		 * is run.
		 */
		public static void main(String[] args)
			{
				frame.startFrame();
				hub.renderer.setStartRenderableObject(hub.world);
				hub.renderer.begin();
			}
	}
