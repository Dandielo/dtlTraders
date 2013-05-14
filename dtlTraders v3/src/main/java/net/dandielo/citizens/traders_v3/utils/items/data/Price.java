package net.dandielo.citizens.traders_v3.utils.items.data;

import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.ItemData;

public class Price extends ItemData {
	double price = 0.0;
	
	public Price() {
		super("price");
	}
	
	public Price(String key) {
		super(key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getValue(StockItem stockItem) {
		return (T) new Double(price);
	}
	
	public double getPrice()
	{
		return price;
	}

	@Override
	public void assing(ItemStack flags) {
	}

	@Override
	public void load(String value) {
		price = Double.valueOf(value);
	}

	@Override
	public String save() {
		return String.valueOf(price);
	}

}
