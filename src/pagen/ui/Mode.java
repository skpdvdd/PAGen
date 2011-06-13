package pagen.ui;

/**
 * An operating mode.
 */
public abstract class Mode
{
	/**
	 * Requests the mode to draw itself.
	 */
	public void draw() { }
	
	/**
	 * Executed when the user pressed a key.
	 */
	public void keyPressed() { }
		
	/**
	 * Executed when the user pressed the mouse.
	 */
	public void mousePressed() { }
		
	/**
	 * Executed when the user released the mouse.
	 */
	public void mouseReleased() { }
		
	/**
	 * Executed when the user moved the mouse.
	 */
	public void mouseMoved() { }
		
	/**
	 * Executed when the user dragged the mouse.
	 */
	public void mouseDragged() { }
	
	/**
	 * Executed when the window size changed.
	 */
	public void sizeChanged() { }
	
	/**
	 * Executed when the user entered a command.
	 * 
	 * @param command The command. Must not be null
	 * @param args The arguments. Must be of length 1 or greater
	 */
	public void commandEntered(String command, String[] args) { }
	
	/**
	 * @return The default command of this mode or null if no default command exists
	 */
	public String getDefaultCommand()
	{
		return null;
	}
}
