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
	 * The key code for toggling patch mode.
	 */
	public static final int patchModeKey = 67; // c
	
	/**
	 * The key code for toggling mode mode.
	 */
	public static final int moveModeKey = 77; // m
	
	/**
	 * The audio buffer size to use.
	 */
	public static int bufferSize = 2048;
	
	/**
	 * The sample rate to use.
	 */
	public static int sampleRate = 44100;
}
