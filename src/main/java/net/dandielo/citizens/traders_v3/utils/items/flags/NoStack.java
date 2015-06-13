package net.dandielo.citizens.traders_v3.utils.items.flags;

import net.dandielo.citizens.traders_v3.utils.items.StockItemFlag;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;

@Attribute(name = "NoStack", key = ".nostack")
public class NoStack extends StockItemFlag {

	public NoStack(dItem item, String key)
	{
		super(item, key);
	}
}
