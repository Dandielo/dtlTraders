package net.dandielo.citizens.traders_v3.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.bankers.tabs.BankItem;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.flags.Abstract;

@SuppressWarnings("deprecation")
public class ItemUtils {

	//durability check
	public static boolean itemHasDurability(ItemStack item)
	{
		int id = item.getTypeId();
		return ( id >= 256 && id <= 259 ) || id == 261 || ( id >= 267 && id <= 279 ) || ( id >= 283 && id <= 286 ) || ( id >= 290 && id <= 294 ) || ( id >= 298 && id <= 317 ) || id == 398;// ? true : false );
	}
	
	public static StockItem createStockItem(ItemStack vItem)
	{
		//creating a clean item
		StockItem sItem = new StockItem(vItem);
		//getting data out of it (by force ;>)
		sItem.factorize(vItem);
		//returning the item
		return sItem;
	}

	public static StockItem createAbstractStockItem(ItemStack vItem)
	{
		//creating a clean item
		StockItem sItem = new StockItem(vItem);
		//set the item as abstract
		sItem.addFlag(".abstract");
		//getting data out of it (by force ;>)
		sItem.factorize(vItem);
		//returning the item
		return sItem;
	}
	
	//create ItemStack
	public static ItemStack createItemStack(String data)
	{
		String[] d = data.split(":", 2);
		
		Material mat = Material.getMaterial(d[0].toUpperCase());
		if ( mat == null )
			mat = Material.getMaterial(Integer.parseInt(d[0]));
		
		if ( d.length > 1 )
			return new ItemStack(mat, 1, Byte.parseByte(d[1]));
		else
			return new ItemStack(mat);
	}

	public static BankItem createBankItem(ItemStack vItem)
	{
		//creating a clean item
		BankItem bItem = new BankItem(vItem);
		
		//getting data out of it (by force ;>)
		bItem.factorize(vItem);
		
		//returning the item
		return bItem;
	}
}
