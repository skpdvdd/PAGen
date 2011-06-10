package pagen.ugen;

import java.util.Arrays;
import ddf.minim.ugens.Wavetable;

public class WaveShaper extends ddf.minim.ugens.WaveShaper
{
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
