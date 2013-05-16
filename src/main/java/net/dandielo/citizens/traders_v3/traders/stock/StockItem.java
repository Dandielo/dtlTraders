package net.dandielo.citizens.traders_v3.traders.stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import net.dandielo.citizens.traders_v3.core.Debugger;
import net.dandielo.citizens.traders_v3.core.exceptions.InvalidDataAssignmentException;
import net.dandielo.citizens.traders_v3.core.exceptions.InvalidDataNodeException;
import net.dandielo.citizens.traders_v3.traders.Trader.Status;
import net.dandielo.citizens.traders_v3.utils.ItemUtils;
import net.dandielo.citizens.traders_v3.utils.RegexMatcher;
import net.dandielo.citizens.traders_v3.utils.items.ItemData;
import net.dandielo.citizens.traders_v3.utils.items.ItemFlag;
import net.dandielo.citizens.traders_v3.utils.items.data.Amount;
import net.dandielo.citizens.traders_v3.utils.items.data.Name;
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
		ItemData.initWithDefaults(this);
	}

	public StockItem(String format)
	{
		//debug info
		Debugger.info("Creating stock item");
		Debugger.info(format);
		
		load(format);
	}

	public StockItem(String format, List<String> list)
	{
		//debug info
		Debugger.info("Creating stock item");
		Debugger.info(format);
		
		//load
		load(format);

		Lore lore = new Lore(".lore").setLore(list);
		if ( flags.containsKey(".lore") )
			flags.put(".lore", lore);
	}

	public void load(String format)
	{
		String[] itemFormat = format.split(" ", 2);
		item = ItemUtils.createItemStack(itemFormat[0]);
		ItemData.initWithDefaults(this);

		Matcher matcher = RegexMatcher.instance().getMatcher("item", itemFormat[1]);

		String value = "";
		String key = "";
		while(matcher.find())
		{
			if ( matcher.group(2) != null )
			{
				try 
				{
					if ( key.startsWith(".") )
						flags.put(key, ItemFlag.createItemFlag(key));
					else if ( !key.isEmpty() )
						data.put(key, ItemData.createItemData(item, key, value.trim()));
				}
				catch (InvalidDataNodeException e) 
				{
					//debug high
					Debugger.high("While loading a StockItem, a exception occured");
					Debugger.high("Exception: ", e.getClass().getSimpleName());
					
					//debug normal
					Debugger.normal("Exception message: ", e.getMessage());
					Debugger.normal("StackTrace: ", e.getStackTrace());
				}
				catch (InvalidDataAssignmentException e) 
				{
					//debug normal
					Debugger.normal("This data cannot be assigned to this item");
					Debugger.normal("Key: ", key, ", value: ", value, ", item: ", item.getType().name().toLowerCase());
					Debugger.normal("Exception: ", e.getClass().getSimpleName());
					
					//debug low
					Debugger.low("Exception message: ", e.getMessage());
					Debugger.low("StackTrace: ", e.getStackTrace());
				}
				key = matcher.group(2);
				value = matcher.group(3);
			}
			else
			if ( matcher.group(4) != null )
			{
				if ( matcher.group(4).startsWith(".") )
				{
					try 
					{
						if ( key.startsWith(".") )
							flags.put(key, ItemFlag.createItemFlag(key));
						else if ( !key.isEmpty() )
							data.put(key, ItemData.createItemData(item, key, value.trim()));
					}
					catch (InvalidDataNodeException e) 
					{
						//debug high
						Debugger.high("While loading a StockItem, a exception occured");
						Debugger.high("Exception: ", e.getClass().getSimpleName());
						
						//debug normal
						Debugger.normal("Exception message: ", e.getMessage());
						Debugger.normal("StackTrace: ", e.getStackTrace());
					}
					catch (InvalidDataAssignmentException e) 
					{
						//debug normal
						Debugger.normal("This data cannot be assigned to this item");
						Debugger.normal("Key: ", key, ", value: ", value, ", item: ", item.getType().name().toLowerCase());
						Debugger.normal("Exception: ", e.getClass().getSimpleName());
						
						//debug low
						Debugger.low("Exception message: ", e.getMessage());
						Debugger.low("StackTrace: ", e.getStackTrace());
					}
					key = matcher.group(4);
					value = "";
				}
				else if ( !matcher.group(4).isEmpty() )
				{
					value += " " + matcher.group(4);
				}
			}
		}
		try 
		{
			if ( key.startsWith(".") )
				flags.put(key, ItemFlag.createItemFlag(key));
			else if ( !key.isEmpty() )
				data.put(key, ItemData.createItemData(item, key, value.trim()));
		}
		catch (InvalidDataNodeException e) 
		{
			//debug high
			Debugger.high("While loading a StockItem, a exception occured");
			Debugger.high("Exception: ", e.getClass().getSimpleName());
			
			//debug normal
			Debugger.normal("Exception message: ", e.getMessage());
			Debugger.normal("StackTrace: ", e.getStackTrace());
		} 
		catch (InvalidDataAssignmentException e) 
		{
			//debug normal
			Debugger.normal("This data cannot be assigned to this item");
			Debugger.normal("Key: ", key, ", value: ", value, ", item: ", item.getType().name().toLowerCase());
			Debugger.normal("Exception: ", e.getClass().getSimpleName());
			
			//debug low
			Debugger.low("Exception message: ", e.getMessage());
			Debugger.low("StackTrace: ", e.getStackTrace());
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
	
	@Override 
	public String toString()
	{
		return save();
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
		//debug info
		Debugger.info("Getting data from StockItem, data key: ", key);
		return data.get(key).<T>getValue(this);
	}

	public void addData(ItemData data)
	{
		this.data.put(data.getKey(), data);
	}
	
	//flags
	public boolean hasFlag(String key)
	{
		return flags.containsKey(key);
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

	public int getAmount(int i)
	{
		return ((Amount) data.get("a")).getAmount(i);
	}
	
	//name
	public String getName()
	{
		return hasData("n") ? ((Name)data.get("n")).getName() : item.getType().name().toLowerCase();
	}
	
	//lore
	public List<String> getLore()
	{
		return ((Lore)flags.get(".lore")).getLore();
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
