package net.dandielo.citizens.traders_v3.utils.items.attributes;

import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

@Attribute(name="Slot", key="s", required = true)
public class Slot extends ItemAttr {
	private int slot;
	
	/**
	 * Default constructor 
	 * @param key
	 *     unique key to register 
	 */
	public Slot(String key) {
		super(key);
		slot = -1;
	}
	
	/**
	 * sets the slot with the given one
	 * @param slot
	 *     new slot value
	 */
	public void setSlot(int slot)
	{
		this.slot = slot;
	}
	
	/**
	 * Returns the item inventory slot value, -1 if item is not a stock item
	 * @return
	 *     inventory slot
	 */
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
		//unused
	}

	@Override
	public void onFactorise(ItemStack item)
	{
		//unused
	}
	
}
