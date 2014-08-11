package net.dandielo.citizens.traders_v3.utils.items.attributes;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import net.dandielo.citizens.traders_v3.core.exceptions.InvalidItemException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

@Attribute(name="Leather color", key="lc", priority = 5, standalone = true)
public class LeatherColor extends ItemAttr {
	private Color color;

	public LeatherColor(String key)
	{
		super(key);
	}

	@Override
	public void onLoad(String data) throws AttributeInvalidValueException
	{
		try
		{
		    String[] colors = data.split("\\^", 3);
			color = Color.fromRGB(Integer.parseInt(colors[0]), Integer.parseInt(colors[1]), Integer.parseInt(colors[2]));
		}
		catch(Exception e)
		{
			throw new AttributeInvalidValueException(getInfo(), data);
		}
	}

	@Override
	public String onSave()
	{
		//save as rgb
		return color.getRed() + "^" + color.getGreen() + "^" + color.getBlue();
	}

	@Override
	public void onAssign(ItemStack item) throws InvalidItemException
	{
		//no leather armor no color
		if ( !isLeatherArmor(item) ) throw new InvalidItemException();

		//set the new color to the item meta
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setColor(color);
		
		//save the meta to the item
		item.setItemMeta(meta);
	}

	@Override
	public void onFactorize(ItemStack item)
			throws AttributeValueNotFoundException
	{
		//no leather armor no color
		if ( !isLeatherArmor(item) || !item.hasItemMeta() ) 
			throw new AttributeValueNotFoundException();
		
		color = ((LeatherArmorMeta)item.getItemMeta()).getColor();
	}
	
	@Override
	public boolean equalsStrong(ItemAttr data)
	{
		return ((LeatherColor)data).color.equals(color);
	}
	
	@Override
	public boolean equalsWeak(ItemAttr data)
	{
		return equalsStrong(data);
	}

	/**
	 * Simple check if the given item has the LeatherArmorMeta.
	 * @param item
	 *     item to check
	 * @return
	 *     true if the item is a leather armor piece
	 */
	private static boolean isLeatherArmor(ItemStack item)
	{
		Material mat = item.getType();
		return mat.equals(Material.LEATHER_BOOTS) || mat.equals(Material.LEATHER_CHESTPLATE) || mat.equals(Material.LEATHER_HELMET) || mat.equals(Material.LEATHER_LEGGINGS);
	}
}
