package net.dandielo.citizens.traders_v3.utils.items.flags;

import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.core.exceptions.InvalidItemException;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemFlag;

@Attribute(name = "DataCheck", key = ".dc", standalone = true)
public class DataCheck extends ItemFlag {

	public DataCheck(String key)
	{
		super(key);
	}

	@Override
	public void onAssign(ItemStack item) throws InvalidItemException
	{
		//unused
	}

}
