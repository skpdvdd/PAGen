package pagen.ui.ugen;

import pagen.Config;
import pagen.ui.PAGen;
import processing.core.PConstants;
import ddf.minim.Minim;
import ddf.minim.ugens.UGen;
import ddf.minim.ugens.UGen.UGenInput;

public class DAC extends UnitGenerator
{
	public static final String IN_MONO = "Mono Input";
	public static final String OUT_DEFAULT = "Default Output";
	
	private final pagen.ugen.DAC _dac;
	
	public DAC(PAGen p)
	{
		super(p);
		
		setSize(50, 50);
		
		in.put(IN_MONO, null);
		out.put(OUT_DEFAULT, null);
		
		_dac = new pagen.ugen.DAC(p.minim().getLineOut(Minim.MONO, Config.bufferSize, Config.sampleRate));
	}
	
	@Override
	public String getDefaultInput()
	{
		return IN_MONO;
	}

	@Override
	public String getDefaultOutput()
	{
		return OUT_DEFAULT;
	}
	
	@Override
	public UGenInput getUGenInput(String input)
	{
		assertInput(input);
		
		return _dac.input;
	}

	@Override
	public void patch(UnitGenerator to, String input, String output)
	{
		assertOutput(output);
		to.assertInput(input);
		
		if(out.get(output) != null) {
			unpatch(output);
		}
		
		out.put(output, new OutgoingConnection(to, input));
		to.connected(this, input, output);
		
		_dac.patch(to.getUGenInput(input));
	}
	
	@Override
	public void unpatch(String output)
	{
		// unsupported
	}
	
	@Override
	public UGen getUGen()
	{
		return _dac;
	}

	@Override
	public void redraw()
	{
		p.rectMode(PConstants.CENTER);
		p.fill(0xFF9900FF);
		p.noStroke();
		p.rect(origin[0], origin[1], size[0], size[1]);
	}
}
