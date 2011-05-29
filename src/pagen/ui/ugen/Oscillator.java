package pagen.ui.ugen;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
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

	private final Oscil _osc;
	private final HashMap<String, UGenInput> _in;
	private final HashMap<String, UnitGenerator> _inConnections;
	
	public Oscillator(PAGen p, float frequency, float amplitude)
	{
		super(p);
		
		setSize(100, 100);
		
		_osc = new Oscil(frequency, amplitude);
		_inConnections = new HashMap<String, UnitGenerator>();
		
		_in = new HashMap<String, UGenInput>(3);
		_in.put(IN_AMPLITUDE, _osc.amplitude);
		_in.put(IN_FREQUENCY, _osc.frequency);
		_in.put(IN_PHASE, _osc.phase);
	}

	@Override
	public UGenInput getUGenInput(String input)
	{
		return _in.get(input);
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
	public String connect(UnitGenerator from)
	{
		throw new PatchException();	// unsupported
	}

	@Override
	public void connect(UnitGenerator from, String input)
	{
		if(! _in.containsKey(input)) {
			throw new PatchException();
		}
		
		disconnect(input);		
		from.getUGen().patch(_in.get(input));	
		_inConnections.put(input, from);
	}
	
	@Override
	public void disconnect(String input)
	{
		UnitGenerator connected = _inConnections.get(input);
		if(connected != null) {
			connected.getUGen().unpatch(_osc); // TODO does this work?
		}
	}

	@Override
	public boolean hasDefaultInput()
	{
		return false;
	}

	@Override
	public Set<String> getInputs()
	{
		return Collections.unmodifiableSet(_in.keySet());
	}
}
