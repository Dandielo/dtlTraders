package net.dandielo.citizens.traders_v3.utils.items.flags;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.dandielo.citizens.traders_v3.core.exceptions.InvalidItemException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemFlag;

@Attribute(name="Lore", key=".lore")
public class Lore extends ItemFlag {
	public static final String traderLorePrefix = "§3§d§d§f"; 
	private List<String> lore = new ArrayList<String>();

	public Lore(String key) {
		super(key);
	}
	
	public void setLore(List<String> lore)
	{
		this.lore = lore;
	}
	public List<String> getRawLore()
	{
		return lore;
	}

	@Override
	public void onAssign(ItemStack item) throws InvalidItemException 
	{
		//get the existing lore
		List<String> itemLore = item.getItemMeta().getLore();
		if ( itemLore == null )
			itemLore = new ArrayList<String>();
		
		//add this lore
		for ( String lore : this.lore )
			itemLore.add(lore.replace('^', '§').replace('&', '§'));
		
		//save the new lore
		ItemMeta meta = item.getItemMeta();
		meta.setLore(itemLore);
		item.setItemMeta(meta);
	}
	
	public void onFactorize(ItemStack item) throws AttributeValueNotFoundException
	{	
		if ( !item.getItemMeta().hasLore() )
			throw new AttributeValueNotFoundException();
		
		//get the lore without any dtlTrader lore lines
		List<String> cleanedLore = cleanLore(item.getItemMeta().getLore());
		if ( cleanedLore.isEmpty() )
			throw new AttributeValueNotFoundException();

		//set the new lore
		for (String line : cleanedLore)
			lore.add(line.replace('§', '&'));
		lore = cleanedLore;
	}

	public List<String> getLore() {
		//parse the whole lore
		List<String> itemLore = new ArrayList<String>();
		for (String lore : this.lore)
			itemLore.add(lore.replace('^', '§').replace('&', '§'));
		//return the parsed lore
		return itemLore;
	}
	
	//this should be always 0 to be assigned first
	@Override
	public int hashCode()
	{
		return 0;
	}
	
	@Override
	public boolean equalsStrong(ItemFlag o)
	{		
		Lore itemLore = (Lore) o;
		if ( item.hasFlag(AnyLore.class) ) return true;
		if ( !(itemLore.lore == null && this.lore == null) && !(itemLore.lore != null && this.lore != null) ) return false;
		if ( itemLore.lore.size() != this.lore.size() ) return false;

		boolean equals = true;
		for ( int i = 0 ; i < itemLore.lore.size() && equals ; ++i )
			equals = itemLore.lore.get(i).equals(this.lore.get(i));
		return equals;
	}

	@Override
	public boolean equalsWeak(ItemFlag flag)
	{
		return equalsStrong(flag);
	}
	
	public static List<String> cleanLore(List<String> lore)
	{
		List<String> cleaned = new ArrayList<String>();
		for (String entry : lore)
			if ( !entry.startsWith(traderLorePrefix) )
				cleaned.add(entry);
		return cleaned;
	}
	
	//static helper methods
	public static ItemStack addLore(ItemStack item, List<String> lore)
	{
		//get the lore
		ItemMeta meta = item.getItemMeta();	
		List<String> newLore = meta.getLore();	
		if (newLore == null)
			newLore = new ArrayList<String>();
		
		//add the new lore
		newLore.addAll(lore);
		meta.setLore(newLore);
		
		//create a new item
		ItemStack newItem = item.clone();
		newItem.setItemMeta(meta);
		return newItem; 
	}
	
	public static boolean hasTraderLore(ItemStack item)
	{
		if ( !item.hasItemMeta() || !item.getItemMeta().hasLore() ) return false;
		
		boolean has = false;
		for (String entry : item.getItemMeta().getLore())
			if ( !has && entry.startsWith(traderLorePrefix) )
				has = true;
		return has;
	}
	
}
