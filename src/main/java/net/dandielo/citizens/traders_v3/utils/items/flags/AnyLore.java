package net.dandielo.citizens.traders_v3.utils.items.flags;

import net.dandielo.citizens.traders_v3.utils.items.StockItemFlag;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;


@Attribute(name="Anylore", key=".anylore", standalone = true)
public class AnyLore extends StockItemFlag {
	public AnyLore(dItem item, String key) {
		super(item, key);
	}
}
