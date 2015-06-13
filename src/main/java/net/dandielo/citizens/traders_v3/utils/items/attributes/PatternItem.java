package net.dandielo.citizens.traders_v3.utils.items.attributes;

import net.dandielo.citizens.traders_v3.utils.items.StockItemAttribute;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;

@Attribute(name = "PatternItem", key = "pat", standalone = true)
public class PatternItem extends StockItemAttribute {
	private int priority = 0;
	
	public PatternItem(dItem item, String key)
	{
		super(item, key);
	}

	@Override
	public boolean deserialize(String data)
	{
		priority = Integer.parseInt(data);
		return true;
	}

	@Override
	public String serialize()
	{
		return String.valueOf(priority);
	}

	@Override
	public boolean similar(ItemAttribute attr)
	{
		return equals(attr);
	}
	
	@Override
	public boolean equals(ItemAttribute attr)
	{
		return priority >= ((PatternItem)attr).priority;
	}
}
