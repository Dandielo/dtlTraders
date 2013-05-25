package net.dandielo.citizens.traders_v3.utils.items.attributes;

import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.core.exceptions.InvalidItemException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

@Attribute(name="Firework", key="fw")
public class Firework extends ItemAttr {

	public Firework(String key)
	{
		super(key);
	}

	@Override
	public void onLoad(String data) throws AttributeInvalidValueException
	{
	}

	@Override
	public String onSave()
	{
		return null;
	}

	@Override
	public void onAssign(ItemStack item) throws InvalidItemException
	{
	}

	@Override
	public void onFactorise(ItemStack item)
			throws AttributeValueNotFoundException
	{
	}

}
