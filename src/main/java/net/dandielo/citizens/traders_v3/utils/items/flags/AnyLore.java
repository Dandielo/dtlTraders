package net.dandielo.citizens.traders_v3.utils.items.flags;

import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.StockItemFlag;
import net.dandielo.core.items.serialize.Attribute;


@Attribute(name="Anylore", key=".anylore", standalone = true)
public class AnyLore extends StockItemFlag {
	public AnyLore(StockItem item, String key) {
		super(item, key);
	}
}
