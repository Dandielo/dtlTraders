package net.dandielo.citizens.traders_v3.traders.limits;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Limit;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
	
	public void init()
	{
		String path = "plugins/dtlTraders";
		//get the file
		limits_file = new File(path, "limits.yml");
		
		//check if it exists
		if (!limits_file.exists())
		{
			try 
			{
				limits_file.createNewFile();
			} 
			catch (IOException e) 
			{
				dB.high("Cannot create limits.yml file for the limits feature");
			}
		}
		load();
	}
	
	void load()
	{
		limits_yaml = new YamlConfiguration();
		
		try 
		{
			//try to load the limits file
			limits_yaml.load(limits_file);
			
			//load all entries
			for (String id : limits_yaml.getKeys(false))
			{
				ConfigurationSection cs = limits_yaml.getConfigurationSection(id);
				
				//load a new entry
				LimitEntry entry = new LimitEntry(id,
						cs.getInt("limit"),  
						cs.getLong("timeout"),
						cs.getInt("plimit"), 
						cs.getLong("ptimeout")
				);
				
				//load each player
				ConfigurationSection pl = cs.getConfigurationSection("players");
				for(String player : pl.getKeys(false))
				{
					ConfigurationSection en = pl.getConfigurationSection(player);
					for (String time : en.getKeys(false))
					{
						entry.playerLoad(player, Long.parseLong(time), en.getInt(time));
					}
				}
				
				//add the entry to the list
				limits.put(id, entry);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public void save()
	{
		try 
		{
			//build the yaml
			limits_yaml = new YamlConfiguration();
			
			for ( Map.Entry<String, LimitEntry> entry : limits.entrySet() )
			{
				String id = entry.getKey();
				LimitEntry limit = entry.getValue();
				limits_yaml.set(id + ".limit", limit.getLimit());
				limits_yaml.set(id + ".timeout", limit.getTimeout());
				limits_yaml.set(id + ".plimit", limit.getPlayerLimit());
				limits_yaml.set(id + ".ptimeout", limit.getPlayerTimeout());
				limits_yaml.set(id + ".players", limit.playerEntries());
			}

			limits_yaml.save(limits_file);
		} 
		catch (IOException e)
		{
			System.out.print("ae");
			dB.high("Cannot save to limits.yml!");
		}
	}
	
	public void refreshAll()
	{
		for (LimitEntry entry : limits.values())
			entry.limitRefresh();
	}
	
	public boolean checkLimit(Player player, StockItem item, int amount, String type)
	{
		if( item.hasAttr(Limit.class) )
		{
			//get the limit attribute
			Limit lm = item.getAttr(Limit.class);

			//get or create a new limit entry
			LimitEntry entry = limits.get(lm.getID());
			if( entry == null ) 
				entry = new LimitEntry(lm.getID(), lm.getLimit(), lm.getTimeout());

			//remove timed out entries
			entry.limitRefresh();
			
			//all checks
			boolean result = true;
			result = entry.isAvailable(type, amount);
			result = result ? entry.isPlayerAvailable(player.getName(), type, amount) : result;

			//save the entry
			limits.put(lm.getID(), entry);
			return result;
		}
		return true;
	}
	
	public void updateLimit(Player player, StockItem item, int amount, String type)
	{
		if( item.hasAttr(Limit.class) )
		{
			//get the limit attribute
			Limit lm = item.getAttr(Limit.class);
			
			//get or create a new limit entry
			LimitEntry entry = limits.get(lm.getID());

			//update the limit
			entry.playerUpdate(player.getName(), type, amount);
			
			//save the entry
			limits.put(lm.getID(), entry);
		}
	}
	
	public long getTotalLimit(StockItem item)
	{
		if( item.hasAttr(Limit.class) )
		{
			//get the limit attribute
			Limit lm = item.getAttr(Limit.class);
			
			//get or create a new limit entry
			LimitEntry entry = limits.get(lm.getID());
			if( entry == null ) 
				entry = new LimitEntry(lm.getID(), lm.getLimit(), lm.getTimeout());
			
			return entry.getLimit();			
		}
		return 0;
	}
	
	public long getPlayerLimit(StockItem item)
	{
		if( item.hasAttr(Limit.class) )
		{
			//get the limit attribute
			Limit lm = item.getAttr(Limit.class);
			
			//get or create a new limit entry
			LimitEntry entry = limits.get(lm.getID());
			if( entry == null ) 
				entry = new LimitEntry(lm.getID(), lm.getLimit(), lm.getTimeout());
			
			return entry.getPlayerLimit();			
		}
		return 0;
	}
	
	public int getTotalUsed(StockItem item)
	{
		if( item.hasAttr(Limit.class) )
		{
			//get the limit attribute
			Limit lm = item.getAttr(Limit.class);
			
			//get or create a new limit entry
			LimitEntry entry = limits.get(lm.getID());
			if( entry == null ) 
				entry = new LimitEntry(lm.getID(), lm.getLimit(), lm.getTimeout());
			
			return entry.totalUsed();			
		}
		return 0;
	}
	
	public int getPlayerUsed(String player, StockItem item)
	{
		if( item.hasAttr(Limit.class) )
		{
			//get the limit attribute
			Limit lm = item.getAttr(Limit.class);
			
			//get or create a new limit entry
			LimitEntry entry = limits.get(lm.getID());
			if( entry == null ) 
				entry = new LimitEntry(lm.getID(), lm.getLimit(), lm.getTimeout());
			
			return entry.totalPlayer(player);			
		}
		return 0;
	}
	
	//timeout parser
	public static long parseTimeout(String raw)
	{
		long result = 0;
		Matcher m = Pattern.compile("(?:(\\d*)d)*(?:(\\d*)h)*(?:(\\d*)m)*(?:(\\d*)s)*").matcher(raw);
		if(m.matches())
		{
			if (m.group(1) != null)
				result += Long.parseLong(m.group(1)) * 24 * 60 * 60;
			if (m.group(2) != null)
				result += Long.parseLong(m.group(2)) * 60 * 60;
			if (m.group(3) != null)
				result += Long.parseLong(m.group(3)) * 60;
			if (m.group(4) != null)
				result += Long.parseLong(m.group(4));
		}
		return result;
	}
	
	//timeoutString
	public static String timeoutString(long raw)
	{
		String result = "";
		if (raw % 60 != 0)
			result = String.valueOf(raw % 60) + "s";
		if ((raw/=60) % 60 != 0)
			result = String.valueOf(raw % 60) + "m" + result;
		if ((raw/=60) % 24 != 0)
			result = String.valueOf(raw % 24) + "h" + result;
		if ((raw/=24) != 0)
			result = String.valueOf(raw) + "d" + result;
		return result;
	}
}
