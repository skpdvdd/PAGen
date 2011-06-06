package pagen.ui.ugen;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import pagen.Config;
import pagen.Console;
import pagen.ui.Mode;
import pagen.ui.PAGen;
import processing.core.PConstants;
import processing.core.PImage;
import ddf.minim.ugens.UGen;
import ddf.minim.ugens.UGen.UGenInput;

/**
 * A unit generator.
 */
public abstract class UnitGenerator
{
	protected final PAGen p;
	protected final Size size;
	protected final PImage image;
	protected final float[] origin;
	protected final float[] boundingBox;
	
	protected final LinkedHashSet<Connection> out;
	protected final HashMap<String, UGenInput> in;
	protected final HashMap<String, float[]> inBB;
	protected final HashMap<String, UnitGenerator> connections;
	
	protected int connectionId;
	
	private boolean _drawInputLabels;
	private HashMap<String, float[]> _inBBAbs;
	
	/**
	 * Ctor.
	 * 
	 * @param p The main window. Must not be null
	 * @param size The display size
	 */
	public UnitGenerator(PAGen p, Size size)
	{
		this.p = p;
		this.size = size;
		origin = new float[2];
		boundingBox = new float[4];
		out = new LinkedHashSet<Connection>(3);
		in = new HashMap<String, UGenInput>(3);
		inBB = new HashMap<String, float[]>(3);
		connections = new HashMap<String, UnitGenerator>(3);
		
		image = (size == Size.NORMAL) ? p.getImage("ugen.png") : p.getImage("ugen-small.png");
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
		_inBBAbs = null;
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
	public Collection<Connection> patchedTo()
	{
		return Collections.unmodifiableSet(out);
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
		if(connected == null) {
			return;
		}
		
		Console.debug("Disconnecting " + connected + " from " + this + " (was on input " + input + ")");

		connected.getUGen().unpatch(getUGen());
		
		if(! input.startsWith("DEFAULT")) {
			in.get(input).setIncomingUGen(null);
		}
		
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
	 * @return A map of all non-default inputs and their bounding boxes (x1, y1, x2, y2) or null if this ugen does not have such inputs
	 */
	public Map<String, float[]> getInputBoundingBoxes()
	{
		if(inBB.size() == 0) {
			return null;
		}
		
		if(_inBBAbs == null) {
			_inBBAbs = new HashMap<String, float[]>(inBB.size());
			for(Map.Entry<String, float[]> bb : inBB.entrySet()) {
				float[] c = bb.getValue();
				_inBBAbs.put(bb.getKey(), new float[] { c[0] + origin[0], c[1] + origin[1], c[2] + origin[0], c[3] + origin[1] });
			}
		}

		return _inBBAbs;
	}
	
	/**
	 * @param draw Whether the ugen should draw labels for non-default inputs
	 */
	public void drawInputLabels(boolean draw)
	{
		_drawInputLabels = draw;
	}
	
	/**
	 * Redraws this ugen.
	 */
	public void redraw()
	{
		p.imageMode(PConstants.CENTER);
		p.image(image, origin[0], origin[1]);
		
		if(in.size() > 0) {
			p.fill(0x99FFFFFF);
			p.noStroke();
			p.ellipseMode(PConstants.CORNERS);

			for(float[] bb : getInputBoundingBoxes().values()) {
				p.ellipse(bb[0], bb[1], bb[2], bb[3]);
			}
		}
		
		if(_drawInputLabels) {
			drawInputLabels();
		}
	}
	
	/**
	 * Executed by the host PAGen when this unit generator was selected.
	 * 
	 * @return The mode the host PAGen should use
	 */
	public abstract Mode selected();
	
	/**
	 * @return The underlying ugen
	 */
	public abstract UGen getUGen();
	
	/**
	 * @return True if this ugen has a default input, otherwise false
	 */
	public abstract boolean hasDefaultInput();
	
	protected void unpatched(Connection connection)
	{
		Console.debug(this + " reveiced unpatched event from " + connection.ugen + " (was on input " + connection.input + ")");
		
		out.remove(connection);
	}
	
	protected void drawInputLabels()
	{
		if(inBB.isEmpty()) {
			return;
		}

		p.fill(0xFFAAAAAA);
		p.textAlign(PConstants.CENTER);
		p.textFont(p.getFont("sans", 12));
		
		for(Map.Entry<String, float[]> input : getInputBoundingBoxes().entrySet()) {
			float[] bb = input.getValue();
			p.text(input.getKey(), bb[0] + (bb[2] - bb[0]) / 2, bb[1] + (bb[3] - bb[1]) / 2 - 10);
		}
	}
	
	protected void calcInputBoundingBoxes()
	{
		if(in.isEmpty()) {
			return;
		}
		
		String[] inputs = in.keySet().toArray(new String[0]);
		
		switch(size) {
			case NORMAL :
				switch(in.size()) {
					case 1 :
						inBB.put(inputs[0], new float[] { -50, -5, -40, 5 });
						break;
					case 2 :
						inBB.put(inputs[0], new float[] { -35, -35, -25, -25 });
						inBB.put(inputs[1], new float[] { -35, 25, -25, 35 });
						break;
					case 3 :
						inBB.put(inputs[0], new float[] { -50, -5, -40, 5 });
						inBB.put(inputs[1], new float[] { -25, -45, -15, -35 });
						inBB.put(inputs[2], new float[] { -25, 45, -15, 35 });
						break;
					default :
						throw new RuntimeException("Input count not supported");
				}
				break;
			case SMALL :
				switch(in.size()) {
					case 1 :
						
						break;
					case 2 :
						
						break;
					case 3 :
						
						break;
					default :
						throw new RuntimeException("Input count not supported");
				}
				break;
		}
	}
			
	private void _updateBoundingBox()
	{
		float dxy = (size == Size.NORMAL) ? 50 : 37.5f;
		
		boundingBox[0] = origin[0] - dxy;
		boundingBox[1] = origin[1] - dxy;
		boundingBox[2] = origin[0] + dxy;
		boundingBox[3] = origin[1] + dxy;
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
	
	/**
	 * Basic ugen mode.
	 */
	protected class UGenMode extends Mode
	{	
		@Override
		public void draw()
		{
			drawInputLabels();
		}
		
		@Override
		public void keyPressed()
		{
			if(p.keyCode == Config.exitUGenModeKey) {
				p.idleMode();
				
				return;
			}
		}
	}
	
	/**
	 * UGen display size.
	 */
	protected enum Size
	{
		NORMAL, SMALL
	}
}
