package net.dandielo.citizens.traders_v3.utils.items.data;

import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.core.exceptions.InvalidDataAssignmentException;
import net.dandielo.citizens.traders_v3.core.exceptions.ItemDataNotFoundException;
import net.dandielo.citizens.traders_v3.traders.Trader.Status;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.DataNode;
import net.dandielo.citizens.traders_v3.utils.items.ItemData;

@DataNode(
name="Price", 
saveKey = "p",
byDefault = false,
assignLore = true,
assignStatus = {Status.BUY, Status.SELL, Status.SELL_AMOUNTS, Status.MANAGE_PRICE})
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
	public void assing(ItemStack item) {
	}

	@Override
	public void checkItemCompatibility(ItemStack item)
			throws InvalidDataAssignmentException {
	}

	@Override
	public void load(String value) {
		price = Double.valueOf(value);
	}

	@Override
	public String save() {
		return String.valueOf(price);
	}

	@Override
	public void peek(ItemStack item) throws ItemDataNotFoundException {
	}

}
