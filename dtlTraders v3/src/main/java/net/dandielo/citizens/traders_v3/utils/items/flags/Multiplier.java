package net.dandielo.citizens.traders_v3.utils.items.flags;

import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.traders.Trader.Status;
import net.dandielo.citizens.traders_v3.utils.items.DataNode;
import net.dandielo.citizens.traders_v3.utils.items.ItemFlag;

@DataNode(
name="Multiplier", 
saveKey = "mp", 
assignLore = true,
assignStatus = {Status.MANAGE_PRICE})
public class Multiplier extends ItemFlag {

	public Multiplier(String key) {
		super(key);
	}

	@Override
	public boolean getValue() 
	{
		return true;
	}

	@Override
	public void assing(ItemStack item) 
	{
		//nothing to do here
	}

}
