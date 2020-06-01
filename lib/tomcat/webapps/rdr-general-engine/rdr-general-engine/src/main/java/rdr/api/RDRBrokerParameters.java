package rdr.api;

import java.util.ArrayList;

public class RDRBrokerParameters 
{
	private ArrayList<Object> params;
	
	public RDRBrokerParameters()
	{
		this.params = new ArrayList<Object>();
	}
	
	public void addObject(Object p)
	{
		this.params.add(p);
	}
	
	public void addObjects(ArrayList<Object> objs)
	{
		this.params.addAll(objs);
	}
	
	public void add(String p)
	{
		this.params.add(p);
	}
	
	public void add(int p)
	{
		this.params.add(new Integer(p));
	}
	
	public void add(double p)
	{
		this.params.add(new Double(p));
	}
	
	public void add(java.util.Date p)
	{
		this.params.add(p);
	}
	
	public int size()
	{
		return this.params.size();
	}
	
	public Object get(int index)
	{
		if (index < this.size())
			return this.params.get(index);
		else 
			return null;
	}
	
	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
		sb.append("BrokerParams [");
		for (int i = 0; i < this.params.size(); i++)
		{
			if (i > 0) sb.append(", ");
			sb.append(this.params.get(i).toString());
		}
		sb.append("]");
		
		return sb.toString();
	}
}
