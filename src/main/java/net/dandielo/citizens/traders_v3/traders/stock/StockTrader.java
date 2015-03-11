package net.dandielo.citizens.traders_v3.traders.stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.traders_v3.TEntityStatus;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.traders.setting.GlobalSettings;
import net.dandielo.citizens.traders_v3.utils.NBTUtils;
import net.dandielo.citizens.traders_v3.utils.items.attributes.PatternItem;
import net.dandielo.citizens.traders_v3.utils.items.flags.Lore;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class StockTrader extends Stock {

	public StockTrader(Settings settings)
	{
		super(settings);
	}

	//stock operations
	public void addItem(StockItem item, String stock)
	{
		this.stock.get(stock).add(item);
	}

	public void removeItem(StockItem item, String stock)
	{
		this.stock.get(stock).remove(item);
	}

	//stock load and save
	@Override
	@SuppressWarnings("unchecked")
	public void load(DataKey data) 
	{
		//debug info
		dB.info("Loading trader specific stock");
		if ( data.keyExists("sell") )
		{
			for ( Object item : (List<Object>) data.getRaw("sell") ) 
			{
				if ( item instanceof String )
				{
					StockItem stockItem = new StockItem((String)item);
					if ( stockItem.getSlot() < 0 )
						stock.get("sell").add(stockItem);
					else
						stock.get("sell").add(0, stockItem);
				}
				else
				{
					StockItem stockItem = null;
					for ( Map.Entry<String, List<String>> entry : ((Map<String, List<String>>) item).entrySet() )
						stockItem = new StockItem(entry.getKey(), entry.getValue());

					if ( stockItem.getSlot() < 0 )
						stock.get("sell").add(stockItem);
					else
						stock.get("sell").add(0, stockItem);
				}
			}
		}

		if ( data.keyExists("buy") ) 
		{
			for ( Object item :  (List<Object>) data.getRaw("buy") )
			{
				if ( item instanceof String )
				{
					StockItem stockItem = new StockItem((String)item);
					if ( stockItem.getSlot() < 0 )
						stock.get("buy").add(stockItem);
					else
						stock.get("buy").add(0, stockItem);
				}
				else
				{
					StockItem stockItem = null;
					for ( Map.Entry<String, List<String>> entry : ((Map<String, List<String>>) item).entrySet() )
						stockItem = new StockItem(entry.getKey(), entry.getValue());

					if ( stockItem.getSlot() < 0 )
						stock.get("buy").add(stockItem);
					else
						stock.get("buy").add(0, stockItem);
				}
			}
		}
	}

	@Override
	public void save(DataKey data)
	{
		//debug info
		dB.info("Saving traders stock");

		List<Object> sellList = new ArrayList<Object>();
		for ( StockItem item : stock.get("sell") )
		{
			if ( !item.hasAttr(PatternItem.class) )
			{
				if ( item.hasFlag(Lore.class) )
				{
					Map<String, List<String>> temp = new HashMap<String, List<String>>();
					temp.put(item.toString(), escapeLore(item.getLore()));
					sellList.add(temp);
				}
				else
					sellList.add(item.toString());
			}
		}

		List<Object> buyList = new ArrayList<Object>();
		for ( StockItem item : stock.get("buy") )
		{
			if ( !item.hasAttr(PatternItem.class) )
			{
				if ( item.hasFlag(Lore.class) )
				{
					Map<String, List<String>> temp = new HashMap<String, List<String>>();
					temp.put(item.toString(), escapeLore(item.getLore()));
					buyList.add(temp);
				}
				else
					buyList.add(item.toString());
			}
		}

		data.setRaw("sell", sellList);
		data.setRaw("buy", buyList);
	}
	
	protected List<String> escapeLore(List<String> lore)
	{
		if (lore == null) return null;
		ArrayList<String> escaped = new ArrayList<String>();
		for (String loreLine : lore) {
			escaped.add(loreLine.replace('§', '^'));
		}
		return escaped;
	}

	//stock display
	@Override
	public Inventory getInventory()
	{
		return Bukkit.createInventory(this, getFinalInventorySize(), settings.getStockName());
	}

	@Override
	public Inventory getInventory(TEntityStatus status) {
		Inventory inventory = getInventory();
		setInventory(inventory, status);
		return inventory;
	}

	@Override
	public Inventory getManagementInventory(TEntityStatus baseStatus, TEntityStatus status) {
		Inventory inventory = getInventory();
		setManagementInventory(inventory, baseStatus, status);
		return inventory;
	}

	public void setInventory(Inventory inventory, TEntityStatus status)
	{
		//debug info
		dB.info("Setting inventory, status: ", status.name().toLowerCase());

		//clear the inventory
		inventory.clear();
		for ( StockItem item : this.stock.get(status.asStock()) )
		{
			if (  item.getSlot() < 0 )
				item.setSlot(inventory.firstEmpty());

			//set the lore
			ItemStack itemStack = item.getItem(false, item.getTempLore(status));

			//set the item 
			inventory.setItem(item.getSlot(), NBTUtils.markItem(itemStack));
		}
		setUi(inventory, null, status);
	}

	public void setAmountsInventory(Inventory inventory, TEntityStatus status, StockItem item)
	{
		//debug info
		dB.info("Setting inventory, status: ", TEntityStatus.SELL_AMOUNTS.name().toLowerCase());

		//clear the inventory
		inventory.clear();
		for ( Integer amount : item.getAmounts() )
		{
			//set new amount
			ItemStack itemStack = item.getItem(false, item.getTempLore(status));
			itemStack.setAmount(amount);

			//set the item
			inventory.setItem(inventory.firstEmpty(), NBTUtils.markItem(itemStack));
		}
		setUi(inventory, null, TEntityStatus.SELL_AMOUNTS);
	}

	public void setManagementInventory(Inventory inventory, TEntityStatus baseStatus, TEntityStatus status)
	{
		//debug info
		dB.info("Setting management inventory, status: ", status.name().toLowerCase(), ", base status: ", baseStatus.name().toLowerCase());

		//clear the inventory
		inventory.clear();
		for ( StockItem item : this.stock.get(baseStatus.asStock()) )
		{
			dB.spec(dB.DebugLevel.S3_ATTRIB, "Set inv: ", item);
			if ( !item.hasAttr(PatternItem.class) )
			{
				if ( item.getSlot() < 0 )
					item.setSlot(inventory.firstEmpty());

				//set the lore
				ItemStack itemStack = item.getItem(false, item.getTempLore(status));

				dB.spec(dB.DebugLevel.S3_ATTRIB, "End item: ", item);
				//set the item 
				inventory.setItem(item.getSlot(), NBTUtils.markItem(itemStack));
			}
		}
		setUi(inventory, baseStatus, status);
	}

	public void setUi(Inventory inventory, TEntityStatus baseStatus, TEntityStatus status)
	{
		Map<String, ItemStack> items = GlobalSettings.getUiItems();

		//TODO mark items
		//Switch betwean all status values
		switch(status)
		{
		case SELL:
			// Don't show buy-action when npc doesn't buy anything 
			if (this.stock.get("buy") != null && this.stock.get("buy").size() > 0) {
				inventory.setItem(this.getFinalInventorySize() - 1, items.get("buy"));
			}
			break;
		case SELL_AMOUNTS:
			inventory.setItem(this.getFinalInventorySize() - 1, items.get("back"));
			break;
		case BUY:
			inventory.setItem(this.getFinalInventorySize() - 1, items.get("sell"));
			break;
		case MANAGE_SELL:
			inventory.setItem(this.getFinalInventorySize() - 1, items.get("buy"));
			inventory.setItem(this.getFinalInventorySize() - 2, items.get("price"));
			inventory.setItem(this.getFinalInventorySize() - 3, items.get("limit"));
			inventory.setItem(this.getFinalInventorySize() - 4, items.get("unlock"));
			break;
		case MANAGE_BUY:
			inventory.setItem(this.getFinalInventorySize() - 1, items.get("sell"));
			inventory.setItem(this.getFinalInventorySize() - 2, items.get("price"));
			inventory.setItem(this.getFinalInventorySize() - 3, items.get("limit"));
			inventory.setItem(this.getFinalInventorySize() - 4, items.get("unlock"));
			break;
		case MANAGE_UNLOCKED:
			inventory.setItem(this.getFinalInventorySize() - 4, items.get("lock"));
			break;
		case MANAGE_PRICE:
			inventory.setItem(this.getFinalInventorySize() - 2, items.get("back"));
			inventory.setItem(this.getFinalInventorySize() - 1, items.get(Stock.opositeStock(baseStatus.asStock())));
			break;
		case MANAGE_AMOUNTS:
			inventory.setItem(this.getFinalInventorySize() - 1, items.get("back"));
			break;
		case MANAGE_LIMIT:
			inventory.setItem(this.getFinalInventorySize() - 3, items.get("back"));
			inventory.setItem(this.getFinalInventorySize() - 2, items.get("plimit"));
			inventory.setItem(this.getFinalInventorySize() - 1, items.get(Stock.opositeStock(baseStatus.asStock())));
			break;
		case MANAGE_PLIMIT:
			inventory.setItem(this.getFinalInventorySize() - 3, items.get("back"));
			inventory.setItem(this.getFinalInventorySize() - 2, items.get("limit"));
			inventory.setItem(this.getFinalInventorySize() - 1, items.get(Stock.opositeStock(baseStatus.asStock())));
		default:
			break;
		}
	}
}
