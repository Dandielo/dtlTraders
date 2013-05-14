package net.dandielo.citizens.traders_v3.traders.stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.core.exceptions.InvalidDataNodeException;
import net.dandielo.citizens.traders_v3.traders.Trader.Status;
import net.dandielo.citizens.traders_v3.utils.ItemUtils;
import net.dandielo.citizens.traders_v3.utils.RegexMatcher;
import net.dandielo.citizens.traders_v3.utils.items.ItemData;
import net.dandielo.citizens.traders_v3.utils.items.ItemFlag;
import net.dandielo.citizens.traders_v3.utils.items.data.Amount;
import net.dandielo.citizens.traders_v3.utils.items.data.Price;
import net.dandielo.citizens.traders_v3.utils.items.data.Slot;
import net.dandielo.citizens.traders_v3.utils.items.flags.Lore;

import org.bukkit.inventory.ItemStack;

public class StockItem {
	public static String ITEM_PATTERN = "(([^ :]+):([^ :]+))|([^ :]*)";

	//Item info
	private ItemStack item;
	private Map<String, ItemData> data = new HashMap<String, ItemData>();
	private Map<String, ItemFlag> flags = new HashMap<String, ItemFlag>();

	//constructors
	public StockItem(ItemStack item)
	{
		this.item = item;  
	}

	public StockItem(String format)
	{
		load(format);
	}

	public StockItem(String format, List<String> list)
	{
		load(format);

		Lore lore = new Lore("lore").setLore(list);
		if ( flags.containsKey("lore") )
			flags.put("lore", lore);
	}

	public void load(String format)
	{
		String[] itemFormat = format.split(" ", 2);
		item = ItemUtils.createItemStack(itemFormat[0]);

		Matcher matcher = RegexMatcher.instance().getMatcher("item", itemFormat[1]);

		String value = "";
		String key = "";
		while(matcher.find())
		{
			if ( !key.isEmpty() && !key.equals(matcher.group(2)) )
			{
				try 
				{
					if ( key.startsWith(".") )
						flags.put(key, ItemFlag.createItemFlag(key));
					else
						data.put(key, ItemData.createItemData(key, value));
				}
				catch (InvalidDataNodeException e) 
				{
					DtlTraders.warning("Could not load item data with key: " + key + ", value: " + value);
				}
			}

			if ( key.equals(matcher.group(2)) )
			{
				value += " " + matcher.group(4);
			}
			else
			{
				key = matcher.group(2);
				value = matcher.group(3);
			}
		}
	}

	public String save()
	{
		String result = "";
		result += item.getTypeId();
		if ( !ItemUtils.itemHasDurability(item) )
			result += ":" + item.getData().getData();

		for ( ItemData entry : data.values() )
			result += " " + entry.saveString();

		for ( ItemFlag flag : flags.values() )
			if ( flag.getValue() )
				result += " " + flag.getKey();		
		return result;
	}

	public ItemStack getItem()
	{
		ItemStack item = this.item.clone();
		for ( ItemData info : data.values() )
			info.assing(item);
		for ( ItemFlag flag : flags.values() )
			flag.assing(item);
		return item;
	}

	public List<String> getDataLore(Status status)
	{
		List<String> lore = new ArrayList<String>();
		for ( ItemData info : data.values() )
			info.lore(status, lore);
		return lore;
	}

	//unspecified data
	public boolean hasData(String key)
	{
		return data.containsKey(key);
	}

	public <T> T getData(String key)
	{
		return data.get(key).<T>getValue(this);
	}

	public void addData(ItemData data)
	{
		this.data.put(data.getKey(), data);
	}

	public void addFlag(ItemFlag flag)
	{
		flags.put(flag.getKey(), flag);
	}

	//price
	public boolean hasPrice()
	{
		return data.containsKey("p");
	}

	public double getPrice()
	{
		return ((Price) data.get("p")).getPrice();
	}

	//slot
	public boolean hasSlot()
	{
		return data.containsKey("s");
	}

	public void setSlot(int slot) {
		data.put("s", new Slot("s").setSlot(slot));
	}

	public int getSlot()
	{
		return hasSlot() ? ((Slot) data.get("s")).getSlot() : -1;
	}

	//slot
	public boolean hasMultipleAmounts()
	{
		return ((Amount) data.get("a")).hasMultipleAmounts();
	}

	public int getAmount()
	{
		return ((Amount) data.get("a")).getAmount();
	}

	//equality checks
	public boolean equals(StockItem item)
	{
		boolean equals = true;
		for ( ItemData data : this.data.values() )
			equals = equals ? item.data.containsValue(data) : equals;
	    for ( ItemFlag flag : flags.values() )
		    equals = equals ? item.flags.containsValue(flag) : equals;
		return equals;
	}

	@Override
	public boolean equals(Object object)
	{
		return equals((StockItem)object);
	}
}
