package net.dandielo.citizens.traders_v3.traders.patterns.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import net.dandielo.citizens.traders_v3.traders.patterns.Pattern;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;

public class Item extends Pattern {

	private Map<String, List<StockItem>> items;
	private Map<String, Item> inherits;
	private Map<String, Item> tiers;

	public Item(String name)
	{
		super(name, Type.ITEM);

		//init all maps
		items = new HashMap<String, List<StockItem>>();
		inherits = new HashMap<String, Item>();
		tiers = new HashMap<String, Item>();
	}

	public Item(String name, boolean tier)
	{
		this(name);

		//set the tier to tier
		this.tier = tier;
	}

	public void loadItems(ConfigurationSection data)
	{
		//create both buy and sell lists
		List<StockItem> sell = new ArrayList<StockItem>();
		List<StockItem> buy = new ArrayList<StockItem>();

		//load the patterns priority
		priority = data.getInt("priority", 0);

		//load the patterns items
		for ( String key : data.getKeys(false) )
		{
			if ( key.equals("all") )
			{
				for ( String item : data.getStringList(key) )
				{
					StockItem stockItem = new StockItem(item);

					if ( tier ) stockItem.addAttr("t", getName());

					sell.add(stockItem);
					buy.add(stockItem);
				}
			}
			else
				if ( key.equals("sell") )
				{
					for ( String item : data.getStringList(key) )
					{
						StockItem stockItem = new StockItem(item);
						if ( tier ) stockItem.addAttr("t", getName());

						sell.add(stockItem);
					}
				}
				else
					if ( key.equals("buy") )
					{
						for ( String item : data.getStringList(key) )
						{
							StockItem stockItem = new StockItem(item);
							if ( tier ) stockItem.addAttr("t", getName());

							buy.add(stockItem);
						}
					}
					else
						if ( !tier && key.equals("inherit") )
						{
							for ( String pat : data.getStringList(key) )
								inherits.put(pat, null);
						}
						else if ( !key.equals("type") && !key.equals("priority") )
						{
							Item pattern = new Item(key, true);
							pattern.loadItems(data.getConfigurationSection(key));

							tiers.put(key, pattern);
						}
		}
		this.items.put("sell", sell);
		this.items.put("buy", buy);
	}
	
	List<StockItem> updateList(List<StockItem> oldList)
	{
		List<StockItem> newList = new ArrayList<StockItem>();
		return newList;
	}
}
