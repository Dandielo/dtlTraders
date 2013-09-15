package net.dandielo.citizens.traders_v3.traders.patterns;

import org.bukkit.configuration.ConfigurationSection;

public abstract class Pattern {
	private Type type;
	private String name;
	
	protected int priority = 0;
	protected boolean tier = false;
	
	protected Pattern(String name, Type type)
	{
		this.type = type;
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Type getType()
	{
		return type;
	}
	
	public abstract void loadItems(ConfigurationSection data);
	
	public static enum Type
	{
		PRICE, ITEM, LAYOUT, CHANCE;
		
	    public boolean isItem()
	    {
	    	return this.equals(ITEM);
	    }
	    
	    public boolean isPrice()
	    {
	    	return this.equals(PRICE);
	    }
	    public boolean isLayout()
	    {
	    	return this.equals(LAYOUT);
	    }
	    
	    public boolean isChance()
	    {
	    	return this.equals(CHANCE);
	    }
	}
}
