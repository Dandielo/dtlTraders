package net.dandielo.citizens.traders_v3.utils.items.flags;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.utils.items.ItemFlag;

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

	@Override
	public void assing(ItemStack item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getValue() {
		// TODO Auto-generated method stub
		return false;
	}

}
