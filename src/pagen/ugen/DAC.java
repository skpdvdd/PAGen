package pagen.ugen;

import ddf.minim.AudioOutput;
import ddf.minim.ugens.UGen;

public class DAC extends UGen
{
	public final UGenInput input;
	
	private final AudioOutput _out;
	
	public DAC(AudioOutput out)
	{
		_out = out;
		input = new UGenInput(InputType.AUDIO);
		
		patch(_out);
	}
	
	@Override
	protected void uGenerate(float[] channels)
	{
		if(input.isPatched()) {
			float val = input.getLastValues()[0];
			for(int i = 0; i < channels.length; i++) {
				channels[i] = val;
			}
		}
	}
}
