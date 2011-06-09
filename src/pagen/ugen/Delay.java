package pagen.ugen;

import java.util.Arrays;

/**
 * Delay based on Minim's Delay ugen.
 */
public class Delay extends ddf.minim.ugens.Delay
{
	/**
	 * Ctor.
	 * 
	 * @param maxDelayTime The max delay time. Must be > 0
	 * @param amplitude The amplitude of feedback. Must be between 0 and 1
	 * @param feedback Whether to feed back repetitions
	 */
	public Delay(float maxDelayTime, float amplitude, boolean feedback)
	{
		super(maxDelayTime, amplitude, feedback);
	}
	
	@Override
	protected void uGenerate(float[] channels)
	{
		if(! audio.isPatched()) {
			Arrays.fill(channels, 0);
			return;
		}
		
		super.uGenerate(channels);
	}
}
