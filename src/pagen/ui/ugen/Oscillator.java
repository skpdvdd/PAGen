package pagen.ui.ugen;

import pagen.Console;
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
		
		_osc = new Oscil(frequency, amplitude);
		_amplitude = amplitude;

		in.put(IN_AMPLITUDE, _osc.amplitude);
		in.put(IN_FREQUENCY, _osc.frequency);
		in.put(IN_PHASE, _osc.phase);
		
		inBB.put(IN_AMPLITUDE, new float[] { -50, -5, -40, 5 });
		inBB.put(IN_FREQUENCY, new float[] { -25, -45, -15, -35 });
		inBB.put(IN_PHASE, new float[] { -25, 45, -15, 35 });
		
		setFrequency(frequency);
		setPhase(0);
	}

	@Override
	public UGen getUGen()
	{
		return _osc;
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
	
	public void setFrequency(float freq)
	{
		Console.debug(this + ": Setting frequency to " + freq);
		
		_frequency = freq;
		_osc.setFrequency(freq);
	}
	
	public void setPhase(float phase)
	{
		Console.debug(this + ": Setting phase to " + phase);
		
		_phase = phase;
		_osc.setPhase(phase);
	}
	
	protected class OscillatorMode extends UGenMode
	{
		public OscillatorMode()
		{
			defaultCommand = "freq";
		}
		
		@Override
		public void draw()
		{
			p.loop();
			
			float freq = (_osc.frequency.isPatched()) ? _osc.frequency.getLastValues()[0] : _frequency;
			float phase = (_osc.phase.isPatched()) ? _osc.phase.getLastValues()[0] : _phase;
			float amp = (_osc.amplitude.isPatched()) ? _osc.amplitude.getLastValues()[0] : _amplitude;
			
			String[] text = new String[3];
			text[0] = String.format("Frequency (f): %.2f", freq);
			text[1] = String.format("Phase (p): %.2f", phase);
			text[2] = String.format("Amplitude: %.2f", + amp);
						
			Tooltip.display(p, Oscillator.this.toString(), text);
		}
		
		@Override
		protected void commandEntered(String cmd, String[] args)
		{
			if(cmd.equals("freq") || cmd.equals("f")) {
				float[] freq = Util.tryParseFloats(args);
				if(freq.length > 0) {
					setFrequency(freq[0]);
				}
				
				return;
			}
			
			if(cmd.equals("phase") || cmd.equals("p")) {
				float[] phase = Util.tryParseFloats(args);
				if(phase.length > 0) {
					setPhase(phase[0]);
				}
				
				return;
			}
		}
	}
}
