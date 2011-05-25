package pagen.ui.ugen;

import pagen.ui.PAGen;
import processing.core.PConstants;
import processing.core.PGraphics;
import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.ugens.UGen;

public class DAC implements UnitGenerator
{
	private final AudioOutput _out;
	
	private float _ox, _oy;
	private boolean _update;
	private PGraphics _g;
	
	public DAC(PGraphics g)
	{
		_update = true;
		_out = PAGen.minim().getLineOut(Minim.MONO, 2048);
		setGraphics(g);
	}
	
	@Override
	public float[] getSize()
	{
		return new float[] { 50, 50 };
	}

	@Override
	public float[] getBoundingBox()
	{
		return new float[] { _ox - 25, _oy - 25, _ox + 25, _oy + 25 };
	}

	@Override
	public float[] getOrigin()
	{
		return new float[] { _ox, _oy };
	}

	@Override
	public UnitGenerator setOrigin(float x, float y)
	{
		_ox = x;
		_oy = y;
		
		return this;
	}
	
	@Override
	public UnitGenerator setGraphics(PGraphics g)
	{
		_g = g;
		
		return this;
	}

	@Override
	public UnitGenerator addInput(UGen input)
	{
		input.patch(_out);
		_update = true;
		
		return this;
	}

	@Override
	public UnitGenerator patch(UGen target)
	{
		// not available
		
		return this;
	}
	
	@Override
	public void update()
	{
		if(_update) {
			redraw();
			_update = false;
		}
	}

	@Override
	public void redraw()
	{
		_g.rectMode(PConstants.CENTER);
		_g.fill(0xFF666666);
		_g.rect(_ox, _oy, 50, 50);
	}
}
