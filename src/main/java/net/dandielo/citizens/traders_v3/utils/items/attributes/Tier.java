package net.dandielo.citizens.traders_v3.utils.items.attributes;

import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.core.exceptions.InvalidItemException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

@Attribute(name="Tier", key = "t", priority = 25)
public class Tier extends ItemAttr {
	private String tier;
	
	public Tier(String key)
	{
		super(key);
	}

	@Override
	public void onLoad(String data) throws AttributeInvalidValueException
	{
		tier = data;
	}

	@Override
	public String onSave()
	{
		return tier;
	}

	@Override
	public void onAssign(ItemStack item) throws InvalidItemException
	{
		//unused
	}

	@Override
	public void onFactorize(ItemStack item)
			throws AttributeValueNotFoundException
	{
		throw new AttributeValueNotFoundException();
	}

}
