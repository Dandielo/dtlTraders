package net.dandielo.citizens.traders_v3.traders.stock;

import java.util.List;

import net.dandielo.citizens.traders_v3.tNpcStatus;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.traders.transaction.ShopSession;
import net.dandielo.citizens.traders_v3.utils.NBTUtils;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Limit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StockPlayer extends StockTrader {
	private Player player;
	
	public StockPlayer(Settings settings, Player player) {
		super(settings);
		this.player = player;
	}

	@Override
	public Inventory getInventory(tNpcStatus status) {
		Inventory inventory = getInventory();
		setInventory(inventory, status);
		return inventory;
	}

	@Override
	public void setInventory(Inventory inventory, tNpcStatus status) {
		//debug info
		dB.info("Setting inventory, status: ", status.name().toLowerCase());
		
		//clear the inventory
		inventory.clear();
		for ( StockItem item : this.stock.get(status.asStock()) )
		{
			if (  item.getSlot() < 0 )
				item.setSlot(inventory.firstEmpty());
			
			//set the lore
			List<String> lore = item.getTempLore(status);
			lore = Limit.loreRequest(player.getName(), item, lore, status);
			lore.addAll(new ShopSession(settings, player).getDescription(status.asStock(), item, item.getAmount()));
			
			ItemStack itemStack = item.getItem(false, lore);
			
			//set the item 
			inventory.setItem(item.getSlot(), NBTUtils.markItem(itemStack));
		}
		setUi(inventory, null, status);
	}

	@Override
	public void setAmountsInventory(Inventory inventory, tNpcStatus status, StockItem item) 
	{
		//debug info
		dB.info("Setting inventory, status: ", tNpcStatus.SELL_AMOUNTS.name().toLowerCase());
		
		//clear the inventory
		inventory.clear();
		for ( Integer amount : item.getAmounts() )
		{
			//set new amount
			List<String> lore = item.getTempLore(status);
			lore = Limit.loreRequest(player.getName(), item, lore, status);
			lore.addAll(new ShopSession(settings, player).getDescription("sell", item, amount));
			
			ItemStack itemStack = item.getItem(false, lore);
			itemStack.setAmount(amount);

			//set the lore
			ItemMeta meta = itemStack.getItemMeta();
			
			meta.setLore(lore);
			
			itemStack.setItemMeta(meta);
			
			//set the item
			inventory.setItem(inventory.firstEmpty(), NBTUtils.markItem(itemStack));
		}
		setUi(inventory, null, tNpcStatus.SELL_AMOUNTS);
	}

	public Player getPlayer()
	{
		return player;
	}
	
	//override to support item patterns
	public StockItem getItem(int slot, String stock)
	{
		StockItem resultItem = null;
		for ( StockItem stockItem : this.stock.get(stock) )
			if ( stockItem.getSlot() == slot )
				resultItem = stockItem;
		return resultItem;
	}

	//override to support item patterns
	public StockItem getItem(StockItem item, String stock)
	{
		for ( StockItem sItem : this.stock.get(stock) )
		{
			if ( sItem.equalsWeak(item) )
			{
				return sItem;
			}
		}
		return null;
	}
}
