package pagen;

import processing.core.PApplet;

/**
 * Entry point.
 */
public class Main
{
	/**
	 * Entry point.
	 * 
	 * @param args Startup args
	 */
	public static void main(String[] args)
	{
		System.out.println("================================");
		System.out.println("== Processing Audio Generator ==");
		System.out.println("================================\n");
		System.out.println("Type 'c' or 'create' followed by the name of the ugen you want to create.");
		System.out.println("Available types are: osc, noise, const");
		
		PApplet.main(new String[] { "pagen.ui.PAGen" });
	}
}
