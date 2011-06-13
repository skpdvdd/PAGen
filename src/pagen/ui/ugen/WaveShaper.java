package pagen.ui.ugen;

import pagen.ui.Mode;
import pagen.ui.PAGen;
import ddf.minim.ugens.UGen;
import ddf.minim.ugens.Waves;
import ddf.minim.ugens.Wavetable;

/**
 * UGen for waveshaping distortion.
 */
public class WaveShaper extends UnitGenerator
{
	public static final String TABLE_SAW = "Saw";
	public static final String TABLE_SINE = "Sine";
	
	public static final String IN_AUDIO = "Audio";
	public static final String IN_MAPAMP = "Map Amplitude";
	public static final String IN_OUTAMP = "Out Amplitude";
	
	private final String _table;
	private final pagen.ugen.WaveShaper _shaper;
	
	/**
	 * 
	 * @param p The main window. Must not be null
	 * @param outAmp Output amplitude multiplier of the shaped wave
	 * @param mapAmp Amplitude over which to map the incoming signal
	 * @param table Wave shape over which to map the incoming signal
	 */
	public WaveShaper(PAGen p, float outAmp, float mapAmp, String table)
	{
		super(p, Type.AUDIO, Size.NORMAL);
		
		Wavetable tbl = null;
		
		if(table.equals(TABLE_SAW)) tbl = Waves.SAW;
		else if(table.equals(TABLE_SINE)) tbl = Waves.SINE;
		else throw new RuntimeException("Unknown wavetable");
		
		_table = table;
		_shaper = new pagen.ugen.WaveShaper(outAmp, mapAmp, tbl, true);
		
		in.put(IN_MAPAMP, _shaper.mapAmplitude);
		in.put(IN_AUDIO, _shaper.audio);
		in.put(IN_OUTAMP, _shaper.outAmplitude);
		
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
		return _shaper;
	}

	@Override
	public boolean hasDefaultInput()
	{
		return false;
	}
	
	@Override
	public String[] getLabels()
	{
		return new String[] { _table };
	}
}
