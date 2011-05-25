package pagen.ui.ugen;

import processing.core.PConstants;
import processing.core.PGraphics;
import ddf.minim.ugens.Oscil;
import ddf.minim.ugens.UGen;

public class Oscillator implements UnitGenerator
{
	private final Oscil _osc;
	
	private float _ox, _oy;
	private boolean _update;
	private PGraphics _g;
	
	public Oscillator(PGraphics g)
	{
		_update = true;
		_osc = new Oscil(440, 0.8f);
		setGraphics(g);
	}

	@Override
	public float[] getSize()
	{
		return new float[] { 100, 100 };
	}

	@Override
	public float[] getBoundingBox()
	{
		return new float[] { _ox - 50, _oy - 50, _ox + 50, _oy + 50 };
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
		_update = true;
		
		return this;
	}

	@Override
	public UnitGenerator patch(UGen target)
	{
		_osc.patch(target);
		_update = true;
		
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
		_g.ellipseMode(PConstants.RADIUS);
		_g.fill(0xFFCC0000);
		_g.ellipse(_ox, _oy, 50, 50);
	}
}
