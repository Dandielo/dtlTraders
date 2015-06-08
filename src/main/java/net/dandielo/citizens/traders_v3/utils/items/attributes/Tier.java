package net.dandielo.citizens.traders_v3.utils.items.attributes;

import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.StockItemAttribute;
import net.dandielo.core.items.serialize.Attribute;

@Attribute(name="Tier", key = "t", priority = 25, standalone = true)
public class Tier extends StockItemAttribute {
	private String tier;
	
	public Tier(StockItem item, String key)
	{
		super(item, key);
	}

	@Override
	public boolean deserialize(String data)
	{
		tier = data;
		return true;
	}

	@Override
	public String serialize()
	{
		return tier;
	}
}
