package net.dandielo.citizens.traders_v3.traders.patterns.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import net.dandielo.citizens.traders_v3.bukkit.Perms;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.dB.DebugLevel;
import net.dandielo.citizens.traders_v3.traders.patterns.Pattern;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.traders.transaction.CurrencyHandler;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Multiplier;

public class PricePattern extends Pattern {
	private Map<String, List<StockItem>> items;
	private Map<String, PricePattern> inherits;
	private Map<String, PricePattern> tiers;

	public PricePattern(String name)
	{
		super(name, Type.PRICE);

		//init all maps
		items = new HashMap<String, List<StockItem>>();
		inherits = new HashMap<String, PricePattern>();
		tiers = new HashMap<String, PricePattern>();
	}

	public PricePattern(String name, boolean tier)
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

					if ( tier ) stockItem.addAttribute("t", getName());

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

						if ( tier ) stockItem.addAttribute("t", getName());

						//specific debug info
						dB.spec(DebugLevel.S2_MAGIC_POWA, "Added \"", stockItem, "\" item to the sell category");
						
						sell.add(stockItem);
					}
				}
				else
					if ( key.equals("buy") )
					{
						for ( String item : data.getStringList(key) )
						{
							StockItem stockItem = new StockItem(item);

							if ( tier ) stockItem.addAttribute("t", getName());

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
							PricePattern pattern = new PricePattern(key, true);
							pattern.loadItems(data.getConfigurationSection(key));

							tiers.put(key, pattern);
						}
		}
		dB.spec(DebugLevel.S2_MAGIC_POWA, "Added ", sell.size(), " items to the sell category");
		dB.spec(DebugLevel.S2_MAGIC_POWA, "Added ", buy.size(), " items to the buy category");
		this.items.put("sell", sell);
		this.items.put("buy", buy);
	}

	private static class Pair<K, V> {
	    private final K key;
	    private V value;

	    public static <K, V> Pair<K, V> createPair(K key, V value) {
	        return new Pair<K, V>(key, value);
	    }

	    public Pair(K key, V value) {
	        this.key = key;
	        this.value = value;
	    }
	}
	
	public static class ItemCurrencies {
		private Map<String, Pair<CurrencyHandler, Integer>> currencies = new HashMap<String, Pair<CurrencyHandler, Integer>>();
		private Pair<Multiplier, Integer> multiplier = Pair.createPair(null, -1);
		
		public void merge(ItemCurrencies that)
		{
			if (multiplier.value <= that.multiplier.value)
				multiplier = that.multiplier;
			
			for (Map.Entry<String, Pair<CurrencyHandler, Integer>> currency : that.currencies.entrySet())
			{
				Pair<CurrencyHandler, Integer> tempCurrency = currency.getValue();
				Pair<CurrencyHandler, Integer> mappedCurrency = currencies.get(currency.getKey());
				
				if (mappedCurrency != null)
				{
					if (mappedCurrency.value <= tempCurrency.value)
					{
						currencies.put(currency.getKey(), tempCurrency);
					}
				}
				else
				{
					currencies.put(currency.getKey(), tempCurrency);
				}
			}
		}
		
		public void merge(CurrencyHandler handler, Integer priority) {
			Pair<CurrencyHandler, Integer> mappedCurrency = currencies.get(handler.getName());

			if (mappedCurrency != null)
			{
				if (mappedCurrency.value <= priority)
				{
					currencies.put(handler.getName(), Pair.createPair(handler, priority));
				}
			}
			else if (priority >= 0)
			{
				currencies.put(handler.getName(), Pair.createPair(handler, priority));
			}
		}
		
		public void multiplier(Multiplier attr, Integer priority)
		{
			if (multiplier.value <= priority)
				multiplier = Pair.createPair(attr, priority);
		}
		
		public void resetPriorities() {
			multiplier.value = -1;
			for (Map.Entry<String, Pair<CurrencyHandler, Integer>> currency : currencies.entrySet())
				currency.getValue().value = -1;
		}
		
		public Set<CurrencyHandler> getCurrencies() {
			Set<CurrencyHandler> result = new HashSet<CurrencyHandler>();
			for (Pair<CurrencyHandler, Integer> entry : currencies.values())
				result.add(entry.key);
			return result;
		}
		
		public double getMultiplier() {
			return multiplier.key == null ? 1.0 : multiplier.key.getMultiplier();
		}
	}
	
	public ItemCurrencies getItemCurrency(Player player, String stock, StockItem item)
	{
		ItemCurrencies result = new ItemCurrencies();

		//check inherited
		for (Map.Entry<String, PricePattern> e : inherits.entrySet())
		{
			if (e.getValue() != null 
					&& Perms.hasPerm(player, "dtl.trader.patterns." + e.getKey()))
			    result.merge(e.getValue().getItemCurrency(player, stock, item));
		}
		
		//reset priorities before we continue
		result.resetPriorities();
		dB.spec(DebugLevel.CURRENCY, "Stock size: ", items.get(stock).size());
		//TODO: Currency update
//		for (StockItem patternItem : items.get(stock))
//		{
//			dB.spec(DebugLevel.CURRENCY, "Item: ", patternItem.toString());
//			int tempPriority = patternItem.priorityMatch(item);
//			dB.spec(DebugLevel.CURRENCY, "Priority: ", tempPriority);
//			if (patternItem.hasMultiplier())
//				result.multiplier(patternItem.getAttr(Multiplier.class), tempPriority + 1000 * priority);
//			for (ItemAttribute patternAttrib : patternItem.getAttribs("p"))
//				if (patternAttrib instanceof CurrencyHandler)
//					result.merge((CurrencyHandler) patternAttrib, tempPriority + 1000 * priority);
//		}
		
		//override with tiers
		for (Map.Entry<String, PricePattern> e : tiers.entrySet())
		{
			if (Perms.hasPerm(player, "dtl.trader.tiers." + e.getKey()))
			{
			    result.merge(e.getValue().getItemCurrency(player, stock, item));
			}
		}		
		return result;
	}
}
