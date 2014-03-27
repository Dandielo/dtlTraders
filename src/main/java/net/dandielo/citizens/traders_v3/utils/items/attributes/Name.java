package net.dandielo.citizens.traders_v3.utils.items.attributes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;
import net.dandielo.citizens.traders_v3.utils.items.flags.Regex;

@Attribute(name="Name", key="n", priority = 300)
public class Name extends ItemAttr {
	private String name;

	public Name(String key)
	{
		super(key);
		name = "";
	}

	public String getName()
	{
		return name;
	}
	
	@Override
	public void onLoad(String data) 
	{
		name = data.replace('^', '§').replace('&', '§');
	}

	@Override
	public String onSave() 
	{
		return name.replace('§', '&');
	}

	@Override
	public void onAssign(ItemStack item)
	{
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
	}

	@Override
	public void onFactorize(ItemStack item) throws AttributeValueNotFoundException 
	{
		if ( !item.hasItemMeta() ) throw new AttributeValueNotFoundException();
		
		ItemMeta meta = item.getItemMeta();
		if ( !meta.hasDisplayName() ) throw new AttributeValueNotFoundException();
		
		name = meta.getDisplayName();
	}
	
	public boolean extendedCheck(ItemAttr attr)
	{
		Matcher match = Pattern.compile(name).matcher(((Name)attr).name);
		return match.matches();
	}
	
	@Override
	public boolean equalsWeak(ItemAttr attr)
	{			
		return equalsStrong(attr);
	}
	
	@Override
	public boolean equalsStrong(ItemAttr attr)
	{
		return item.hasFlag(Regex.class) ? extendedCheck(attr) : name.equals(((Name)attr).name);
	}


}
