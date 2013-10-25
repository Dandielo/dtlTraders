package net.dandielo.citizens.traders_v3.utils.items.attributes;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.dB.DebugLevel;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.tNpcStatus;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

@Attribute(
name="Price", key = "p", standalone = true, priority = 0,
status = {tNpcStatus.BUY, tNpcStatus.SELL, tNpcStatus.SELL_AMOUNTS, tNpcStatus.MANAGE_PRICE})
public class Price extends ItemAttr {
    public static String lorePattern = ChatColor.GOLD + "Price: " + ChatColor.GRAY;
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
	
	public void setPrice(double value)
	{
		price = value < 0 ? 0 : value;
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
			dB.spec(DebugLevel.S2_MAGIC_POWA, "A exception occured when parsing the price");
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
		//If not in manager mode then we don't want to manage this request
		//Maybe later just update the Attribute settings?
		if ( !status.inManagementMode() ) return;

		//add the lore to the item
		for ( String pLore : LocaleManager.locale.getLore("item-unitPrice") )
			lore.add(pLore.replace("{price}", String.format("%.2f", price)).replace(',', '.'));
	}
	
	//This is here ONLY because I KNOW that when I need to debug anything 
	//price related I will look in the Price Attribute automatically
	public static List<String> loreRequest(double price, List<String> lore, tNpcStatus status)
	{
		if ( price < 0 ) return lore;
		
		//add the Price lore
		for ( String pLore : LocaleManager.locale.getLore("item-price") )
			lore.add(pLore.replace("{price}", String.format("%.2f", price)).replace(',', '.'));

		//add additional click info lore
		lore.addAll(LocaleManager.locale.getLore("item-" + status));
		return lore;
	}
	
	/*
	public static List<String> loreRequest(double price, List<String> lore, tNpcStatus status)
	{
		if ( price < 0 ) return lore;
		
		//add the Price lore
		for ( String pLore : LocaleManager.locale.getLore("item-price") )
			lore.add(pLore.replace("{price}", String.format("%.2f", price)).replace(',', '.'));

		if (status == tNpcStatus.BUY || status == tNpcStatus.SELL) {
    		//add additional click info lore
    		lore.addAll(LocaleManager.locale.getLore("item-" + status));
		}
		return lore;
	}*/
}
