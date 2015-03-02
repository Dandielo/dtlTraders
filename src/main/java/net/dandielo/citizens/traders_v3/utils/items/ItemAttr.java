package net.dandielo.citizens.traders_v3.utils.items;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dandielo.citizens.traders_v3.tNpcStatus;
import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.exceptions.InvalidItemException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidClassException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.tools.StringTools;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Amount;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Banner;
import net.dandielo.citizens.traders_v3.utils.items.attributes.BlockCurrency;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Book;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Durability;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Enchant;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Firework;
import net.dandielo.citizens.traders_v3.utils.items.attributes.GenericDamage;
import net.dandielo.citizens.traders_v3.utils.items.attributes.GenericHealth;
import net.dandielo.citizens.traders_v3.utils.items.attributes.GenericKnockback;
import net.dandielo.citizens.traders_v3.utils.items.attributes.GenericSpeed;
import net.dandielo.citizens.traders_v3.utils.items.attributes.LeatherColor;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Limit;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Multiplier;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Name;
import net.dandielo.citizens.traders_v3.utils.items.attributes.PatternItem;
import net.dandielo.citizens.traders_v3.utils.items.attributes.PlayerResourcesCurrency;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Potion;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Price;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Skull;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Slot;
import net.dandielo.citizens.traders_v3.utils.items.attributes.StoredEnchant;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Tier;

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
	 * @brief Attribute key, used for saving and identification (is Unique)
	 */
	private final String key;
	private final String sub;

	/**
	 * @brief All informations about this attribute 
	 */
	protected Attribute info;

	/***
	 * @brief The item associated with the attribute
	 */
	protected StockItem item;

	/**
	 * @brief Default constructor (needs a key)
	 * @param key
	 *     the attribute key
	 */
	public ItemAttr(String key)
	{
		this.key = key;
		this.sub = null;
	}

	/** 
	 * Sub constructor, creates a sub-attribute
	 *  
	 * Creates a sub-attribute, using the same class that the main key uses. 
	 * The sub parameter is readable later in each request thus allowing to make one class for multiple parameters
	 * The created serialized key will be have the following pattern: {key}.{sub}:{value}
	 * 
	 *  @param key
	 *      The key parameter for the attribute class (from the @Attribute interface)
	 *  @param sub
	 *      Provided on registration sub-name for the new attribute
	 */
	public ItemAttr(String key, String sub)
	{
		this.key = key;// + "." + sub;
		this.sub = sub;
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
	 * @return 
	 *     The updated item
	 * @param item
	 *     The item for which we set the attribute values
	 * @param endItem 
	 *     tells the method if the item is just displayed in the traders inventory or if it's the users end-item he bought  
	 * @throws InvalidItemException
	 */
	public ItemStack onReturnAssign(ItemStack item, boolean endItem) throws InvalidItemException
	{
		onAssign(item, endItem);
		return item;
	}

	/**
	 * Called when the given item needs attributes re-set
	 * @return 
	 *     The updated item
	 * @param item
	 *     The item for which we set the attribute values
	 * @param endItem 
	 *     tells the method if the item is just displayed in the traders inventory or if it's the users end-item he bought  
	 * @throws InvalidItemException
	 */
	public void onAssign(ItemStack item, boolean endItem) throws InvalidItemException
	{
		onAssign(item);
	}

	/**
	 * Called when the given item needs attributes re-set. This method is called as a end-user item assigning method.
	 * @param item
	 *     The item for which we set the attribute values
	 * @throws InvalidItemException
	 */
	public void onAssign(ItemStack item) throws InvalidItemException
	{
		//DO NOTHING IF NOT NEEDED!
	}

	/**
	 * Called when trying to get attribute data information from the given item. If no valid data for this attribute is found then it throws an exception.
	 * @param item
	 * @throws AttributeValueNotFoundException
	 */
	public abstract void onFactorize(ItemStack item) throws AttributeValueNotFoundException;

	/**
	 * Called when a status lore request is send for the given status set in the attributes information.
	 * @param status
	 *     The calling status.
	 * @param lore
	 *     Lore list, allows to re-arrange previous assigned lore. 
	 */
	public void onStatusLoreRequest(tNpcStatus status, List<String> lore)
	{
	}

	/**
	 * Tells the item factorizing function to not check the lore anymore because it was already checked, retrieved and saved somewhere else. 
	 */
	public void loreManaged()
	{
		item.loreManaged(true);
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
	 * Sets the stock item for this item attribute
	 */
	public void setItem(StockItem item)
	{
		this.item = item;
	}

	/**
	 * @return returns the attributes save string.
	 */
	@Override
	public final String toString()
	{
		return key + (sub != null ? "." + sub : "") + ":" + onSave();
	}

	/**
	 * @return
	 *     The attributes unique key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return
	 *     Returns the attributes subkey.
	 */
	public String getSub() {
		return sub;
	}

	@Override
	@SuppressWarnings("all")
	public final boolean equals(Object o)
	{
		return (o instanceof ItemAttr && key.equals(((ItemAttr)o).key) && (sub == null ? ((ItemAttr)o).sub == null : sub.equals(((ItemAttr)o).sub)));
	}

	@Override
	public final int hashCode()
	{
		return (key + (sub != null ? "." + sub : "")).hashCode();
	}

	/**
	 * Static data section, holds all registered attributes and provides some utilities
	 */

	/**
	 * Map containing all registered attributes
	 */
	private static final Map<Attribute, Class<? extends ItemAttr>> attributes = new HashMap<Attribute, Class<? extends ItemAttr>>();
	private static final Map<String, Attribute> keys = new HashMap<String, Attribute>();

	/**
	 * Returns all attribute instances in a list. This list is used later to factorize data from a item.
	 * @param item
	 *     The item thats material will be checked for getting all attributes for it.</br>
	 *     Set to <strong>null</strong> if you want to get ALL attributes
	 * @return
	 *     A list of each attribute instance
	 */
	public static List<ItemAttr> getAllAttributes(ItemStack item)
	{
		//create the list holding all attribute instances
		List<ItemAttr> result = new ArrayList<ItemAttr>();
		for ( Map.Entry<Attribute, Class<? extends ItemAttr>> attr : attributes.entrySet() )
		{
			Attribute attrInfo = attr.getKey();
			
			//check if we need this attribute
			if (attrInfo.required() || Arrays.binarySearch(attrInfo.items(), item.getType()) >= 0 ||
				(attrInfo.items().length == 0 && !attrInfo.standalone()))
			{
				try 
				{
					try {
						Constructor<? extends ItemAttr> constr = attr.getValue().getConstructor(String.class);
						if (constr != null)
						{
							ItemAttr iAttr = constr.newInstance(attrInfo.key());
							iAttr.info = attrInfo;
							result.add(iAttr);
						}
					}catch(NoSuchMethodException ex) {
						dB.info("No Attribute constructor available");
					}

					//With subkeys
					for (String sub : attrInfo.sub())
					{
						ItemAttr iAttr = attr.getValue().getConstructor(String.class, String.class).newInstance(attrInfo.key(), sub);
						iAttr.info = attrInfo;
						result.add(iAttr);
					}
				} 
				catch (Exception e)
				{
					//debug high
					dB.high("Attribute exception on initialization");
					dB.high("Attribute name: ", ChatColor.GREEN, attr.getKey().name());

					//debug normal
					dB.normal("Exception: ", e.getClass().getSimpleName());
					dB.normal("Stack trace: ", StringTools.stackTrace(e.getStackTrace()));
				}
			}
		}
		return result;
	}

	/**
	 * Returns required attribute instances in a list. This list is used later to init any item with required attributes.
	 * Initial attributes do not contain sub-attributes.
	 * @return
	 *     A list of each attribute instance
	 */
	public static List<ItemAttr> getRequiredAttributes()
	{
		//create the list holding all attribute instances
		List<ItemAttr> result = new ArrayList<ItemAttr>();
		for ( Map.Entry<Attribute, Class<? extends ItemAttr>> attr : attributes.entrySet() )
		{
			try 
			{
				if ( attr.getKey().required() )
				{
					ItemAttr attrInstance = attr.getValue().getConstructor(String.class).newInstance(attr.getKey().key());
					attrInstance.info = attr.getKey();
					result.add(attrInstance);
				}
			} 
			catch (Exception e)
			{
				//debug high
				dB.high("Attribute exception on initialization");
				dB.high("Attribute name: ", ChatColor.GREEN, attr.getKey().name());

				//debug normal
				dB.normal("Exception: ", e.getClass().getSimpleName());
				dB.normal("Stack trace: ", StringTools.stackTrace(e.getStackTrace()));
			}
		}
		return result;
	}

	/**
	 * Registers a new attribute to the system, should be done before Citizens2 loading.
	 * @param clazz
	 *     The attribute class that should be registered.
	 * @throws InvalidDataNodeException
	 */
	public static void registerAttr(Class<? extends ItemAttr> clazz) throws AttributeInvalidClassException
	{
		if ( !clazz.isAnnotationPresent(Attribute.class) )
			throw new AttributeInvalidClassException();

		Attribute attr = clazz.getAnnotation(Attribute.class);

		//debug low
		dB.low("Registering attribute \'", ChatColor.GREEN, attr.name(), ChatColor.RESET, "\' with key: ", attr.key());

		attributes.put(attr, clazz);

		//create all key pairs
		keys.put(attr.key(), attr);
		for (String sub : attr.sub())
			keys.put(attr.key() + "." + sub, attr);
	}

	/**
	 * Registers a new attribute to the system, should be done before Citizens2 loading.
	 * @param clazz
	 *     The attribute class that should be registered.
	 * @throws AttributeException 
	 * @throws InvalidDataNodeException
	 */
	public static void extendAttrKey(String key, Class<? extends ItemAttr> clazz) throws AttributeException
	{
		if ( !clazz.isAnnotationPresent(Attribute.class) )
			throw new AttributeInvalidClassException();

		//if ( !keys.containsKey(key) )
		//	throw new AttributeException();

		Attribute attr = clazz.getAnnotation(Attribute.class);

		//debug low
		dB.low("Extending attribute with key \'", ChatColor.GREEN, key, ChatColor.RESET, "\' with: ", attr.name() + "." + attr.key());

		attributes.put(attr, clazz);

		//create all key pairs
		for (String sub : attr.sub())
			keys.put(key + "." + sub, attr);
	}

	/**
	 * Creates a attribute with default values based on the class given. Does not create sub attributes.
	 * @param item
	 *     The item associated with the flag
	 * @param clazz
	 *     The specified attribute class
	 * @return
	 *     Returns the initialized attribute if successful.
	 * @throws AttributeInvalidClassException 
	 * @throws AttributeInvalidValueException 
	 */
	public static <T extends ItemAttr> T initAttribute(StockItem item, Class<T> clazz) throws AttributeInvalidClassException, AttributeInvalidValueException
	{
		Attribute attr = clazz.getAnnotation(Attribute.class);
		try 
		{
			//debug low
			dB.low("Initializing new attribute instance");
			dB.low("Attribute: " + attr.name());
			dB.info("-------------------------------------");

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
	 * @param stockItem
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
	public static ItemAttr initAttribute(StockItem stockItem, String key, String value) throws AttributeInvalidClassException, AttributeInvalidValueException
	{
		//Search for the attribute
		Attribute attr = null;
		for ( String attrKey : keys.keySet() )
			if ( attrKey.equals(key) )
				attr = keys.get(attrKey);

		//if attribute key is not valid return null
		if ( attr == null ) return null;

		try 
		{
			//debug low
			dB.low("Initializing new attribute instance");
			dB.low("Attribute: " + attr.name());
			dB.info("With key: " + key);
			dB.info("With value: " + value);
			dB.info("-------------------------------------");

			
			ItemAttr itemAttr;
			if (key.contains("."))
			{
				String[] ks = key.split("\\.");
				itemAttr = attributes.get(attr).getConstructor(String.class, String.class).newInstance(ks[0], ks[1]);
			}
			else
			{
				itemAttr = attributes.get(attr).getConstructor(String.class).newInstance(key);
			} 

			//get the attribute declaring class
			//assoc the item 
			itemAttr.item = stockItem;
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
	private static void debugInfo(Attribute attr, Exception e)
	{
		//debug high
		dB.high("Attribute exception on initialization");
		dB.high("Attribute name: ", ChatColor.GREEN, attr.name());

		//debug normal
		dB.normal("Exception: ", e.getClass().getSimpleName());
		dB.normal("Stack trace: ", StringTools.stackTrace(e.getStackTrace()));
	}


	/**
	 * Registers all core attributes
	 */
	public static void registerCoreAttributes()
	{
		//debug info
		dB.info("Registering core item attributes");

		try 
		{
			//item related
			registerAttr(StoredEnchant.class);
			registerAttr(LeatherColor.class);
			registerAttr(PatternItem.class);
			registerAttr(Multiplier.class);
			registerAttr(Durability.class);
			registerAttr(Firework.class);
			registerAttr(Enchant.class);
			registerAttr(Banner.class);
			registerAttr(Amount.class);
			registerAttr(Potion.class);
			registerAttr(Skull.class);
			registerAttr(Tier.class);
			registerAttr(Book.class);
			registerAttr(Name.class);

			//NBT generic data
			extendAttrKey("g", GenericKnockback.class);
			extendAttrKey("g", GenericDamage.class);
			extendAttrKey("g", GenericHealth.class);
			extendAttrKey("g", GenericSpeed.class);
			
	 		//Stock item related
			registerAttr(Limit.class);
			registerAttr(Price.class);
			registerAttr(Slot.class);
			
			//extending classes
			extendAttrKey("p", BlockCurrency.class);
			extendAttrKey("p", PlayerResourcesCurrency.class);

			DtlTraders.info("Registered core attributes: " + attributesAsString());
		} 
		catch (AttributeInvalidClassException e) 
		{
			//debug critical
			dB.critical("Core attributes invalid");

			//debug high
			dB.high("Exception message: ", e.getMessage());
			dB.high("Stack trace: ", StringTools.stackTrace(e.getStackTrace()));
		}
		catch (AttributeException e)
		{
			//debug critical
			dB.critical("Core extended attributes invalid");

			//debug high
			dB.high("Exception message: ", e.getMessage());
			dB.high("Stack trace: ", StringTools.stackTrace(e.getStackTrace()));
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
		for ( Attribute attr : attributes.keySet() )
			result += ", " + ChatColor.YELLOW + attr.name() + ChatColor.RESET;

		return ChatColor.WHITE + "[" + result.substring(2) + ChatColor.WHITE + "]";
	}
}
