package net.dandielo.citizens.traders_v3.utils.items.attributes;

import java.util.List;

import net.dandielo.citizens.traders_v3.TEntityStatus;
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
status = {TEntityStatus.BUY, TEntityStatus.SELL, TEntityStatus.SELL_AMOUNTS, TEntityStatus.MANAGE_PRICE})
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
	public void onStatusLoreRequest(TEntityStatus status, List<String> lore)
	{
		if ( !status.inManagementMode() ) return;

		//add the lore to the item
		for ( String pLore : LocaleManager.locale.getLore("item-unitPrice") )
			lore.add(pLore.replace("{price}", String.format("%.2f", price)).replace(',', '.'));
	}
	
	public double getTotalPrice(TransactionInfo info) {
		return info.getTotalScaling() * price;
	}

	@Override
	public boolean finalizeTransaction(TransactionInfo info) {
		info.getSeller().deposit(info.getTotalScaling() * price);
		return info.getBuyer().withdraw(info.getTotalScaling() * price);
	}

	@Override
	public boolean allowTransaction(TransactionInfo info) {
		if (price < 0 || info.getBuyer() == null)
			return false;
		return info.getBuyer().check(info.getTotalScaling() * price);
	}

	@Override
	public void getDescription(TransactionInfo info, List<String> lore) {
		ChatColor mReqColor = allowTransaction(info) ? ChatColor.GREEN : ChatColor.RED;
		
		//add the Price lore
		for ( String pLore : LocaleManager.locale.getLore("item-price") )
			lore.add(pLore
				.replace("{price}", mReqColor + String.format("%.2f", info.getTotalScaling() * price))
				.replace(',', '.')
			);
	}

	@Override
	public String getName() {
		return "Virtual money";
	} 
}
