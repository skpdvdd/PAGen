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
	private final ConstantMode _mode;
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
		_mode = new ConstantMode();
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
		return _mode;
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
	
	/**
	 * @return The value
	 */
	public float getValue()
	{
		return _value;
	}
	
	/**
	 * @return The value os a string
	 */
	public String getValueAsString()
	{
		return String.format("Value (v): %.2f", _value);
	}
	
	protected class ConstantMode extends UGenMode
	{
		private final Tooltip _tooltip;
		
		public ConstantMode()
		{
			_tooltip = new Tooltip(p, Constant.this.toString());
		}
		
		@Override
		public String getDefaultCommand()
		{
			return "value";
		}
		
		@Override
		public void draw()
		{
			p.noLoop();
			
			_tooltip.display(new String[] { getValueAsString() });
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
