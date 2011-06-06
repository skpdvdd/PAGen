package pagen.ui;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import pagen.Config;
import pagen.Console;
import pagen.Util;
import pagen.ui.ugen.DAC;
import pagen.ui.ugen.Oscillator;
import pagen.ui.ugen.PatchException;
import pagen.ui.ugen.UnitGenerator;
import pagen.ui.ugen.UnitGenerator.Connection;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PImage;
import ddf.minim.Minim;

/**
 * Processing Audio Generation - main window.
 */
public class PAGen extends PApplet
{
	private static final long serialVersionUID = -6644461677737169761L;
	
	private Mode _mode;
	private Minim _minim;
	private StringBuilder _inputBuffer;
	
	private final LinkedList<UnitGenerator> _ugens;
	private final HashMap<String, PFont> _fontCache;
	private final HashMap<String, PImage> _imageCache;

	public PAGen()
	{
		_ugens = new LinkedList<UnitGenerator>();
		_fontCache = new HashMap<String, PFont>(3);
		_imageCache = new HashMap<String, PImage>(3);
		_inputBuffer = new StringBuilder();
		
		idleMode();
	}

	/**
	 * @return The minim instance to use
	 */
	public Minim minim()
	{
		return _minim;
	}
	
	/**
	 * @return The current input buffer, holding the current user input
	 */
	public StringBuilder inputBuffer()
	{
		return _inputBuffer;
	}
	
	/**
	 * Returns the font with the given name in the given size.
	 * 
	 * @param name The font name. Must not be null
	 * @param size The font size. Must be > 0
	 * @return The font
	 */
	public PFont getFont(String name, int size)
	{
		String hash = name + "-" + size;
		
		if(! _fontCache.containsKey(hash)) {
			_fontCache.put(hash, createFont(name, size));
		}
		
		return _fontCache.get(hash);
	}
	
	/**
	 * Returns the image with the given name.
	 * 
	 * @param name The image name. Must not be null
	 * @return The image
	 */
	public PImage getImage(String name)
	{
		if(! _imageCache.containsKey(name)) {
			_imageCache.put(name, loadImage(name));
		}
		
		return _imageCache.get(name);
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
	
	public void idleMode()
	{
		_switchMode(new IdleMode());
	}
	
	@Override
	public void setup()
	{
		size(800, 600, JAVA2D);
		frameRate(30);
		smooth();
		noLoop();

		_minim = new Minim(this);
		
		frame.setTitle("Processing Audio Generator");
		frame.setResizable(true);
		frame.addComponentListener(new ComponentListener()
		{
			@Override
			public void componentShown(ComponentEvent arg0) { }
			
			@Override
			public void componentResized(ComponentEvent arg0)
			{
				redraw();
			}
			
			@Override
			public void componentMoved(ComponentEvent arg0) { }
			
			@Override
			public void componentHidden(ComponentEvent arg0) { }
		});

		// some test ugens
		DAC dac = new DAC(this);
		Oscillator oscil1 = new Oscillator(this, 10, 1);
		Oscillator oscil2 = new Oscillator(this, 150, 0.8f);
		
		oscil1.setOrigin(150, 150);
		oscil2.setOrigin(300, 200);
		dac.setOrigin(450, 300);
		
		_ugens.add(oscil1);
		_ugens.add(oscil2);
		_ugens.add(dac);
	}
	
	@Override
	public void draw()
	{
		_mode.draw();
		_drawInput();
	}
	
	@Override
	public void mouseMoved()
	{
		_mode.mouseMoved();
	}
	
	@Override
	public void keyPressed()
	{
		if(keyCode == 8) {
			if(_inputBuffer.length() > 0) {
				_inputBuffer.deleteCharAt(_inputBuffer.length() - 1);
				redraw();
			}
			
			return;
		}
		
		if(keyCode != 10) {
			_inputBuffer.append(key);
		}
		else {
			String cmd = null;
			String[] args = null;
			String[] in = _inputBuffer.toString().split(" ");
			
			if(in.length == 1) {
				if(_mode.getDefaultCommand() != null) {
					cmd = _mode.getDefaultCommand();
					args = in;
				}
				else {
					return;
				}
			}
			else {
				cmd = in[0];
				args = Util.removeFirst(in);
			}
			
			_inputBuffer = new StringBuilder();
			_mode.commandEntered(cmd, args);
		}
	
		_mode.keyPressed();
		redraw();
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
			for(Connection patched : ugen.patchedTo()) {
				if(patched.input.startsWith("DEFAULT")) {
					line(ugen.getOrigin()[0], ugen.getOrigin()[1], patched.ugen.getOrigin()[0], patched.ugen.getOrigin()[1]);	
				}
				else {
					float[] bb = patched.ugen.getInputBoundingBoxes().get(patched.input);
					line(ugen.getOrigin()[0], ugen.getOrigin()[1], bb[0] + (bb[2] - bb[0]) / 2, bb[1] + (bb[3] - bb[1]) / 2);
				}
			}
		}
		
		// draw ugens
		for(UnitGenerator ugen : _ugens) {
			ugen.redraw();
		}
	}
	
	private void _drawInput()
	{		
		String input = _inputBuffer.toString();
		
		fill(10);
		noStroke();
		rectMode(PConstants.CORNERS);
		rect(0, height - 17, width, height);
		
		fill(0xFF00FF00);
		textAlign(LEFT);
		textFont(getFont("sans", 14));
		text(input, 3, height - 3);
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
	
	private void _switchMode(Mode mode)
	{
		Console.debug("Switched to mode " + mode.getClass().getName());
		
		_mode = mode;
		
		redraw();
	}
	
	private class IdleMode extends Mode
	{	
		@Override
		public void draw()
		{
			noLoop();

			_drawUGens();
		}
		
		@Override
		public void mousePressed()
		{
			UnitGenerator ugen = _isMouseOver(mouseX, mouseY);
			if(ugen != null) {
				Mode mode = ugen.selected();
				if(mode != null) {
					_switchMode(mode);
				}
				else {
					Console.info("Detail view not supported.");
				}
			}
		}
		
		@Override
		public void keyPressed()
		{
			switch(keyCode) {
				case Config.moveModeKey :
					if(_isMouseOver(mouseX, mouseY) != null) {
						_switchMode(new MoveMode(_isMouseOver(mouseX, mouseY)));
					}
					
					break;
				case Config.patchModeKey :
					if(_isMouseOver(mouseX, mouseY) != null) {
						_switchMode(new PatchMode(_isMouseOver(mouseX, mouseY)));
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
			_subject = subject;
			_ox = subject.getOrigin()[0];
			_oy = subject.getOrigin()[1];
			_mx = mouseX;
			_my = mouseY;
		}
		
		@Override
		public void draw()
		{
			noLoop();
			
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
			if(keyCode == Config.moveModeKey) {
				idleMode();
			}
		}
	}
	
	private class PatchMode extends Mode
	{
		private final UnitGenerator _subject;
		
		public PatchMode(UnitGenerator subject)
		{
			_subject = subject;
			
			for(UnitGenerator ugen : _ugens) {
				ugen.drawInputLabels(true);
			}
		}
		
		@Override
		public void draw()
		{
			noLoop();
			
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
			if(keyCode != Config.patchModeKey) {
				return;
			}
			
			UnitGenerator to = _isMouseOver(mouseX, mouseY);
			
			try {
				if(to == null) {
					_subject.unpatch();
					_idleMode();
					return;
				}
				
				Map<String, float[]> inBBs = to.getInputBoundingBoxes();
				if(inBBs != null) {
					for(Map.Entry<String, float[]> input : inBBs.entrySet()) {
						float[] bb = input.getValue();
						if(mouseX >= bb[0] && mouseY >= bb[1] && mouseX <= bb[2] && mouseY <= bb[3]) {
							System.out.println("path to input " + input.getKey());
							_subject.patch(to, input.getKey());
							_idleMode();
							return;
						}
					}
				}
				
				if(to.hasDefaultInput()) {
					_subject.patch(to);
				}
				else {
					Console.info("Unit Generator does not have a default input.");
				}
			}
			catch(PatchException e) {
				Console.info("Patching not supported");
			}
			
			_idleMode();
		}
		
		private void _idleMode()
		{
			for(UnitGenerator ugen : _ugens) {
				ugen.drawInputLabels(false);
			}
			
			idleMode();
		}
	}
}
