package net.dandielo.citizens.traders_v3.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.dandielo.citizens.traders_v3.bukkit.CraftBukkitInterface;
import net.dandielo.citizens.traders_v3.utils.items.Modifier;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Price;
import net.dandielo.citizens.traders_v3.utils.items.flags.Lore;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings({"rawtypes", "unchecked"})
public class NBTUtils {	
	
	/*
     * Some static methods for dealing with Minecraft NBT toString, which is used to store
     * custom NBT.
     * 
     * All credits to Denizen - Aufdemrand
     */
	
	/** added static loaded classes and methods for later to use */ /***/
	
	//net.minecraft.server.{$VERSION}.NBTTagCompound
	private static Class NBTTagCompoundClazz = CraftBukkitInterface.getNMClass("NBTTagCompound");
	//net.minecraft.server.{$VERSION}.NBTTagString
	private static Class NBTTagStringClazz = CraftBukkitInterface.getNMClass("NBTTagString");
	//net.minecraft.server.{$VERSION}.NBTTagDouble
	private static Class NBTTagDoubleClazz = CraftBukkitInterface.getNMClass("NBTTagDouble");
	//net.minecraft.server.{$VERSION}.NBTTagList
	private static Class NBTTagListClazz = CraftBukkitInterface.getNMClass("NBTTagList");
	//net.minecraft.server.{$VERSION}.NBTTagLong
	private static Class NBTTagLongClazz = CraftBukkitInterface.getNMClass("NBTTagLong");
	//net.minecraft.server.{$VERSION}.NBTTagInt
	private static Class NBTTagIntClazz = CraftBukkitInterface.getNMClass("NBTTagInt");
	//net.minecraft.server.{$VERSION}.NBTBase
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
	remove, add, get, size, getTypeID, getName, getDouble, getInt, toString;
	
	
	//NTBTagString toString field	
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
			toString = NBTTagCompoundClazz.getMethod("toString");
			hasKey = NBTTagCompoundClazz.getMethod("hasKey", String.class);
			getString = NBTTagCompoundClazz.getMethod("getString", String.class);
			getDouble = NBTTagCompoundClazz.getMethod("getDouble", String.class);
			getInt = NBTTagCompoundClazz.getMethod("getInt", String.class);
			setString = NBTTagCompoundClazz.getMethod("setString", String.class, String.class);
			getCompound = NBTTagCompoundClazz.getMethod("getCompound", String.class);
			getList = NBTTagCompoundClazz.getMethod("getList", String.class, int.class);
			set = NBTTagCompoundClazz.getMethod("set", String.class, NBTBaseClazz);
			remove = NBTTagCompoundClazz.getMethod("remove", String.class);
			add = NBTTagListClazz.getMethod("add", NBTBaseClazz);
			get = NBTTagListClazz.getMethod("get", int.class);
			size = NBTTagListClazz.getMethod("size");
			getTypeID = NBTBaseClazz.getMethod("getTypeId");
			getName = NBTBaseClazz.getMethod("getTagName", int.class);
		}
		catch( Exception e )
		{ e.printStackTrace(); }
	}
	
	static String getTagName(Object tag) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (String) getName.invoke(null, getTypeID.invoke(tag));
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
    
    /* Attributes support
     * 
     * 
     */
    private static ItemStack _setModifier(ItemStack item, String name, String attrName, double value, int operation) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, NoSuchMethodException, SecurityException
    {
    //	System.out.print("MInit - Done");
    	// tag (net.minecraft.server.{$VERSION}.NBTTagCompound) 
    	Object tag;
    	
        //cis (net.minecraft.server.{$VERSION}.ItemStack)
    	Object nms = asNMSCopy.invoke(null, item);

    //	System.out.print("Start - Done");
		//search for the tag
		if( !((Boolean) hasTag.invoke(nms)) )
			setTag.invoke(nms, NBTTagCompoundClazz.newInstance());
		tag = getTag.invoke(nms);

    //	System.out.print("MTag - Done");
		//get the attribute list
    	Object attrList = null;
    	if ( (Boolean) hasKey.invoke(tag, "AttributeModifiers") )
    		attrList = getList.invoke(tag, "AttributeModifiers", 0);
		else
			attrList = NBTTagListClazz.newInstance();
    	
    	//create a new list
    	Object nList = NBTTagListClazz.newInstance();

   // 	System.out.print("MList - Done");
    	
    	//copy all tags (not including the set one)
		for ( int j = 0 ; j < (Integer) size.invoke(attrList) ; ++j )
			if ( !getString.invoke(get.invoke(attrList, j), "Name").equals(name) )
				add.invoke(nList, get.invoke(attrList, j));

    //	System.out.print("OldList - Done");
    	
    	
    	
		//create the new attribute tag
		Object attr = NBTTagCompoundClazz.newInstance();
		set.invoke(attr, "Name", NBTTagStringClazz.getConstructor(String.class).newInstance(name));
		set.invoke(attr, "AttributeName", NBTTagStringClazz.getConstructor(String.class).newInstance(attrName));
		set.invoke(attr, "Amount", NBTTagDoubleClazz.getConstructor(double.class).newInstance(value));
    	set.invoke(attr, "Operation", NBTTagIntClazz.getConstructor(int.class).newInstance(operation));
    	
    	//generate a random UUID
        UUID randUUID = UUID.randomUUID();
    	set.invoke(attr, "UUIDMost", NBTTagLongClazz.getConstructor(long.class).newInstance(randUUID.getMostSignificantBits()));
    	set.invoke(attr, "UUIDLeast", NBTTagLongClazz.getConstructor(long.class).newInstance(randUUID.getLeastSignificantBits()));
		add.invoke(nList, attr);

    //	System.out.print("NewList - Done");
    	
		set.invoke(tag, "AttributeModifiers", nList);
   // 	System.out.print("DoneList - Done");
		
        //return as new item
        return (ItemStack) asCraftMirror.invoke(null, nms);
    }
    
    public static ItemStack setModifier(ItemStack item, String name, String attr, double value, int operation)
    {
    	try
		{
			return _setModifier(item, name, attr, value, operation);
		}
		catch(Exception e) { return null; }
    }
    
    private static List<Modifier> _getModifiers(ItemStack item, String attrName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, NoSuchMethodException, SecurityException
    {
    	// tag (net.minecraft.server.{$VERSION}.NBTTagCompound) 
    	Object tag;
    	
        //cis (net.minecraft.server.{$VERSION}.ItemStack)
    	Object nms = asNMSCopy.invoke(null, item);
		
		//search for the tag
		if( !((Boolean) hasTag.invoke(nms)) ) return null;
		tag = getTag.invoke(nms);
    	
		//get the attribute list
    	Object attrList = null;
    	if ( (Boolean) hasKey.invoke(tag, "AttributeModifiers") )
    		attrList = getList.invoke(tag, "AttributeModifiers", 0);
		else return null;
    	

    	List<Modifier> mods = new ArrayList<Modifier>();
    	//copy all tags (not including the set one)
		for ( int j = 0 ; j < (Integer) size.invoke(attrList) ; ++j )
		{
			Object attr = get.invoke(attrList, j);
			if ( getString.invoke(attr, "AttributeName").equals(attrName) )
			mods.add(
					new Modifier(
							(String)  getString.invoke(attr, "Name"), 
							(Double)  getDouble.invoke(attr, "Amount"), 
							(Integer) getInt.invoke(attr, "Operation")
					)
			);
		}
		
        //return as new item
        return mods;
    }
    
    public static List<Modifier> getModifiers(ItemStack item, String attr)
    {
    	try
		{
			return _getModifiers(item, attr);
		}
		catch(Exception e) { return null; }
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
	/*
	public static ItemStack addLore(ItemStack i, List<String> lore)
	{
		try
    	{
    		return _addLore(i, lore);
    	} catch(Exception e) { }
    	return null;
	}*/
	
	private static ItemStack _addLore(ItemStack i, List<String> lore) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, NoSuchMethodException, SecurityException
	{		
	//	System.out.print("Init - Done");
		//create a NMS copy (net.minecraft.server.{$VERSION}.ItemStack)
		Object nms = asNMSCopy.invoke(null, i);
		
		//net.minecraft.server.{$VERSION}.NBTTagCompound
		Object tag;

	//	System.out.print("Start - Done");
		
		//search for the tag
		if( !((Boolean) hasTag.invoke(nms)) )
			setTag.invoke(nms, NBTTagCompoundClazz.newInstance());
		tag = getTag.invoke(nms);

	//	System.out.print("GeneralTag - Done");
		
		//net.minecraft.server.{$VERSION}.NBTTagCompound (display tag)
		Object display;
		if ( !((Boolean) hasKey.invoke(tag, "display")) )
			set.invoke(tag, "display", NBTTagCompoundClazz.newInstance());
		display = getCompound.invoke(tag, "display");

	//	System.out.print("DisplayTag - Done");
		
		//net.minecraft.server.{$VERSION}.NBTTagList
		Object list;
		if ( (Boolean) hasKey.invoke(display, "Lore") )
			list = getList.invoke(display, "Lore", getTypeID.invoke(NBTTagStringClazz.newInstance()));
		else
			list = NBTTagListClazz.newInstance();

	//	System.out.print("ListTag - Done");
		
		//add the lore
		for ( String line : lore )
	    //net.minecraft.server.{$VERSION}.NBTTagList (add method)
			add.invoke(list, NBTTagStringClazz.getConstructor(String.class).newInstance(line));//new NBTTagString("dtltrader", line));

	//	System.out.print("EditList - Done");
		
		//set the new list
		set.invoke(display, "Lore", list);

	//	System.out.print("SaveList - Done");
		
		//return the new item;
		return (ItemStack) asCraftMirror.invoke(null, nms);
	}
	
/*	public static List<String> getLore(ItemStack i)
	{
		try
    	{
    		return _getLore(i);
    	} catch(Exception e) { }
    	return null;
	}*/
	
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
			list = getList.invoke(display, "Lore", 8);
		else
			list = NBTTagListClazz.newInstance();

		System.out.print("ID: " + getTypeID.invoke(NBTTagStringClazz.newInstance()));
		System.out.print(result);
		System.out.print(size.invoke(list));
		
		
		//get the specific and normal lore!
		for ( int j = 0 ; j < (Integer) size.invoke(list) ; ++j )
		{
			System.out.print("---- " + toString.invoke(get.invoke(list, j)) + " ----");
			if ( !getTagName(get.invoke(list, j)).equals("dtltrader") &&
				 !(((String) toString.invoke(get.invoke(list, j))).startsWith(Lore.traderLorePrefix)) )
				result.add((String) toString.invoke(get.invoke(list, j)));
		}

		//return the new item;
		return result;
	}
	
	/*
	public static boolean hasTraderLore(ItemStack i)
	{
		try
    	{
    		return _hasTraderLore(i);
    	} catch(Exception e) { }
    	return false;
	}*/
	
	private static boolean _hasTraderLore(ItemStack i) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException
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
			list = getList.invoke(display, "Lore", getTypeID.invoke(NBTTagStringClazz.newInstance()));
		else
			return false;

		//search for trader lores
		for ( int j = 0 ; j < (Integer) size.invoke(list) ; ++j )
			if ( getTagName(get.invoke(list, j)).equals("dtltrader") || 
				 ((String) toString.invoke(get.invoke(list, j))).startsWith(Lore.traderLorePrefix) )
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
		return ItemUtils.createStockItem(i).getItem(true);
	}
}
