package net.dandielo.citizens.traders_v3.utils.items.attributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import net.dandielo.citizens.traders_v3.core.exceptions.InvalidItemException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

@Attribute(name="Firework", key="fw", priority = 5)
public class Firework extends ItemAttr {
	//the firework effect builder
	private static FireworkEffect.Builder effectBuilder = FireworkEffect.builder();
	
	//a list of all firework effects 
	List<FireworkEffect> effects;

	public Firework(String key)
	{
		super(key);
		effects = new ArrayList<FireworkEffect>();
	}

	@Override
	public void onLoad(String data) throws AttributeInvalidValueException
	{
		//split each firework effect 
		String[] values = data.split("/");

		//for each saved firework effect do this
		for( String effectData : values )
		{			
			//specific effects of the firework
			List<String> fwe = Arrays.asList(effectData.split("\\."));

			//if the list contains the "trail" name, then it should have a trail
			effectBuilder.trail(fwe.contains("trail"));
			
			//if the list contains the "flicked" name, then it should flicked
			effectBuilder.flicker(fwe.contains("flicker"));

			int i = 0;
			//for each other line do this
			for( String line : fwe )
			{
				//if the line contains "^" thens its a RGB color save
				if( line.contains("^") )
				{
					//get a list that will contain all colors
					List<Color> colors = new ArrayList<Color>();
					
					//for each saved color
					for( String colorData : line.split("-") )
					{
						//split the saved color into three values (RBG)
						String[] RGBdata = colorData.split("\\^");
						
						//create and add that color to the list
						colors.add(Color.fromRGB(
								Integer.parseInt(RGBdata[0]), // red
								Integer.parseInt(RGBdata[1]), // green
								Integer.parseInt(RGBdata[2])  // blue
						        ));

					}
					//if i == then the color list is the fireworks main color
					if( i++ == 0 )
						effectBuilder.withColor(colors);
					//else the color list applies to the fireworks fade colors
					else
						effectBuilder.withFade(colors);
				}
				else
				{
					//if the line is not empty, and it's not a trail or clicked option
					if( !line.isEmpty()
							&& !(line.equals("flicker") || line.equals("trail")) )
					{
						//set the firework effect type 
						effectBuilder.with(FireworkEffect.Type.valueOf(line
								.toUpperCase()));
					}
				}
			}
			//build the effect and add it to the list
			effects.add(effectBuilder.build());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String onSave()
	{
		//the result string that will be used for saving
		String result = "";
		
		int i = 0;
		//for each effect
		for( FireworkEffect effect : effects )
		{
			int j = 0;
			
			//for each effect part
			for( Map.Entry<String, Object> entry : effect.serialize().entrySet() )
			{
				//it the value is a list then it's the color
				if( entry.getValue() instanceof List )
				{
					int z = 0;
					
					//go through each of the colors in the list
					List<Color> tempList = (List<Color>) entry.getValue();
					for( Color color : tempList )
					{
						//add the color to the result string
						result += color.getRed() + "^" + color.getGreen() + "^" + color.getBlue();
						
						//if there is still a color in the list, separate it with a "-" char
						if( z++ + 1 < tempList.size() )
							result += "-";
					}
				}
				//if the value is a boolean, then check if it's true, if it is add the key name 
				else 
				if( (entry.getValue() instanceof Boolean) )
				{
					if( (Boolean) entry.getValue() )
						result += entry.getKey();
					else
						continue;
				}
				//the effect type
				else
					result += entry.getValue();
				
				//add a "." character if there is still a effect part to add
				if( j++ + 1 < effect.serialize().size() )
					result += ".";
			}
			//add anther effect delimiter if there are still effects to handle
			if( i++ + 1 < effects.size() )
				result += "/";
		}
		return result;
	}

	@Override
	public void onAssign(ItemStack item) throws InvalidItemException
	{
		//if the item is not a firework item
		if ( !(item.getItemMeta() instanceof FireworkMeta) ) throw new InvalidItemException();
		
		//get the meta
	    FireworkMeta firework = (FireworkMeta) item.getItemMeta();
	    
	    //add all effects
	    firework.addEffects(effects);
	    
	    //save the meta
	    item.setItemMeta(firework);
	}

	@Override
	public void onFactorize(ItemStack item)
			throws AttributeValueNotFoundException
	{
		//if the item is not a firework item
		if ( !(item.getItemMeta() instanceof FireworkMeta) ) 
			throw new AttributeValueNotFoundException();
		
		FireworkMeta firework = (FireworkMeta) item.getItemMeta();
		
		//no effects, no fireworks!
		if ( !firework.hasEffects() )
			throw new AttributeValueNotFoundException();
		
		//add all effects
		effects.addAll(firework.getEffects());
	}

	@Override
	public boolean equalsStrong(ItemAttr that)
	{
		if ( this.effects.size() != ((Firework)that).effects.size() ) return false;
		
		boolean equals = true;
		
		//check for each effect
		for ( FireworkEffect effect : ((Firework)that).effects )
			equals = equals ? this.effects.contains(effect) : equals;
			
		return equals;
	}

	@Override
	public boolean equalsWeak(ItemAttr that)
	{
		return equalsStrong(that);
	}
}
