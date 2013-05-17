package net.dandielo.citizens.traders_v3.utils.items.attributes;

import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

@Attribute(name="Slot", key="s", required = true)
public class Slot extends ItemAttr {
	private int slot;
	
	public Slot(String key) {
		super(key);
		slot = -1;
	}
	
	public void setSlot(int slot)
	{
		this.slot = slot;
	}
	
	public int getSlot()
	{
		return slot;
	}

	@Override
	public void onLoad(String data) throws AttributeInvalidValueException 
	{
		try
		{
			slot = Integer.parseInt(data);
		}
		catch(NumberFormatException e)
		{
			throw new AttributeInvalidValueException(getInfo(), data);
		}
	}

	@Override
	public String onSave()
	{
		return String.valueOf(slot);
	}

	@Override
	public void onAssign(ItemStack item)
	{		
	}

	@Override
	public void onFactorise(ItemStack item)
	{
	}
	
	public boolean equalsStrong(ItemAttr attr)
	{
		return true;// slot == ((Slot)attr).slot;
	}
	
}
