package net.dandielo.citizens.traders_v3.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.dandielo.citizens.traders_v3.bukkit.CraftBukkitInterface;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Price;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings({"rawtypes", "unchecked"})
public class NBTUtils {	
	
	/*
     * Some static methods for dealing with Minecraft NBT data, which is used to store
     * custom NBT.
     * 
     * All credits to Denizen - Aufdemrand
     */
	
	/** added static loaded classes and methods for later to use */ /***/
	
	//net.minecraft.server.{$VERSION}.NBTTagCompound
	private static Class NBTTagCompoundClazz = CraftBukkitInterface.getNMClass("NBTTagCompound");
	//net.minecraft.server.{$VERSION}.NBTTagString
	private static Class NBTTagStringClazz = CraftBukkitInterface.getNMClass("NBTTagString");
	//net.minecraft.server.{$VERSION}.NBTTagList
	private static Class NBTTagListClazz = CraftBukkitInterface.getNMClass("NBTTagList");
	//net.minecraft.server.{$VERSION}.NBTTagList
	private static Class NBTBaseClazz = CraftBukkitInterface.getNMClass("NBTBase");
	//net.minecraft.server.{$VERSION}.ItemStack
	private static Class ItemStackClazz = CraftBukkitInterface.getNMClass("ItemStack");
	
	//org.bukkit.craftbukkit.{$VERSION}.inventory.CraftItemStack
	private static Class CraftItemStackClazz = CraftBukkitInterface.getCBClass("inventory.CraftItemStack");
	
	//org.bukkit.craftbukkit.{$VERSION}.inventory.CraftItemStack
	private static Method asNMSCopy, asCraftMirror;
	//net.minecraft.server.{$VERSION}.ItemStack
	private static Method hasTag, getTag, setTag;
	//net.minecraft.server.{$VERSION}.NBTTagCompound
	private static Method hasKey, getString, setString, getCompound, getList, set, 
	remove, add, get, size, getName;
	
	//NTBTagString data field
	private static Field data;
	
	static
	{
	    try
		{
	    	//CB methods
			asNMSCopy = CraftItemStackClazz.getMethod("asNMSCopy", ItemStack.class);
			asCraftMirror = CraftItemStackClazz.getMethod("asCraftMirror", ItemStackClazz);
			
			//Native minecraft methods
			hasTag = ItemStackClazz.getMethod("hasTag");
			getTag = ItemStackClazz.getMethod("getTag");
			setTag = ItemStackClazz.getMethod("setTag", NBTTagCompoundClazz);
			hasKey = NBTTagCompoundClazz.getMethod("hasKey", String.class);
			getString = NBTTagCompoundClazz.getMethod("getString", String.class);
			setString = NBTTagCompoundClazz.getMethod("setString", String.class, String.class);
			getCompound = NBTTagCompoundClazz.getMethod("getCompound", String.class);
			getList = NBTTagCompoundClazz.getMethod("getList", String.class);
			set = NBTTagCompoundClazz.getMethod("set", String.class, NBTBaseClazz);
			remove = NBTTagCompoundClazz.getMethod("remove", String.class);
			add = NBTTagListClazz.getMethod("add", NBTBaseClazz);
			get = NBTTagListClazz.getMethod("get", int.class);
			size = NBTTagListClazz.getMethod("size");
			getName = NBTTagStringClazz.getMethod("getName");
			
			data = NBTTagStringClazz.getField("data");
		}
		catch( Exception e )
		{ e.printStackTrace(); }
	}
	
	public static boolean hasCustomNBT(ItemStack item, String key)
	{
		try
		{
			return _hasCustomNBT(item, key);
		} catch(Exception e) { }
		return false;
	}

    private static boolean _hasCustomNBT(ItemStack item, String key) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	// tag (net.minecraft.server.{$VERSION}.NBTTagCompound) 
    	Object tag;
    	
        //cis (net.minecraft.server.{$VERSION}.ItemStack)
    	Object cis = asNMSCopy.invoke(null, item);
    	
    	//check and get the tag
        if ( !((Boolean)hasTag.invoke(cis)) ) return false;
        tag = getTag.invoke(cis);
        
        // if this item has the NBTData for 'stockitem', there is an mark.
        return (Boolean) hasKey.invoke(tag, key);
    }

    public static String getCustomNBT(ItemStack item, String key)
    {
    	try
    	{
    	    return _getCustomNBT(item, key);
    	} catch(Exception e) { }
    	return null;
    }
    
    private static String _getCustomNBT(ItemStack item, String key) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
    	// tag (net.minecraft.server.{$VERSION}.NBTTagCompound) 
    	Object tag;
    	
        //cis (net.minecraft.server.{$VERSION}.ItemStack)
    	Object cis = asNMSCopy.invoke(null, item);
    	
    	//check the tag and if not exists make a new instance of it
        if ( !((Boolean)hasTag.invoke(cis)) ) 
        	setTag.invoke(cis, NBTTagCompoundClazz.newInstance());
        tag = getTag.invoke(cis);
        
        // if this item has the NBTData for 'stockitem', return the value, which is the playername of the 'stockitem'.
        if ((Boolean) hasKey.invoke(tag, key)) return (String) getString.invoke(tag, key);
        return null;
    }
    
    public static ItemStack removeCustomNBT(ItemStack item, String key)
    {
    	try
    	{
    		return _removeCustomNBT(item, key);
    	} catch(Exception e) { }
    	return null;
    }

    private static ItemStack _removeCustomNBT(ItemStack item, String key) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	// tag (net.minecraft.server.{$VERSION}.NBTTagCompound) 
    	Object tag;
    	
        //cis (net.minecraft.server.{$VERSION}.ItemStack)
    	Object cis = asNMSCopy.invoke(null, item);
    	
    	//check for the tag if not exists do not need to do anything more
        if ( !((Boolean)hasTag.invoke(cis)) ) 
        	return (ItemStack) asCraftMirror.invoke(null, cis);
        tag = getTag.invoke(cis);
        
        // remove 'stockitem' NBTData
        remove.invoke(tag, key);
        
        //return the cleared item
        return (ItemStack) asCraftMirror.invoke(null, cis);
    }
    
    public static ItemStack addCustomNBT(ItemStack item, String key, String value)
    {
    	try
    	{
    		return _addCustomNBT(item, key, value);
    	} catch(Exception e) { }
    	return null;
    }

    private static ItemStack _addCustomNBT(ItemStack item, String key, String value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
    	// tag (net.minecraft.server.{$VERSION}.NBTTagCompound) 
    	Object tag;
    	
        //cis (net.minecraft.server.{$VERSION}.ItemStack)
    	Object cis = asNMSCopy.invoke(null, item);

    	//check the tag and if not exists make a new instance of it
        if ( !((Boolean)hasTag.invoke(cis)) ) 
        	setTag.invoke(cis, NBTTagCompoundClazz.newInstance());
        tag = getTag.invoke(cis);
        
        //set the new NBT value
        setString.invoke(tag, key, value);
        
        //return as new item
        return (ItemStack) asCraftMirror.invoke(null, cis);
    }
	
	/*
	 * marking and demarking an item
	 */
    
    public static ItemStack removeMark(ItemStack i)
    {
    	return removeCustomNBT(i, "stockitem");
    }
	
	public static boolean isMarked(ItemStack i)
	{
		return hasCustomNBT(i, "stockitem");
	}
	
	public static ItemStack markItem(ItemStack i)
	{
		return addCustomNBT(i, "stockitem", "player");
	}
	
	/**
	 * adding and removing NBT lores
	 */
	public static ItemStack addLore(ItemStack i, List<String> lore)
	{
		try
    	{
    		return _addLore(i, lore);
    	} catch(Exception e) { }
    	return null;
	}
	
	private static ItemStack _addLore(ItemStack i, List<String> lore) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, NoSuchMethodException, SecurityException
	{		
		//create a NMS copy (net.minecraft.server.{$VERSION}.ItemStack)
		Object nms = asNMSCopy.invoke(null, i);
		
		//net.minecraft.server.{$VERSION}.NBTTagCompound
		Object tag;
		
		//search for the tag
		if( !((Boolean) hasTag.invoke(nms)) )
			setTag.invoke(nms, NBTTagCompoundClazz.newInstance());
		tag = getTag.invoke(nms);

		//net.minecraft.server.{$VERSION}.NBTTagCompound (display tag)
		Object display;
		if ( !((Boolean) hasKey.invoke(tag, "display")) )
			set.invoke(tag, "display", NBTTagCompoundClazz.newInstance());
		display = getCompound.invoke(tag, "display");

		//net.minecraft.server.{$VERSION}.NBTTagList
		Object list;
		if ( (Boolean) hasKey.invoke(display, "Lore") )
			list = getList.invoke(display, "Lore");
		else
			list = NBTTagListClazz.newInstance();

		//add the lore
		for ( String line : lore )
	    //net.minecraft.server.{$VERSION}.NBTTagList (add method)
			add.invoke(list, NBTTagStringClazz.getConstructor(String.class, String.class).newInstance("dtltrader", line));//new NBTTagString("dtltrader", line));

		//set the new list
		set.invoke(display, "Lore", list);

		//return the new item;
		return (ItemStack) asCraftMirror.invoke(null, nms);
	}
	
	public static List<String> getLore(ItemStack i)
	{
		try
    	{
    		return _getLore(i);
    	} catch(Exception e) { }
    	return null;
	}
	
	private static List<String> _getLore(ItemStack i) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException
	{
		//create a NMS copy (net.minecraft.server.{$VERSION}.ItemStack)
		Object nms = asNMSCopy.invoke(null, i);

		//net.minecraft.server.{$VERSION}.NBTTagCompound
		Object tag;

		//search for the tag
		if( !((Boolean) hasTag.invoke(nms)) )
			setTag.invoke(nms, NBTTagCompoundClazz.newInstance());
		tag = getTag.invoke(nms);

		//net.minecraft.server.{$VERSION}.NBTTagCompound (display tag)
		Object display;
		if ( !((Boolean) hasKey.invoke(tag, "display")) )
			set.invoke(tag, "display", NBTTagCompoundClazz.newInstance());
		display = getCompound.invoke(tag, "display");
		
		//the result list
		List<String> result = new ArrayList<String>();

		//net.minecraft.server.{$VERSION}.NBTTagList
		Object list;
		if ( (Boolean) hasKey.invoke(display, "Lore") )
			list = getList.invoke(display, "Lore");
		else
			list = NBTTagListClazz.newInstance();

		//get the specific and normal lore!
		for ( int j = 0 ; j < (Integer) size.invoke(list) ; ++j )
			if ( !getName.invoke(get.invoke(list, j)).equals("dtltrader") &&
				 !(((String) data.get(get.invoke(list, j))).startsWith(Price.lorePattern)) )
				result.add((String) data.get(get.invoke(list, j)));

		//return the new item;
		return result;
	}
	
	public static boolean hasTraderLore(ItemStack i)
	{
		try
    	{
    		return _hasTraderLore(i);
    	} catch(Exception e) { }
    	return false;
	}
	
	private static boolean _hasTraderLore(ItemStack i) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		//create a NMS copy (net.minecraft.server.{$VERSION}.ItemStack)
		Object nms = asNMSCopy.invoke(null, i);

		//net.minecraft.server.{$VERSION}.NBTTagCompound
		Object tag;

		//search for the tag
		if( !((Boolean) hasTag.invoke(nms)) )
			return false;
		tag = getTag.invoke(nms);

		//net.minecraft.server.{$VERSION}.NBTTagCompound (display tag)
		Object display;
		if ( !((Boolean) hasKey.invoke(tag, "display")) )
			return false;
		display = getCompound.invoke(tag, "display");

		//net.minecraft.server.{$VERSION}.NBTTagList
		Object list;
		if ( (Boolean) hasKey.invoke(display, "Lore") )
			list = getList.invoke(display, "Lore");
		else
			return false;

		//search for trader lores
		for ( int j = 0 ; j < (Integer) size.invoke(list) ; ++j )
			if ( getName.invoke(get.invoke(list, j)).equals("dtltrader") || 
				 ((String) data.get(get.invoke(list, j))).startsWith(Price.lorePattern) )
				return true;

		//return false as no lores was found;
		return false;
	}
	
	public static void main(String[] a)
	{
		System.out.print((ChatColor.GOLD + "Price: " + ChatColor.GRAY + "8.00").startsWith(ChatColor.GOLD + "Price: " + ChatColor.GRAY));
	}
	
	public static ItemStack cleanItem(ItemStack i)
	{
	//	System.out.print(i.getAmount());
	//	System.out.print(ItemUtils.createStockItem(i).getItem().getAmount());
		return ItemUtils.createStockItem(i).getItem();
	}
}
