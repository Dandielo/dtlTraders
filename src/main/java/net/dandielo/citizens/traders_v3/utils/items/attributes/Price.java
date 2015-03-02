package net.dandielo.citizens.traders_v3.utils.items.attributes;

import java.util.List;

import net.dandielo.citizens.traders_v3.tNpcStatus;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.dB.DebugLevel;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.traders.transaction.CurrencyHandler;
import net.dandielo.citizens.traders_v3.traders.transaction.TransactionInfo;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

@Attribute(
name="Price", key = "p", standalone = true, priority = 0,
status = {tNpcStatus.BUY, tNpcStatus.SELL, tNpcStatus.SELL_AMOUNTS, tNpcStatus.MANAGE_PRICE})
public class Price extends ItemAttr implements CurrencyHandler {
    //public static String lorePattern = ChatColor.GOLD + "Price: " + ChatColor.GRAY;
	//private static Econ econ = Econ.econ;
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
	public void onStatusLoreRequest(tNpcStatus status, List<String> lore)
	{
		//If not in manager mode then we don't want to manage this request
		//Maybe later just update the Attribute settings?
		if ( !status.inManagementMode() ) return;

		//add the lore to the item
		for ( String pLore : LocaleManager.locale.getLore("item-unitPrice") )
			lore.add(pLore.replace("{price}", String.format("%.2f", price)).replace(',', '.'));
	}
	
//	public static List<String> playerLoreRequest(double price, List<String> lore, tNpcStatus status)
//	{
//		return loreRequest(price, lore, status, "player-");
//	}
//	
//	//This is here ONLY because I KNOW that when I need to debug anything 
//	//price related I will look in the Price Attribute automatically
//	public static List<String> loreRequest(double price, List<String> lore, tNpcStatus status)
//	{
//		return loreRequest(price, lore, status, "");
//	}
//	
//	protected static List<String> loreRequest(double price, List<String> lore, tNpcStatus status, String prefix)
//	{
//		if ( price < 0 ) return lore;
//		
//		//add the Price lore
//		for ( String pLore : LocaleManager.locale.getLore("item-price") )
//			lore.add(pLore.replace("{price}", String.format("%.2f", price)).replace(',', '.'));
//
//		//add additional click info lore
//		lore.addAll(LocaleManager.locale.getLore("item-" + prefix + status));
//		return lore;
//	}

	@Override
	public boolean finalizeTransaction(TransactionInfo info) {
		info.getSeller().deposit(info.getTotalScaling() * price);
		return info.getBuyer().withdraw(info.getTotalScaling() * price);
	}

	@Override
	public boolean allowTransaction(TransactionInfo info) {
		if (price < 0)
			return false;
		return info.getBuyer().check(info.getTotalScaling() * price);
	}

	@Override
	public void getDescription(TransactionInfo info, List<String> lore) {
		//String status = info.getStock().name().toLowerCase();
		ChatColor mReqColor = allowTransaction(info) ? ChatColor.GREEN : ChatColor.RED;
		
		//add the Price lore
		for ( String pLore : LocaleManager.locale.getLore("item-price") )
			lore.add(pLore
				.replace("{price}", mReqColor + String.format("%.2f", info.getTotalScaling() * price))
				.replace(',', '.')
			);

		//add additional click info lore
		//lore.addAll(LocaleManager.locale.getLore("item-" + prefix + status));
	}

	@Override
	public String getName() {
		return "Virtual money";
	} 
}
