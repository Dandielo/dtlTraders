package net.dandielo.citizens.traders_v3.traders.wallet;

import java.util.ArrayList;
import java.util.List;

import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

public class ItemPricing {
	private StockItem sItem;
	
	public ItemPricing(StockItem item)
	{
		sItem = item;
	}
	
	public List<String> getFullPriceDescription() {
		List<String> result = new ArrayList<String>();
		for (ItemAttr attr : sItem.getAttribs("p"))
		{
			
		}
		return result;
	}
}
