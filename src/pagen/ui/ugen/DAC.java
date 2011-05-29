package pagen.ui.ugen;

import java.util.HashMap;
import java.util.Set;
import pagen.Config;
import pagen.ui.PAGen;
import processing.core.PConstants;
import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.ugens.UGen;
import ddf.minim.ugens.UGen.UGenInput;

public class DAC extends UnitGenerator
{
	private final AudioOutput _out;
	private final HashMap<String, UnitGenerator> _in;
	
	private int _cid;
	
	public DAC(PAGen p)
	{
		super(p);
		
		setSize(50, 50);
		
		_in = new HashMap<String, UnitGenerator>();
		_out = p.minim().getLineOut(Minim.MONO, Config.bufferSize, Config.sampleRate);
	}

	@Override
	public UGenInput getUGenInput(String input)
	{
		return null;
	}

	@Override
	public UGen getUGen()
	{
		return null; //TODO
	}

	@Override
	public void redraw()
	{
		p.rectMode(PConstants.CENTER);
		p.fill(0xFF9900FF);
		p.noStroke();
		p.rect(origin[0], origin[1], size[0], size[1]);
	}

	@Override
	public String connect(UnitGenerator from)
	{
		String cid = "DEFAULT_" + ++_cid;
		
		from.getUGen().patch(_out);
		_in.put(cid, from);
		
		return cid;
	}

	@Override
	public void connect(UnitGenerator from, String input)
	{
		throw new PatchException();
	}

	@Override
	public void disconnect(String input)
	{
		UnitGenerator connected = _in.get(input);
		
		if(connected != null) {
			connected.getUGen().unpatch(_out);
			connected.unpatched(new Connection(this, input));
			
			_in.remove(input);
		}
	}

	@Override
	public boolean hasDefaultInput()
	{
		return true;
	}

	@Override
	public Set<String> getInputs()
	{
		return null;
	}
}
