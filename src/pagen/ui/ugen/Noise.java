package pagen.ui.ugen;

import java.util.LinkedList;
import pagen.Util;
import pagen.ui.Mode;
import pagen.ui.PAGen;
import pagen.ui.Tooltip;
import ddf.minim.ugens.UGen;

/**
 * Simple white noise generator.
 */
public class Noise extends UnitGenerator
{
	private ddf.minim.ugens.Noise _noise;
	private float _amplitude;
	
	/**
	 * Ctr.
	 * 
	 * @param p The main window. Must not be null
	 * @param amplitude The amplitude
	 */
	public Noise(PAGen p, float amplitude)
	{
		super(p, Type.SOUND, Size.SMALL);
		
		_amplitude = amplitude;
		_noise = new ddf.minim.ugens.Noise(amplitude);
	}
	
	@Override
	public String[] getLabels()
	{
		return new String[] { "Noise" };
	}

	@Override
	public Mode selected()
	{
		return new NoiseMode();
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
	
	public void setAmplitude(float value)
	{
		LinkedList<Connection> outcp = new LinkedList<Connection>(out);
		unpatch();
		
		_amplitude = value;
		_noise = new ddf.minim.ugens.Noise(value);
		
		for(Connection con : outcp) {
			patch(con.ugen);
		}
	}
	
	@Override
	public String toString()
	{
		return "Noise #" + Integer.toHexString(hashCode());
	}
	
	protected class NoiseMode extends UGenMode
	{
		@Override
		public String getDefaultCommand()
		{
			return "amp";
		}
		
		@Override
		public void draw()
		{
			p.noLoop();
			
			String[] text = new String[] { String.format("Amplitude (a): %.2f", _amplitude) };
						
			Tooltip.display(p, Noise.this.toString(), text);
		}
		
		@Override
		public void commandEntered(String command, String[] args)
		{
			if(command.equals("amp") || command.equals("a")) {
				float[] freq = Util.tryParseFloats(args);
				if(freq.length > 0) {
					setAmplitude(freq[0]);
					
					p.redraw();
				}
				
				return;
			}
		}
	}
}
