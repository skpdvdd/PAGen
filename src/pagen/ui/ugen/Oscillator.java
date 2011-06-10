package pagen.ui.ugen;

import pagen.Util;
import pagen.ui.Mode;
import pagen.ui.PAGen;
import pagen.ui.Tooltip;
import ddf.minim.ugens.Oscil;
import ddf.minim.ugens.UGen;
import ddf.minim.ugens.Waves;
import ddf.minim.ugens.Wavetable;

/**
 * Oscillator ugen.
 */
public class Oscillator extends UnitGenerator
{
	public static final String WAVEFORM_SAW = "Saw";
	public static final String WAVEFORM_SINE = "Sine";
	public static final String WAVEFORM_SQUARE = "Square";
	public static final String WAVEFORM_PHASOR = "Phasor";
	public static final String WAVEFORM_TRIANGLE = "Triangle";
	public static final String WAVEFORM_QUARTERPULSE = "Quarterpulse";
	
	public static final String IN_AMPLITUDE = "Amplitude";
	public static final String IN_FREQUENCY = "Frequency";
	public static final String IN_PHASE = "Phase";

	private final Oscil _osc;
	private final String _waveform;
	private final OscillatorMode _mode;
	
	private float _phase;
	private float _amplitude;
	private float _frequency;
	
	/**
	 * Ctor.
	 * 
	 * @param p The main window. Must not be null
	 * @param waveform The waveform. See WAVEFORM constants
	 * @param frequency The initial frequency. Must be > 0
	 * @param amplitude The initial amplitude. Must be >= 0 and <= 1
	 */
	public Oscillator(PAGen p, String waveform, float frequency, float amplitude)
	{
		super(p, Type.AUDIO, Size.NORMAL);
		
		Wavetable table = null;
		if(waveform.equals(WAVEFORM_SAW)) table = Waves.SAW;
		else if(waveform.equals(WAVEFORM_SINE)) table = Waves.SINE;
		else if(waveform.equals(WAVEFORM_SQUARE)) table = Waves.SQUARE;
		else if(waveform.equals(WAVEFORM_PHASOR)) table = Waves.PHASOR;
		else if(waveform.equals(WAVEFORM_TRIANGLE)) table = Waves.TRIANGLE;
		else if(waveform.equals(WAVEFORM_QUARTERPULSE)) table = Waves.QUARTERPULSE;
		else throw new RuntimeException("Unsupported waveform.");
		
		_waveform = waveform;
		_amplitude = amplitude;
		_frequency = frequency;
		_osc = new Oscil(frequency, amplitude, table);
		_mode = new OscillatorMode();

		in.put(IN_AMPLITUDE, _osc.amplitude);
		in.put(IN_FREQUENCY, _osc.frequency);
		in.put(IN_PHASE, _osc.phase);
		
		calcInputBoundingBoxes();		
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
		return new String[] { _waveform, String.format("%.1f Hz", getFrequency()) };
	}

	@Override
	public boolean hasDefaultInput()
	{
		return false;
	}
	
	@Override
	public Mode selected()
	{
		return _mode;
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
	
	@Override
	public String toString()
	{
		return _waveform + " #" + Integer.toHexString(hashCode());
	}
	
	/**
	 * @return The current amplitude of the oscillator
	 */
	public float getAmplitude()
	{
		return (_osc.amplitude.isPatched()) ? _osc.amplitude.getLastValues()[0] : _amplitude;
	}
	
	/**
	 * @return The waveform of the oscillator
	 */
	public String getWaveform()
	{
		return _waveform;
	}
	
	protected class OscillatorMode extends UGenMode
	{
		private final Tooltip _tooltip;
		
		public OscillatorMode()
		{
			_tooltip = new Tooltip(p, Oscillator.this.toString());
		}
		
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
						
			_tooltip.display(text);
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
