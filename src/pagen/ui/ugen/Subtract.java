package pagen.ui.ugen;

import ddf.minim.ugens.UGen;
import pagen.ui.Mode;
import pagen.ui.PAGen;

public class Subtract extends UnitGenerator
{
	public static final String IN_AUDIO = "Audio";
	public static final String IN_SUBTRACT = "Subtract";
	
	private final String[] _label;
	private final pagen.ugen.Subtract _subtract;
	
	public Subtract(PAGen p)
	{
		super(p, Type.CONTROL, Size.SMALL);
		
		_label = new String[] { "-" };
		_subtract = new pagen.ugen.Subtract();
		
		in.put(IN_AUDIO, _subtract.audio);
		in.put(IN_SUBTRACT, _subtract.subtract);
		
		calcInputBoundingBoxes();
	}

	@Override
	public Mode selected()
	{
		return null;
	}

	@Override
	public UGen getUGen()
	{
		return _subtract;
	}

	@Override
	public boolean hasDefaultInput()
	{
		return false;
	}
	
	@Override
	public String[] getLabels()
	{
		return _label;
	}
}
