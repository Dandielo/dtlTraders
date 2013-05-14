package net.dandielo.citizens.traders_v3.utils.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.core.exceptions.InvalidDataNodeException;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;

import org.bukkit.inventory.ItemStack;

public abstract class ItemData {
	private final String key;
	
	public ItemData(String key)
	{
		this.key = key;
	}
	
	public abstract <T> T getValue(StockItem stockItem);
	
	public abstract void assing(ItemStack item);
	public void assignManagerLore(Trader trader, List<String> lore) { }
	
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
	
	//getting item datas
	private final static Map<DataNode, Class<? extends ItemData>> data = new HashMap<DataNode, Class<? extends ItemData>>();
	
	public final static void registerData(Class<? extends ItemData> clazz) throws InvalidDataNodeException
	{
		if ( !clazz.isAnnotationPresent(DataNode.class) )
			throw new InvalidDataNodeException();
		
		data.put(clazz.getAnnotation(DataNode.class), clazz);
		
		//TODO registration info
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
			return itemData;
		} 
		catch (Exception e) 
		{
			DtlTraders.warning("Item data could not be read, invalid data set! Key: " + key + ", value: " + value);
			throw new InvalidDataNodeException();
		}
	}
}
