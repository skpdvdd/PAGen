package pagen.ui.ugen;

import java.util.LinkedList;
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
	protected final LinkedList<Connection> out;
	
	public UnitGenerator(PAGen p)
	{
		this.p = p;
		size = new float[2];
		origin = new float[2];
		boundingBox = new float[4];
		out = new LinkedList<Connection>();
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
		out.add(new Connection(to, to.connect(this)));
	}

	public void patch(UnitGenerator to, String input)
	{
		to.connect(this, input);
		out.add(new Connection(to, input));
	}
	
	public void unpatch(Connection connection)
	{
		out.remove(connection);
		connection.ugen.disconnect(connection.input);
	}
	
	public void unpatched(Connection connection)
	{
		out.remove(connection);
	}
	
	public abstract UGenInput getUGenInput(String input);
	
	public abstract UGen getUGen();
		
	public abstract void redraw();
	
	public abstract String connect(UnitGenerator from);
	
	public abstract void connect(UnitGenerator from, String input);
	
	public abstract void disconnect(String input);
		
	public abstract boolean hasDefaultInput();
		
	public abstract Set<String> getInputs();
		
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
