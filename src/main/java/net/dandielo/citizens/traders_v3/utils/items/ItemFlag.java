package net.dandielo.citizens.traders_v3.utils.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.core.exceptions.InvalidDataNodeException;
import net.dandielo.citizens.traders_v3.traders.Trader.Status;
import net.dandielo.citizens.traders_v3.utils.items.flags.Lore;
import net.dandielo.citizens.traders_v3.utils.items.flags.Multiplier;
import net.dandielo.citizens.traders_v3.utils.items.flags.PatternPrice;
import net.dandielo.citizens.traders_v3.utils.items.flags.StackPrice;

import org.bukkit.inventory.ItemStack;

public abstract class ItemFlag {
    private final String key;
    private DataNode info;
	
	public ItemFlag(String key)
	{
		this.key = key;
	}
	
	public abstract boolean getValue();
	public abstract void assing(ItemStack item);
	public void assignLore(Status status, List<String> lore) { };
	
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
		assignLore(status, lore);
	}
	
	public String getKey()
	{
		return key;
	}
	
	@Override
	public int hashCode()
	{
		return key.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		return true;
	}
	
	//getting item datas
	private final static Map<DataNode, Class<? extends ItemFlag>> data = new HashMap<DataNode, Class<? extends ItemFlag>>();
	
	public final static void registerFlag(Class<? extends ItemFlag> clazz) throws InvalidDataNodeException
	{
		if ( !clazz.isAnnotationPresent(DataNode.class) )
			throw new InvalidDataNodeException();
		
		data.put(clazz.getAnnotation(DataNode.class), clazz);
	}
	
	public final static ItemFlag createItemFlag(String key) throws InvalidDataNodeException
	{
		DataNode nodeInfo = null;
		for ( DataNode info : data.keySet() )
			if ( info.saveKey().equals(key) )
				nodeInfo = info;
		try 
		{
			ItemFlag itemFlag = data.get(nodeInfo).getConstructor(String.class).newInstance(key);
			itemFlag.info = nodeInfo;
			return itemFlag;
		} 
		catch (Exception e) 
		{
			DtlTraders.warning("Item flag could not be read, invalid flag set! Key: " + key);
			throw new InvalidDataNodeException();
		}
	}
	
	public static void registerCoreFlags()
	{
		try
		{
			registerFlag(Lore.class);
			registerFlag(PatternPrice.class);
			registerFlag(StackPrice.class);
			registerFlag(Multiplier.class);
		} 
		catch (InvalidDataNodeException e) 
		{
			DtlTraders.severe("Core item flag values are bugged!");
		}
	}
}
