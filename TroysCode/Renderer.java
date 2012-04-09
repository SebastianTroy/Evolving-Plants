package TroysCode;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

/**
 * This class extends {@link java.awt.Canvas}, it is added to the program's
 * {@link Frame} and is what is drawn onto by the <code>render()</code> methods.
 * It also holds the program's main loop.
 * <p>
 * This class also holds the currenr {@link RenderableObject}, this means it's '
 * <code>render(Graphics g)</code>' and '<code>tick()</code>' methods are called
 * by the main loop, and any Events detected by the {@link InputReciever} or
 * {@link Frame} class are passed onto it.
 * 
 * @author Sebastian Troy
 */
public class Renderer extends Canvas
	{
		private static final long serialVersionUID = 1L;

		/**
		 * This is the current {@link RenderablObject}. this means it's '
		 * <code>render(Graphics g)</code>' and '<code>tick()</code>' methods
		 * are called by the main loop, and any Events detected by the
		 * {@link InputReciever} or {@link Frame} class are passed onto it.
		 */
		private RenderableObject renderableObject = null;

		/**
		 * This boolean is set to <code>false</code> during the switching of
		 * {@link RenderableObjects}. This stops unexpected behavior during the
		 * switchover.
		 */
		private boolean rendering;

		/**
		 * This boolean is <code>true</code> when the program's {@link Frame}
		 * holds the user's focus. When it is <code>false</code> the main loop
		 * is suspended and
		 */
		public boolean focused = true;

		/**
		 * This variable represents the number of times the <code>tick()</code>
		 * method has been called in the last second.
		 */
		private int lastFrames = 0;

		/**
		 * The time it took to process the last <code>tick()</code> call, in
		 * microseconds.
		 */
		private int timeToTick = 0;

		/**
		 * The time it took to process the last <code>render()</code> call, in
		 * milliseconds.
		 */
		private int timeToRender = 0;

		public Renderer()
			{
			}

		/**
		 * This method is called by the {@link hub} once instances of all of the
		 * vital classes have been created. It adds the listners in the
		 * {@link InputListener} to itself, makes the program current and calls
		 * the <code>run()</code> method, which contains the program's main
		 * loop.
		 */
		public final void begin()
			{
				setSize(hub.frame.startWidth, hub.frame.startHeight);

				hub.frame.addWindowFocusListener(hub.input);

				addMouseMotionListener(hub.input);
				addMouseListener(hub.input);
				addKeyListener(hub.input);
				addMouseWheelListener(hub.input);

				// This brings the keyboard focus to our window
				this.requestFocus();

				changeRenderableObject(renderableObject);

				run();
			}

		/**
		 * This method is called by the {@link hub} class. This sets the
		 * {@link RenderableObject} that will be made current at the start of
		 * the program, without initialising the {@link RenderableObject} yet.
		 * <p>
		 * This mothod will do nothing after the {@link hub} has called it.
		 * 
		 * @param object
		 *            - The {@link RenderableObject} to be made current at the
		 *            start of the program.
		 */
		public final void setStartRenderableObject(RenderableObject object)
			{
				if (renderableObject == null)
					renderableObject = object;
			}

		/**
		 * This method is used to change the current {@link RenderableObject}
		 * safely, it also calls the {@link RenderableObject}'s
		 * <code>initiate()</code> method if it is the first time the
		 * {@link RenderableObject} has been added, otherwise it calls the
		 * <code>refresh()</code> method.
		 * 
		 * @param object
		 *            - The {@link RenderableObject} to be made current.
		 */
		public final void changeRenderableObject(RenderableObject object)
			{
				rendering = false;
				renderableObject = object;
				if (renderableObject.initiated == false)
					{
						renderableObject.tInitiate();
						renderableObject.initiated = true;
					}
				renderableObject.refresh();
				rendering = true;
			}

		/**
		 * @return the current {@link RenderableObject}.
		 */
		public final RenderableObject getRenderableObject()
			{
				return this.renderableObject;
			}

		/**
		 * This method contains the main loop, this is a set of statements which
		 * are called again and again while the program is running.
		 * <p>
		 * The example loop calls <code>tick()</code> and <code>render</code>
		 * sixty times a second with regular intervals between.
		 * <p>
		 * <strong>Warning:</strong> The example loop is fairly complex, you may
		 * want to experiment with your own.
		 */
		private void run()
			{
				int frames = 0;

				double unprocessedSeconds = 0;
				long lastTime = System.nanoTime();
				double secondsPerTick = 1 / 60.0;
				int tickCount = 0;

				while (true)
					{
						if (focused)
							{
								long now = System.nanoTime();
								long passedTime = now - lastTime;
								lastTime = now;
								if (passedTime < 0)
									passedTime = 0;
								if (passedTime > 100000000)
									passedTime = 0;

								unprocessedSeconds += passedTime / 1000000000.0;

								boolean ticked = false;
								while (unprocessedSeconds > secondsPerTick)
									{
										tick();
										ticked = true;
										tickCount++;

										unprocessedSeconds -= secondsPerTick;

										if (tickCount % 60 == 0)
											{
												lastFrames = frames;
												frames = 0;
											}
									}

								if (ticked)
									{
										render();
										frames++;
									}
							}
						else
							render();
						try
							{
								Thread.sleep(focused ? 1 : 100);
							}
						catch (InterruptedException e)
							{
								Tools.errorWindow(e, "MAIN LOOP");
							}
					}
			}

		private void tick()
			{
				long time1 = System.nanoTime();
				renderableObject.tick();
				long time2 = System.nanoTime();

				/*
				 * Only changes the value 50% of the time, this slows the
				 * flickering of the value in the DEBUG square.
				 */
				if (Tools.randPercent() > 50)
					timeToTick = (int) ((time2 - time1) / 1000f);
			}

		/**
		 * This method creates a double buffered {@link Graphics} object and
		 * draws onto it. It then passes the {@link Graphics} object onto the
		 * current {@link RenderableObject} so that it too can draw to it. Once
		 * the program has finished rendering, this method shows the newly
		 * rendered screen and disposes of the old {@link BufferStrategy}.
		 */
		private void render()
			{
				long time1 = System.nanoTime();

				BufferStrategy bs = getBufferStrategy();
				if (bs == null)
					{
						createBufferStrategy(2);
						bs = getBufferStrategy();
					}

				if (bs != null)
					{
						Graphics g = bs.getDrawGraphics();

						if (rendering)
							renderableObject.tRenderObject(g);

						long time2 = System.nanoTime();

						/*
						 * Only changes the value 50% of the time, this slows
						 * the flickering of the value in the DEBUG square.
						 */
						if (Tools.randPercent() > 50)
							timeToRender = (int) ((time2 - time1) / 1000000f);

						/*
						 * This block of code draws the square in the top right
						 * corner of the program, it displays the frames per
						 * second ""FPS"", whether the program is rendering (if
						 * an error occours while changing renderableObject it
						 * will be false), and whether the window has focus or
						 * not.
						 */
						if (hub.DEBUG)
							{
								g.setColor(hub.renderer.focused ? Color.black : Color.gray);
								g.fillRect(hub.frame.getWidth() - 130, 0, 130, 50);

								Font font = new Font(g.getFont().toString(), Font.PLAIN, 12);
								g.setFont(font);

								g.setColor(Color.white);
								g.drawString("FPS: " + hub.renderer.lastFrames, hub.frame.getWidth() - 130, 15);
//								g.drawString("Rendering: " + hub.renderer.rendering, hub.frame.getWidth() - 130, 30);
//								g.drawString("Focused: " + hub.renderer.focused, hub.frame.getWidth() - 130, 45);
								g.drawString("Tick (micros): " + timeToTick, hub.frame.getWidth() - 130, 30);
								g.drawString("Render (millis): " + timeToRender, hub.frame.getWidth() - 130, 45);
							}

						g.dispose();
						bs.show();
					}
			}
	}