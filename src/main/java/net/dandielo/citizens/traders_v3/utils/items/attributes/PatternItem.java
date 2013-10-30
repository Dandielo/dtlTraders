package net.dandielo.citizens.traders_v3.utils.items.attributes;

import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

@Attribute(name = "PatternItem", key = "pat", standalone = true)
public class PatternItem extends ItemAttr {
	private int priority = 0;
	
	public PatternItem(String key)
	{
		super(key);
	}

	@Override
	public void onLoad(String data) throws AttributeInvalidValueException
	{
		priority = Integer.parseInt(data);
	}

	@Override
	public String onSave()
	{
		return String.valueOf(priority);
	}

	@Override
	public void onFactorize(ItemStack item)
			throws AttributeValueNotFoundException
	{
		throw new AttributeValueNotFoundException();
	}
	
	@Override
	public boolean equalsWeak(ItemAttr attr)
	{
		return equalsStrong(attr);
	}
	@Override
	public boolean equalsStrong(ItemAttr attr)
	{
		return priority >= ((PatternItem)attr).priority;
	}
}
