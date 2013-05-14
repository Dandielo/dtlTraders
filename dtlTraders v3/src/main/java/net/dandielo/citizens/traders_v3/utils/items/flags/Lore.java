package net.dandielo.citizens.traders_v3.utils.items.flags;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.core.exceptions.ItemDataNotFoundException;
import net.dandielo.citizens.traders_v3.utils.items.DataNode;
import net.dandielo.citizens.traders_v3.utils.items.ItemFlag;

@DataNode(name="Lore", saveKey="lore")
public class Lore extends ItemFlag {

	private List<String> lore;
	
	public Lore(String key) {
		super(key);
	}
	
	public Lore setLore(List<String> lore)
	{
		this.lore = lore;
		return this;
	}
	
	public List<String> getLore()
	{
		return lore;
	}

	@Override
	public void assing(ItemStack item) {
		//get the existing lore
		List<String> lore = item.getItemMeta().getLore();
		if ( lore == null )
			lore = new ArrayList<String>();
		
		//add this lore
		lore.addAll(this.lore);
		
		//save the new list
		item.getItemMeta().setLore(lore);
	}
	
	public void peek(ItemStack item) throws ItemDataNotFoundException
	{
		if ( !item.getItemMeta().hasLore() )
			throw new ItemDataNotFoundException();
		this.lore = item.getItemMeta().getLore();
	}

	@Override
	public boolean getValue() {
		return lore != null && !lore.isEmpty();
	}
	
	@Override
	public boolean equals(Object o)
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
