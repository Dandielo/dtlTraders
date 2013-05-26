package net.dandielo.citizens.traders_v3.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.traders.stock.StockItem;

public class ItemUtils {

	//durability check
	public static boolean itemHasDurability(ItemStack item)
	{
		int id = item.getTypeId();
		return ( id >= 298 && id <= 317 ) || ( id >= 290 && id <= 294 ) || ( id >= 283 && id <= 286 ) || ( id >= 267 && id <= 279 ) || ( id >= 256 && id <= 259 ) || id == 261;// ? true : false );
	}
	
	public static StockItem createStockItem(ItemStack bItem)
	{
		//creating a clean item
		StockItem sItem = new StockItem(bItem);
		//getting data out of it (by force ;>)
		sItem.factorize(bItem);
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
}
