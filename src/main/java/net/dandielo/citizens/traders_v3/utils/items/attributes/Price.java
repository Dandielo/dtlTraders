package net.dandielo.citizens.traders_v3.utils.items.attributes;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.tNpcStatus;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;
import net.dandielo.citizens.traders_v3.utils.items.flags.StackPrice;

@Attribute(
name="Price", key = "p", standalone = true,
status = {tNpcStatus.BUY, tNpcStatus.SELL, tNpcStatus.SELL_AMOUNTS, tNpcStatus.MANAGE_PRICE})
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
		return String.format("%.2f", price).replace(',', '.');
	}

	@Override
	public void onAssign(ItemStack item) 
	{
	}

	@Override
	public void onFactorize(ItemStack item)	throws AttributeValueNotFoundException 
	{
		throw new AttributeValueNotFoundException();
	}
	
	@Override
	public void onStatusLoreRequest(tNpcStatus status, ItemStack target, List<String> lore)
	{
		//as because this is the price attribute it can only apply for stock items
		StockItem item = (StockItem) this.item;
		
		double m;
		//has the item the stack price flag?
		if ( item.hasFlag(StackPrice.class) )
			m = 1;
		else
			m = item.getAmount();

		//assign a multiplier to each item sold in the amounts inventory
		if ( status.equals(tNpcStatus.SELL_AMOUNTS) )
		{
			m *= (double) target.getAmount() / (double) item.getAmount();
		}
		//multiplier for items in player stock
		else
		{
		    m *= (int) ( target.getAmount() / item.getAmount() );
		}
		
		lore.add(ChatColor.GOLD + "Price: " + ChatColor.GRAY + 
				String.format("%.2f", price*m).replace(',', '.'));
		
	}
	
}
