package net.dandielo.citizens.traders_v3.utils.items.attributes;

import java.util.List;

import net.dandielo.citizens.traders_v3.TEntityStatus;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.dB.DebugLevel;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.traders.transaction.CurrencyHandler;
import net.dandielo.citizens.traders_v3.traders.transaction.TransactionInfo;
import net.dandielo.citizens.traders_v3.utils.items.StockItemAttribute;
import net.dandielo.core.items.dItem;

import org.bukkit.ChatColor;

@net.dandielo.core.items.serialize.Attribute(
		name="Price", key = "p", standalone = true, priority = 0)
public class Price extends StockItemAttribute implements CurrencyHandler {
	private double price;

	public Price(dItem item, String key) {
		super(item, key);
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
	public boolean deserialize(String data)
	{
		try
		{
			price = Double.parseDouble(data);
		}
		catch(NumberFormatException e)
		{
			dB.spec(DebugLevel.S2_MAGIC_POWA, "A exception occured when parsing the price");
			return false;
		}
		return true;
	}

	@Override
	public String serialize()
	{
		return String.format("%.2f", price).replace(',', '.');
	}	

	@Override
	public void getDescription(TEntityStatus status, List<String> lore)
	{
		if (!status.inManagementMode()) return;

		//add the lore to the item
		double totalPrice = item.hasFlag(".sp") ? price : price * item.getAmount();
		for ( String pLore : LocaleManager.locale.getLore("item-price-summary") )
			lore.add(pLore
					.replace("{unit}", String.format("%.2f", price)).replace(',', '.')
					.replace("{total}", String.format("%.2f", totalPrice)).replace(',', '.')
				);
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
