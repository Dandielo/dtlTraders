package net.dandielo.citizens.traders_v3.utils;

import java.util.ArrayList;
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
		net.minecraft.server.v1_5_R3.ItemStack nms = CraftItemStack.asNMSCopy(i);

		//get the "tag" tag
		NBTTagCompound tag = null;
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
			if ( !((NBTTagString)list.get(j)).getName().equals("dtltrader") )
				result.add(((NBTTagString)list.get(j)).data);
			else
				System.out.print("rem" + " " + ((NBTTagString)list.get(j)));

		//return the new item;
		return result;
	}
	
	public static ItemStack cleanItem(ItemStack i)
	{
		return ItemUtils.createStockItem(i).getItem();
	}
}
