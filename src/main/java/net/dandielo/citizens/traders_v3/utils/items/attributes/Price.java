package net.dandielo.citizens.traders_v3.utils.items.attributes;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.traders.Trader.Status;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

@Attribute(
name="Price", key = "p", 
status = {Status.BUY, Status.SELL, Status.SELL_AMOUNTS, Status.MANAGE_PRICE})
public class Price extends ItemAttr {
	private double price;

	public Price(String key) {
		super(key);
		price = 0.0;
	}
	
	public double getPrice()
	{
		return price;
	}

	public void increase(double value)
	{
		price += value;
	}
	
	public void decrease(double value)
	{
		price = (price -= value) < 0 ? 0 : price;
	}

	@Override
	public void onLoad(String data) throws AttributeInvalidValueException 
	{
		try
		{
			price = Double.parseDouble(data);
		}
		catch(NumberFormatException e)
		{
			throw new AttributeInvalidValueException(getInfo(), data);
		}
	}

	@Override
	public String onSave()
	{
		return String.format("%.2f", price);
	}

	@Override
	public void onAssign(ItemStack item) 
	{
	}

	@Override
	public void onFactorise(ItemStack item)	throws AttributeValueNotFoundException 
	{
		throw new AttributeValueNotFoundException();
	}
	
	@Override
	public void onStatusLoreRequest(Status status, List<String> lore)
	{
		//TODO handle this request
	}
	
}
