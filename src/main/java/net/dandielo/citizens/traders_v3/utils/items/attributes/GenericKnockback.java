package net.dandielo.citizens.traders_v3.utils.items.attributes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.utils.NBTUtils;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;
import net.dandielo.citizens.traders_v3.utils.items.Modifier;

@Attribute(name="GenericKnockback", key="g.kr", priority = 5)
public class GenericKnockback extends ItemAttr {
	private static String ATTRIBUTE = "generic.knockbackResistance";
	private List<Modifier> modifiers;
	
	public GenericKnockback(String key)
	{
		super(key);
		modifiers = new ArrayList<Modifier>();
	}

	@Override
	public void onLoad(String data) throws AttributeInvalidValueException
	{
		String[] mods = data.split(";");
		for ( String mod : mods )
			modifiers.add(new Modifier(mod.split("/")));
	}

	@Override
	public String onSave()
	{
		String result = "";
		for ( Modifier mod : modifiers )
			result += ";" + mod.toString();
		return result.substring(1);
	}

	@Override
	public void onFactorize(ItemStack item)	throws AttributeValueNotFoundException
	{
		List<Modifier> mods = NBTUtils.getModifiers(item, ATTRIBUTE);
		if ( mods == null || mods.isEmpty() ) throw new AttributeValueNotFoundException();
		modifiers.addAll(mods);
	}
	
	@Override
	public ItemStack onReturnAssign(ItemStack item, boolean endItem)
	{
		for ( Modifier mod : modifiers )
		{
			item = NBTUtils.setModifier(item, mod.getName(), ATTRIBUTE, mod.getValue(), mod.getOperation());
		}
		return item;
	}
}
