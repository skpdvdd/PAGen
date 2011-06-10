package pagen.ui.ugen;

import pagen.ui.Mode;
import pagen.ui.PAGen;
import ddf.minim.ugens.UGen;

public class Line extends UnitGenerator
{
	private final ddf.minim.ugens.Line _line;
	
	public Line(PAGen p, float dt, float start, float end)
	{
		super(p, Type.CONTROL, Size.SMALL);
		
		_line = new ddf.minim.ugens.Line(dt, start, end);
	}

	@Override
	public Mode selected()
	{
		_line.activate();
		
		return null;
	}

	@Override
	public UGen getUGen()
	{
		return _line;
	}

	@Override
	public boolean hasDefaultInput()
	{
		return false;
	}
}
