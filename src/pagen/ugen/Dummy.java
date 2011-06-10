package pagen.ugen;

import java.util.Arrays;
import ddf.minim.ugens.UGen;

/**
 * Dummy UGen without functionality. Simply passes the incoming signal unchanged.
 */
public class Dummy extends UGen
{
	/**
	 * Audio input.
	 */
	public final UGenInput audio;
	
	/**
	 * Ctor.
	 */
	public Dummy()
	{
		audio = new UGenInput(InputType.AUDIO);
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
	}
}
