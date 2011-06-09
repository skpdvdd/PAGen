package pagen.ui.ugen;

import ddf.minim.ugens.UGen;
import pagen.ui.Mode;
import pagen.ui.PAGen;

/**
 * Delay effect.
 */
public class Delay extends UnitGenerator
{
	public static final String IN_AUDIO = "Audio";
	public static final String IN_AMPLITUDE = "Amplitude";
	public static final String IN_TIME = "Time";

	private final pagen.ugen.Delay _delay;
	
	private float _maxDelayTime;
	private float _amplitude;
	private boolean _feedback;
	
	/**
	 * Ctor.
	 * 
	 * @param p The main window. Must not be null
	 * @param maxDelayTime The max delay time. Must be > 0
	 * @param amplitude The amplitude of feedback. Must be between 0 and 1
	 * @param feedback Whether to feed back repetitions
	 */
	public Delay(PAGen p, float maxDelayTime, float amplitude, boolean feedback)
	{
		super(p, Type.CONTROL, Size.NORMAL);
		
		_maxDelayTime = maxDelayTime;
		_amplitude = amplitude;
		_feedback = feedback;
		_delay = new pagen.ugen.Delay(maxDelayTime, amplitude, feedback);
		
		in.put(IN_AUDIO, _delay.audio);
		in.put(IN_AMPLITUDE, _delay.delAmp);
		in.put(IN_TIME, _delay.delTime);
		
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
		return _delay;
	}

	@Override
	public boolean hasDefaultInput()
	{
		return false;
	}
	
	@Override
	public String[] getLabels()
	{
		return new String[] { "Delay", String.format("Time: %.1f", getMaxDelayTime()), String.format("Amp: %.1f", getAmplitude()) };
	}
	
	/**
	 * @return The current max delay time
	 */
	public float getMaxDelayTime()
	{
		return (_delay.delTime.isPatched()) ? _delay.delTime.getLastValues()[0] : _maxDelayTime;
	}

	/**
	 * Sets the max delay time
	 * 
	 * @param maxDelayTime The value. Must be > 0
	 */
	public void setMaxDelayTime(float maxDelayTime)
	{
		_maxDelayTime = maxDelayTime;
	}

	/**
	 * @return The current delay amplitude
	 */
	public float getAmplitude()
	{
		return (_delay.delAmp.isPatched()) ? _delay.delAmp.getLastValues()[0] : _amplitude;
	}

	/**
	 * Sets the delay amplitude.
	 * 
	 * @param amplitude The value. Must be between 0 and 1
	 */
	public void setAmplitude(float amplitude)
	{
		_amplitude = amplitude;
	}
	
	/**
	 * @return Whether feedback mode is enabled
	 */
	public boolean getFeedback()
	{
		return _feedback;
	}
}
