package pagen.ui.ugen;

import pagen.Util;
import pagen.ui.Mode;
import pagen.ui.PAGen;
import pagen.ui.Tooltip;
import ddf.minim.ugens.Oscil;
import ddf.minim.ugens.UGen;

/**
 * Oscillator ugen.
 */
public class Oscillator extends UnitGenerator
{
	public static final String IN_AMPLITUDE = "Amplitude";
	public static final String IN_FREQUENCY = "Frequency";
	public static final String IN_PHASE = "Phase";

	private final Oscil _osc;
	
	private float _phase;
	private float _amplitude;
	private float _frequency;
	
	/**
	 * Ctor.
	 * 
	 * @param p The main window. Must not be null
	 * @param frequency The initial frequency. Must be > 0
	 * @param amplitude The initial amplitude. Must be >= 0 and <= 1
	 */
	public Oscillator(PAGen p, float frequency, float amplitude)
	{
		super(p, Size.NORMAL);
		
		_phase = 0;
		_amplitude = amplitude;
		_frequency = frequency;
		_osc = new Oscil(frequency, amplitude);

		in.put(IN_AMPLITUDE, _osc.amplitude);
		in.put(IN_FREQUENCY, _osc.frequency);
		in.put(IN_PHASE, _osc.phase);
		
		calcInputBoundingBoxes();		
		setFrequency(frequency);
		setPhase(0);
	}

	@Override
	public UGen getUGen()
	{
		return _osc;
	}
	
	@Override
	public String[] getLabels()
	{
		return new String[] { "Osc", String.format("%.1f Hz", getFrequency()) };
	}

	@Override
	public boolean hasDefaultInput()
	{
		return false;
	}
	
	@Override
	public String toString()
	{
		return "Oscillator #" + Integer.toHexString(hashCode());
	}

	@Override
	public Mode selected()
	{
		return new OscillatorMode();
	}
	
	/**
	 * @return The current frequency of the oscillator
	 */
	public float getFrequency()
	{
		return (_osc.frequency.isPatched()) ? _osc.frequency.getLastValues()[0] : _frequency;
	}
	
	/**
	 * Sets the frequency of the oscillator.
	 * 
	 * @param freq The frequency. Must be > 0
	 */
	public void setFrequency(float freq)
	{
		_frequency = freq;
		_osc.setFrequency(freq);
	}
	
	/**
	 * @return The current phase of the oscillator
	 */
	public float getPhase()
	{
		return (_osc.phase.isPatched()) ? _osc.phase.getLastValues()[0] : _phase;
	}
	
	/**
	 * Sets the phase of the oscillator.
	 * 
	 * @param freq The phase
	 */
	public void setPhase(float phase)
	{
		_phase = phase;
		_osc.setPhase(phase);
	}
	
	/**
	 * @return The current amplitude of the oscillator
	 */
	public float getAmplitude()
	{
		return (_osc.amplitude.isPatched()) ? _osc.amplitude.getLastValues()[0] : _amplitude;
	}
	
	protected class OscillatorMode extends UGenMode
	{
		@Override
		public String getDefaultCommand()
		{
			return "freq";
		}
		
		@Override
		public void draw()
		{
			p.loop();
			
			String[] text = new String[3];
			text[0] = String.format("Frequency (f): %.2f", getFrequency());
			text[1] = String.format("Phase (p): %.2f", getPhase());
			text[2] = String.format("Amplitude: %.2f", + getAmplitude());
						
			Tooltip.display(p, Oscillator.this.toString(), text);
		}
		
		@Override
		public void commandEntered(String command, String[] args)
		{
			if(command.equals("freq") || command.equals("f")) {
				float[] freq = Util.tryParseFloats(args);
				if(freq.length > 0) {
					setFrequency(freq[0]);
				}
				
				return;
			}
			
			if(command.equals("phase") || command.equals("p")) {
				float[] phase = Util.tryParseFloats(args);
				if(phase.length > 0) {
					setPhase(phase[0]);
				}
				
				return;
			}
		}
	}
}
