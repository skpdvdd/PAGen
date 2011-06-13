package pagen.ui.ugen;

import ij.measure.SplineFitter;
import java.util.Iterator;
import java.util.LinkedList;
import pagen.Config;
import pagen.Util;
import pagen.ui.Mode;
import pagen.ui.PAGen;
import processing.core.PConstants;
import ddf.minim.ugens.UGen;

/**
 * Generates waveforms based on polygon (spline) interpolation.
 */
public class Poly extends UnitGenerator
{
	/**
	 * Keycode to reset the polygon.
	 */
	public static final int RESET_KEY = 127; // del
	
	private final String[] _label;
	private final PolyMode _mode;
	private final pagen.ugen.Poly _poly;
	
	private float[] _x;
	private float[] _y;
	private float _duration;
	
	/**
	 * Ctor.
	 * 
	 * @param p The main window. Must not be null
	 */
	public Poly(PAGen p)
	{
		super(p, Type.AUDIO, Size.SMALL);
		
		_mode = new PolyMode();
		_poly = new pagen.ugen.Poly();
		_label = new String[] { "Poly" };
		_duration = 0.03f;
	}

	@Override
	public Mode selected()
	{
		return _mode;
	}

	@Override
	public UGen getUGen()
	{
		return _poly;
	}

	@Override
	public boolean hasDefaultInput()
	{
		return false;
	}
	
	@Override
	public String[] getLabels()
	{
		return _label;
	}
	
	private void _setDuration(float duration)
	{
		_duration = duration;
		_update();
	}
	
	private void _setCoords(float[] x, float[] y)
	{
		_x = x;
		_y = y;	
		_update();
	}
	
	private void _update()
	{
		_poly.update(_x, _y, _duration);
	}
	
	protected class PolyMode extends UGenMode
	{		
		private final Object _listLock;
		private final LinkedList<Integer> _x;
		private final LinkedList<Integer> _y;
		
		private int _x1;
		private int _y1;
		private int _x2;
		private int _y2;
		private int _y0;
		private volatile boolean _modified;
		
		public PolyMode()
		{			
			_listLock = new Object();
			_x = new LinkedList<Integer>();
			_y = new LinkedList<Integer>();
			
			_recalcArea();
		}
		
		@Override
		public void draw()
		{
			p.noLoop();
			
			// bg
			p.stroke(0xFFAAAAAA);
			p.strokeWeight(1);
			p.fill(0xFF222222);
			p.rectMode(PConstants.CORNERS);
			p.rect(_x1, _y1, _x2, _y2);
			
			// y0
			p.stroke(0xFF00FF00);
			p.line(_x1, _y0, _x2, _y0);
			
			// duration
			
			p.fill(0xFFCCCCCC);
			p.textAlign(PConstants.CENTER);
			p.textFont(p.getFont("Arial", 15));
			p.text(String.format("Duration (d): %.4f secs", _duration), p.width / 2, p.height - 23);
			
			// poly
			
			int len = 0;
			int ilast = 0;
			int[] x = null;
			int[] y = null;
			
			synchronized(_listLock) {
				if(_x.size() < 2) {
					return;
				}
				
				len = _x.size();
				ilast = _x.getLast();
				x = new int[len];
				y = new int[len];
				
				_toArray(x, y);
			}
			
			p.stroke((0xAAFF0000));
			p.strokeWeight(3);

			SplineFitter fitter = new SplineFitter(x, y, len);
			for(int i = _x1; i < ilast; i++) {
				p.point(i, (float) fitter.evalSpline(x, y, len, i));
			}
			
			// control points
			
			p.strokeWeight(3);
			p.stroke(0xFFFFFF00);
			
			for(int i = 0; i < len; i++) {
				p.point(x[i], y[i]);
			}
		}
		
		@Override
		public void commandEntered(String command, String[] args)
		{
			if(command.equals("d") || command.equals("duration")) {
				float[] dur = Util.tryParseFloats(args);
				if(dur.length > 0) {
					_setDuration(dur[0]);
					redraw();
				}
			}
		}
		
		@Override
		public void mousePressed()
		{
			boolean redraw = false;
			
			if(p.mouseButton == PConstants.RIGHT) {
				synchronized(_listLock) {
					if(! _x.isEmpty()) {
						_x.removeLast();
						_y.removeLast();
						_modified = true;
						redraw = true;
					}
				}
			}
			else {
				int x = p.mouseX;
				int y = p.mouseY;
				
				if(x < _x1 || y < _y1 || x > _x2 || y > _y2) {
					return;
				}
				
				synchronized(_listLock) {
					if(_x.isEmpty()) {
						x = _x1;
					}
					else {
						int xl = _x.getLast();
						if(xl >= x) {
							return;
						}
					}
					
					_x.add(x);
					_y.add(y);
				}
				
				redraw = true;
				_modified = true;
			}
			
			if(redraw) {
				p.redraw();	
			}
		}
		
		@Override
		public void keyPressed()
		{
			switch(p.keyCode) {
				case RESET_KEY :
					synchronized(_listLock) {
						_x.clear();
						_y.clear();
					}
					_modified = true;
					break;
				case Config.exitUGenModeKey :
					_updateAndExit();
					break;
			}
		}
		
		@Override
		public String getDefaultCommand()
		{
			return "duration";
		}
		
		@Override
		public void sizeChanged()
		{
			_recalcArea();
		}
				
		private void _recalcArea()
		{
			_x1 = 20;
			_y1 = 20;
			_x2 = p.width - 20;
			_y2 = p.height - 20;
			_y0 = p.height / 2;
		}
		
		private void _updateAndExit()
		{
			if(! _modified) {
				p.idleMode();
				return;
			}
			
			synchronized(_listLock) {
				if(_x.size() < 2) {
					_x.clear();
					_y.clear();
					
					p.idleMode();
					return;
				}
			}
			
			int len = 0;
			int[] x = null;
			int[] y = null;
			
			synchronized(_listLock) {
				// add interpolation point
				_x.add(_x2);
				_y.add(_y.getFirst());
				
				len = _x.size();
				x = new int[len];
				y = new int[len];
				_toArray(x, y);
			}
			
			// scale x to [0 1]
			
			float[] xf = new float[len];
			float[] yf = new float[len];
			
			int xmax = _x2 - _x1;			
			for(int i = 0; i < len; i++) {
				xf[i] = (x[i] - _x1) / (float) xmax;
			}
			
			// scale y to [-1 1]
			
			for(int i = 0; i < len; i++) {
				yf[i] = PAGen.map(y[i], _y1, _y2, 1, -1);
			}
			
			_setCoords(xf, yf);
			_modified = false;
			
			p.idleMode();
		}
		
		private void _toArray(int[] x, int[] y)
		{
			int i = 0;
			Iterator<Integer> xiter = _x.iterator();
			Iterator<Integer> yiter = _y.iterator();
			while(xiter.hasNext()) {
				x[i] = xiter.next();
				y[i] = yiter.next();
				i++;
			}
		}
	}
}
