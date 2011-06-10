package pagen.ui.ugen;

import pagen.ui.Mode;
import pagen.ui.PAGen;
import ddf.minim.ugens.UGen;

/**
 * Simple white noise generator.
 */
public class Noise extends UnitGenerator
{
	/**
	 * Amplitude input.
	 */
	public static final String IN_AMPLITUDE = "Amplitude";
	
	private pagen.ugen.Noise _noise;

	/**
	 * Ctr.
	 * 
	 * @param p The main window. Must not be null
	 */
	public Noise(PAGen p)
	{
		super(p, Type.AUDIO, Size.SMALL);
		
		_noise = new pagen.ugen.Noise(ddf.minim.ugens.Noise.Tint.WHITE);
		
		in.put(IN_AMPLITUDE, _noise.amplitude);
		
		calcInputBoundingBoxes();
	}
	
	@Override
	public String[] getLabels()
	{
		return new String[] { "Noise" };
	}

	@Override
	public Mode selected()
	{
		return null;
	}

	@Override
	public UGen getUGen()
	{
		return _noise;
	}

	@Override
	public boolean hasDefaultInput()
	{
		return false;
	}
}
