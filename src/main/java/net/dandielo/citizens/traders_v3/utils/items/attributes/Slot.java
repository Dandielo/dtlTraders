package net.dandielo.citizens.traders_v3.utils.items.attributes;

import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.StockItemAttribute;
import net.dandielo.core.items.serialize.Attribute;

@Attribute(name="Slot", key="s", required = true, priority = 0)
public class Slot extends StockItemAttribute {
	private int slot;
	
	/**
	 * Default constructor 
	 * @param key
	 *     unique key to register 
	 */
	public Slot(StockItem item, String key) {
		super(item, key);
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
	public boolean deserialize(String data)
	{
		try
		{
			slot = Integer.parseInt(data);
		}
		catch(NumberFormatException e)
		{
			return false;
		}
		return true;
	}

	@Override
	public String serialize()
	{
		return String.valueOf(slot);
	}	
}
