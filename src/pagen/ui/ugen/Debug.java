package pagen.ui.ugen;

import pagen.ugen.Dummy;
import pagen.ui.Mode;
import pagen.ui.PAGen;
import pagen.ui.Tooltip;
import ddf.minim.ugens.UGen;

/**
 * Debugs the incoming signal, passing it to the output unchanged.
 */
public class Debug extends UnitGenerator
{
	/**
	 * The audio input.
	 */
	public static final String IN_AUDIO = "Audio";
	
	private final Dummy _dummy;
	private final DebugMode _mode;
	
	/**
	 * 
	 * @param p The main window. Must not be null
	 */
	public Debug(PAGen p)
	{
		super(p, Type.CONTROL, Size.SMALL);
		
		_dummy = new Dummy();
		_mode = new DebugMode();
		
		in.put(IN_AUDIO, _dummy.audio);	
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
		return _dummy;
	}

	@Override
	public boolean hasDefaultInput()
	{
		return false;
	}
	
	@Override
	public String[] getLabels()
	{
		return new String[] { getLastValueString() };
	}
	
	/**
	 * @return The most recent value of the input signal
	 */
	public float getLastValue()
	{
		return _dummy.getLastValues()[0];
	}
	
	/**
	 * @return The most recent value of the input signal as a string
	 */
	public String getLastValueString()
	{
		return String.format("%.2f", getLastValue());
	}
	
	protected class DebugMode extends UGenMode
	{		
		private final Tooltip _tooltip;
		
		public DebugMode()
		{
			_tooltip = new Tooltip(p, Debug.this.toString());
		}
		
		@Override
		public void draw()
		{
			p.loop();
				
			_tooltip.display(new String[] { getLastValueString() });
		}
	}
}
