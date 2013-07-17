package net.dandielo.citizens.traders_v3.utils.items.flags;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.dandielo.citizens.traders_v3.core.exceptions.InvalidItemException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.utils.NBTUtils;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemFlag;

@Attribute(name="Lore", key=".lore")
public class Lore extends ItemFlag {
	private List<String> lore = new ArrayList<String>();

	public Lore(String key) {
		super(key);
	}
	
	public void setLore(List<String> lore)
	{
		this.lore = lore;
	}

	@Override
	public void onAssign(ItemStack item) throws InvalidItemException 
	{
		//get the existing lore
		List<String> itemLore = item.getItemMeta().getLore();
		if ( itemLore == null )
			itemLore = new ArrayList<String>();
		
		//add this lore
		itemLore.addAll(this.lore);

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
		List<String> cleanedLore = NBTUtils.getLore(item);
		if ( cleanedLore.isEmpty() )
			throw new AttributeValueNotFoundException();
		
		this.lore = cleanedLore;
	}

	public List<String> getLore() {
		return lore;
	}
	
	
	@Override
	public boolean equalsStrong(ItemFlag o)
	{		
		Lore itemLore = (Lore) o;
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
}
