package pagen.ui.ugen;

import pagen.ui.PAGen;
import processing.core.PConstants;
import ddf.minim.ugens.Oscil;
import ddf.minim.ugens.UGen;

/**
 * Oscillator ugen.
 */
public class Oscillator extends UnitGenerator
{
	public static final String IN_AMPLITUDE = "Amplitude";
	public static final String IN_FREQUENCY = "Frequency";
	public static final String IN_PHASE = "Phase";

	private final Oscil _osc;
	
	/**
	 * Ctor.
	 * 
	 * @param p The main window. Must not be null
	 * @param frequency The initial frequency. Must be > 0
	 * @param amplitude The initial amplitude. Must be >= 0 and <= 1
	 */
	public Oscillator(PAGen p, float frequency, float amplitude)
	{
		super(p);
		
		setSize(100, 100);
		
		_osc = new Oscil(frequency, amplitude);

		in.put(IN_AMPLITUDE, _osc.amplitude);
		in.put(IN_FREQUENCY, _osc.frequency);
		in.put(IN_PHASE, _osc.phase);
	}

	@Override
	public UGen getUGen()
	{
		return _osc;
	}

	@Override
	public void redraw()
	{
		p.ellipseMode(PConstants.RADIUS);
		p.fill(0xFFCC0000);
		p.noStroke();
		p.ellipse(origin[0], origin[1], size[0] / 2, size[1] / 2);
	}

	@Override
	public boolean hasDefaultInput()
	{
		return false;
	}
	
	@Override
	public String toString()
	{
		return "Oscillator #" + Integer.toHexString(hashCode());
	}
}
