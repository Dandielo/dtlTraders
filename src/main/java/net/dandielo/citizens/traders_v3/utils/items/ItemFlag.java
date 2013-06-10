package net.dandielo.citizens.traders_v3.utils.items;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dandielo.citizens.traders_v3.tNpcItem;
import net.dandielo.citizens.traders_v3.tNpcStatus;
import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.core.Debugger;
import net.dandielo.citizens.traders_v3.core.exceptions.InvalidItemException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidClassException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.core.tools.StringTools;
import net.dandielo.citizens.traders_v3.utils.items.flags.Lore;
import net.dandielo.citizens.traders_v3.utils.items.flags.Splash;
import net.dandielo.citizens.traders_v3.utils.items.flags.StackPrice;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

/**
 * This structure describes the item for what they are assigned. Each item can have only one flag of each type.
 * Flags allow to store and get fast additional information, they are also used for pattern setups
 * 
 * @author dandielo
 */
public abstract class ItemFlag {
	
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
	protected tNpcItem item;
	
	/**
	 * default constructor (needs a key)
	 * @param key
	 *     the flag key
	 */
	public ItemFlag(String key)
	{
		this.key = key;
	}

	/**
	 * Called when the given item needs flags re-set
	 * @param item
	 *     The item for which we set the flag values
	 * @throws InvalidItemException
	 */
	public abstract void onAssign(ItemStack item) throws InvalidItemException; 
	
	/**
	 * Called when a status lore request is send for the given status set in the flags information.
	 * @param status
	 *     The calling status.
	 * @param lore
	 *     Lore list, allows to re-arrange previous assigned lore. 
	 */
	public void onStatusLoreRequest(tNpcStatus status, List<String> lore)
	{
	}
	
	/**
	 * Called when trying to get flag data information from the given item. If no valid data for this flag is found then it throws an exception.
	 * @param item
	 * @throws AttributeValueNotFoundException
	 */
	public void onFactorize(ItemStack item) throws AttributeValueNotFoundException
	{
		throw new AttributeValueNotFoundException();
	}
	
	/**
	 * Called when a week equality is needed. Allows sometimes a value to be in range of another value, used for priority requests
	 * @return
	 *    true when equal, false instead 
	 */
	public boolean equalsWeak(ItemFlag flag)
	{
		return true;
	}
	
	/**
	 * Called when a strong equality is needed. Values are compared strict.
	 * @return
	 *    true when equal, false instead 
	 */
	public boolean equalsStrong(ItemFlag flag)
	{
		return true;
	}
	
	/**
	 * Returns information about the flag
	 * @return
	 */
	public Attribute getInfo()
	{
		return info;
	}
	
	/**
	 * @return returns the flags save string.
	 */
	@Override
	public final String toString()
	{
		return key;
	}
	
	/**
	 * @return
	 *     the flags unique key
	 */
	public String getKey() {
		return key;
	}
	
	@Override
	public int hashCode()
	{
		return key.hashCode();
	}
	
	@Override
	public final boolean equals(Object o)
	{
		return (o instanceof ItemFlag && key.equals(((ItemFlag)o).key));
	}
	
	//getting item datas
	private final static Map<Attribute, Class<? extends ItemFlag>> flags = new HashMap<Attribute, Class<? extends ItemFlag>>();
	
	/**
	 * Returns all flag instances in a list. This list is used later to factorize data from a item.
	 * @return
	 *     A list of each flag instance
	 */
	public static List<ItemFlag> getAllFlags()
	{
		//create the list holding all flag instances
		List<ItemFlag> result = new ArrayList<ItemFlag>();
		for ( Map.Entry<Attribute, Class<? extends ItemFlag>> flag : flags.entrySet() )
		{
			//we don't want the lore flag in here
			if ( flag.getValue().equals(Lore.class) ) continue;
			
			try 
			{
				ItemFlag iFlag = flag.getValue().getConstructor(String.class).newInstance(flag.getKey().key());
				iFlag.info = flag.getKey();
				result.add(iFlag);
			} 
			catch (Exception e)
			{
				//debug normal
				Debugger.normal("Flag exception on initialization");
				Debugger.normal("Flag name: ", ChatColor.GREEN, flag.getKey().name());
				
				//debug low
				Debugger.low("Exception: ", e.getClass().getSimpleName());
				Debugger.low("Stack trace: ", StringTools.stackTrace(e.getStackTrace()));
			}
		}
		return result;
	}
	
	/**
	 * Registers a new flag to the system, should be done before Citizens2 loading.
	 * @param clazz
	 *     The falg class that should be registered.
	 * @throws InvalidDataNodeException
	 */
	public static void registerFlag(Class<? extends ItemFlag> clazz) throws AttributeInvalidClassException
	{
		if ( !clazz.isAnnotationPresent(Attribute.class) )
			throw new AttributeInvalidClassException();
		
		Attribute attr = clazz.getAnnotation(Attribute.class);

		//debug low
		Debugger.low("Registering flag \'", ChatColor.GREEN, attr.name(), ChatColor.RESET, "\' with key: ", attr.key());
		
		flags.put(attr, clazz);
	}
	
	/**
	 * Creates a flag based on the key. 
	 * @param item
	 *     The item associated with the flag
	 * @param key
	 *     The flag key, this is the unique key for each flag.
	 * @return
	 *     Returns the initialized flag if successful.
	 * @throws AttributeInvalidClassException 
	 * @throws AttributeInvalidValueException 
	 */
	public static ItemFlag initFlag(tNpcItem item, String key) throws AttributeInvalidClassException
	{
		//Search for the attribute
		Attribute attr = null;
		for ( Attribute attrEntry : flags.keySet() )
			if ( attrEntry.key().equals(key) )
				attr = attrEntry;
		
		try 
		{
			//debug low
			Debugger.low("Initializing new flag instance");
			
			//get the attribute declaring class
			ItemFlag itemflag = flags.get(attr).getConstructor(String.class).newInstance(key);
			//assoc the item
			itemflag.item = item;
			//assigning attribute information
			itemflag.info = attr;
			//returning the initialized attribute
			return itemflag;
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
	private static void debugInfo(Attribute attr, Exception e)
	{
		//debug high
		Debugger.high("Flag exception on initialization");
		Debugger.high("Flag name: ", ChatColor.GREEN, attr.name());
		
		//debug normal
		Debugger.normal("Exception: ", e.getClass().getSimpleName());
		Debugger.normal("Stack trace: ", StringTools.stackTrace(e.getStackTrace()));
	}
	

	/**
	 * Registers all core flags
	 */
	public static void registerCoreFlags()
	{
		//debug info
		Debugger.info("Registering core item flags");
		
		try 
		{
			registerFlag(StackPrice.class);
			registerFlag(Splash.class);
			registerFlag(Lore.class);
		//	registerFlag(PaternPrice.class);
		//	registerFlag(Price.class);
		//	registerFlag(Slot.class);
			
			DtlTraders.info("Registered core flags: " + flagsAsString());
		} 
		catch (AttributeInvalidClassException e) 
		{
			//debug critical
			Debugger.critical("Core flags invalid");

			//debug high
			Debugger.high("Exception message: ", e.getMessage());
			Debugger.high("Stack trace: ", StringTools.stackTrace(e.getStackTrace()));
		}
	}
	
	private static String flagsAsString()
	{
		String result = "";
		//format the string
		for ( Attribute attr : flags.keySet() )
			result += " ," + ChatColor.YELLOW + attr.name() + ChatColor.RESET;
		
		return ChatColor.WHITE + "[" + result.substring(2) + ChatColor.WHITE + "]";
	}
}
