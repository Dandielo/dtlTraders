package net.dandielo.citizens.traders_v3.traders.stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import net.dandielo.citizens.traders_v3.tNpcStatus;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.dB.DebugLevel;
import net.dandielo.citizens.traders_v3.core.exceptions.InvalidItemException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidClassException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.utils.ItemUtils;
import net.dandielo.citizens.traders_v3.utils.RegexMatcher;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;
import net.dandielo.citizens.traders_v3.utils.items.ItemFlag;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Amount;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Multiplier;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Name;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Price;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Slot;
import net.dandielo.citizens.traders_v3.utils.items.flags.Abstract;
import net.dandielo.citizens.traders_v3.utils.items.flags.DataCheck;
import net.dandielo.citizens.traders_v3.utils.items.flags.Lore;

import org.bukkit.inventory.ItemStack;

/**
 * Item structure using in each trader stock, this structure allows to save and store more data than the commom ItemStack bukkit structure.
 * @author dandielo
 */
@SuppressWarnings({"deprecation"})
public final class StockItem {
	/**
	 * Pattern used for loading data form strings
	 */
	public static String ITEM_PATTERN = "(([^ :]+):([^ :]+))|([^ :]*)";

	/**
	 * The bukkit representation of the item, this item is always saved without any data assigned. Assigning is done on a copy when needed.
	 */
	private ItemStack item;
	
	/**
	 * All additional data set for the item
	 */
	private Map<Class<? extends ItemAttr>, ItemAttr> attr = new HashMap<Class<? extends ItemAttr>, ItemAttr>();
	
	/**
	 * Flags for checking some things fast
	 */
	private Map<Class<? extends ItemFlag>, ItemFlag> flags = new HashMap<Class<? extends ItemFlag>, ItemFlag>();

	/**
	 * Creates a stock item base on the bukkit item provided. It only saves the id and data, nothing else.
	 * @param item
	 *     bukkit item stack item
	 */
	public StockItem(ItemStack item)
	{
		this.item = new ItemStack(item.getType(), 1, ItemUtils.itemHasDurability(item) ? 0 : item.getDurability()); 
	}

	/**
	 * Creates a stock item by using the given format string. It will also initialize with all attributes found in the format string.
	 * Default attributes will also apply
	 * @param format
	 *     the formated save string
	 */
	public StockItem(String format)
	{
		load(format);
	}

	/**
	 * This constructor is called when a lore was found for the item. The lore will apply only if the format contains the <b>.lore</b> flag.  
	 * @param format
	 *     the formated save string
	 * @param list
	 *     the lore that should be applied to the item
	 */
	public StockItem(String format, List<String> list)
	{
		//load the item
		this(format);

		//assign the lore if the flag  was found, or remove the flag
		if ( hasFlag(Lore.class) )
			getFlag(Lore.class).setLore(list);
	}
 
	/**
	 * Reads the format string and loads all attributes and flags into the structure.
	 * @param
	 *     the formated save string
	 */
	public void load(String format)
	{
		//always create the item as abstract
		addFlag(".abstract");
		
		//split the item format string
		String[] itemData = format.split(" ", 2);
		
		//creating the item using ID and Data
		item = ItemUtils.createItemStack(itemData[0]);
		
		//Reset all attributes
		resetAttr();
		
		//add data check flag if data was set for an item by hand
		if ( itemData[0].contains(":") )
			addFlag(".dc");
		
		//load the regex matcher
		Matcher matcher = RegexMatcher.instance().getMatcher("item", itemData[1]);

		//temporary value
		String value = "";
		//temporary key
		String key = "";
		
		//find matches
		while(matcher.find())
		{
			if ( matcher.group(2) != null )
			{ 
				//if the key starts with a dot then it's a flag
				if ( key.startsWith(".") )
					addFlag(key);
				//else if it's not empty it's a attribute 
				else /* compatibility start */ if ( !key.startsWith("!") && value != null ) /* compatibility end */ 
				if ( !key.isEmpty() )
					addAttr(key, value.trim());
				
				//set new values
				key = matcher.group(2);
				value = matcher.group(3);
			}
			else
			if ( matcher.group(4) != null )
			{
				if ( matcher.group(4).startsWith(".") )
				{
					//if the key starts with a dot then it's a flag
					if ( key.startsWith(".") )
						addFlag(key);
					//else if it's not empty it's a attribute 
					else /* compatibility start */ if ( !key.startsWith("!") && value != null ) /* compatibility end */
					if ( !key.isEmpty() )
						addAttr(key, value.trim());
					
					//set new values
					key = matcher.group(4);
					value = "";
				}
				else if ( !matcher.group(4).isEmpty() )
				{
					//fix for 2.5 compatibility
					if ( !matcher.group(4).startsWith("!") )
					    value += " " + matcher.group(4);
				}
			}
		}
		//if the key starts with a dot then it's a flag
		if ( key.startsWith(".") )
			addFlag(key);
		//else if it's not empty it's a attribute 
		else /* compatibility start */ if ( !key.startsWith("!") && value != null ) /* compatibility end */
		if ( !key.isEmpty() )
			addAttr(key, value.trim());
	}

	/**
	 * Creates a string representation of the item, used for saving. 
	 * Only lores are saved in another way.
	 * @return
	 *     prepared save string
	 */
	public String save()
	{		
		//result string
		String result = "";
		
		//add id and data information
		result += item.getTypeId();
		if ( !ItemUtils.itemHasDurability(item) && item.getData().getData() != 0 )
			result += ":" + item.getData().getData();

		//save each attribute
		for ( ItemAttr entry : attr.values() )
			result += " " + entry.toString();


		//remove abstract modifier
		ItemFlag abs = flags.remove(Abstract.class);
		
		//save each flag
		for ( ItemFlag flag : flags.values() )
			result += " " + flag.getKey();		
		
		//put it back
		if ( abs != null ) flags.put(Abstract.class, abs);
		
		//return the result
		return result;
	}
	
	/**
	 * Tries to factorize the given item getting all possible data out of it. The factorizing methods will create flags and attributes for the item. 
	 * @param item
	 *     item to factorize
	 */
	public void factorize(ItemStack item)
	{
		//debug low
	//	dB.low("Factorizing item: ", item.getType().name().toLowerCase());
		
		for ( ItemAttr iAttr : ItemAttr.getAllAttributes() )
		{
			try 
			{
				iAttr.setItem(this);
				iAttr.onFactorize(item);
				attr.put(iAttr.getClass(), iAttr);
			}
			catch (AttributeValueNotFoundException e)
			{
				this.debugMsgValue(iAttr.getInfo(), "factorized");
			}
		}
		
		//factorize flags
		for ( ItemFlag iFlag : ItemFlag.getAllFlags() )
		{
			try 
			{
				iFlag.setItem(this);
				iFlag.onFactorize(item);
				flags.put(iFlag.getClass(), iFlag);
			}
	    	catch (AttributeValueNotFoundException e)
			{
				this.debugMsgValue(iFlag.getInfo(), "factorized");
			}
		}
		
		//if the lore was already managed don't load it anymore
		if ( loreManaged ) return;

		Lore lore = null;
		try 
		{
			lore = (Lore) ItemFlag.initFlag(this, ".lore");
			lore.onFactorize(item);
			
			flags.put(lore.getClass(), lore); 
		}
		catch (AttributeValueNotFoundException e)
		{
			this.debugMsgValue(lore != null ? lore.getInfo() : null, "factorized");
		}
		catch (AttributeInvalidClassException e) 
		{
			this.debugMsgClass(".lore");
		} 
		
	}
	
	/**
	 * @param clazz
	 *     flag class that should be checked for
	 * @return
	 *     true if the flag was found
	 */
	public boolean hasFlag(Class<? extends ItemFlag> clazz)
	{
		return flags.containsKey(clazz);
	}
	
	/**
	 * @param clazz
	 *     flag class that will be retrieved
	 * @return
	 *     the flag if its present in the structure, nil otherwise
	 */
	@SuppressWarnings("unchecked")
	public <T extends ItemFlag> T getFlag(Class<T> clazz)
	{
		return hasFlag(clazz) ? (T) flags.get(clazz) : null;
	}
	
	/**
	 * Looks for the flag declaration that has the specified <b>key</b>. If found it will add it automatically to the items structure. 
	 * @param key
	 *     the unique flag key
	 */
	public void addFlag(String key)
	{
		try
		{
			ItemFlag flag = ItemFlag.initFlag(this, key.toLowerCase());
			flags.put(flag.getClass(), flag);
		} 
		catch (Exception e)
		{
			debugMsgClass(key);
		}
	}

	/**
	 * @param clazz
	 *     flag class that should be removed
	 * @return
	 *     true if the flag that was removed
	 */
	@SuppressWarnings("unchecked")
	public <T extends ItemFlag> T removeFlag(Class<T> clazz)
	{
		return (T) flags.remove(clazz);
	}
	
	/**
	 * @param clazz
	 *     attribute class that should be checked for
	 * @return
	 *     true if the attribute was found
	 */
	public boolean hasAttr(Class<? extends ItemAttr> clazz)
	{
		return attr.containsKey(clazz);
	}
	
	/**
	 * @param clazz
	 *     attribute class that will be retrieved
	 * @return
	 *     the attribute if its present in the structure, nil otherwise
	 */
	@SuppressWarnings("unchecked")
	public <T extends ItemAttr> T getAttr(Class<T> clazz)
	{
		return hasAttr(clazz) ? (T) attr.get(clazz) : null;
	}

	/**
	 * Looks for the attribute declaration that has the specified <b>key</b>. 
	 * If found it will add it automatically to the items structure and initialize with the given value. 
	 * @param key
	 *     the unique attribute key
	 * @param value
	 *     the value to initialize with
	 */
	@SuppressWarnings("null")
	public void addAttr(String key, String value)
	{
		dB.spec(DebugLevel.S2_MAGIC_POWA, "Attribute: ", key, " | ", value);
	
		ItemAttr itemAttr = null;
		try
		{
			itemAttr = ItemAttr.initAttribute(this, key.toLowerCase(), value);
			
			//2.x compatibility fix
			if ( itemAttr.getKey().equals("d") && !ItemUtils.itemHasDurability(item) )
				itemAttr = null;
			
			if ( itemAttr != null )
			    this.attr.put(itemAttr.getClass(), itemAttr);
			else
			{
				//debug message that this key does not exists
	//			dB.high("The given key is not registered, skipping...");
	//			dB.high("key: ", key);
			}
		} 
		catch (AttributeInvalidClassException e) 
		{
			debugMsgClass(key);
		} 
		catch (AttributeInvalidValueException e)
		{
			debugMsgValue(itemAttr.getInfo(), value);
		}
	}

	/**
	 * Tries to get the specified attribute, if it's not assigned to this item, it will try to create a new instance and assign it to the item with default attribute values.
	 * @param clazz
	 *     the attribute to retrieve
	 * @return
	 *     the searched attribute
	 */
	private <T extends ItemAttr> T tryGetAttr(Class<T> clazz)
	{
		if ( hasAttr(clazz) ) return getAttr(clazz);
		
		T itemAttr = null;
		try
		{
			itemAttr = ItemAttr.initAttribute(this, clazz);
			this.attr.put(clazz, itemAttr);
		} 
		catch (Exception e)
		{
			this.debugMsgClass(itemAttr != null ? itemAttr.getInfo().key() : null);
		}
		return itemAttr;
	}

	/**
	 * @param clazz
	 *     attr class that should be removed
	 * @return
	 *     true if the attr that was removed
	 */
	@SuppressWarnings("unchecked")
	public <T extends ItemAttr> T removeAttr(Class<T> clazz)
	{
		return (T) attr.remove(clazz);
	}
	
	/**
	 * Removes all attributes and adds only the required ones.
	 */
	private void resetAttr()
	{
		attr.clear();
		for ( ItemAttr reqAttr : ItemAttr.getRequiredAttributes() )
			attr.put(reqAttr.getClass(), reqAttr);
	}
	
	/**
	 * Same as the save string representation
	 */
	@Override 
	public String toString()
	{
		return save();
	}
	
	/**
	 * @param inStock tells the onAssign method if the item is going to be displayed in the traders stock or if it's the users new End-Item
	 * @return
	 *     a Item Stack item with all attributes and flag data assigned to it.
	 */
	public ItemStack getItem(boolean endItem)
	{
		//clone the "clean" item
		ItemStack clone = this.item.clone();

		//add the lore as the first one
		if ( flags.containsKey(Lore.class) )
			try { flags.get(Lore.class).onAssign(clone, endItem); } catch(Exception e) { }
		
		//assign attribute data to it
		for ( ItemAttr itemAttr : this.attr.values() )
		{
			try 
			{
				//try assign the attribute
			    itemAttr.onAssign(clone, endItem);
			} 
			catch (InvalidItemException e)
			{
				debugMsgItem(itemAttr.getInfo());
			}
		}
		
		for ( ItemFlag flag : flags.values() )
		{
			try 
			{
				//try assign the flag
				if ( !flag.getKey().equals(".lore") )
				    flag.onAssign(clone, endItem);
			} 
			catch (InvalidItemException e)
			{
				debugMsgItem(flag.getInfo());
			}
		}
		//returns the valid item
		return clone;
	}

	/**
	 * Returns a list of temporary lore strings that should be applied depending on the traders status. 
	 * @param status
	 *     Status that is checked
	 * @param target
	 *     The a copy of item that gets the lore assigned
	 * @return
	 *     List of lore strings
	 */ 
	public List<String> getTempLore(tNpcStatus status, ItemStack target)
	{
		//create a new list
		List<String> lore = new ArrayList<String>();
		
		//check and add if there is already lore attached
		if ( this.hasFlag(Lore.class) )
			lore.addAll(this.getLore());
		
		//for each attribute
		for ( ItemAttr itemAttr : this.attr.values() )
			for ( tNpcStatus attrStatus : itemAttr.getInfo().status() )
				if ( attrStatus.equals(status) )
			        itemAttr.onStatusLoreRequest(status, target, lore);
		
		//for each flag
		for ( ItemFlag itemFlag : flags.values() )
			for ( tNpcStatus attrStatus : itemFlag.getInfo().status() )
				if ( attrStatus.equals(status) )
			        itemFlag.onStatusLoreRequest(status, lore);
		
		//return the lore
		return lore;
	}

	/**
	 * Attribute helper methods, these methods are just shortcuts wti safety checks for some attributes.
	 */

	/**
	 * @return
	 *     true if the price attribute is present
	 */
	public boolean hasPrice()
	{
		return hasAttr(Price.class);
	}
	
	/**
	 * Gets the items price if the attribute is present.
	 * @return
	 *     the price set, or <b>-1.0</b> instead
	 */
	public double getPrice()
	{
		return hasAttr(Price.class) ? getAttr(Price.class).getPrice() : -1.0;
	}

	/**
	 * @return
	 *     the price values as a formated string
	 */
	public String getPriceFormated()
	{
		return hasPrice() ? String.format("%.2f", getPrice()) : "none";
	}
	
	/**
	 * @return
	 *     the price attribute, if there is no attrib it will create one.
	 */
	public Price getPriceAttr()
	{
		return tryGetAttr(Price.class);
	}

	/**
	 * Gets the items multiplier value
	 * @return
	 * the multiplier set, or -1.0 instead
	 */
	public double getMultiplier()
	{
		return hasAttr(Multiplier.class) ? this.getAttr(Multiplier.class).getMultiplier() : -1.0;
	}
	
	/**
	 * Checks if the item has a multiplier assigned
	 * @return
	 * true if a the multiplier attribute is set
	 */
	public boolean hasMultiplier()
	{
		return hasAttr(Multiplier.class);
	}
	
	/**
	 * Gets the items slot attribute, always a valid value.
	 * @return
	 *     the items slot in stock. 
	 */
	public int getSlot()
	{
		return hasAttr(Slot.class) ? getAttr(Slot.class).getSlot() : -1;
	}

	/**
	 * Checks if the given slot is equal to the items one.
	 * @return
	 *     true if slots are equal
	 */
	public boolean checkSlot(int slot)
	{
		return getAttr(Slot.class).getSlot() == slot;
	}
	
	/**
	 * Sets the slot attribute for the item
	 * @param slot
	 */
	public void setSlot(int slot)
	{
		getAttr(Slot.class).setSlot(slot);
	}
	
	/**
	 * @return
	 *     true if more than 1 amount is set
	 */
	public boolean hasMultipleAmounts()
	{
		return getAttr(Amount.class).hasMultipleAmounts();
	}

	/**
	 * @return
	 *    the first set amount, considered as the major one
	 */
	public int getAmount()
	{
		return getAttr(Amount.class).getAmount();
	}

	/**
	 * @param i
	 *     index of the amount to get
	 * @return
	 *     amount under the given index 
	 */
	public int getAmount(int i)
	{
		return getAttr(Amount.class).getAmount(i);
	}
	
	/**
	 * @return
	 *     all saved amounts
	 */
	public List<Integer> getAmounts()
	{
		return getAttr(Amount.class).getAmounts();
	}
	
	/**
	 * Adds a new amount to the stock item
	 * @param a
	 * amount that should be added
	 */
	public void addAmount(int a)
	{ 
		getAttr(Amount.class).addAmount(a);
	}
	
	/**
	 * This method returns the items name if its present. When no name is found it will use the material name, with lower case letters.  
	 * @return
	 *     the item name, or material name instead
	 */
	public String getName()
	{
		return hasAttr(Name.class) ? getAttr(Name.class).getName() : item.getType().name().toLowerCase();
	}
	
	/**
	 * @return
	 *    the assigned lore, or null otherwise
	 */
	public List<String> getLore()
	{
		return hasFlag(Lore.class) ? getFlag(Lore.class).getLore() : null;
	}

	/**
	 * holds info if the lore was managed by another plugin
	 */
	private boolean loreManaged = false;
	
	/**
	 * This function tells the item factorizer should load the lore or if it skip it because some other attribute has managed it. 
	 * @param managed
	 * true if the lore loading should be skipped
	 */
	public void loreManaged(boolean managed)
	{
		loreManaged = managed;
	}
	
	private boolean standaloneAttrCheck(StockItem item)
	{
		boolean containsAll = true;
		for ( ItemAttr key : item.attr.values() )
			containsAll = containsAll && !key.getInfo().standalone() ? this.attr.containsKey(key.getClass()) : containsAll;
		for ( ItemAttr key : this.attr.values() )
			containsAll = containsAll && !key.getInfo().standalone() ? item.attr.containsKey(key.getClass()) : containsAll;
		return containsAll;
	}
	private boolean standaloneFlagCheck(StockItem item)
	{
		boolean containsAll = true;
		for ( ItemFlag key : item.flags.values() )
			containsAll = containsAll && !key.getInfo().standalone() ? this.flags.containsKey(key.getClass()) : containsAll;
		for ( ItemFlag key : this.flags.values() )
			containsAll = containsAll && !key.getInfo().standalone() ? item.flags.containsKey(key.getClass()) : containsAll;
		return containsAll;
	}
	private boolean attributeMissmatch(StockItem item)
	{
		return !(standaloneAttrCheck(item) && standaloneFlagCheck(item));
	}
	
	
	
	private boolean patternStandaloneAttrCheck(StockItem item)
	{
		boolean containsAll = true;
		for ( ItemAttr key : this.attr.values() )
			containsAll = containsAll && !key.getInfo().standalone() ? item.attr.containsKey(key.getClass()) : containsAll;
		return containsAll;
	}
	private boolean patternStandaloneFlagCheck(StockItem item)
	{
		boolean containsAll = true;
		for ( ItemFlag key : this.flags.values() )
			containsAll = containsAll && !key.getInfo().standalone() ? item.flags.containsKey(key.getClass()) : containsAll;
		return containsAll;
	}
	private boolean patternAttributeMissmatch(StockItem item)
	{
		return !(patternStandaloneAttrCheck(item) && patternStandaloneFlagCheck(item));
	}
	
	/**
	 * Strong equality is needed when we need precise information if an item is equal, example: Stock placement
	 * @param item
	 *     item to compare against
	 * @return
	 *     true if equal 
	 */
	public final boolean equalsStrong(StockItem item)
	{
		boolean equals;
		//check id
		equals = item.item.getTypeId() == this.item.getTypeId();
		//if equals check data, if not durability
		equals = equals && !ItemUtils.itemHasDurability(item.item) ? item.item.getDurability() == this.item.getDurability() : equals; 
		
		//checking attribute missmatching
		equals = equals ? !attributeMissmatch(item) : equals;
		
	//	dB.low("After ID and data check: ", equals);
		
		//now a if block to not make thousands of not needed checks 
		if ( equals )
		{
			//for each attribute in this item
			for ( ItemAttr tAttr : attr.values() )
			{
				//if only once is false then return false
				if ( !equals ) break;
				
				//temporary false
				equals = tAttr.getInfo().standalone();
				
				//debug low
		//		dB.low("Before ", tAttr.getInfo().name() ," check: ", equals, ", with: ", tAttr.onSave());
				
				//check each item in the second item, if the attribute is found and strong equal continue
				for ( ItemAttr iAttr : item.attr.values() )
				{
					//debug low
		//			dB.info("Checking ", iAttr.getInfo().name() ," with: ", iAttr.onSave());
					
					//same attributes
					if ( tAttr.getClass().equals(iAttr.getClass()) )
						equals = tAttr.equalsStrong(iAttr);
				}
				
				//debug low
				dB.low("After ", tAttr.getInfo().name() ," check: ", equals);
			}
			
			//for each attribute in this item
			for ( ItemFlag tFlag : flags.values() )
			{
				//if only once is false then return false
				if ( !equals ) break;
				
				//temporary false
				equals = tFlag.getInfo().standalone();
				
				
				//check each item in the second item, if the attribute is found and strong equal continue
				for ( ItemFlag iFlag : item.flags.values() )
					//same attributes
					if ( tFlag.getClass().equals(iFlag.getClass()) )
						equals = tFlag.equalsStrong(iFlag);
				
			}
		}
		return equals;
	}
	
	/**
	 * Weak equality is needed when we need only some informations about the equality, example: adding items to players inventory does not need amount equality.
	 * @param item
	 *     item to compare against
	 * @return
	 *     true if equal 
	 */
	public final boolean equalsWeak(StockItem item)
	{
		boolean equals;
		//check id
		equals = item.item.getTypeId() == this.item.getTypeId();
		//if equals check data, if not durability
		equals = equals && !ItemUtils.itemHasDurability(item.item) ? item.item.getDurability() == this.item.getDurability() : equals; 

		//checking attribute missmatching
		equals = equals ? !attributeMissmatch(item) : equals;

	//	dB.low("After ID and data check: ", equals);
		
		//now a if block to not make thousands of not needed checks 
		if ( equals )
		{
			//for each attribute in this item
			for ( ItemAttr tAttr : attr.values() )
			{
				//if only once is false then return false
				if ( !equals ) break;
				
				//temporary false
				equals = tAttr.getInfo().standalone();

				//debug low
		//		dB.low("Before ", tAttr.getInfo().name() ," check: ", equals, ", with: ", tAttr.onSave());
				
				//check each item in the second item, if the attribute is found and strong equal continue
				for ( ItemAttr iAttr : item.attr.values() )
				{
					//debug low
			//		dB.info("Checking ", iAttr.getInfo().name() ," with: ", iAttr.onSave());
					
					//same attributes
					if ( tAttr.getClass().equals(iAttr.getClass()) )
						equals = tAttr.equalsWeak(iAttr);
				}
				
				//debug low
		//		dB.low("After ", tAttr.getInfo().name() ," check: ", equals);
			}
			
			//for each attribute in this item
			for ( ItemFlag tFlag : flags.values() )
			{
				//if only once is false then return false
				if ( !equals ) break; //tFlag.getInfo().standalone();
				
				//temporary false
				equals = tFlag.getInfo().standalone();
				
				//check each item in the second item, if the attribute is found and strong equal continue
				for ( ItemFlag iFlag : item.flags.values() )
					//same attributes
					if ( tFlag.getClass().equals(iFlag.getClass()) )
						equals = tFlag.equalsWeak(iFlag);
			}
		}
		return equals;
	}

	/**
	 * uses strong equality
	 */
	@Override
	public final boolean equals(Object object)
	{
		return (object instanceof StockItem && equalsStrong((StockItem)object));
	}
	
	public final int priorityMatch(StockItem that)
	{
		int priority = 0;

		//id and data check
		if ( this.hasFlag(DataCheck.class) )
		{
			if ( this.item.getTypeId() != 0 )
			{
				if ( this.item.getTypeId() == that.item.getTypeId() &&
					 this.item.getDurability() == that.item.getDurability() )
					priority += 140;
				else
					priority = -2;
			}
			else
			{
				if ( this.item.getTypeId() == 0 && this.item.getDurability() == that.item.getDurability() )
					priority += 120;
				else
					priority = -2;
			}
		}
		else
		{
			if ( this.item.getTypeId() != 0 )
			{
				if ( this.item.getTypeId() == that.item.getTypeId() )
				{
					priority = 130;
				}
				else priority = -2;
			}
			else
			    priority = 0;
		}

		if ( patternAttributeMissmatch(that) ) return -2;
		
		//now a if block to not make thousands of not needed checks 
		if ( priority < 0 ) return priority;

		//for each attribute in this item
		for ( ItemAttr tAttr : attr.values() )
		{
			//check each item in the second item, if the attribute is found and strong equal continue
			for ( ItemAttr iAttr : that.attr.values() )
			{
				//debug low
		//		dB.info("Checking ", iAttr.getInfo().name() ," with: ", iAttr.onSave());

				//same attributes
				if ( tAttr.getClass().equals(iAttr.getClass()) && tAttr.equalsStrong(iAttr) )
					priority += tAttr.getInfo().priority();
			}
			//debug low
		//	dB.low("After ", tAttr.getInfo().name() ," check: ", String.valueOf(priority));
		}
			
		//for each attribute in this item
		for ( ItemFlag tFlag : flags.values() )
		{
			//check each item in the second item, if the attribute is found and strong equal continue
			for ( ItemFlag iFlag : that.flags.values() )
			{
				//same attributes
				if ( tFlag.getClass().equals(iFlag.getClass()) && tFlag.equalsStrong(iFlag) )
					priority += tFlag.getInfo().priority();
			}
		}
		
	//	dB.info("Priority result: ", priority);
		return priority;
	}
	
	@Override
	public int hashCode() {
	    int hash = 7;
	    
	    hash = 73 * hash + (this.item != null ? this.item.hashCode() : 0);
	    hash = 73 * hash + (this.attr != null ? this.attr.hashCode() : 0);
	    hash = 73 * hash + (this.flags != null ? this.flags.hashCode() : 0);
	    hash = 73 * hash + (this.loreManaged ? 1 : 0);
	    
	    return hash;
	}
	
	/**
	 * Debug messages for attribute and flag class errors
	 */
	private void debugMsgClass(String key)
	{
	//	dB.high("Attribute/Flag class exception, the attribute class is invalid");
	//	dB.high("Attribute/Flag key: ", ChatColor.GOLD, key);
	}

	/**
	 * Debug messages for attribute value errors
	 */
	private void debugMsgValue(Attribute attr, String value)
	{
	//	dB.normal("Attribute value initialization exception");
	//	dB.normal("Attribute: ", (attr != null ? attr.name() : "null"), ", value: ", ChatColor.GOLD, value);
	}

	/**
	 * Debug messages for attribute and flag item incompatibility
	 */
	private void debugMsgItem(Attribute attr)
	{
	//	dB.normal("Attribute/Flag item incompatibility");
	//	dB.normal("Attribute/Flag: ", (attr != null ? attr.name() : "null"), ", item: ", ChatColor.GOLD, this.getName());
	}
}
