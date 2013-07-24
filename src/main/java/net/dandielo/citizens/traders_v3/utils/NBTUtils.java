package net.dandielo.citizens.traders_v3.utils;

import java.util.ArrayList;
import java.util.List;

import net.dandielo.citizens.traders_v3.utils.items.attributes.Price;
import net.minecraft.server.v1_6_R2.NBTTagCompound;
import net.minecraft.server.v1_6_R2.NBTTagList;
import net.minecraft.server.v1_6_R2.NBTTagString;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_6_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NBTUtils {	
	
	/*
     * Some static methods for dealing with Minecraft NBT data, which is used to store
     * custom NBT.
     * 
     * All credits to Denizen - Aufdemrand
     */

    public static boolean hasCustomNBT(ItemStack item, String key) {
        NBTTagCompound tag;
        net.minecraft.server.v1_6_R2.ItemStack cis = CraftItemStack.asNMSCopy(item);
        if (!cis.hasTag()) return false;
        tag = cis.getTag();
        // if this item has the NBTData for 'stockitem', there is an mark.
        return tag.hasKey(key);
    }

    public static String getCustomNBT(ItemStack item, String key) {
        net.minecraft.server.v1_6_R2.ItemStack cis = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag;
        if (!cis.hasTag())
            cis.setTag(new NBTTagCompound());
        tag = cis.getTag();
        // if this item has the NBTData for 'stockitem', return the value, which is the playername of the 'stockitem'.
        if (tag.hasKey(key)) return tag.getString(key);
        return null;

    }

    public static ItemStack removeCustomNBT(ItemStack item, String key) {
        net.minecraft.server.v1_6_R2.ItemStack cis = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag;
        if (!cis.hasTag())
            cis.setTag(new NBTTagCompound());
        tag = cis.getTag();
        // remove 'stockitem' NBTData
        tag.remove(key);
        return CraftItemStack.asCraftMirror(cis);
    }

    public static ItemStack addCustomNBT(ItemStack item, String key, String value) {
        net.minecraft.server.v1_6_R2.ItemStack cis = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = null;
        // Do stuff with tag
        if (!cis.hasTag())
            cis.setTag(new NBTTagCompound());
        tag = cis.getTag();
        tag.setString(key, value);
        return CraftItemStack.asCraftMirror(cis);
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
	
	/*
	 * adding and removing lores
	 */
	public static ItemStack addLore(ItemStack i, List<String> lore)
	{		
		//create a NMS copy
		net.minecraft.server.v1_6_R2.ItemStack nms = CraftItemStack.asNMSCopy(i);
		
		//get the "tag" tag
		NBTTagCompound tag;
		if(nms.tag != null) tag = nms.tag;
		else
		{
		    tag = new NBTTagCompound();
		    nms.tag = tag;
		}
		
		//get the display tag
		NBTTagCompound display;
		if ( tag.hasKey("display") )
			display = tag.getCompound("display");
		else
		{
			display = new NBTTagCompound();
			tag.set("display", display);
		}
		
		//get the Lore tag
		NBTTagList list;
		if ( display.hasKey("Lore") )
			list = display.getList("Lore");
		else
			list = new NBTTagList();

		//add the lore
		for ( String line : lore )
			list.add(new NBTTagString("dtltrader", line));

		//set the new list
		display.set("Lore", list);

		//return the new item;
		return CraftItemStack.asCraftMirror(nms);
	}
	
	public static List<String> getLore(ItemStack i)
	{
		//create a NMS copy
		net.minecraft.server.v1_6_R2.ItemStack nms = CraftItemStack.asNMSCopy(i);

		//get the "tag" tag
		NBTTagCompound tag;
		if(nms.tag != null) tag = nms.tag;
		else
		{
			tag = new NBTTagCompound();
			nms.tag = tag;
		}

		//get the display tag
		NBTTagCompound display;
		if ( tag.hasKey("display") )
			display = tag.getCompound("display");
		else
		{
			display = new NBTTagCompound();
			tag.set("display", display);
		}
		
		//the result list
		List<String> result = new ArrayList<String>();

		//get the Lore tag
		NBTTagList list;
		if ( display.hasKey("Lore") )
			list = display.getList("Lore");
		else
			list = new NBTTagList();

		for ( int j = 0 ; j < list.size() ; ++j )
			if ( !((NBTTagString)list.get(j)).getName().equals("dtltrader") &&
					!((NBTTagString)list.get(j)).data.startsWith(Price.lorePattern) )
				result.add(((NBTTagString)list.get(j)).data);

		//return the new item;
		return result;
	}
	
	public static boolean hasTraderLore(ItemStack i)
	{
		//create a NMS copy
		net.minecraft.server.v1_6_R2.ItemStack nms = CraftItemStack.asNMSCopy(i);

		//get the "tag" tag
		NBTTagCompound tag;
		if(nms.tag != null) tag = nms.tag;
		else
		{
			return false;
		}

		//get the display tag
		NBTTagCompound display;
		if ( tag.hasKey("display") )
			display = tag.getCompound("display");
		else
		{
			return false;
		}

		//get the Lore tag
		NBTTagList list;
		if ( display.hasKey("Lore") )
			list = display.getList("Lore");
		else
			return false;

		for ( int j = 0 ; j < list.size() ; ++j )
		{
			if ( ((NBTTagString)list.get(j)).getName().equals("dtltrader")
					|| ((NBTTagString)list.get(j)).data.startsWith(ChatColor.GOLD + "Price: " + ChatColor.GRAY) )
				
				return true;
		}

		//return the new item;
		return false;
	}
	
	public static void main(String[] a)
	{
		System.out.print((ChatColor.GOLD + "Price: " + ChatColor.GRAY + "8.00").startsWith(ChatColor.GOLD + "Price: " + ChatColor.GRAY));
	}
	
	public static ItemStack cleanItem(ItemStack i)
	{
		return ItemUtils.createStockItem(i).getItem();
	}
}
