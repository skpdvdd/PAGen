package pagen.ui.ugen;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import pagen.Console;
import pagen.ui.PAGen;
import ddf.minim.ugens.UGen;
import ddf.minim.ugens.UGen.UGenInput;

public abstract class UnitGenerator
{
	protected final PAGen p;
	protected final float[] size;
	protected final float[] origin;
	protected final float[] boundingBox;
	
	protected final LinkedList<Connection> out;
	protected final HashMap<String, UGenInput> in;
	protected final HashMap<String, UnitGenerator> connections;
	
	protected int connectionId;
	
	public UnitGenerator(PAGen p)
	{
		this.p = p;
		size = new float[2];
		origin = new float[2];
		boundingBox = new float[4];
		out = new LinkedList<Connection>();
		in = new HashMap<String, UGenInput>();
		connections = new HashMap<String, UnitGenerator>();
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
		Console.debug("Patching " + this + " to " + to);
		
		out.add(new Connection(to, to.connect(this)));
	}

	public void patch(UnitGenerator to, String input)
	{
		Console.debug("Patching " + this + " to " + to + " on input " + input);
		
		to.connect(this, input);
		out.add(new Connection(to, input));
	}
	
	public void unpatch()
	{
		Console.debug("Unpatching all connections from " + this);
		
		for(Connection connection : new LinkedList<Connection>(out)) {
			unpatch(connection);
		}
	}
	
	public void unpatch(Connection connection)
	{
		Console.debug("Unpatching connection from " + this + " to " + connection.ugen + " (input " + connection.input + ")");
		
		out.remove(connection);
		connection.ugen.disconnect(connection.input);
	}
		
	public UGenInput getUGenInput(String input)
	{
		return in.get(input);
	}
	
	public String connect(UnitGenerator from)
	{
		Console.debug("Connecting " + from + " to " + this + " on default input");
		
		if(! hasDefaultInput()) {
			throw new PatchException();
		}
		
		String cid = String.format("DEFAULT_%d", ++connectionId);
		
		from.getUGen().patch(getUGen());
		connections.put(cid, from);
				
		return cid;
	}
	
	public void connect(UnitGenerator from, String input)
	{
		Console.debug("Connecting " + from + " to " + this + " on input " + input);
		
		if(getInputs() == null || ! getInputs().contains(input)) {
			throw new PatchException();
		}
		
		disconnect(input);
		from.getUGen().patch(in.get(input));
		connections.put(input, from);
	}
	
	public void disconnect(String input)
	{
		UnitGenerator connected = connections.get(input);
		
		Console.debug("Disconnecting " + connected + " from " + this + " (was on input " + input + ")");
		
		if(connected == null) {
			return;
		}
		
		connected.getUGen().unpatch(getUGen());
		connections.remove(input);
		connected.unpatched(new Connection(this, input));
	}
	
	public Set<String> getInputs()
	{
		return Collections.unmodifiableSet(in.keySet());
	}
	
	public abstract UGen getUGen();
		
	public abstract void redraw();
	
	public abstract boolean hasDefaultInput();
	
	protected void unpatched(Connection connection)
	{
		Console.debug(this + " reveiced unpatched event from " + connection.ugen + " (was on input " + connection.input + ")");
		
		out.remove(connection);
	}
		
	protected void setSize(float x, float y)
	{
		size[0] = x;
		size[1] = y;
		
		_updateBoundingBox();
	}
			
	private void _updateBoundingBox()
	{
		boundingBox[0] = origin[0] - size[0] / 2;
		boundingBox[1] = origin[1] - size[1] / 2;
		boundingBox[2] = origin[0] + size[0] / 2;
		boundingBox[3] = origin[1] + size[1] / 2;
	}
		
	public class Connection
	{
		public final UnitGenerator ugen;
		public final String input;
				
		public Connection(UnitGenerator ugen, String input)
		{
			this.ugen = ugen;
			this.input = input;
		}

		@Override
		public boolean equals(Object obj)
		{
			if(getClass() != obj.getClass()) {
				return false;
			}
			
			Connection other = (Connection) obj;
			
			return ugen.equals(other.ugen) && input.equals(other.input);
		}
	}
}
