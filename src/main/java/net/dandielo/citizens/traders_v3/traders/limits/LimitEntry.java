package net.dandielo.citizens.traders_v3.traders.limits;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("unused")
public class LimitEntry {	
	//named ID
	private String id;
	
	//limit settings
	private int limit;
	private long timeout;

	private int playerLimit;
	private long playerTimeout;
	
	//limit usage
	private Map<String, Map<Long, Integer>> used;	
	
	//constructors
	public LimitEntry(String id, int limit, long timeout)
	{
		//limit id
		this.id = id;
		
		playerLimit = this.limit = limit;
		playerTimeout = this.timeout = timeout;
		
		used = new HashMap<String, Map<Long, Integer>>();
	}
	
	public LimitEntry(String id, int limit, long timeout, int plimit, long ptimeout)
	{
		//limit id
		this.id = id;

		this.limit = limit;
		this.timeout = timeout;
		
		playerLimit = plimit;
		playerTimeout = ptimeout;
		
		used = new HashMap<String, Map<Long, Integer>>();
	}
	
	//methods
	public void limitUpdate()
	{
		long now = new Date().getTime();
		
		//remove all timed out data
		for ( Map<Long, Integer> entries : used.values() )
		{
			Iterator<Map.Entry<Long, Integer>> it = entries.entrySet().iterator();
			while(it.hasNext())
			{
				if (now >= it.next().getKey().longValue() + timeout * 1000)
				{
					it.remove();
				}
			}
		}
	}
	
	//methods
	public int totalPlayer(String palyer)
	{
		int result = 0;
		//get the players total amount
		for ( Integer value : used.get(palyer).values() )
			result += value.intValue();
		return result;
	}
	public int totalUsed()
	{
		int result = 0;
		for( String player : used.keySet() )
			result += totalPlayer(player);
		return result;
	}
	
	//general methods
	public boolean isAvailable(int amount)
	{
		return totalUsed() + amount <= limit;
	}
	
	public boolean isAvailable(String player, int amount)
	{
		//TODO player limits
		return used.containsKey(player) ? totalPlayer(player) + amount <= limit : amount <= limit;
	}

	public void playerUpdate(String player, int amount)
	{
		if ( !used.containsKey(player) )
			used.put(player, new HashMap<Long, Integer>());
		used.get(player).put(new Date().getTime(), amount);
	}
	
	public void playerLoad(String player, long time, int amount)
	{
		if ( !used.containsKey(player) )
			used.put(player, new HashMap<Long, Integer>());
		used.get(player).put(time, amount);
	}
	
	//getters
	int getLimit()
	{
		return limit;
	}
	long getTimeout()
	{
		return timeout;
	}
	int getPlayerLimit() 
	{
		return playerLimit;
	}
	long getPlayerTimeout()
	{
		return playerTimeout;
	}
	Map<String, Map<Long, Integer>> entries()
	{
		return used;
	}
}
