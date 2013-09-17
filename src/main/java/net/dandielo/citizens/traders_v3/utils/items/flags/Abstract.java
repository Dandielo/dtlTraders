package net.dandielo.citizens.traders_v3.utils.items.flags;

import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.core.exceptions.InvalidItemException;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemFlag;

/**
 * @author dandielo
 *
 * This flag is added to an item if it's generated only for a small amount of time 
 * or if it's owned only by a trader
 */
@Attribute(name="Abstract", key = ".abstract")
public class Abstract extends ItemFlag {

	public Abstract(String key)
	{
		super(key);
	}

	@Override
	public void onAssign(ItemStack item) throws InvalidItemException
	{
	}
}
