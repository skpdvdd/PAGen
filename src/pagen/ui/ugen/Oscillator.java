package pagen.ui.ugen;

import pagen.ui.PAGen;
import processing.core.PConstants;
import ddf.minim.ugens.Oscil;
import ddf.minim.ugens.UGen;
import ddf.minim.ugens.UGen.UGenInput;

public class Oscillator extends UnitGenerator
{
	public static final String IN_AMPLITUDE = "Amplitude";
	public static final String IN_FREQUENCY = "Frequency";
	public static final String IN_PHASE = "Phase";
	public static final String OUT_WAVEFORM = "Waveform";
	
	private final Oscil _osc;
	
	public Oscillator(PAGen p, float frequency, float amplitude)
	{
		super(p);
		
		setSize(100, 100);
		
		out.put(OUT_WAVEFORM, null);
		
		_osc = new Oscil(frequency, amplitude);
	}

	@Override
	public void patch(UnitGenerator to, String input, String output)
	{
		super.patch(to, input, output);
		
		_osc.patch(to.getUGenInput(input));
	}
	
	@Override
	public void unpatch(String output)
	{
		OutgoingConnection con = out.get(output);
		
		super.unpatch(output);
		
		_osc.unpatch(con.ugen.getUGen());
		
		p.requestUpdate(this);
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
	public String getDefaultInput()
	{
		return IN_AMPLITUDE;
	}

	@Override
	public String getDefaultOutput()
	{
		return OUT_WAVEFORM;
	}

	@Override
	public UGenInput getUGenInput(String input)
	{
		assertInput(input);
		
		if(input == IN_AMPLITUDE) {
			return _osc.amplitude;
		}
		
		if(input == IN_FREQUENCY) {
			return _osc.amplitude;
		}
		
		return _osc.phase;
	}
}
