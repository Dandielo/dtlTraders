package net.dandielo.citizens.traders_v3.utils.items.attributes;

import java.util.HashMap;
import java.util.Map;

import net.dandielo.citizens.traders_v3.core.exceptions.InvalidItemException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

@Attribute(name="StoredEnchants", key="se")
public class StoredEnchant extends ItemAttr {
	Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
	
	public StoredEnchant(String key)
	{
		super(key);
	}

	@Override
	public void onLoad(String data) throws AttributeInvalidValueException
	{
		//split all enchants into name/id
		for ( String enchantment : data.split(",") )
		{
			//split the string into name and lvl values
			String[] enchData = enchantment.split("/");
			
			//get the enchant by name or id
			Enchantment ench = Enchantment.getByName( enchData[0].toUpperCase() );
			if ( ench == null )
				ench = Enchantment.getById( Integer.parseInt(enchData[0]));
			
			try
			{
				//save the enchant with lvl
				enchants.put(ench, Integer.parseInt(enchData[1]));
			}
			catch(NumberFormatException e)
			{
				throw new AttributeInvalidValueException(getInfo(), data);
			}
		}
	}

	@Override
	public String onSave()
	{
		String result = "";
		
		//for each enchant saved, with name and lvl
		for ( Map.Entry<Enchantment, Integer> enchant : enchants.entrySet() )
			result += "," + enchant.getKey().getId() + "/" + enchant.getValue();
		
		//return the save string
		return result.substring(1);
	}

	@Override
	public void onAssign(ItemStack item) throws InvalidItemException
	{
		if ( !item.getType().equals(Material.ENCHANTED_BOOK) ) throw new InvalidItemException();
		
		//get the meta
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();

		//set the enchanted book with enchants
		for ( Map.Entry<Enchantment, Integer> enchant : enchants.entrySet() )
		    meta.addStoredEnchant(enchant.getKey(), enchant.getValue(), true);
		
		//re-assign the meta
		item.setItemMeta(meta);
	}

	@Override
	public void onFactorise(ItemStack item)
			throws AttributeValueNotFoundException
	{
		//if its the wrong item
		if ( !item.getType().equals(Material.ENCHANTED_BOOK) ) throw new AttributeValueNotFoundException();
		
		//if enchants are not present the just say goodbye ;)
		if ( !((EnchantmentStorageMeta)item.getItemMeta()).hasStoredEnchants() ) throw new AttributeValueNotFoundException();
		
		//saving all enchants into the list
		for ( Map.Entry<Enchantment, Integer> enchant : ((EnchantmentStorageMeta)item.getItemMeta()).getStoredEnchants().entrySet() )
			enchants.put(enchant.getKey(), enchant.getValue());
	}

	public boolean equalsStrong(ItemAttr data)
	{
		if ( ((StoredEnchant)data).enchants.size() != enchants.size() ) return false;
		
		boolean equals = true;
		for ( Map.Entry<Enchantment, Integer> enchant : ((StoredEnchant)data).enchants.entrySet() )
		{
			if ( equals && enchants.get(enchant.getKey()) != null )
				equals = enchants.get(enchant.getKey()) == enchant.getValue();
			else
				equals = false;
		}
		return equals;
	}
	
	public boolean equalsWeak(ItemAttr data)
	{
		return equalsStrong(data);
	}
}