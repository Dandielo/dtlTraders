package net.dandielo.citizens.traders_v3.utils.items.attributes;

import net.dandielo.citizens.traders_v3.utils.items.StockItemAttribute;
import net.dandielo.core.items.dItem;

@net.dandielo.core.items.serialize.Attribute(
		name = "Multiplier", key = "m", standalone = true, priority = 0)
public class Multiplier extends StockItemAttribute {
	private double multiplier;
	
	public Multiplier(dItem item, String key)
	{
		super(item, key);
	}
	
	public double getMultiplier()
	{
		return multiplier;
	}

	@Override
	public boolean deserialize(String data) 
	{
		try
		{
		    multiplier = Double.parseDouble(data);
		}
		catch( Exception e )
		{
			return false;
		}
		return true;
	}

	@Override
	public String serialize()
	{
		return String.format("%.2f", multiplier);
	}
}
