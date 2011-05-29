package pagen.ui.ugen;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import pagen.Console;
import pagen.ui.PAGen;
import ddf.minim.ugens.UGen;
import ddf.minim.ugens.UGen.UGenInput;

/**
 * A unit generator.
 */
public abstract class UnitGenerator
{
	protected final PAGen p;
	protected final float[] size;
	protected final float[] origin;
	protected final float[] boundingBox;
	
	protected final LinkedHashSet<Connection> out;
	protected final HashMap<String, UGenInput> in;
	protected final HashMap<String, UnitGenerator> connections;
	
	protected int connectionId;
	
	/**
	 * Ctor.
	 * 
	 * @param p The main window. Must not be null
	 */
	public UnitGenerator(PAGen p)
	{
		this.p = p;
		size = new float[2];
		origin = new float[2];
		boundingBox = new float[4];
		out = new LinkedHashSet<Connection>();
		in = new HashMap<String, UGenInput>();
		connections = new HashMap<String, UnitGenerator>();
	}
	
	/**
	 * @return The display size of this ugen (0: x, 1: y)
	 */
	public float[] getSize()
	{
		return size;
	}
	
	/**
	 * @return The display bounding box of this ugen (0: x1, 1: y1, 2: x2, 3: y2)
	 */
	public float[] getBoundingBox()
	{
		return boundingBox;
	}
	
	/**
	 * @return The display origin (center) of this ugen (0: x, 1: y)
	 */
	public float[] getOrigin()
	{
		return origin;
	}
	
	/**
	 * Sets the origin of this ugen.
	 * 
	 * @param x X origin
	 * @param y Y origin
	 */
	public void setOrigin(float x, float y)
	{
		origin[0] = x;
		origin[1] = y;
		
		_updateBoundingBox();
	}
	
	/**
	 * Patches this ugen to the default input of to. 
	 * Throws a PatchException if to does not have a default input.
	 * 
	 * @param to Where to patch to. Must not be null
	 */
	public void patch(UnitGenerator to)
	{
		Console.debug("Patching " + this + " to " + to);
		
		out.add(new Connection(to, to.connect(this)));
	}

	/**
	 * Patches this ugen to the specified input of to.
	 * Throws a PatchException if to does not have this input.
	 * 
	 * @param to Where to patch to. Must not be null
	 * @param input The input to patch to
	 */
	public void patch(UnitGenerator to, String input)
	{
		Console.debug("Patching " + this + " to " + to + " on input " + input);
		
		to.connect(this, input);
		out.add(new Connection(to, input));
	}
	
	/**
	 * Unpatches this ugen from all other ugens.
	 */
	public void unpatch()
	{
		Console.debug("Unpatching all connections from " + this);
		
		for(Connection connection : new LinkedList<Connection>(out)) {
			unpatch(connection);
		}
	}
	
	/**
	 * Unpatches this ugen from the given connection.
	 * 
	 * @param connection the connection to unpatch. Must not be null
	 */
	public void unpatch(Connection connection)
	{
		Console.debug("Unpatching connection from " + this + " to " + connection.ugen + " (input " + connection.input + ")");
		
		out.remove(connection);
		connection.ugen.disconnect(connection.input);
	}
	
	/**
	 * @return All ugens this ugen is patched to
	 */
	public Collection<UnitGenerator> patchedTo()
	{
		LinkedList<UnitGenerator> patched = new LinkedList<UnitGenerator>();
		
		for(Connection connection : out) {
			patched.add(connection.ugen);
		}
		
		return patched;
	}
	
	/**
	 * Returns true if this ugen is patched to other, otherwise false.
	 * 
	 * @param other The other ugen. Must not be null
	 * @return If this ugen is patched to other
	 */
	public boolean isPatchedTo(UnitGenerator other)
	{
		for(Connection connection : out) {
			if(connection.equals(other)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns the ugen input with the specified name.
	 * 
	 * @param input The input name
	 * @return The ugen or null
	 */
	public UGenInput getUGenInput(String input)
	{
		return in.get(input);
	}
	
	/**
	 * Connects from to the default input of this ugen.
	 * Throws a PatchException if this ugen does not have a default input.
	 * 
	 * @param from The ugen to connect. Must not be null
	 * @return The id of the input from is connected to
	 */
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
	
	/**
	 * Connects from to the specified input of this ugen.
	 * Throws a PatchException if this ugen does not have this input.
	 * 
	 * @param from The ugen to connect. Must not be null
	 * @param input The name of the input
	 */
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
	
	/**
	 * Disconnects the ugen that is connected to this ugen at the specified input.
	 * 
	 * @param input The input
	 */
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
	
	/**
	 * Returns true if the specified ugen is connected to this ugen.
	 * 
	 * @param other The ugen
	 * @return True if other is connected to this ugen
	 */
	public boolean isConnected(UnitGenerator other)
	{
		for(UnitGenerator gen : connections.values()) {
			if(gen.equals(other)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @return A set of all available non-default inputs
	 */
	public Set<String> getInputs()
	{
		return Collections.unmodifiableSet(in.keySet());
	}
	
	/**
	 * @return The underlying ugen
	 */
	public abstract UGen getUGen();
	
	/**
	 * Redraws this ugen.
	 */
	public abstract void redraw();
	
	/**
	 * @return True if this ugen has a default input, otherwise false
	 */
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
	
	/**
	 * A connection.
	 */
	public class Connection
	{
		/**
		 * The ugen
		 */
		public final UnitGenerator ugen;
		
		/**
		 * The input
		 */
		public final String input;
		
		/**
		 * Ctor.
		 * 
		 * @param ugen The ugen. Must not be null
		 * @param input The input. Must not be null
		 */
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
