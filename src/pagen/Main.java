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
		if(args.length > 0 && args[0].equals("-debug")) {
			Config.debug = true;
		}
		
		System.out.println("================================");
		System.out.println("== Processing Audio Generator ==");
		System.out.println("================================");
		System.out.println();
		System.out.println("Type 'c' or 'create' followed by the name of the ugen you want to create.");
		System.out.println("Available types are: sine, saw, triangle, square, phasor, quarterpulse, noise, poly; bandpass, dac, sum, subtract, const, scale, delay, debug");
		System.out.println();
		
		PApplet.main(new String[] { "pagen.ui.PAGen" });
	}
}
