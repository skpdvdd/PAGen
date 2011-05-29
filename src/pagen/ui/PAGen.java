package pagen.ui;

import java.util.LinkedList;
import pagen.ui.ugen.DAC;
import pagen.ui.ugen.Oscillator;
import pagen.ui.ugen.UnitGenerator;
import processing.core.PApplet;
import ddf.minim.Minim;

/**
 * Processing Audio Generation - main window.
 */
public class PAGen extends PApplet
{
	private static final long serialVersionUID = -6644461677737169761L;
	
	private Minim _minim;
	
	private LinkedList<UnitGenerator> _ugens;
	private UnitGenerator _selected;
	
	private boolean _mouseDragging;
	private int _mouseDragStartX, _mouseDragStartY;

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
		_ugens = new LinkedList<UnitGenerator>();

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
		
		// draw bb around selected ugen
		if(_selected != null) {
			float[] bb = _selected.getBoundingBox();
			
			rectMode(CORNERS);
			noFill();
			stroke(0xFFFFFF00);
			strokeWeight(1);
			rect(bb[0], bb[1], bb[2], bb[3]);
		}
		
		// draw dragging line if mouse is dragged
		if(_mouseDragging) {
			stroke(0x99FFFF00);
			strokeWeight(2);
			line(_mouseDragStartX, _mouseDragStartY, mouseX, mouseY);
		}
	}
	
	@Override
	public void mouseDragged()
	{
		if(! _mouseDragging) {
			_mouseDragging = true;
			_mouseDragStartX = mouseX;
			_mouseDragStartY = mouseY;
		}
		
		redraw();
	}
	
	@Override
	public void mouseReleased()
	{
		if(_mouseDragging) {
			_mouseDragFinished();
		}
	}
	
	@Override
	public void mousePressed()
	{
		_selected = _isMouseOver(mouseX, mouseY);

		redraw();
	}
	
	private void _mouseDragFinished()
	{
		// check if we need to connect two ugens
		// if both drag start and end point to ugens, we connect them
		// if drag start points to an ugen, but drag end does not, we unpatch the ugen
		
		_mouseDragging = false;
		
		UnitGenerator from = _isMouseOver(_mouseDragStartY, _mouseDragStartX);
		UnitGenerator to = _isMouseOver(mouseX, mouseY);
		
		if(from != null && to != null) {
			from.patch(to);
		}
		else if(from != null && to == null) {
			from.unpatch();
		}
		
		redraw();
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
}
