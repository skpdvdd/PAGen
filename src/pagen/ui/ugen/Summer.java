package pagen.ui.ugen;

import ddf.minim.ugens.UGen;
import pagen.ui.Mode;
import pagen.ui.PAGen;

/**
 * Sums incoming signals.
 */
public class Summer extends UnitGenerator
{
	private static final String[] _label = { "Sum" };
	
	private final ddf.minim.ugens.Summer _summer;
	
	/**
	 * Ctor.
	 * 
	 * @param p The main window. Must not be null
	 */
	public Summer(PAGen p)
	{
		super(p, Type.AUDIO, Size.SMALL);
		
		_summer = new ddf.minim.ugens.Summer();
	}

	@Override
	public Mode selected()
	{
		return null;
	}

	@Override
	public UGen getUGen()
	{
		return _summer;
	}

	@Override
	public boolean hasDefaultInput()
	{
		return true;
	}
	
	@Override
	public String[] getLabels()
	{
		return _label;
	}
}
