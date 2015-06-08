package net.dandielo.citizens.traders_v3.utils.items.flags;

import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.StockItemFlag;
import net.dandielo.core.items.serialize.Attribute;

@Attribute(name = "Regex", key=".regex", standalone = true)
public class Regex extends StockItemFlag {
	public Regex(StockItem item, String key) {
		super(item, key);
	}
}
