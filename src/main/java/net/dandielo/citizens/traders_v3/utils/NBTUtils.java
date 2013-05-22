package net.dandielo.citizens.traders_v3.utils;

import java.util.List;

import net.minecraft.server.v1_5_R3.NBTTagCompound;
import net.minecraft.server.v1_5_R3.NBTTagList;
import net.minecraft.server.v1_5_R3.NBTTagString;

import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NBTUtils {	
	
	public static boolean isMarked(ItemStack i)
	{
		return false;
	}
	
	public static void markItem(ItemStack i)
	{
		
	}
	
	public static ItemStack addLore(ItemStack i, List<String> lore)
	{		
		//create a NMS copy
		net.minecraft.server.v1_5_R3.ItemStack nms = CraftItemStack.asNMSCopy(i);
		
		//get the "tag" tag
		NBTTagCompound tag = null;
		if(nms.tag != null) tag = nms.tag;
		else
		{
		    nms.tag = new NBTTagCompound();
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
		if ( tag.hasKey("Lore") )
			list = tag.getList("Lore");
		else
			list = new NBTTagList();
		
		//add the lore
		for ( String line : lore )
			list.add(new NBTTagString("trader", line));
		
		//set the new list
		tag.set("Lore", list);
		
		//return the new item;
		return CraftItemStack.asCraftMirror(nms);
	}
	
}
