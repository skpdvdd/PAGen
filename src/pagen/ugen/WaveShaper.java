package pagen.ugen;

import java.util.Arrays;
import ddf.minim.ugens.Wavetable;

/**
 * Wave shaper based on Minim's wave shaper.
 */
public class WaveShaper extends ddf.minim.ugens.WaveShaper
{
	/**
	 * Ctor.
	 * 
	 * @param outAmp Output amplitude multiplier of the shaped wave
	 * @param mapAmp Amplitude over which to map the incoming signal
	 * @param mapShape Wave shape over which to map the incoming signal
	 * @param wrapMap Whether to wrap the map
	 */
	public WaveShaper(float outAmp, float mapAmp, Wavetable mapShape, boolean wrapMap)
	{
		super(outAmp, mapAmp, mapShape, wrapMap);
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
