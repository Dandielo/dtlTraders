package net.dandielo.citizens.traders_v3.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dandielo.citizens.traders_v3.traders.stock.StockItem;

public class RegexMatcher {
	//regex matcher instance
	private static RegexMatcher instance = new RegexMatcher();
	
	public static RegexMatcher instance()
	{
		return instance;
	}
	
	//regex matcher class
	private Map<String, Pattern> patterns = new HashMap<String, Pattern>();
	
	private RegexMatcher()
	{
		registerPattern("item", StockItem.ITEM_PATTERN);
	}
	
	public void registerPattern(String name, String pattern)
	{
		patterns.put(name, Pattern.compile(pattern));
	}
	
	public Matcher getMatcher(String name, String string)
    {
    	return patterns.get(name).matcher(string);
    }
}
