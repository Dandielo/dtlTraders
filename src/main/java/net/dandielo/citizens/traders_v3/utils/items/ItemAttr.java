package net.dandielo.citizens.traders_v3.utils.items;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.core.Debugger;
import net.dandielo.citizens.traders_v3.core.exceptions.InvalidItemException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidClassException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.tools.StringTools;
import net.dandielo.citizens.traders_v3.traders.Trader.Status;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Amount;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Durability;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Enchant;
import net.dandielo.citizens.traders_v3.utils.items.attributes.LeatherColor;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Name;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Potion;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Price;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Skull;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Slot;
import net.dandielo.citizens.traders_v3.utils.items.attributes.StoredEnchant;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

/**
 * This structure describes the item for what they are assigned. Each item can have only one attribute of each type.
 * Attributes allow to save and load additional data with items, they are also used for pattern setups
 * 
 * @author dandielo
 */
public abstract class ItemAttr {
	
	/**
	 * Attribute key, used for saving and identification (is Unique)
	 */
	private final String key;
	
	/**
	 * All informations about this attribute 
	 */
	private Attribute info;
	
	/**
	 * The item associated with the attribute
	 */
	protected StockItem item;
	
	/**
	 * default constructor (needs a key)
	 * @param key
	 *     the attribute key
	 */
	public ItemAttr(String key)
	{
		this.key = key;
	}
	
	/**
	 * Called when a attribute was found in a save string.
	 * @param data
	 *     the value associated with the attribute key
	 * @throws AttributeInvalidValueException
	 */
	public abstract void onLoad(String data) throws AttributeInvalidValueException;
	
	/**
	 * Called upon a save request, should return a string representation of its values 
	 * @return
	 */
	public abstract String onSave();
	
	/**
	 * Called when the given item needs attributes re-set
	 * @param item
	 *     The item for which we set the attribute values
	 * @throws InvalidItemException
	 */
	public abstract void onAssign(ItemStack item) throws InvalidItemException; 
	
	/**
	 * Called when trying to get attribute data information from the given item. If no valid data for this attribute is found then it throws an exception.
	 * @param item
	 * @throws AttributeValueNotFoundException
	 */
	public abstract void onFactorise(ItemStack item) throws AttributeValueNotFoundException;
	
	/**
	 * Called when a status lore request is send for the given status set in the attributes information.
	 * @param status
	 *     The calling status.
	 * @param lore
	 *     Lore list, allows to re-arrange previous assigned lore. 
	 */
	public void onStatusLoreRequest(Status status, List<String> lore)
	{
	}
	
	/**
	 * Called when a week equality is needed. Allows sometimes a value to be in range of another value, used for priority requests
	 * @return
	 *    true when equal, false instead 
	 */
	public boolean equalsWeak(ItemAttr attr)
	{
		return true;
	}
	
	/**
	 * Called when a strong equality is needed. Values are compared strict.
	 * @return
	 *    true when equal, false instead 
	 */
	public boolean equalsStrong(ItemAttr attr)
	{
		return true;
	}

	/**
	 * Returns information about the attribute
	 * @return
	 */
	public Attribute getInfo()
	{
		return info;
	}
	
	/**
	 * @return returns the attributes save string.
	 */
	@Override
	public final String toString()
	{
		return key + ":" + onSave();
	}
	
	/**
	 * @return
	 *     The attributes unique key
	 */
	public String getKey() {
		return key;
	}
	
	@Override
	@SuppressWarnings("all")
	public final boolean equals(Object o)
	{
		return key.equals(((ItemAttr)o).key);
	}
	
	@Override
	public final int hashCode()
	{
		return key.hashCode();
	}
	
	/**
	 * Static data section, holds all registered attributes and provides some utilities
	 */
	
	/**
	 * Map containing all registered attributes
	 */
	private static final Map<Attribute, Class<? extends ItemAttr>> attributes = new HashMap<Attribute, Class<? extends ItemAttr>>();
	
	/**
	 * Returns all attribute instances in a list. This list is used later to factorize data from a item.
	 * @return
	 *     A list of each attribute instance
	 */
	public static final List<ItemAttr> getAllAttributes()
	{
		//create the list holding all attribute instances
		List<ItemAttr> result = new ArrayList<ItemAttr>();
		for ( Map.Entry<Attribute, Class<? extends ItemAttr>> attr : attributes.entrySet() )
		{
			try 
			{
				ItemAttr iAttr = attr.getValue().getConstructor(String.class).newInstance(attr.getKey().key());
				iAttr.info = attr.getKey();
				result.add(iAttr);
			} 
			catch (Exception e)
			{
				//debug high
				Debugger.high("Attribute exception on initialization");
				Debugger.high("Attribute name: ", ChatColor.GREEN, attr.getKey().name());
				
				//debug normal
				Debugger.normal("Exception: ", e.getClass().getSimpleName());
				Debugger.normal("Stack trace: ", StringTools.stackTrace(e.getStackTrace()));
			}
		}
		return result;
	}
	
	/**
	 * Returns required attribute instances in a list. This list is used later to init any item with required attributes.
	 * @return
	 *     A list of each attribute instance
	 */
	public static final List<ItemAttr> getRequiredAttributes()
	{
		//create the list holding all attribute instances
		List<ItemAttr> result = new ArrayList<ItemAttr>();
		for ( Map.Entry<Attribute, Class<? extends ItemAttr>> attr : attributes.entrySet() )
		{
			try 
			{
				if ( attr.getKey().required() )
				    result.add(attr.getValue().getConstructor(String.class).newInstance(attr.getKey().key()));
			} 
			catch (Exception e)
			{
				//debug high
				Debugger.high("Attribute exception on initialization");
				Debugger.high("Attribute name: ", ChatColor.GREEN, attr.getKey().name());
				
				//debug normal
				Debugger.normal("Exception: ", e.getClass().getSimpleName());
				Debugger.normal("Stack trace: ", StringTools.stackTrace(e.getStackTrace()));
			}
		}
		return result;
	}

	/**
	 * Registers a new arrtibute to the system, should be done before Citizens2 loading.
	 * @param clazz
	 *     The attribute class that should be registered.
	 * @throws InvalidDataNodeException
	 */
	public final static void registerAttr(Class<? extends ItemAttr> clazz) throws AttributeInvalidClassException
	{
		if ( !clazz.isAnnotationPresent(Attribute.class) )
			throw new AttributeInvalidClassException();
		
		Attribute attr = clazz.getAnnotation(Attribute.class);

		//debug low
		Debugger.low("Registering attribute \'", ChatColor.GREEN, attr.name(), ChatColor.RESET, "\' with key: ", attr.key());
		
		attributes.put(attr, clazz);
	}
	
	/**
	 * Creates a attribute with default values based class given.
	 * @param item
	 *     The item associated with the flag
	 * @param clazz
	 *     The specified attribute class
	 * @return
	 *     Returns the initialized attribute if successful.
	 * @throws AttributeInvalidClassException 
	 * @throws AttributeInvalidValueException 
	 */
	public final static <T extends ItemAttr> T initAttribute(StockItem item, Class<T> clazz) throws AttributeInvalidClassException, AttributeInvalidValueException
	{
		Attribute attr = clazz.getAnnotation(Attribute.class);
		try 
		{
			//debug low
			Debugger.low("Initializing new attribute instance");
			
			//get the attribute declaring class
			T itemAttr = clazz.getConstructor(String.class).newInstance(attr.key());
			//assoc the item
			itemAttr.item = item;
			//assigning attribute information
			itemAttr.info = attr;
			//returning the initialized attribute
			return itemAttr;
		} 
		catch (InvocationTargetException e) 
		{
			debugInfo(attr, e);
			throw new AttributeInvalidClassException();
		} 
		catch (InstantiationException e)
		{
			debugInfo(attr, e);
			throw new AttributeInvalidClassException();
		} 
		catch (IllegalAccessException e) 
		{
			debugInfo(attr, e);
			throw new AttributeInvalidClassException();
		} 
		catch (IllegalArgumentException e) 
		{
			debugInfo(attr, e);
			throw new AttributeInvalidClassException();
		} 
		catch (NoSuchMethodException e)
		{
			debugInfo(attr, e);
			throw new AttributeInvalidClassException();
		} 
		catch (SecurityException e)
		{
			debugInfo(attr, e);
			throw new AttributeInvalidClassException();
		} 
		
	}
	
	/**
	 * Creates a attribute based on the key. If a attribute is found it will call the <i><b>onLoad</b></i> method with the given <b>value</b>.
	 * @param item
	 *     The item associated with the flag
	 * @param key
	 *     The attribute key, this is the unique key for each attribute.
	 * @param value
	 *     The value we will use to init the attribute.
	 * @return
	 *     Returns the initialized attribute if successful.
	 * @throws AttributeInvalidClassException 
	 * @throws AttributeInvalidValueException 
	 */
	public final static ItemAttr initAttribute(StockItem item, String key, String value) throws AttributeInvalidClassException, AttributeInvalidValueException
	{
		//Search for the attribute
		Attribute attr = null;
		for ( Attribute attrEntry : attributes.keySet() )
			if ( attrEntry.key().equals(key) )
				attr = attrEntry;
		
		try 
		{
			//debug low
			Debugger.low("Initializing new attribute instance");
			
			//get the attribute declaring class
			ItemAttr itemAttr = attributes.get(attr).getConstructor(String.class).newInstance(key);
			//assoc the item
			itemAttr.item = item;
			//calling the onLoad method
			itemAttr.onLoad(value);
			//assigning attribute information
			itemAttr.info = attr;
			//returning the initialized attribute
			return itemAttr;
		} 
		catch (InvocationTargetException e) 
		{
			debugInfo(attr, e);
			throw new AttributeInvalidClassException();
		} 
		catch (InstantiationException e)
		{
			debugInfo(attr, e);
			throw new AttributeInvalidClassException();
		} 
		catch (IllegalAccessException e) 
		{
			debugInfo(attr, e);
			throw new AttributeInvalidClassException();
		} 
		catch (IllegalArgumentException e) 
		{
			debugInfo(attr, e);
			throw new AttributeInvalidClassException();
		} 
		catch (NoSuchMethodException e)
		{
			debugInfo(attr, e);
			throw new AttributeInvalidClassException();
		} 
		catch (SecurityException e)
		{
			debugInfo(attr, e);
			throw new AttributeInvalidClassException();
		} 
		
	}

	/**
	 * Debug information
	 */
	private final static void debugInfo(Attribute attr, Exception e)
	{
		//debug high
		Debugger.high("Attribute exception on initialization");
		Debugger.high("Attribute name: ", ChatColor.GREEN, attr.name());
		
		//debug normal
		Debugger.normal("Exception: ", e.getClass().getSimpleName());
		Debugger.normal("Stack trace: ", StringTools.stackTrace(e.getStackTrace()));
	}
	

	/**
	 * Registers all core attributes
	 */
	public static void registerCoreAttributes()
	{
		//debug info
		Debugger.info("Registering core item attributes");
		
		try 
		{
			//item related
			registerAttr(Amount.class);
			registerAttr(Durability.class);
			//	registerData(Book.class);
			registerAttr(LeatherColor.class);
			registerAttr(Enchant.class);
			registerAttr(StoredEnchant.class);
			registerAttr(Potion.class);
			registerAttr(Skull.class);
			//	registerData(Firework.class);
			registerAttr(Name.class);
			
			//Stock item related
			//	registerData(Multiplier.class);
			registerAttr(Price.class);
			registerAttr(Slot.class);
			
			DtlTraders.info("Registered core attributes: " + attributesAsString());
		} 
		catch (AttributeInvalidClassException e) 
		{
			//debug critical
			Debugger.critical("Core attributes invalid");

			//debug high
			Debugger.high("Exception message: ", e.getMessage());
			Debugger.high("Stack trace: ", StringTools.stackTrace(e.getStackTrace()));
		}
	}
	
	/**
	 * Creates a string with all registered core attribute names
	 * @return 
	 *     formated result string
	 */
	private static String attributesAsString()
	{
		String result = "";
		//format the string
		for ( Attribute attr : attributes.keySet() )
			result += " ," + ChatColor.YELLOW + attr.name() + ChatColor.RESET;
		
		return ChatColor.WHITE + "[" + result.substring(2) + ChatColor.WHITE + "]";
	}
}
