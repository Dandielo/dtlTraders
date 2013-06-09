package net.dandielo.citizens.traders_v3.utils.items.attributes;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import net.dandielo.citizens.traders_v3.core.exceptions.InvalidItemException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

@Attribute(name = "Map", key = "map")
public class Map extends ItemAttr {

	public Map(String key)
	{
		super(key);
	}

	@Override
	public void onLoad(String data) throws AttributeInvalidValueException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public String onSave()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onAssign(ItemStack item) throws InvalidItemException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFactorize(ItemStack item)
			throws AttributeValueNotFoundException
	{
		if ( !(item.getItemMeta() instanceof MapMeta) )
			throw new AttributeValueNotFoundException();
		
		MapMeta meta = (MapMeta) item.getItemMeta();
	}

}
