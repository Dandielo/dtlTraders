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
	private Map<String, Map<Long, Integer>> playerUsed;	
	
	//constructors
	public LimitEntry(String id, int limit, long timeout)
	{
		//limit id
		this.id = id;
		
		this.limit = limit;
		this.timeout = timeout;
		
		playerLimit = limit;
		playerTimeout = timeout;
		
		playerUsed = new HashMap<String, Map<Long, Integer>>();
	}
	
	public LimitEntry(String id, int limit, long timeout, int plimit, long ptimeout)
	{
		//limit id
		this.id = id;
		
		this.limit = limit;
		this.timeout = timeout;
		
		playerLimit = -1;
		playerTimeout = -1;
		
		playerUsed = new HashMap<String, Map<Long, Integer>>();
	}
	
	//methods
	public void limitRefresh()
	{
		long now = new Date().getTime();
		
		//remove all timed out data
		for ( Map<Long, Integer> entries : playerUsed.values() )
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
	public int totalPlayer(String palyerEntry)
	{
		int result = 0;
		//get the players total amount
		for ( Integer value : playerUsed.get(palyerEntry).values() )
			result += value.intValue();
		return result;
	}
	public int totalUsed()
	{
		int result = 0;
		for(String playerEntry : playerUsed.keySet())
			result += totalPlayer(playerEntry);
		return result;
	}
	
	//general methods
	/**
	 * Checks if the given amount can be added or subtracted from the limit pool
	 * <br><br>
	 * If the <strong>type</strong> equals <i>sell</i> it checks if the player can <i>sell</i> the given
	 * amount to a trader. Amount should be a negative integer.
	 * <br>
	 * If the <strong>type</strong> equals <i>buy</i> it checks if the player can <i>buy</i> the given
	 * amount from the trader. Amount should be a positive.
	 */
	public boolean isAvailable(String type, int amount)
	{
		boolean result = false;
		if (type.equals("buy"))
		{
			int h = totalUsed();
			result = (h>0?0:h) + amount <= limit;
		}
		else if (type.equals("sell"))
		{
			result = totalUsed() - amount >= 0;
		}
		return result;
	}
	
	public boolean isPlayerAvailable(String player, String type, int amount)
	{
		return playerLimit == -1 || ( playerUsed.containsKey(player + "@" + type) ? Math.abs(totalPlayer(player + "@" + type)) + Math.abs(amount) <= playerLimit : Math.abs(amount) <= playerLimit );
	}
	
	public void playerUpdate(String player, String type, int amount)
	{
		if ( !playerUsed.containsKey(player + "@" + type) )
			playerUsed.put(player + "@" + type, new HashMap<Long, Integer>());
		playerUsed.get(player + "@" + type).put(new Date().getTime(), amount);
	}
	
	public void playerLoad(String playerEntry, long time, int amount)
	{
		if ( !playerUsed.containsKey(playerEntry) )
			playerUsed.put(playerEntry, new HashMap<Long, Integer>());
		playerUsed.get(playerEntry).put(time, amount);
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
	Map<String, Map<Long, Integer>> playerEntries()
	{
		return playerUsed;
	}
}
