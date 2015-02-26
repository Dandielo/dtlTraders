package net.dandielo.citizens.traders_v3.utils.items.attributes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.dandielo.citizens.traders_v3.core.exceptions.InvalidItemException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

@Attribute(name="Skull", key="sk", priority = 5, items = {Material.SKULL, Material.SKULL_ITEM})
public class Skull extends ItemAttr {

	private String owner;
	
	public Skull(String key)
	{
		super(key);
		owner = null;
	}

	@Override
	public void onLoad(String data) throws AttributeInvalidValueException
	{
		owner = data;
	}

	@Override
	public String onSave()
	{
		return owner;
	}

	@Override
	public void onAssign(ItemStack item) throws InvalidItemException
	{
		//check if the item is valid
		if ( !(item.getItemMeta() instanceof SkullMeta) ) throw new InvalidItemException();
		
		//get the meta
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		
		//set the skull owner
		meta.setOwner(owner);
		
		//set the meta
		item.setItemMeta(meta);
	}

	@Override
	public void onFactorize(ItemStack item)
			throws AttributeValueNotFoundException
	{
		//check the item meta
		if ( !(item.getItemMeta() instanceof SkullMeta) ) throw new AttributeValueNotFoundException();
		
		//check is a owner is set
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		if ( !meta.hasOwner() ) throw new AttributeValueNotFoundException();
		
		//save the skull owner
		owner = meta.getOwner();
	}
	
}
