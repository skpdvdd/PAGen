package pagen.ugen;

import java.util.Arrays;
import ddf.minim.ugens.UGen;

/**
 * Subtracts a signal from another signal.
 */
public class Subtract extends UGen
{
	/**
	 * The main (audio) signal.
	 */
	public final UGenInput audio;
	
	/**
	 * The signal that should be subtracted from the main signal.
	 */
	public final UGenInput subtract;

	/**
	 * Ctor.
	 */
	public Subtract()
	{
		audio = new UGenInput(InputType.AUDIO);
		subtract = new UGenInput(InputType.CONTROL);
	}
	
	@Override
	protected void uGenerate(float[] channels)
	{
		if(! audio.isPatched()) {
			Arrays.fill(channels, 0);
			return;
		}
		
		for(int i = 0; i < channels.length; i++) {
			channels[i] = audio.getLastValues()[i];
		}
		
		if(subtract.isPatched()) {
			for(int i = 0; i < channels.length; i++) {
				channels[i] -= subtract.getLastValues()[i];
			}
		}
	}
}
