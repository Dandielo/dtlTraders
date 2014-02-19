package net.dandielo.citizens.traders_v3.utils.items.attributes;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.tNpcStatus;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.traders.limits.LimitManager;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

@Attribute(name = "Limit", key = "l")
public class Limit extends ItemAttr {
	private String id;
	private int limit;
	private long timeout;

	public Limit(String key)
	{
		super(key);
	}
	
	public String getID()
	{
		return id;
	}
	
	public int getLimit()
	{
		return limit;
	}
	
	public long getTimeout()
	{
		return timeout;
	}
	
	@Override
	public void onLoad(String raw) throws AttributeInvalidValueException
	{
		String[] data = raw.split("/");
		
		try
		{
			id = data[0];
			limit = Integer.parseInt(data[1]);
			timeout = LimitManager.parseTimeout(data[2]);
		}
		catch(NumberFormatException e)
		{
			throw new AttributeInvalidValueException(this.info, raw);
		}
	}

	@Override
	public String onSave()
	{
		return id + "/" + limit + "/" + LimitManager.timeoutString(timeout);
	}

	@Override
	public void onFactorize(ItemStack item)
			throws AttributeValueNotFoundException
	{		
		throw new AttributeValueNotFoundException();
	}
	
	public static List<String> loreRequest(String player, StockItem item, List<String> lore, tNpcStatus status)
	{
		LimitManager limits = LimitManager.self;
		
		if (!item.hasAttr(Limit.class)) return lore;
		
		//add the Price lore
		for ( String pLore : LocaleManager.locale.getLore("item-limit") )
		{
			lore.add(
					pLore
					.replace("{limit-total}", String.valueOf(limits.getTotalLimit(item)))
					.replace("{limit-used}",  String.valueOf(limits.getTotalUsed(item)))
					.replace("{limit-avail}", String.valueOf(limits.getTotalLimit(item)-limits.getTotalUsed(item)))
					//.replace("{limit-player}",  String.valueOf(limits.getPlayerLimit(item)))
					//.replace("{limit-player-used}",  String.valueOf(limits.getPlayerUsed(player, item)))
					//.replace("{limit-player-avail}", String.valueOf(limits.getPlayerLimit(item)-limits.getPlayerUsed(player, item)))
			);
		}
		
		//return the result
		return lore;
	}

}
