package net.dandielo.citizens.traders_v3.utils.items.data;

import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.core.Debugger;
import net.dandielo.citizens.traders_v3.core.exceptions.InvalidDataAssignmentException;
import net.dandielo.citizens.traders_v3.core.exceptions.ItemDataNotFoundException;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.ItemUtils;
import net.dandielo.citizens.traders_v3.utils.items.DataNode;
import net.dandielo.citizens.traders_v3.utils.items.ItemData;

@DataNode(name="Durability", saveKey="d")
public class Durability extends ItemData
{
	private double durabilityPercent;
    private short durability;
    
	public Durability(String key)
	{
		super(key);
		durability = 0;
		durabilityPercent = -1;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getValue(StockItem stockItem) 
	{
		if ( durabilityPercent > -1 )
			durability = (short) (stockItem.getItem().getType().getMaxDurability() * durabilityPercent);
		return (T) new Short(durability); 
	}
	
	public short getDurability()
	{
		return durability;
	}

	@Override
	public void assing(ItemStack item)
	{
		if ( !ItemUtils.itemHasDurability(item) ) return;
		
		//debug low
		Debugger.low("Settings items durability");
		
		if ( durabilityPercent > -1 )
			durability = (short) (item.getType().getMaxDurability() * durabilityPercent);
		
		item.setDurability(durability);
	}

	@Override
	public void peek(ItemStack item) throws ItemDataNotFoundException 
	{
		if ( !ItemUtils.itemHasDurability(item) ) throw new ItemDataNotFoundException();

		//debug low
		Debugger.low("Loading durability from ItemStack item");
		
		durability = item.getDurability();
	}

	@Override
	public void checkItemCompatibility(ItemStack item) throws InvalidDataAssignmentException 
	{
		if ( !ItemUtils.itemHasDurability(item) ) 
			throw new InvalidDataAssignmentException();
	}

	@Override
	public void load(String value) 
	{
		//debug low
		Debugger.low("Loading durability from string");
		
		if ( value.contains("%") )
			durabilityPercent = Integer.parseInt(value.substring(1)) / 100.0;
		else
			durability = Short.parseShort(value);
	}

	@Override
	public String save()
	{
		//debug low
		Debugger.low("Saving durability to string");
		
		if ( durabilityPercent > -1 )
			return String.valueOf(durabilityPercent*100);
		return String.valueOf(durability);
	}

	public boolean equals(Durability d)
	{
		return d.durability == durability || d.durabilityPercent == durabilityPercent;
	}
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof Durability ? equals((Durability) o) : false;
	}
}
