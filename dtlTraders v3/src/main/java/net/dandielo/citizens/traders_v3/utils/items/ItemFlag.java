package net.dandielo.citizens.traders_v3.utils.items;

import java.util.HashMap;
import java.util.Map;

import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.core.exceptions.InvalidDataNodeException;

import org.bukkit.inventory.ItemStack;

public abstract class ItemFlag {
    private final String key;
	
	public ItemFlag(String key)
	{
		this.key = key;
	}
	
	public abstract boolean getValue();
	public abstract void assing(ItemStack item);
	
	public String getKey()
	{
		return key;
	}
	
	@Override
	public int hashCode()
	{
		return key.hashCode();
	}
	
	//getting item datas
	private final static Map<DataNode, Class<? extends ItemFlag>> data = new HashMap<DataNode, Class<? extends ItemFlag>>();
	
	public final static void registerData(Class<? extends ItemFlag> clazz) throws InvalidDataNodeException
	{
		if ( !clazz.isAnnotationPresent(DataNode.class) )
			throw new InvalidDataNodeException();
		
		data.put(clazz.getAnnotation(DataNode.class), clazz);
		
		//TODO registration info
	}
	
	public final static ItemFlag createItemFlag(String key) throws InvalidDataNodeException
	{
		DataNode nodeInfo = null;
		for ( DataNode info : data.keySet() )
			if ( info.saveKey().equals(key) )
				nodeInfo = info;
		try 
		{
			ItemFlag itemData = data.get(nodeInfo).getConstructor(String.class).newInstance(key);
			return itemData;
		} 
		catch (Exception e) 
		{
			DtlTraders.warning("Item flag could not be read, invalid flag set! Key: " + key);
			throw new InvalidDataNodeException();
		}
	}
}
