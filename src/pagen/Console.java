package pagen;

/**
 * Console utilities.
 */
public class Console
{
	/**
	 * Prints a debug message if debugging is enabled.
	 * 
	 * @param msg The message
	 */
	public static void debug(String msg)
	{
		if(Config.debug) {
			System.out.println("[D] " + msg);	
		}
	}
	
	/**
	 * Prints a message to stdout.
	 * 
	 * @param msg The message
	 */
	public static void info(String msg)
	{
		System.out.println(msg);
	}
	
	/**
	 * Prints a message to stderr.
	 * 
	 * @param msg The message
	 */
	public static void error(String msg)
	{
		System.err.println(msg);
	}
}
