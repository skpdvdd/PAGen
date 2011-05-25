package pagen.ui.ugen;

import processing.core.PGraphics;
import ddf.minim.ugens.UGen;

public interface UnitGenerator
{
	float[] getSize();
	
	float[] getBoundingBox();
	
	float[] getOrigin();
	
	UnitGenerator setOrigin(float x, float y);
	
	UnitGenerator setGraphics(PGraphics g);
		
	UnitGenerator addInput(UGen input);
	
	UnitGenerator patch(UGen target);
	
	void update();
	
	void redraw();
}
