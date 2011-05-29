package pagen.ui;

import java.util.LinkedList;
import pagen.ui.ugen.DAC;
import pagen.ui.ugen.Oscillator;
import pagen.ui.ugen.UnitGenerator;
import processing.core.PApplet;
import ddf.minim.Minim;

public class PAGen extends PApplet
{
	private static final long serialVersionUID = -6644461677737169761L;
	
	private Minim _minim;
	
	private LinkedList<UnitGenerator> _ugens;
	private UnitGenerator _selected;

	public Minim minim()
	{
		return _minim;
	}
	
	public void requestUpdate(UnitGenerator enquirer)
	{
		
	}
	
	@Override
	public void setup()
	{
		size(800, 600, JAVA2D);
		smooth();
		noLoop();
		
		_minim = new Minim(this);
		
		_ugens = new LinkedList<UnitGenerator>();

		Oscillator osci = new Oscillator(this, 440, 0.8f);
		DAC dac = new DAC(this);
		
		osci.setOrigin(150, 150);
		dac.setOrigin(400, 300);
		
		_ugens.add(osci);
		_ugens.add(dac);
	}
		
	@Override
	public void draw()
	{
		background(0);
		
		for(UnitGenerator ugen : _ugens) {
			ugen.redraw();
		}
		
		if(_selected != null) {
			float[] bb = _selected.getBoundingBox();
			
			rectMode(CORNERS);
			noFill();
			stroke(0xFFFFFF00);
			strokeWeight(1);
			rect(bb[0], bb[1], bb[2], bb[3]);
		}
	}
	
	@Override
	public void keyPressed()
	{
		if(key == 'c') {
			System.out.println("patching");
			_ugens.get(0).patch(_ugens.get(1));
		}
		
		if(key == 'd') {
			System.out.println("unpatch");
			_ugens.get(0).unpatch();
		}
	}
	
	@Override
	public void mousePressed()
	{
		UnitGenerator selected = _isMouseOver();
		if(selected == null) {
			return;
		}
		
		_selected = selected;
		redraw();
	}
	
	private UnitGenerator _isMouseOver()
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
