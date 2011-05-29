package pagen.ui.ugen;

import pagen.Config;
import pagen.Console;
import pagen.ugen.Dummy;
import pagen.ui.PAGen;
import processing.core.PConstants;
import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.ugens.UGen;

/**
 * DAC (audio output) unit generator.
 */
public class DAC extends UnitGenerator
{
	private final AudioOutput _out;
	
	/**
	 * Ctor.
	 * 
	 * @param p The main window. Must not be null
	 */
	public DAC(PAGen p)
	{
		super(p);
		
		setSize(50, 50);
		
		_out = p.minim().getLineOut(Minim.MONO, Config.bufferSize, Config.sampleRate);
	}

	@Override
	public UGen getUGen()
	{
		return new Dummy();
	}
	
	@Override
	public String connect(UnitGenerator from)
	{
		Console.debug("Connecting " + from + " to " + this);
		
		String cid = String.format("DEFAULT_%d", ++connectionId);
		
		from.getUGen().patch(_out);
		connections.put(cid, from);
				
		return cid;
	}
	
	@Override
	public void disconnect(String input)
	{
		UnitGenerator connected = connections.get(input);
		
		Console.debug("Disconnecting " + connected + " from " + this);
		
		if(connected == null) {
			return;
		}
		
		connected.getUGen().unpatch(_out);
		connections.remove(input);
		connected.unpatched(new Connection(this, input));
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
	public boolean hasDefaultInput()
	{
		return true;
	}
	
	@Override
	public String toString()
	{
		return "DAC #" + Integer.toHexString(hashCode());
	}
}
