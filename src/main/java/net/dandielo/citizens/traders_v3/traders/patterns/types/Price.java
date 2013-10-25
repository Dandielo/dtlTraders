package net.dandielo.citizens.traders_v3.traders.patterns.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import net.dandielo.citizens.traders_v3.bukkit.Perms;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.dB.DebugLevel;
import net.dandielo.citizens.traders_v3.traders.patterns.Pattern;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;

public class Price extends Pattern {

	private Map<String, List<StockItem>> items;
	private Map<String, Price> inherits;
	private Map<String, Price> tiers;

	public Price(String name)
	{
		super(name, Type.PRICE);

		//init all maps
		items = new HashMap<String, List<StockItem>>();
		inherits = new HashMap<String, Price>();
		tiers = new HashMap<String, Price>();
	}

	public Price(String name, boolean tier)
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
							Price pattern = new Price(key, true);
							pattern.loadItems(data.getConfigurationSection(key));

							tiers.put(key, pattern);
						}
		}
		dB.spec(DebugLevel.S2_MAGIC_POWA, "Added ", sell.size(), " items to the sell category");
		dB.spec(DebugLevel.S2_MAGIC_POWA, "Added ", buy.size(), " items to the buy category");
		this.items.put("sell", sell);
		this.items.put("buy", buy);
	}

	public PriceMatch findPriceFor(Player player, String stock, StockItem item)
	{
		PriceMatch result = new PriceMatch();

		//check inherited
		for ( Map.Entry<String, Price> e : inherits.entrySet() )
		{
			if ( e.getValue() == null ) continue;

			if ( Perms.hasPerm(player, "dtl.trader.patterns." + e.getKey()) )
			    result.merge(e.getValue().findPriceFor(player, stock, item));
		}
		result.resetPriority();
		
		//override with current
		for ( StockItem price : items.get(stock) )
		{
			int temp = price.priorityMatch(item);
			temp = temp < 0 ? temp : this.priority*1000 + temp;

			result.price(price.getPrice(), temp);
			result.multiplier(price.getMultiplier(), temp);
		}

		//override with tiers
		for ( Map.Entry<String, Price> e : tiers.entrySet() )
		{
			if ( Perms.hasPerm(player, "dtl.trader.tiers." + e.getKey()) )
			{
			    result.merge(e.getValue().findPriceFor(player, stock, item));
			}
		}
		
		if ( !tier )
		{
			dB.spec(DebugLevel.S2_MAGIC_POWA, "Matching result for item: ", item.toString());
			dB.spec(DebugLevel.S2_MAGIC_POWA, "Price: ", result.finalPrice());
			dB.spec(DebugLevel.S2_MAGIC_POWA, "Multiplier: ", result.data[1]);
		}
		
		if ( item.hasPrice() )
			result.price(item.getPrice());
		
		if ( item.hasMultiplier() )
			result.multiplier(item.getMultiplier());
		
		return result;
	}
	
	public static class PriceMatch
	{
		double data[] = new double[2];
		int priority[] = new int[2];
		
		public PriceMatch()
		{
			for ( int i = 0 ; i < 2 ; ++i )
			{
				data[i] = -1.0;
				priority[i] = -1;
			}
		}

		public void price(double value)
		{
			data[0] = value;
		}
		
		public void multiplier(double value)
		{
			data[1] = value;
		}

		public void price(double value, int priority)
		{
			if ( this.priority[0] > priority || value < 0.0 ) return;
			
			data[0] = value;
			this.priority[0] = priority;
		}

		public void multiplier(double value, int priority)
		{
			if ( this.priority[1] > priority || value < 0.0 ) return;
			
			data[1] = value;
			this.priority[1] = priority;
		}
		
		public void resetPriority()
		{
			priority[0] = -1; priority[1] = -1;
		}
		
		public void merge(PriceMatch that)
		{
			for ( int i = 0 ; i < 2 ; ++i )
			if ( priority[i] <= that.priority[i] )
			{
				priority[i] = that.priority[i];
				data[i] = that.data[i];
			}
		}
		
		public double finalPrice()
		{
			if ( data[1] < 0.0 ) data[1] = 1.0;
			
			return data[0]*data[1];
		}
	}
	
	public static void main(String[] args)
	{
		System.out.println(0.7 < 0);
	}
	
	/*public static void main(String[] a) throws IOException, ScriptException
	{ScriptEngineManager mgr = new ScriptEngineManager();
    ScriptEngine engine = mgr.getEngineByName("JavaScript");
    String foo = "40+2*2.1";
    System.out.println(engine.eval(foo));
	}*/
}
