package net.dandielo.citizens.traders_v3.utils.items.flags;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

import net.dandielo.citizens.traders_v3.core.exceptions.InvalidItemException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemFlag;

@Attribute(name="SplashPotion", key = ".splash")
public class Splash extends ItemFlag {

	public Splash(String key)
	{
		super(key);
	}

	@Override
	public void onAssign(ItemStack item) throws InvalidItemException
	{
		if ( !item.getType().equals(Material.POTION) )
			throw new InvalidItemException();
		
		//set the potion as splash potion
		//bit representation 
		//
		// 0 X X 0 | 0 0 0 0 | 0 0 0 0 | 0 0 0 0
		//
		// first (from left) X bit should be 1 and the second one should be 0
		// we are achieving this masking the durability in this way
		//
		// 0 0 0 1 | 1 1 1 1 | 1 1 1 1 | 1 1 1 1
		//
		// and setting the second bit (from left) to 1 with a OR function 
		//
		// 0 1 0 0 | 0 0 0 0 | 0 0 0 0 | 0 0 0 0
		//
		//splash potion ready :)
		item.setDurability((short) ((item.getDurability()&0x1fff)|0x4000));
	}
	
	@Override
	public void onFactorize(ItemStack item) throws AttributeValueNotFoundException
	{
		if ( !item.getType().equals(Material.POTION) )
			throw new AttributeValueNotFoundException();
		
		//get the potion and check if it's a splash potion
		try
		{
		    Potion potion = Potion.fromItemStack(item);
		    if ( !potion.isSplash() )
			    throw new AttributeValueNotFoundException();
		}
		catch ( Exception e )
		{
		    throw new AttributeValueNotFoundException();
		}
	}
}
