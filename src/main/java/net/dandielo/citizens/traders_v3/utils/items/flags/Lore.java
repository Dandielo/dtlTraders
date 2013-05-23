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
		List<String> lore = item.getItemMeta().getLore();
		if ( lore == null )
			lore = new ArrayList<String>();
		
		System.out.print("ADDING! " + this.lore.get(0));
		
		//add this lore
		lore.addAll(this.lore);

		//save the new lore
		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
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
		
		for ( String l : cleanedLore )
		System.out.print(l);
		
		this.lore = cleanedLore;//item.getItemMeta().getLore();
	}

	public List<String> getLore() {
		return lore;
	}
	
	
	@Override
	public boolean equalsStrong(ItemFlag o)
	{		
		Lore lore = (Lore) o;
		if ( !(lore.lore == null && this.lore == null) && !(lore.lore != null && this.lore != null) ) return false;
		if ( lore.lore.size() != this.lore.size() ) return false;

		boolean equals = true;
		for ( int i = 0 ; i < lore.lore.size() && equals ; ++i )
			equals = lore.lore.get(i).equals(this.lore.get(i));
		return equals;
	}

}
