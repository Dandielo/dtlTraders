package net.dandielo.citizens.traders_v3.utils.items.flags;

import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.traders.Trader.Status; 
import net.dandielo.citizens.traders_v3.utils.items.DataNode;
import net.dandielo.citizens.traders_v3.utils.items.ItemFlag;

@DataNode( 
name="StackPrice", 
saveKey = ".sp", 
assignLore = true,
assignStatus = {Status.MANAGE_SELL, Status.MANAGE_BUY})
public class StackPrice extends ItemFlag {

	public StackPrice(String key) {
		super(key);
	}

	@Override
	public boolean getValue() {
		return true;
	}

	@Override
	public void assing(ItemStack item) {
		//nothing to do here
	}

}
