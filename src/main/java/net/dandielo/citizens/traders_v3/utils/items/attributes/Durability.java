package net.dandielo.citizens.traders_v3.utils.items.attributes;

import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.core.exceptions.InvalidItemException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.utils.ItemUtils;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

@Attribute(name="Durability", key="d", priority = 45)
public class Durability extends ItemAttr
{
	private double durabilityPercent;
    private short durability;

	public Durability(String key) {
		super(key);
		durability = 0;
		durabilityPercent = -1;
	}

	public short getDurability()
	{
		return durability;
	}
	
	public double getPercent()
	{
		return durabilityPercent;
	}
	
	@Override
	public void onLoad(String data) throws AttributeInvalidValueException {
		try
		{
			if ( data.endsWith("%") )
			{
				durabilityPercent = Integer.parseInt(data.substring(0, data.length() - 1)) / 100.0;
				durability = (short) (item.getItem(false).getType().getMaxDurability() * durabilityPercent);
			}
			else
			{
				durability = Short.parseShort(data.substring(0));
			}
		}
		catch(NumberFormatException e)
		{
			throw new AttributeInvalidValueException(getInfo(), data);
		}
	}

	@Override
	public String onSave() 
	{
		if ( durabilityPercent > -1 )
			return String.format("%.0f%%", durabilityPercent*100);
		return String.valueOf(durability);
	}

	@Override
	public void onAssign(ItemStack item) throws InvalidItemException
	{
		if ( !ItemUtils.itemHasDurability(item) ) throw new InvalidItemException();
		
		if ( durabilityPercent > -1 )
			durability = (short) (item.getType().getMaxDurability() * durabilityPercent);
		
		item.setDurability(durability);
	}

	@Override
	public void onFactorize(ItemStack item)	throws AttributeValueNotFoundException {
		if ( !ItemUtils.itemHasDurability(item) )
			throw new AttributeValueNotFoundException();
		
		durability = item.getDurability();
	}

	@Override
	public boolean equalsWeak(ItemAttr attr)
	{
		return durability >= ((Durability)attr).durability;
	}
	
	@Override
	public boolean equalsStrong(ItemAttr attr)
	{
		return durability == ((Durability)attr).durability;
	}
}
