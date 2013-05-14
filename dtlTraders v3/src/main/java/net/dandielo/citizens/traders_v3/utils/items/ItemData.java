package net.dandielo.citizens.traders_v3.utils.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.core.exceptions.InvalidDataNodeException;
import net.dandielo.citizens.traders_v3.core.exceptions.ItemDataNotFoundException;
import net.dandielo.citizens.traders_v3.traders.Trader.Status;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;

import org.bukkit.inventory.ItemStack;

public abstract class ItemData {
	private final String key;
	private DataNode info;
	
	public ItemData(String key)
	{
		this.key = key;
	}
	
	public abstract <T> T getValue(StockItem stockItem);
	
	public abstract void assing(ItemStack item);
	public abstract void peek(ItemStack item) throws ItemDataNotFoundException;
	
	public void assignDataLore(Status status, List<String> lore) { };
	
	public final void lore(Status status, List<String> lore) 
	{
		//no lore assignment 
		if ( !info.assignLore() ) return;
		
		//check if trader has the required status
		boolean assign = false;
		for ( int i = 0 ; i < info.assignStatus().length && !assign ; ++i )
			assign = info.assignStatus()[i].equals(status);
		
		//wrong status = see ya 
		if ( !assign ) return;
		
		//call the method that can be override
		assignDataLore(status, lore);
	}
	
	public abstract void load(String value);
	public abstract String save();
	
	public final String saveString()
	{
		return toString();
	}
	
	@Override
	public final String toString()
	{
		return key + ":" + save();
	}
	
	@Override
	public int hashCode()
	{
		return key.hashCode();
	}

	public String getKey() {
		return key;
	}
	
	@Override
	public boolean equals(Object o)
	{
		return true;
	}
	
	//getting item datas
	private final static Map<DataNode, Class<? extends ItemData>> data = new HashMap<DataNode, Class<? extends ItemData>>();
	
	//needed to convert ItemStack to StockItem  
	public final static List<ItemData> itemDataList()
	{
		List<ItemData> result = new ArrayList<ItemData>();
		for ( Map.Entry<DataNode, Class<? extends ItemData>> entry : data.entrySet() )
		{
			try 
			{
				result.add(entry.getValue().getConstructor(String.class).newInstance(entry.getKey().saveKey()));
			} 
			catch (Exception e)
			{
				DtlTraders.warning("Item data could not be read, invalid data set!");
			}
		}
		return result;
	}
	
	public final static void registerData(Class<? extends ItemData> clazz) throws InvalidDataNodeException
	{
		if ( !clazz.isAnnotationPresent(DataNode.class) )
			throw new InvalidDataNodeException();
		
		data.put(clazz.getAnnotation(DataNode.class), clazz);
	}
	
	public final static ItemData createItemData(String key, String value) throws InvalidDataNodeException
	{
		DataNode nodeInfo = null;
		for ( DataNode info : data.keySet() )
			if ( info.saveKey().equals(key) )
				nodeInfo = info;
		try 
		{
			ItemData itemData = data.get(nodeInfo).getConstructor(String.class).newInstance(key);
			itemData.load(value);
			itemData.info = nodeInfo;
			return itemData;
		} 
		catch (Exception e) 
		{
			DtlTraders.warning("Item data could not be read, invalid data set! Key: " + key + ", value: " + value);
			throw new InvalidDataNodeException();
		}
	}
}
