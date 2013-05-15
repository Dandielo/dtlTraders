package net.dandielo.citizens.traders_v3.traders.stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.traders_v3.traders.Trader.Status;
import net.dandielo.citizens.traders_v3.traders.setting.TGlobalSettings;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class StockTrader extends Stock {

	public StockTrader(String name, int size)
	{
		super(name, size);
	}

	public StockPlayer toPlayerStock(Player player)
	{
		StockPlayer stock = new StockPlayer(name, size, player);
		return stock;
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

	public StockItem getItem(int slot, String stock)
	{
		StockItem resultItem = null;
		for ( StockItem stockItem : this.stock.get(stock) )
			if ( stockItem.getSlot() == slot )
				resultItem = stockItem;
		return resultItem;
	}

	//stock load and save
	@Override
	@SuppressWarnings("unchecked")
	public void load(DataKey data) 
	{
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
		List<Object> sellList = new ArrayList<Object>();
		for ( StockItem item : stock.get("sell") )
			if ( item.hasFlag(".lore") )
			{
				Map<String, List<String>> temp = new HashMap<String, List<String>>();
				temp.put(item.toString(), item.getLore());
				sellList.add(temp);
			}
			else
				sellList.add(item.toString());

		List<Object> buyList = new ArrayList<Object>();
		for ( StockItem item : stock.get("buy") )
			if ( item.hasFlag(".lore") )
			{
				Map<String, List<String>> temp = new HashMap<String, List<String>>();
				temp.put(item.toString(), item.getLore());
				buyList.add(temp);
			}
			else
				buyList.add(item.toString());

		data.setRaw("sell", sellList);
		data.setRaw("buy", buyList);
	}

	//stock display
	@Override
	public Inventory getInventory()
	{
		return Bukkit.createInventory(this, getFinalInventorySize(), name);
	}

	@Override
	public Inventory getInventory(Status status) {
		Inventory inventory = getInventory();
		setInventory(inventory, status);
		return inventory;
	}

	@Override
	public Inventory getManagementInventory(Status status) {
		Inventory inventory = getInventory();
		setManagementInventory(inventory, status);
		return inventory;
	}

	public void setInventory(Inventory inventory, Status status)
	{
		//clear the inventory
		inventory.clear();
		for ( StockItem item : this.stock.get(status.asStock()) )
		{
			if ( !item.hasSlot() || item.getSlot() < 0 )
				item.setSlot(inventory.firstEmpty());

			//set the lore
			ItemStack itemStack = item.getItem();
		//	itemStack.getItemMeta().setLore(item.getDataLore(status));
			inventory.setItem(item.getSlot(), itemStack);
		}
		setUi(inventory, status);
	}

	public void setManagementInventory(Inventory inventory, Status status)
	{
		//clear the inventory
		inventory.clear();
		for ( StockItem item : this.stock.get(status.asStock()) )
		{
			if ( !item.hasSlot() || item.getSlot() < 0 )
				item.setSlot(inventory.firstEmpty());

			//set the lore
			ItemStack itemStack = item.getItem();
		//	itemStack.getItemMeta().setLore(item.getDataLore(status));
			inventory.setItem(item.getSlot(), itemStack);
		}
		setUi(inventory, status);
	}

	public void setUi(Inventory inventory, Status status)
	{
		Map<String, ItemStack> items = TGlobalSettings.getUiItems();

		//Switch betwean all status values
		switch(status)
		{
		case SELL:
			inventory.setItem(this.getFinalInventorySize() - 1, items.get("buy"));
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
			break;
		case MANAGE_BUY:
			inventory.setItem(this.getFinalInventorySize() - 1, items.get("sell"));
			inventory.setItem(this.getFinalInventorySize() - 2, items.get("price"));
			inventory.setItem(this.getFinalInventorySize() - 3, items.get("limit"));
			break;
		case MANAGE_PRICE:
			inventory.setItem(this.getFinalInventorySize() - 1, items.get("back"));
			break;
		case MANAGE_AMOUNTS:
			inventory.setItem(this.getFinalInventorySize() - 1, items.get("back"));
			break;
		case MANAGE_LIMITS:
			inventory.setItem(this.getFinalInventorySize() - 1, items.get("back"));
		default:
			break;
		}
	}
	
	//public void setInventory(Inventory inventory, String stock)
	//{
	/*	if ( !s.isManaging() )
		{
			for( StockItem item : stock.get(s.toString()) ) 
			{
				ItemStack chk = setLore(item.getItemStack(), getPriceLore(item, 0, s.toString(), patterns, player));

				if ( item.getSlot() < 0 )
            		item.setSlot(inventory.firstEmpty());

	            inventory.setItem( item.getSlot() ,chk);

	        }
			if ( !stock.get( opositeStock(s.toString()) ).isEmpty() )
	        	inventory.setItem(stockSize - 1, itemsConfig.getItemManagement( opositeStock(s.toString()) ) );

		} 
		else 
		{
			for( StockItem item : stock.get(s.toString()) )
			{
				ItemStack chk = setLore(item.getItemStack(), getLore(type, item, s.toString(), patterns, player));

	            if ( item.getSlot() < 0 )
            		item.setSlot(inventory.firstEmpty());
	            inventory.setItem( item.getSlot() ,chk);

	        }
            inventory.setItem(stockSize - 3, itemsConfig.getItemManagement(4) );
            inventory.setItem(stockSize - 2, itemsConfig.getItemManagement(2) );
            inventory.setItem(stockSize - 1, itemsConfig.getItemManagement(opositeStock(s.toString())) );
		} 

		return inventory;*/
	//	inventory.clear();
	//}

}
