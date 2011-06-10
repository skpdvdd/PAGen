package pagen.ugen;

/**
 * Noise generator based on Minim's Noise ugen, with an additional amplitude input.
 */
public class Noise extends ddf.minim.ugens.Noise
{
	/**
	 * Amplitude input.
	 */
	public final UGenInput amplitude;
	
	private final Tint _type;
	
	/**
	 * Ctor.
	 * 
	 * @param type The noise type
	 */
	public Noise(Tint type)
	{
		super(1, type);
		
		_type = type;
		
		this.amplitude = new UGenInput(InputType.CONTROL);
	}
	
	/**
	 * @return The noise type
	 */
	public Tint getType()
	{
		return _type;
	}
	
	/**
	 * The current amplitude
	 */
	public float getAmplitude()
	{
		return (amplitude.isPatched()) ? amplitude.getLastValues()[0] : 1;
	}
	
	@Override
	protected void uGenerate(float[] channels)
	{
		super.uGenerate(channels);
		
		if(amplitude.isPatched()) {
			for(int i = 0; i < channels.length; i++) {
				channels[i] = channels[i] * amplitude.getLastValues()[i];
			}
		}
	}
}
