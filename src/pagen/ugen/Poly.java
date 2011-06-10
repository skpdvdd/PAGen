package pagen.ugen;

import ij.measure.SplineFitter;
import java.util.Arrays;
import pagen.Config;
import ddf.minim.ugens.UGen;

/**
 * Generates waveforms based on polygon (spline) interpolation.
 */
public class Poly extends UGen
{
	private float[] _buffer;
	private float[] _x;
	private float[] _y;
	private float _dt;
	private int _pos;
	
	/**
	 * Ctor.
	 */
	public Poly()
	{
		setSampleRate(Config.sampleRate);
	}
	
	/**
	 * Updates the ugen.
	 * 
	 * @param x The control point x values of the new polygon. Values must be ordered, first must be 0, last must be 1
	 * @param y The control point y values of the new polygon. Values must be between -1 and 1. Must be of the same size as y
	 * @param dt The duration of one polygon iteration (how long the polygon is played) in secs. Must be > 0
	 */
	public void update(float[] x, float[] y, float dt)
	{
		_x = x;
		_y = y;
		_dt = dt;
		
		_update();
	}
	
	@Override
	protected void sampleRateChanged()
	{
		super.sampleRateChanged();
		
		_update();
	}
	
	@Override
	protected void uGenerate(float[] channels)
	{
		float val = (_buffer != null) ? _buffer[_pos] : 0;
		Arrays.fill(channels, val);
		
		if(_buffer == null) {
			return;
		}
		
		_pos++;	
		if(_pos == _buffer.length) {
			_pos = 0;
		}
	}
	
	private void _update()
	{
		if(_x == null || _y == null || _dt <= 0) {
			return;
		}
		
		_pos = 0;
		
		int len = _x.length;
		int dts = (int) (_dt * sampleRate());

		int[] x = new int[len];
		int[] y = new int[len];
		
		// scale x to length and y to bit depth (16 bit assumed)		
		for(int i = 0; i < len; i++) {
			x[i] = (int) (_x[i] * dts);
			y[i] = (int) (_y[i] * 32768);
		}
		
		SplineFitter fitter = new SplineFitter(x, y, len);
		
		_buffer = new float[dts];
		for(int i = 0; i < dts; i++) {
			_buffer[i] = (float) (fitter.evalSpline(x, y, len, i) / 32768);
		}
	}
}
