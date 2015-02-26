package net.dandielo.citizens.traders_v3.utils.items.attributes;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.tNpcStatus;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.traders.wallet.TransactionHandler;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

@Attribute(key = "p", sub = {"b"}, name = "Block Price", standalone = true, priority = 0,
status = {tNpcStatus.BUY, tNpcStatus.SELL, tNpcStatus.SELL_AMOUNTS, tNpcStatus.MANAGE_PRICE})
public class BlockCurrency extends ItemAttr implements TransactionHandler {
	private ItemStack is;
	private int amount;

	public BlockCurrency(String key, String sub) {
		super(key, sub);
	}

	@Override
	public boolean onCompleteTransaction(Player player, StockItem sItem, String stock, int amount) {
		boolean result = false;
		int endAmount = amount * this.amount;
		ItemStack clone = is.clone();
		clone.setAmount(endAmount);
		
		if (stock == "sell")
		{
			ItemStack[] contents = player.getInventory().getContents();
			for (int i = 0; i < contents.length && endAmount > 0; ++i)
			{
				ItemStack nItem = contents[i];
				if (nItem != null && nItem.isSimilar(clone))
				{
					int diff = endAmount - nItem.getAmount();
					if (diff < 0)
						nItem.setAmount(-diff);
					else
						player.getInventory().setItem(i, null);
					endAmount = diff;
				}
			}
			result = true;
		}
		else if (stock == "buy")
		{			
			player.getInventory().addItem(clone);
			result = true;
		}
		return result;
	}

	@Override
	public boolean onCheckTransaction(Player player, StockItem item, String stock, int amount) {
		boolean result = false;
		int endAmount = amount * this.amount;
		if (stock == "sell")
		{
			result = player.getInventory().containsAtLeast(is, endAmount);
		}
		else if (stock == "buy")
		{			
			ItemStack[] contents = player.getInventory().getContents();
			for (int i = 0; i < contents.length && endAmount > 0; ++i)
			{
				if (contents[i] == null)
					endAmount -= 64;
				else
				if (contents[i].isSimilar(is))
					endAmount -= contents[i].getAmount(); 
			}
		}
		return result;
	}

	@Override
	public void onPriceLoreRequest(Player pl, StockItem it, String st, int am, List<String> lore) {
		

		for ( String pLore : LocaleManager.locale.getLore("item-currency-price") )
			lore.add(
					pLore
					    .replace("{amount}", String.valueOf(amount * am))
					    .replace("{text}", " block of ")
					    .replace("{currency}", is.getType().name().toLowerCase())
					);
		
		//String res = "&6Costs: &7" + String.valueOf(amount * am) + " &6blocks of &7" + is.getType().name().toLowerCase();
		//lore.add(res.replace('&', '§'));
	}

	@Override
	public void onLoad(String data) throws AttributeInvalidValueException {
		String[] info = data.split("-");
		is = new ItemStack(Material.getMaterial(info[0].toUpperCase()));
		amount = Integer.parseInt(info[1]);
	}

	@Override
	public String onSave() {
		return is.getType().name().toLowerCase() + "-" + String.valueOf(amount);
	}

	@Override
	public void onFactorize(ItemStack item)
			throws AttributeValueNotFoundException {
		throw new AttributeValueNotFoundException();
	}

}
