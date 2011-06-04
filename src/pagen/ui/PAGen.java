package pagen.ui;

import java.util.LinkedList;
import pagen.Console;
import pagen.ui.ugen.DAC;
import pagen.ui.ugen.Oscillator;
import pagen.ui.ugen.PatchException;
import pagen.ui.ugen.UnitGenerator;
import processing.core.PApplet;
import ddf.minim.Minim;

/**
 * Processing Audio Generation - main window.
 */
public class PAGen extends PApplet
{
	private static final long serialVersionUID = -6644461677737169761L;
	
	private static final char _moveModeToggleKey = 'q';
	private static final char _patchModeToggleKey = 'c';
	
	private Mode _mode;
	private Minim _minim;
	
	private final LinkedList<UnitGenerator> _ugens;

	public PAGen()
	{
		_mode = new IdleMode();
		_ugens = new LinkedList<UnitGenerator>();
	}

	/**
	 * @return The minim instance to use
	 */
	public Minim minim()
	{
		return _minim;
	}
	
	/**
	 * Tells the window to redraw.
	 * 
	 * @param enquirer The ugen that requested the update
	 */
	public void requestUpdate(UnitGenerator enquirer)
	{
		redraw();
	}
	
	@Override
	public void setup()
	{
		size(800, 600, JAVA2D);
		smooth();
		noLoop();

		_minim = new Minim(this);

		// some test ugens
		DAC dac = new DAC(this);
		Oscillator osci = new Oscillator(this, 440, 0.8f);
		
		osci.setOrigin(150, 150);
		dac.setOrigin(400, 300);
		
		_ugens.add(osci);
		_ugens.add(dac);
	}
	
	@Override
	public void draw()
	{
		_mode.draw();
	}
	
	@Override
	public void mouseMoved()
	{
		_mode.mouseMoved();
	}
	
	@Override
	public void keyPressed()
	{
		System.out.println(keyCode);
		_mode.keyPressed();
	}
	
	@Override
	public void mouseDragged()
	{
		_mode.mouseDragged();
	}
	
	@Override
	public void mouseReleased()
	{
		_mode.mouseReleased();
	}
	
	@Override
	public void mousePressed()
	{
		_mode.mousePressed();
	}
	
	private void _drawUGens()
	{
		background(0);
		
		strokeWeight(2);
		stroke(0xFFFFFF00);
		
		// draw connections
		for(UnitGenerator ugen : _ugens) {
			for(UnitGenerator patched : ugen.patchedTo()) {
				line(ugen.getOrigin()[0], ugen.getOrigin()[1], patched.getOrigin()[0], patched.getOrigin()[1]);
			}
		}
		
		// draw ugens
		for(UnitGenerator ugen : _ugens) {
			ugen.redraw();
		}
	}
	
	private UnitGenerator _isMouseOver(int mouseX, int mouseY)
	{
		for(UnitGenerator ugen : _ugens) {
			float[] bb = ugen.getBoundingBox();
			if(mouseX >= bb[0] && mouseY >= bb[1] && mouseX <= bb[2] && mouseY <= bb[3]) {
				return ugen;
			}
		}
		
		return null;
	}
	
	public void switchMode(Mode mode)
	{
		//TODO
		if(mode == null) mode = new IdleMode();
		
		_mode = mode;
		
		redraw();
	}
	
	private class IdleMode extends Mode
	{
		public IdleMode()
		{
			Console.info("Switched to idle mode");
			
			noLoop();
		}
		
		@Override
		public void draw()
		{
			_drawUGens();
		}
		
		@Override
		public void mousePressed()
		{
			UnitGenerator ugen = _isMouseOver(mouseX, mouseY);
			if(ugen != null) {
				Mode mode = ugen.selected();
				if(mode != null) {
					switchMode(mode);
				}
				else {
					Console.info("Detail view not supported.");
				}
			}
		}
		
		@Override
		public void keyPressed()
		{
			switch(key) {
				case _moveModeToggleKey :
					if(_isMouseOver(mouseX, mouseY) != null) {
						switchMode(new MoveMode(_isMouseOver(mouseX, mouseY)));
					}
					
					break;
				case _patchModeToggleKey :
					if(_isMouseOver(mouseX, mouseY) != null) {
						switchMode(new PatchMode(_isMouseOver(mouseX, mouseY)));
					}
					
					break;
			}
		}
	}
	
	private class MoveMode extends Mode
	{
		private final UnitGenerator _subject;
		private final float _ox, _oy;
		private final float _mx, _my;
		
		public MoveMode(UnitGenerator subject)
		{
			Console.info("Switched to move mode");
			
			_subject = subject;
			_ox = subject.getOrigin()[0];
			_oy = subject.getOrigin()[1];
			_mx = mouseX;
			_my = mouseY;
			
			noLoop();
		}
		
		@Override
		public void draw()
		{
			_drawUGens();
		}
		
		@Override
		public void mouseMoved()
		{
			float ox = _ox - (_mx - mouseX);
			float oy = _oy - (_my - mouseY);
			
			_subject.setOrigin(ox, oy);
			
			redraw();
		}
		
		@Override
		public void keyPressed()
		{
			if(key == _moveModeToggleKey) {
				switchMode(new IdleMode());
			}
		}
	}
	
	private class PatchMode extends Mode
	{
		private final UnitGenerator _subject;
		
		public PatchMode(UnitGenerator subject)
		{
			Console.info("Switched to patch mode");
			
			_subject = subject;
			
			noLoop();
		}
		
		@Override
		public void draw()
		{
			_drawUGens();
			
			stroke(0xFFFF9900);
			strokeWeight(2);
			line(_subject.getOrigin()[0], _subject.getOrigin()[1], mouseX, mouseY);
		}
		
		@Override
		public void mouseMoved()
		{
			redraw();
		}
		
		@Override
		public void keyPressed()
		{
			if(key != _patchModeToggleKey) {
				return;
			}
			
			UnitGenerator to = _isMouseOver(mouseX, mouseY);
			
			try {
				if(to != null) _subject.patch(to); else _subject.unpatch();
			}
			catch(PatchException e) {
				Console.info("Patching not supported");
			}
			
			switchMode(new IdleMode());
		}
	}
}
