package pagen.ui.ugen;

import pagen.Util;
import pagen.ui.Mode;
import pagen.ui.PAGen;
import pagen.ui.Tooltip;
import ddf.minim.ugens.UGen;

/**
 * Modifies an audio signal by multiplying it with a given multiplier and adding a value to the result (linear scaling).
 */
public class Scale extends UnitGenerator
{
	public static final String IN_AUDIO = "Audio";
	public static final String IN_MULTIPLIER = "Multiplier";
	public static final String IN_SUMMAND = "Summand";
	
	private final ScaleMode _mode;
	private final pagen.ugen.Scale _scale;
	
	/**
	 * Ctor.
	 * 
	 * @param p The main window. Must not be null
	 * @param mult The multiplier
	 * @param sum The summand
	 */
	public Scale(PAGen p, float mult, float sum)
	{
		super(p, Type.CONTROL, Size.SMALL);
		
		_mode = new ScaleMode();
		_scale = new pagen.ugen.Scale(mult, sum);
		
		in.put(IN_AUDIO, _scale.audio);
		in.put(IN_MULTIPLIER, _scale.multiplier);
		in.put(IN_SUMMAND, _scale.summand);
		
		calcInputBoundingBoxes();
	}
	
	@Override
	public String[] getLabels()
	{
		return new String[] { String.format("* %.1f",  _scale.getMultiplier()), String.format("+ %.1f",  _scale.getSummand()) };
	}

	@Override
	public Mode selected()
	{
		return _mode;
	}

	@Override
	public UGen getUGen()
	{
		return _scale;
	}

	@Override
	public boolean hasDefaultInput()
	{
		return false;
	}
	
	/**
	 * @return The current multiplier
	 */
	public float getMultiplier()
	{
		return _scale.getMultiplier();
	}
	
	/**
	 * Sets the multiplier
	 * 
	 * @param value The value
	 */
	public void setMultiplier(float value)
	{
		_scale.setMultiplier(value);
	}
	
	/**
	 * @return The current summand
	 */
	public float getSummand()
	{
		return _scale.getSummand();
	}
	
	/**
	 * Sets the summand
	 * 
	 * @param value The value
	 */
	public void setSummand(float value)
	{
		_scale.setSummand(value);
	}
	
	protected class ScaleMode extends UGenMode
	{
		private final Tooltip _tooltip;
		
		public ScaleMode()
		{
			_tooltip = new Tooltip(p, Scale.this.toString());
		}
		
		@Override
		public String getDefaultCommand()
		{
			return "mult";
		}
		
		@Override
		public void draw()
		{
			p.loop();
			
			String[] text = new String[2];
			text[0] = String.format("Multiplier (m): %.2f", getMultiplier());
			text[1] = String.format("Summand (s): %.2f", getSummand());
						
			_tooltip.display(text);
		}
		
		@Override
		public void commandEntered(String command, String[] args)
		{
			if(command.equals("mult") || command.equals("m")) {
				float[] mult = Util.tryParseFloats(args);
				if(mult.length > 0) {
					setMultiplier(mult[0]);
				}
				
				return;
			}
			
			if(command.equals("sum") || command.equals("s")) {
				float[] sum = Util.tryParseFloats(args);
				if(sum.length > 0) {
					setSummand(sum[0]);
				}
				
				return;
			}
		}
	}
}
