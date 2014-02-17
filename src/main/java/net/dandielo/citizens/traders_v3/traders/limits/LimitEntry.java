package net.dandielo.citizens.traders_v3.traders.limits;

import java.util.HashMap;
import java.util.Map;

public class LimitEntry {	
	//named ID
	private String id;
	
	//limit settings
	private int limit;
	private long timeout;

	private int playerLimit;
	private long playerTimeout;
	
	//limit usage
	private Map<String, Integer> used;	
	
	//constructors
	public LimitEntry(String id, int limit, long timeout)
	{
		//limit id
		this.id = id;
		
		playerLimit = this.limit = limit;
		playerTimeout = this.timeout = timeout;
		
		used = new HashMap<String, Integer>();
	}
	
	//methods
	public int totalUsed()
	{
		int result = 0;
		for( Integer value : used.values() )
			result += value.intValue();
		return result;
	}
	
	//general methods
	public boolean isAvailable(int amount)
	{
		return totalUsed() + amount <= limit;
	}
	public boolean isAvailable(String player, int amount)
	{
		return used.containsKey(player) ? used.get(player).intValue() + amount <= playerLimit : amount <= playerLimit;
	}

	public void update(String player, int amount)
	{
		if ( used.containsKey(player) )
		{
			used.put(player, used.get(player).intValue() + amount);
		}
		else
		{
			used.put(player, amount);
		}
	}
}
