package net.dandielo.citizens.traders_v3.utils.items;

//modifier class
public class Modifier
{
	private String name;
	private int operation;
	private double value;
	
	public Modifier(String[] args)
	{
		name = args[0];
		value = Double.parseDouble(args[1]);
		operation = Integer.parseInt(args[2]); 
	}
	public Modifier(String name, double val, int op)
	{
		this.name = name;
		value = val;
		operation = op;
	}
	
	public String getName() { return name; }
	public double getValue() { return value; }
	public int getOperation() { return operation; } 
	
	public String toString()
	{
		return name + "/" + String.format("%.1f", value) + "/" + operation; 
	}
}