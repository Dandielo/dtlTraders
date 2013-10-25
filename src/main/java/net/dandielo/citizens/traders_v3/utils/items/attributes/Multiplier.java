package net.dandielo.citizens.traders_v3.utils.items.attributes;

import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.core.exceptions.InvalidItemException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

@Attribute(name = "Multiplier", key = "m", standalone = true, priority = 0)
public class Multiplier extends ItemAttr {

	private double multiplier;
	
	public Multiplier(String key)
	{
		super(key);
	}
	
	public double getMultiplier()
	{
		return multiplier;
	}

	@Override
	public void onLoad(String data) throws AttributeInvalidValueException
	{
		try
		{
		    multiplier = Double.parseDouble(data);
		}
		catch( Exception e )
		{
			throw new AttributeInvalidValueException(getInfo(), data);
		}
	}

	@Override
	public String onSave()
	{
		return String.format("%.2f", multiplier);
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
