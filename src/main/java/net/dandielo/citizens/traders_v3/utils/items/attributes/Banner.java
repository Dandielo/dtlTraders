package net.dandielo.citizens.traders_v3.utils.items.attributes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import net.dandielo.citizens.traders_v3.core.exceptions.InvalidItemException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

@Attribute(name = "Banner", key = "br", priority = 5, items = {Material.BANNER})
public class Banner extends ItemAttr {
	private List<Pattern> bPatterns = new ArrayList<Pattern>();
	private DyeColor baseColor;
	
	public Banner(String key) {
		super(key);
	}

	@Override
	public void onLoad(String data) throws AttributeInvalidValueException {
		String[] bData = data.split("/");
		for (String entry : bData)
		{
			String[] cpVal = entry.split(":");
			if (cpVal.length == 1)
			{
				baseColor = DyeColor.getByColor(Color.fromRGB(Integer.parseInt(cpVal[0])));
			}
			else
			{
				PatternType type = PatternType.getByIdentifier(cpVal[0]);
				DyeColor color = DyeColor.getByColor(Color.fromRGB(Integer.parseInt(cpVal[1])));
				
				bPatterns.add(new Pattern(color, type));
			}
		}
	}

	@Override
	public String onSave() {
		String result = String.valueOf(baseColor.getColor().asRGB());
		for (Pattern pat : bPatterns)
		{
			result += "/" + pat.getPattern().getIdentifier() + ":" + String.valueOf(pat.getColor().getColor().asRGB());
		}
		return result;
	}

	@Override
	public void onFactorize(ItemStack item)	throws AttributeValueNotFoundException {
		//check the item meta
		if ( !(item.getItemMeta() instanceof BannerMeta) ) throw new AttributeValueNotFoundException();
		
		//check is a owner is set
		BannerMeta meta = (BannerMeta) item.getItemMeta();
		bPatterns.addAll(meta.getPatterns());
		baseColor = meta.getBaseColor();
	}
	
	@Override
	public void onAssign(ItemStack item) throws InvalidItemException {
		if ( !(item.getItemMeta() instanceof BannerMeta) ) throw new InvalidItemException();
		
		BannerMeta meta = (BannerMeta) item.getItemMeta();
		meta.setBaseColor(baseColor);
		for (Pattern pat : bPatterns)
			meta.addPattern(pat);
		
		item.setItemMeta(meta);
	}
}
