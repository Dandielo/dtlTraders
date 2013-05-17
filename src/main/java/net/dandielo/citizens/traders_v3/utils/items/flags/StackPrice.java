package net.dandielo.citizens.traders_v3.utils.items.flags;

import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.core.exceptions.InvalidItemException;
import net.dandielo.citizens.traders_v3.traders.Trader.Status; 
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemFlag;

@Attribute( 
name="StackPrice", key = ".sp", 
status = {Status.MANAGE_SELL, Status.MANAGE_BUY})
public class StackPrice extends ItemFlag {

	public StackPrice(String key) {
		super(key);
	}

	@Override
	public void onAssign(ItemStack item) throws InvalidItemException 
	{
	}

}
