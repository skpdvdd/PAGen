package pagen.ui.ugen;

import pagen.ui.Mode;
import pagen.ui.PAGen;
import ddf.minim.ugens.UGen;

public class Poly extends UnitGenerator
{
	private final pagen.ugen.Poly _poly;
	
	public Poly(PAGen p)
	{
		super(p, Type.AUDIO, Size.NORMAL);

		_poly = new pagen.ugen.Poly();
		
		//TODO
	}

	@Override
	public Mode selected()
	{
		return new PolyMode();
	}

	@Override
	public UGen getUGen()
	{
		//TODO implement
		return null;
	}

	@Override
	public boolean hasDefaultInput()
	{
		//TODO implement
		return false;
	}

	protected class PolyMode extends UGenMode
	{
		//TODO implement
	}
}
