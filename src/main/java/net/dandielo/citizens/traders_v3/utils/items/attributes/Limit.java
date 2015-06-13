package net.dandielo.citizens.traders_v3.utils.items.attributes;

import java.util.List;

import net.dandielo.citizens.traders_v3.TEntityStatus;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.traders.limits.LimitManager;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.StockItemAttribute;
import net.dandielo.core.items.serialize.Attribute;

@Attribute(name = "Limit", key = "l", standalone = true, priority = 0)
public class Limit extends StockItemAttribute {
	private String id;
	private int limit, plimit;
	private long timeout, ptimeout;

	public Limit(StockItem item, String key)
	{
		super(item, key);
	}
	
	public String getID()
	{
		return id;
	}
	
	//Global limits and timeouts
	public int getLimit()
	{
		return limit;
	}
	public long getTimeout()
	{
		return timeout;
	}
	public void increaseLimit(int l)
	{
		limit += l;
	}
	public void decreaseLimit(int l)
	{
		limit = limit - l < 0 ? 0 : limit - l;
	}
	public void increaseTimeout(long t)
	{
		timeout += t;
	}
	public void decreaseTimeout(long t)
	{
		timeout = timeout - t < 0 ? 0 : timeout - t;
	}
	
	//Player limits and timeouts
	public int getPlayerLimit()
	{
		return limit;
	}
	public long getPlayerTimeout()
	{
		return timeout;
	}
	public void increasePlayerLimit(int l)
	{
		limit += l;
	}
	public void decreasePlayerLimit(int l)
	{
		limit = limit - l < 0 ? 0 : limit - l;
	}
	public void increasePlayerTimeout(long t)
	{
		timeout += t;
	}
	public void decreasePlayerTimeout(long t)
	{
		timeout = timeout - t < 0 ? 0 : timeout - t;
	}
	
	@Override
	public boolean deserialize(String raw)
	{
		String[] data = raw.split("/");
		
		try
		{
			id = data[0];
			limit = Integer.parseInt(data[1]);
			timeout = LimitManager.parseTimeout(data[2]);
			
			if (data.length == 5)
			{
				plimit = Integer.parseInt(data[3]);
				ptimeout = LimitManager.parseTimeout(data[4]);
			}
		}
		catch(NumberFormatException e)
		{
			return false;
		}
		return true;
	}

	@Override
	public String serialize()
	{
		String result = id + "/" + limit + "/" + LimitManager.timeoutString(timeout);
		if (plimit != 0)
			result += "/" + plimit + "/" + LimitManager.timeoutString(ptimeout);
		return result;
	}

	@Override
	public void getDescription(TEntityStatus status, List<String> lore)
	{
		if ( !status.inManagementMode() ) return;

		//add the lore to the item
		for ( String pLore : LocaleManager.locale.getLore("item-rawLimit") )
			lore.add(pLore.replace("{limit}", String.valueOf(limit)).replace("{timeout}", LimitManager.timeoutString(timeout)));
	}
	
	//TODO: check this one?
	public static List<String> loreRequest(String player, StockItem item, List<String> lore, TEntityStatus status)
	{
		LimitManager limits = LimitManager.self;
		
		if (!item.hasAttribute(Limit.class)) return lore;

		//add the limit lore
		for ( String pLore : LocaleManager.locale.getLore("item-" + status.asStock() + "-limit") )
		{
			lore.add(
					pLore
					.replace("{limit-total}", String.valueOf(limits.getTotalLimit(item)))
					.replace("{limit-used}",  String.valueOf(Math.abs(limits.getTotalUsed(item))))
					.replace("{limit-avail}", String.valueOf(limits.getTotalLimit(item)-Math.abs(limits.getTotalUsed(item))))
			);
		}
		//add the player limit lore
		for ( String pLore : LocaleManager.locale.getLore("item-" + status.asStock() + "-plimit") )
		{
			lore.add(
					pLore
					.replace("{limit-total}", String.valueOf(limits.getTotalLimit(item)))
					.replace("{limit-used}",  String.valueOf(Math.abs(limits.getTotalUsed(item))))
					.replace("{limit-avail}", String.valueOf(limits.getTotalLimit(item)-Math.abs(limits.getTotalUsed(item))))
			);
		}
		
		//return the result
		return lore;
	}

}
