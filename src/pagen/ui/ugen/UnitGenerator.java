package pagen.ui.ugen;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import pagen.ui.PAGen;
import ddf.minim.ugens.UGen;
import ddf.minim.ugens.UGen.UGenInput;

public abstract class UnitGenerator
{
	protected final PAGen p;
	protected final float[] size;
	protected final float[] origin;
	protected final float[] boundingBox;
	protected final HashMap<String, IncomingConnection> in;
	protected final HashMap<String, OutgoingConnection> out;
	
	public UnitGenerator(PAGen p)
	{
		this.p = p;
		size = new float[2];
		origin = new float[2];
		boundingBox = new float[4];
		in = new HashMap<String, IncomingConnection>(2);
		out = new HashMap<String, OutgoingConnection>(1);
	}
	
	public float[] getSize()
	{
		return size;
	}
	
	public float[] getBoundingBox()
	{
		return boundingBox;
	}
	
	public float[] getOrigin()
	{
		return origin;
	}
	
	public void setOrigin(float x, float y)
	{
		origin[0] = x;
		origin[1] = y;
		
		_updateBoundingBox();
	}
	
	public void patch(UnitGenerator to)
	{
		patch(to, to.getDefaultInput());
	}
	
	public void patch(UnitGenerator to, String input)
	{
		patch(to, input, getDefaultOutput());
	}
	
	public void patch(UnitGenerator to, String input, String output)
	{
		assertOutput(output);
		to.assertInput(input);
		
		if(out.get(output) != null) {
			unpatch(output);
		}
		
		to.connected(this, input, output);
		out.put(output, new OutgoingConnection(to, input));
	}
	
	public void unpatch()
	{
		unpatch(getDefaultOutput());
	}
	
	public void unpatch(String output)
	{
		assertOutput(output);
		
		if(out.get(output) != null) {
			out.get(output).ugen.disconnected(out.get(output).input);
			out.put(output, null);
		}
	}
	
	public Set<String> getInputs()
	{
		return Collections.unmodifiableSet(in.keySet());
	}
	
	public Set<String> getOutputs()
	{
		return Collections.unmodifiableSet(out.keySet());
	}
	
	protected void connected(UnitGenerator ugen, String input, String output)
	{
		in.put(input, new IncomingConnection(ugen, output));
	}
	
	protected void disconnected(String input)
	{
		in.remove(input);
	}
	
	protected void setSize(float x, float y)
	{
		size[0] = x;
		size[1] = y;
		
		_updateBoundingBox();
	}
	
	protected void assertInput(String name)
	{
		if(! in.containsKey(name)) {
			throw new RuntimeException("Input '" + name + "' does not exist.");
		}
	}
	
	protected void assertOutput(String name)
	{
		if(! out.containsKey(name)) {
			throw new RuntimeException("Output '" + name + "' does not exist.");
		}
	}
	
	private void _updateBoundingBox()
	{
		boundingBox[0] = origin[0] - size[0] / 2;
		boundingBox[1] = origin[1] - size[1] / 2;
		boundingBox[2] = origin[0] + size[0] / 2;
		boundingBox[3] = origin[1] + size[1] / 2;
	}
	
	public abstract String getDefaultInput();
	
	public abstract String getDefaultOutput();
	
	public abstract UGenInput getUGenInput(String input);
	
	public abstract UGen getUGen();
		
	public abstract void redraw();
	
	public class IncomingConnection
	{
		public final UnitGenerator ugen;
		public final String output;
		
		public IncomingConnection(UnitGenerator ugen, String output)
		{
			this.ugen = ugen;
			this.output = output;
		}
	}
	
	public class OutgoingConnection
	{
		public final UnitGenerator ugen;
		public final String input;
		
		public OutgoingConnection(UnitGenerator ugen, String input)
		{
			this.ugen = ugen;
			this.input = input;
		}
	}
}
