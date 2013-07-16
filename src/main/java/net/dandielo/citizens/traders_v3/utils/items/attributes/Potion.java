package net.dandielo.citizens.traders_v3.utils.items.attributes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.dandielo.citizens.traders_v3.core.exceptions.InvalidItemException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

@Attribute(name="Potion", key="pt", priority = 5)
public class Potion extends ItemAttr {

	/**
	 * List of all potion effect for this potion item
	 */
	private List<PotionEffect> effects = new ArrayList<PotionEffect>();
	
	/**
	 * Potion attribute constructor, default values
	 * @param key
	 */
	public Potion(String key)
	{
		super(key);
	}

	@Override
	public void onLoad(String data) throws AttributeInvalidValueException
	{
		String[] savedEffects = data.split(",");
		for ( String savedEffect : savedEffects )
		{
			String[] effectData = savedEffect.split("/");
			PotionEffect effect = new PotionEffect(
					PotionEffectType.getById(Integer.parseInt(effectData[0])),
					Integer.parseInt(effectData[1]),
					Integer.parseInt(effectData[2]),
					Boolean.parseBoolean(effectData[3]));
			effects.add(effect);
		}
	}

	@Override
	public String onSave()
	{
		String result = "";
		
		//save each potion effect with a comma separated
		for ( PotionEffect e : effects )
			result += "," + e.getType().getId() + "/" + e.getDuration() + "/" + e.getAmplifier() + "/" + e.isAmbient();
		
		return result.substring(1);
	}

	@Override
	public void onAssign(ItemStack item) throws InvalidItemException
	{
		if ( !item.getType().equals(Material.POTION) ) 
			throw new InvalidItemException();

		PotionMeta meta = (PotionMeta) item.getItemMeta();
		
		for ( PotionEffect effect : effects )
		    meta.addCustomEffect(effect, false);
		
		//set the main effect
		meta.setMainEffect(effects.get(0).getType());
		
		item.setItemMeta(meta);
	}

	@Override
	public void onFactorize(ItemStack item)
			throws AttributeValueNotFoundException
	{
		if ( !item.hasItemMeta() || !item.getType().equals(Material.POTION) ) 
			throw new AttributeValueNotFoundException();
		
		PotionMeta meta = (PotionMeta) item.getItemMeta();
		if ( !meta.hasCustomEffects() ) throw new AttributeValueNotFoundException();
		
		effects = meta.getCustomEffects();
	}

	@Override
	public boolean equalsStrong(ItemAttr attr)
	{
		if ( ((Potion)attr).effects.size() != effects.size() ) return false;
		
		boolean equals = true;
		for ( PotionEffect effect : ((Potion)attr).effects )
		    equals = equals ? effects.contains(effect) : equals;
			
		return equals;
	}
	
	@Override
	public boolean equalsWeak(ItemAttr attr)
	{
		return equalsStrong(attr);
	}
}
