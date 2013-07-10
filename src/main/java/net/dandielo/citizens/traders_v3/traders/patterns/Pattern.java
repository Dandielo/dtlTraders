package net.dandielo.citizens.traders_v3.traders.patterns;

public abstract class Pattern {
	private Type type;
	
	Pattern(Type type)
	{
		this.type = type;
	}
	
	public Type getType()
	{
		return type;
	}
	
	
	static enum Type
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
