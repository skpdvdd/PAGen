package pagen;

/**
 * Global configuration.
 */
public class Config
{
	/**
	 * The key code for exiting UGen detail views.
	 */
	public static final int exitUGenModeKey = 115; // F4
	
	/**
	 * The key code for deleting a UGen.
	 */
	public static final int deleteUGenKey = 127; // del
		
	/**
	 * The audio buffer size to use.
	 */
	public static final int bufferSize = 2048;
	
	/**
	 * The sample rate to use.
	 */
	public static final int sampleRate = 44100;
	
	/**
	 * Whether to enable debug mode.
	 */
	public static boolean debug = false;
}
