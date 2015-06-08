package net.dandielo.citizens.traders_v3.utils.items.flags;

import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.StockItemFlag;
import net.dandielo.core.items.serialize.Attribute;

@Attribute(name = "NoStack", key = ".nostack")
public class NoStack extends StockItemFlag {

	public NoStack(StockItem item, String key)
	{
		super(item, key);
	}
}
