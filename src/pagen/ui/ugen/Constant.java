package pagen.ui.ugen;

import pagen.Util;
import pagen.ui.Mode;
import pagen.ui.PAGen;
import pagen.ui.Tooltip;
import ddf.minim.ugens.UGen;

/**
 * Generates constant values.
 */
public class Constant extends UnitGenerator
{
	private final ddf.minim.ugens.Constant _constant;

	private float _value;

	/**
	 * Ctor.
	 * 
	 * @param p The main window. Must not be null
	 * @param value The value to generate
	 */
	public Constant(PAGen p, float value)
	{
		super(p, Type.CONTROL, Size.SMALL);
		
		_value = value;
		_constant = new ddf.minim.ugens.Constant(value);
	}
	
	@Override
	public String[] getLabels()
	{
		return new String[] { _value + "" };
	}

	@Override
	public Mode selected()
	{
		return new ConstantMode();
	}

	@Override
	public UGen getUGen()
	{
		return _constant;
	}

	@Override
	public boolean hasDefaultInput()
	{
		return false;
	}
	
	protected class ConstantMode extends UGenMode
	{
		@Override
		public String getDefaultCommand()
		{
			return "value";
		}
		
		@Override
		public void draw()
		{
			p.noLoop();
			
			String[] text = new String[] { String.format("Value (v): %.2f", _value) };
						
			Tooltip.display(p, Constant.this.toString(), text);
		}
		
		@Override
		public void commandEntered(String command, String[] args)
		{
			if(command.equals("value") || command.equals("v")) {
				float[] freq = Util.tryParseFloats(args);
				if(freq.length > 0) {
					_value = freq[0];
					_constant.setConstant(freq[0]);
					
					p.redraw();
				}
				
				return;
			}
		}
	}
}
