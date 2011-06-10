package pagen.ui.ugen;

import pagen.Config;
import pagen.Util;
import pagen.ui.Mode;
import pagen.ui.PAGen;
import pagen.ui.Tooltip;
import ddf.minim.ugens.UGen;

/**
 * Bandpass filter.
 */
public class BandPass extends UnitGenerator
{
	/**
	 * Audio input.
	 */
	public static final String IN_AUDIO = "Audio";
	
	/**
	 * Cutoff frequency input.
	 */
	public static final String IN_CUTOFF = "Cutoff";
	
	private final BandPassMode _mode;
	private final ddf.minim.effects.BandPass _bandPass;
	
	/**
	 * Ctor.
	 * 
	 * @param p The main window. Must not be null
	 * @param frequency The center frequency
	 * @param bandWidth The bandwidth
	 */
	public BandPass(PAGen p, float frequency, float bandWidth)
	{
		super(p, Type.CONTROL, Size.NORMAL);
		
		_mode = new BandPassMode();
		_bandPass = new ddf.minim.effects.BandPass(frequency, bandWidth, Config.sampleRate);
		
		in.put(IN_AUDIO, _bandPass.audio);
		in.put(IN_CUTOFF, _bandPass.cutoff);
		
		calcInputBoundingBoxes();
	}

	@Override
	public Mode selected()
	{
		return _mode;
	}

	@Override
	public UGen getUGen()
	{
		return _bandPass;
	}

	@Override
	public boolean hasDefaultInput()
	{
		return false;
	}
	
	@Override
	public String[] getLabels()
	{
		float freq = getFrequency();
		float df = getBandWidth() / 2;
		return new String[] { "BPF", String.format("[%.0f,%.0f]", freq - df, freq + df) };
	}
	
	/**
	 * @return The current center frequency
	 */
	public float getFrequency()
	{
		return (_bandPass.cutoff.isPatched()) ? _bandPass.cutoff.getLastValues()[0] : _bandPass.frequency();
	}
	
	/**
	 * Sets the center frequency
	 * 
	 * @param frequency The frequency
	 */
	public void setFrequency(float frequency)
	{
		_bandPass.setFreq(frequency);
	}
	
	/**
	 * @return the current band width
	 */
	public float getBandWidth()
	{
		return _bandPass.getBandWidth();
	}
	
	/**
	 * Sets the current band width
	 * 
	 * @param bandWidth The band width. Must be > 0
	 */
	public void setBandWidth(float bandWidth)
	{
		_bandPass.setBandWidth(bandWidth);
	}
	
	protected class BandPassMode extends UGenMode
	{
		private final Tooltip _tooltip;
		
		public BandPassMode()
		{
			_tooltip = new Tooltip(p, BandPass.this.toString());
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
			
			String[] text = new String[2];
			text[0] = String.format("Frequency (f): %.2f", getFrequency());
			text[1] = String.format("Band Width (b): %.2f", getBandWidth());
						
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
			
			if(command.equals("bandwidth") || command.equals("b")) {
				float[] bw = Util.tryParseFloats(args);
				if(bw.length > 0) {
					setBandWidth(bw[0]);
				}
				
				return;
			}
		}
	}
}
