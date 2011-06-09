package pagen.ui.ugen;

import pagen.Util;
import pagen.ui.Mode;
import pagen.ui.PAGen;
import pagen.ui.Tooltip;
import ddf.minim.ugens.UGen;

public class Linear extends UnitGenerator
{
	public static final String IN_AUDIO = "Audio";
	public static final String IN_MULTIPLIER = "Multiplier";
	public static final String IN_SUMMAND = "Summand";
	
	private final pagen.ugen.Linear _linear;
	
	/**
	 * Ctor.
	 * 
	 * @param p The main window. Must not be null
	 * @param mult The multiplier
	 * @param sum The summand
	 */
	public Linear(PAGen p, float mult, float sum)
	{
		super(p, Type.CONTROL, Size.NORMAL);
		
		_linear = new pagen.ugen.Linear(mult, sum);
		
		in.put(IN_AUDIO, _linear.audio);
		in.put(IN_MULTIPLIER, _linear.multiplier);
		in.put(IN_SUMMAND, _linear.summand);
		
		calcInputBoundingBoxes();
	}
	
	@Override
	public String[] getLabels()
	{
		return new String[] { "Linear", String.format("*%.1f",  _linear.getMultiplier()), String.format("+%.1f",  _linear.getSummand()) };
	}

	@Override
	public Mode selected()
	{
		return new LinearMode();
	}

	@Override
	public UGen getUGen()
	{
		return _linear;
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
		return _linear.getMultiplier();
	}
	
	/**
	 * Sets the multiplier
	 * 
	 * @param value The value
	 */
	public void setMultiplier(float value)
	{
		_linear.setMultiplier(value);
	}
	
	/**
	 * @return The current summand
	 */
	public float getSummand()
	{
		return _linear.getSummand();
	}
	
	/**
	 * Sets the summand
	 * 
	 * @param value The value
	 */
	public void setSummand(float value)
	{
		_linear.setSummand(value);
	}
	
	protected class LinearMode extends UGenMode
	{
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
						
			Tooltip.display(p, Linear.this.toString(), text);
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
