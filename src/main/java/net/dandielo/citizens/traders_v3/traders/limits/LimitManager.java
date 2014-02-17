package net.dandielo.citizens.traders_v3.traders.limits;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Limit;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class LimitManager {
	public final static LimitManager self = new LimitManager();
	
	//file and configuration fields
	private File limits_file;
	private FileConfiguration limits_yaml;
	
	//actual limit holder
	private Map<String, LimitEntry> limits;
	
	private LimitManager()
	{
		limits = new HashMap<String, LimitEntry>();
	}
	
	public boolean check_limit(Player player, StockItem item, int amount)
	{
		if( item.hasAttr(Limit.class) )
		{
			//get the limit attribute
			Limit lm = item.getAttr(Limit.class);
			
			//get or create a new limit entry
			LimitEntry entry = limits.get(lm.getID());
			if( entry == null ) 
				entry = new LimitEntry(lm.getID(), lm.getLimit(), lm.getTimeout());

			//all checks
			boolean result = true;
			result = entry.isAvailable(amount);
			result = result ? entry.isAvailable(player.getName(), amount) : result;
			
			//save the entry
			limits.put(lm.getID(), entry);
			return result;
		}
		return true;
	}
	
	public void update_limit(Player player, StockItem item, int amount)
	{
		if( item.hasAttr(Limit.class) )
		{
			//get the limit attribute
			Limit lm = item.getAttr(Limit.class);
			
			//get or create a new limit entry
			LimitEntry entry = limits.get(lm.getID());

			//update the limit
			entry.update(player.getName(), amount);
			
			//save the entry
			limits.put(lm.getID(), entry);
		}
	}
	
}
