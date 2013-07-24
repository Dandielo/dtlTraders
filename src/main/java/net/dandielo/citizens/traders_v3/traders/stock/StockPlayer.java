package net.dandielo.citizens.traders_v3.traders.stock;

import java.util.List;

import net.dandielo.citizens.traders_v3.tNpcStatus;
import net.dandielo.citizens.traders_v3.bukkit.Perms;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.traders.patterns.Pattern;
import net.dandielo.citizens.traders_v3.traders.patterns.Pattern.Type;
import net.dandielo.citizens.traders_v3.traders.patterns.PatternManager;
import net.dandielo.citizens.traders_v3.traders.patterns.types.Price.PriceMatch;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.utils.NBTUtils;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Price;
import net.dandielo.citizens.traders_v3.utils.items.flags.StackPrice;

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
			ItemStack itemStack = item.getItem();
			ItemMeta meta = itemStack.getItemMeta();
			
			List<String> lore = item.getTempLore(status, itemStack.clone());
			//if ( !item.hasPrice() )
			lore = Price.loreRequest(parsePrice(item, status.asStock(), item.getAmount()), lore);
			meta.setLore(lore);
			
			itemStack.setItemMeta(meta);
			
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
			ItemStack itemStack = item.getItem();
			itemStack.setAmount(amount);

			//set the lore
			ItemMeta meta = itemStack.getItemMeta();
			
			List<String> lore = item.getTempLore(status, itemStack.clone());
			//if ( !item.hasPrice() )
			lore = Price.loreRequest(parsePrice(item, "sell", amount), lore);
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
	
	@Override
	public double parsePrice(StockItem item, int amount) 
	{
		return -1.0;
	}
	
	/* price methods */
	@Override
	public double parsePrice(StockItem item, String stock, int amount) 
	{
		PriceMatch match = new PriceMatch();

		if ( settings.getPatterns() != null && !settings.getPatterns().isEmpty() )
		{
			for ( Pattern pattern : PatternManager.getPatterns(settings.getPatterns()) )
			{
				if ( pattern.getType().equals(Type.PRICE) &&
						Perms.hasPerm(player, "dtl.trader.patterns." + pattern.getName()) )
					match.merge(((net.dandielo.citizens.traders_v3.traders.patterns.types.Price)pattern).findPriceFor(player, stock, item));
			}
		}
		else
		{
			match.price(item.getPrice());
			match.multiplier(item.getMultiplier());
		}

		if ( item.hasFlag(StackPrice.class) )
			return match.finalPrice();
		return match.finalPrice() * amount;
	}
}
