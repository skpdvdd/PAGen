package pagen.ugen;

import java.util.Arrays;
import ddf.minim.ugens.UGen;

/**
 * Linearly scales an input signal: out = audio * multiplier + summand.
 */
public class Linear extends UGen
{
	/**
	 * Audio input.
	 */
	public final UGenInput audio;
	
	/**
	 * Multiplier input.
	 */
	public final UGenInput multiplier;
	
	/**
	 * Summand input.
	 */
	public final UGenInput summand;
	
	private float _multiplier;
	private float _summand;
	
	/**
	 * Ctor.
	 * 
	 * @param multiplier The multiplier
	 * @param summand The summand
	 */
	public Linear(float multiplier, float summand)
	{
		super();
		
		_multiplier = multiplier;
		_summand = summand;
		
		audio = new UGenInput(InputType.AUDIO);
		this.multiplier = new UGenInput(InputType.CONTROL);
		this.summand = new UGenInput(InputType.CONTROL);
	}
	
	/**
	 * @return The current multiplier
	 */
	public float getMultiplier()
	{
		return (multiplier.isPatched()) ? multiplier.getLastValues()[0] : _multiplier;
	}
	
	/**
	 * Sets the multiplier
	 * 
	 * @param value The value
	 */
	public void setMultiplier(float value)
	{
		_multiplier = value;
	}
	
	/**
	 * @return The current summand
	 */
	public float getSummand()
	{
		return (summand.isPatched()) ? summand.getLastValues()[0] : _summand;
	}
	
	/**
	 * Sets the summand
	 * 
	 * @param value The value
	 */
	public void setSummand(float value)
	{
		_summand = value;
	}
	
	@Override
	protected void uGenerate(float[] channels)
	{
		if(! audio.isPatched()) {
			Arrays.fill(channels, 0);
			return;
		}
		
		for(int i = 0; i < channels.length; i++) {
			channels[i] = audio.getLastValues()[i];
			channels[i] *= (multiplier.isPatched()) ? multiplier.getLastValues()[i] : _multiplier;
			channels[i] += (summand.isPatched()) ? summand.getLastValues()[i] : _summand;
		}
	}
}
