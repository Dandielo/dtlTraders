package net.dandielo.citizens.traders_v3.utils.items.flags;

import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.StockItemFlag;
import net.dandielo.core.items.serialize.Attribute;

@Attribute(  
name="StackPrice", key = ".sp", standalone = true)
//status = {TEntityStatus.MANAGE_SELL, TEntityStatus.MANAGE_BUY})
public class StackPrice extends StockItemFlag {

	public StackPrice(StockItem item, String key) {
		super(item, key);
	}
}
