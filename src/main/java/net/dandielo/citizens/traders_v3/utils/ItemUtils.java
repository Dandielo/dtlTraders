package net.dandielo.citizens.traders_v3.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.core.exceptions.ItemDataNotFoundException;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.ItemData;
import net.dandielo.citizens.traders_v3.utils.items.flags.Lore;

public class ItemUtils {

	//durability check
	public static boolean itemHasDurability(ItemStack item)
	{
		int id = item.getTypeId();
		return ( id > 275 && id < 289 ) || ( id > 291 && id < 296 ) || ( id > 298 && id < 304 ) || ( id > 306 && id < 326 );// ? true : false );
	}
	
	public static StockItem createStockItem(ItemStack bItem)
	{
		StockItem sItem = new StockItem(bItem);
		for ( ItemData data : ItemData.itemDataList() )
		{
			try 
			{
				data.peek(bItem);
				sItem.addData(data);
			}
			catch (ItemDataNotFoundException e)
			{
				DtlTraders.warning("No data found!");
			}
		}

		//try to the lore
		try 
		{
			Lore lore = new Lore(".lore");
			lore.peek(bItem);
			sItem.addFlag(lore);
		}
		catch (ItemDataNotFoundException e)
		{
			DtlTraders.warning("No lore found!");
		}
		
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
			return new ItemStack(mat, 0, Byte.parseByte(d[1]));
		else
			return new ItemStack(mat);
	}
}
