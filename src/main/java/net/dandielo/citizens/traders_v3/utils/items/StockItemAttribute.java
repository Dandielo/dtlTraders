package net.dandielo.citizens.traders_v3.utils.items;

import java.util.List;
import net.dandielo.citizens.traders_v3.TEntityStatus;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.core.items.serialize.ItemAttribute;

/**
 * This structure describes the item for what they are assigned. Each item can have only one attribute of each type.
 * Attributes allow to save and load additional data with items, they are also used for pattern setups
 * 
 * @author dandielo
 */
public abstract class StockItemAttribute extends ItemAttribute {
	/**
	 * @brief All informations about this stock attribute 
	 */
	protected ShopStatus status;

	/***
	 * @brief The item associated with the attribute
	 */
	protected StockItem item;

	/**
	 * @brief Default constructor (needs a key)
	 * @param key
	 *     the attribute key
	 */
	public StockItemAttribute(StockItem item, String key)
	{
		super(item, key);
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
	public StockItemAttribute(StockItem item, String key, String sub)
	{
		super(item, key, sub);
	}
	
	/**
	 * Called when a status lore request is send for the given status set in the attributes information.
	 * @param status
	 *     The calling status.
	 * @param lore
	 *     Lore list, allows to re-arrange previous assigned lore. 
	 */
	public void getDescription(TEntityStatus status, List<String> lore)
	{
	}

//	/**
//	 * Tells the item factorizing function to not check the lore anymore because it was already checked, retrieved and saved somewhere else. 
//	 */
//	public void loreManaged()
//	{
//		item.loreManaged(true);
//	}

	/**
	 * Returns information about the attribute
	 * @return
	 */
	public ShopStatus getShopStatus()
	{
		return status;
	}

	/**
	 * Sets the stock item for this item attribute
	 */
	@Deprecated
	public void setItem(StockItem item)
	{
		this.item = item;
	}

	@Override
	public final int hashCode()
	{
		return (key + (sub != null ? "." + sub : "")).hashCode();
	}

//	/**
//	 * Static data section, holds all registered attributes and provides some utilities
//	 */
//
//	/**
//	 * Map containing all registered attributes
//	 */
//	private static final Map<Attribute, Class<? extends ItemAttribute>> attributes = new HashMap<Attribute, Class<? extends ItemAttribute>>();
//	private static final Map<String, Attribute> keys = new HashMap<String, Attribute>();
//
//	/**
//	 * Returns all attribute instances in a list. This list is used later to factorize data from a item.
//	 * @param item
//	 *     The item thats material will be checked for getting all attributes for it.</br>
//	 *     Set to <strong>null</strong> if you want to get ALL attributes
//	 * @return
//	 *     A list of each attribute instance
//	 */
//	public static List<ItemAttribute> getAllAttributes(ItemStack item)
//	{
//		//create the list holding all attribute instances
//		List<ItemAttribute> result = new ArrayList<ItemAttribute>();
//		for ( Map.Entry<Attribute, Class<? extends ItemAttribute>> attr : attributes.entrySet() )
//		{
//			Attribute attrInfo = attr.getKey();
//			
//			//check if we need this attribute
//			if (attrInfo.required() || Arrays.binarySearch(attrInfo.items(), item.getType()) >= 0 ||
//				(attrInfo.items().length == 0 && !attrInfo.standalone()))
//			{
//				try 
//				{
//					try {
//						Constructor<? extends ItemAttribute> constr = attr.getValue().getConstructor(String.class);
//						if (constr != null)
//						{
//							ItemAttribute iAttr = constr.newInstance(attrInfo.key());
//							iAttr.info = attrInfo;
//							result.add(iAttr);
//						}
//					}catch(NoSuchMethodException ex) {
//						dB.info("No Attribute constructor available");
//					}
//
//					//With subkeys
//					for (String sub : attrInfo.sub())
//					{
//						ItemAttribute iAttr = attr.getValue().getConstructor(String.class, String.class).newInstance(attrInfo.key(), sub);
//						iAttr.info = attrInfo;
//						result.add(iAttr);
//					}
//				} 
//				catch (Exception e)
//				{
//					//debug high
//					dB.high("Attribute exception on initialization");
//					dB.high("Attribute name: ", ChatColor.GREEN, attr.getKey().name());
//
//					//debug normal
//					dB.normal("Exception: ", e.getClass().getSimpleName());
//					dB.normal("Stack trace: ", StringTools.stackTrace(e.getStackTrace()));
//				}
//			}
//		}
//		return result;
//	}
//
//	/**
//	 * Returns required attribute instances in a list. This list is used later to init any item with required attributes.
//	 * Initial attributes do not contain sub-attributes.
//	 * @return
//	 *     A list of each attribute instance
//	 */
//	public static List<ItemAttribute> getRequiredAttributes()
//	{
//		//create the list holding all attribute instances
//		List<ItemAttribute> result = new ArrayList<ItemAttribute>();
//		for ( Map.Entry<Attribute, Class<? extends ItemAttribute>> attr : attributes.entrySet() )
//		{
//			try 
//			{
//				if ( attr.getKey().required() )
//				{
//					ItemAttribute attrInstance = attr.getValue().getConstructor(String.class).newInstance(attr.getKey().key());
//					attrInstance.info = attr.getKey();
//					result.add(attrInstance);
//				}
//			} 
//			catch (Exception e)
//			{
//				//debug high
//				dB.high("Attribute exception on initialization");
//				dB.high("Attribute name: ", ChatColor.GREEN, attr.getKey().name());
//
//				//debug normal
//				dB.normal("Exception: ", e.getClass().getSimpleName());
//				dB.normal("Stack trace: ", StringTools.stackTrace(e.getStackTrace()));
//			}
//		}
//		return result;
//	}
//
//	/**
//	 * Registers a new attribute to the system, should be done before Citizens2 loading.
//	 * @param clazz
//	 *     The attribute class that should be registered.
//	 * @throws InvalidDataNodeException
//	 */
//	public static void registerAttr(Class<? extends ItemAttribute> clazz) throws AttributeInvalidClassException
//	{
//		if ( !clazz.isAnnotationPresent(Attribute.class) )
//			throw new AttributeInvalidClassException();
//
//		Attribute attr = clazz.getAnnotation(Attribute.class);
//
//		//debug low
//		dB.low("Registering attribute \'", ChatColor.GREEN, attr.name(), ChatColor.RESET, "\' with key: ", attr.key());
//
//		attributes.put(attr, clazz);
//
//		//create all key pairs
//		keys.put(attr.key(), attr);
//		for (String sub : attr.sub())
//			keys.put(attr.key() + "." + sub, attr);
//	}
//
//	/**
//	 * Registers a new attribute to the system, should be done before Citizens2 loading.
//	 * @param clazz
//	 *     The attribute class that should be registered.
//	 * @throws AttributeException 
//	 * @throws InvalidDataNodeException
//	 */
//	public static void extendAttrKey(String key, Class<? extends ItemAttribute> clazz) throws AttributeException
//	{
//		if ( !clazz.isAnnotationPresent(Attribute.class) )
//			throw new AttributeInvalidClassException();
//
//		//if ( !keys.containsKey(key) )
//		//	throw new AttributeException();
//
//		Attribute attr = clazz.getAnnotation(Attribute.class);
//
//		//debug low
//		dB.low("Extending attribute with key \'", ChatColor.GREEN, key, ChatColor.RESET, "\' with: ", attr.name() + "." + attr.key());
//
//		attributes.put(attr, clazz);
//
//		//create all key pairs
//		for (String sub : attr.sub())
//			keys.put(key + "." + sub, attr);
//	}
//
//	/**
//	 * Creates a attribute with default values based on the class given. Does not create sub attributes.
//	 * @param item
//	 *     The item associated with the flag
//	 * @param clazz
//	 *     The specified attribute class
//	 * @return
//	 *     Returns the initialized attribute if successful.
//	 * @throws AttributeInvalidClassException 
//	 * @throws AttributeInvalidValueException 
//	 */
//	public static <T extends ItemAttribute> T initAttribute(StockItem item, Class<T> clazz) throws AttributeInvalidClassException, AttributeInvalidValueException
//	{
//		Attribute attr = clazz.getAnnotation(Attribute.class);
//		try 
//		{
//			//debug low
//			dB.low("Initializing new attribute instance");
//			dB.low("Attribute: " + attr.name());
//			dB.info("-------------------------------------");
//
//			//get the attribute declaring class
//			T itemAttr = clazz.getConstructor(String.class).newInstance(attr.key());
//			//assoc the item
//			itemAttr.item = item;
//			//assigning attribute information
//			itemAttr.info = attr;
//			//returning the initialized attribute
//			return itemAttr;
//		} 
//		catch (InvocationTargetException e) 
//		{
//			debugInfo(attr, e);
//			throw new AttributeInvalidClassException();
//		} 
//		catch (InstantiationException e)
//		{
//			debugInfo(attr, e);
//			throw new AttributeInvalidClassException();
//		} 
//		catch (IllegalAccessException e) 
//		{
//			debugInfo(attr, e);
//			throw new AttributeInvalidClassException();
//		} 
//		catch (IllegalArgumentException e) 
//		{
//			debugInfo(attr, e);
//			throw new AttributeInvalidClassException();
//		} 
//		catch (NoSuchMethodException e)
//		{
//			debugInfo(attr, e);
//			throw new AttributeInvalidClassException();
//		} 
//		catch (SecurityException e)
//		{
//			debugInfo(attr, e);
//			throw new AttributeInvalidClassException();
//		} 
//
//	}
//
//	/**
//	 * Creates a attribute based on the key. If a attribute is found it will call the <i><b>onLoad</b></i> method with the given <b>value</b>.
//	 * @param stockItem
//	 *     The item associated with the flag
//	 * @param key
//	 *     The attribute key, this is the unique key for each attribute.
//	 * @param value
//	 *     The value we will use to init the attribute.
//	 * @return
//	 *     Returns the initialized attribute if successful.
//	 * @throws AttributeInvalidClassException 
//	 * @throws AttributeInvalidValueException 
//	 */
//	public static ItemAttribute initAttribute(StockItem stockItem, String key, String value) throws AttributeInvalidClassException, AttributeInvalidValueException
//	{
//		//Search for the attribute
//		Attribute attr = null;
//		for ( String attrKey : keys.keySet() )
//			if ( attrKey.equals(key) )
//				attr = keys.get(attrKey);
//
//		//if attribute key is not valid return null
//		if ( attr == null ) return null;
//
//		try 
//		{
//			//debug low
//			dB.low("Initializing new attribute instance");
//			dB.low("Attribute: " + attr.name());
//			dB.info("With key: " + key);
//			dB.info("With value: " + value);
//			dB.info("-------------------------------------");
//
//			
//			ItemAttribute itemAttr;
//			if (key.contains("."))
//			{
//				String[] ks = key.split("\\.");
//				itemAttr = attributes.get(attr).getConstructor(String.class, String.class).newInstance(ks[0], ks[1]);
//			}
//			else
//			{
//				itemAttr = attributes.get(attr).getConstructor(String.class).newInstance(key);
//			} 
//
//			//get the attribute declaring class
//			//assoc the item 
//			itemAttr.item = stockItem;
//			//calling the onLoad method
//			itemAttr.onLoad(value);
//			//assigning attribute information
//			itemAttr.info = attr;
//			//returning the initialized attribute
//			return itemAttr;
//		} 
//		catch (InvocationTargetException e) 
//		{
//			debugInfo(attr, e);
//			throw new AttributeInvalidClassException();
//		} 
//		catch (InstantiationException e)
//		{
//			debugInfo(attr, e);
//			throw new AttributeInvalidClassException();
//		} 
//		catch (IllegalAccessException e) 
//		{
//			debugInfo(attr, e);
//			throw new AttributeInvalidClassException();
//		} 
//		catch (IllegalArgumentException e) 
//		{
//			debugInfo(attr, e);
//			throw new AttributeInvalidClassException();
//		} 
//		catch (NoSuchMethodException e)
//		{
//			debugInfo(attr, e);
//			throw new AttributeInvalidClassException();
//		} 
//		catch (SecurityException e)
//		{
//			debugInfo(attr, e);
//			throw new AttributeInvalidClassException();
//		} 
//
//	}
//
//	/**
//	 * Debug information
//	 */
//	private static void debugInfo(Attribute attr, Exception e)
//	{
//		//debug high
//		dB.high("Attribute exception on initialization");
//		dB.high("Attribute name: ", ChatColor.GREEN, attr.name());
//
//		//debug normal
//		dB.normal("Exception: ", e.getClass().getSimpleName());
//		dB.normal("Stack trace: ", StringTools.stackTrace(e.getStackTrace()));
//	}
//
//
//	/**
//	 * Registers all core attributes
//	 */
//	public static void registerCoreAttributes()
//	{
//		//debug info
//		dB.info("Registering core item attributes");
//
//		try 
//		{
//			//item related
//			registerAttr(StoredEnchant.class);
//			registerAttr(LeatherColor.class);
//			registerAttr(PatternItem.class);
//			registerAttr(Multiplier.class);
//			registerAttr(Durability.class);
//			registerAttr(Firework.class);
//			registerAttr(Enchant.class);
//			registerAttr(Banner.class);
//			registerAttr(Amount.class);
//			registerAttr(Potion.class);
//			registerAttr(Skull.class);
//			registerAttr(Tier.class);
//			registerAttr(Book.class);
//			registerAttr(Name.class);
//
//			//NBT generic data
//			extendAttrKey("g", GenericKnockback.class);
//			extendAttrKey("g", GenericDamage.class);
//			extendAttrKey("g", GenericHealth.class);
//			extendAttrKey("g", GenericSpeed.class);
//			
//	 		//Stock item related
//			registerAttr(Limit.class);
//			registerAttr(Price.class);
//			registerAttr(Slot.class);
//			
//			//extending classes
//			extendAttrKey("p", BlockCurrency.class);
//			extendAttrKey("p", PlayerResourcesCurrency.class);
//
//			DtlTraders.info("Registered core attributes: " + attributesAsString());
//		} 
//		catch (AttributeInvalidClassException e) 
//		{
//			//debug critical
//			dB.critical("Core attributes invalid");
//
//			//debug high
//			dB.high("Exception message: ", e.getMessage());
//			dB.high("Stack trace: ", StringTools.stackTrace(e.getStackTrace()));
//		}
//		catch (AttributeException e)
//		{
//			//debug critical
//			dB.critical("Core extended attributes invalid");
//
//			//debug high
//			dB.high("Exception message: ", e.getMessage());
//			dB.high("Stack trace: ", StringTools.stackTrace(e.getStackTrace()));
//		}
//	}
//
//	/**
//	 * Creates a string with all registered core attribute names
//	 * @return 
//	 *     formated result string
//	 */
//	private static String attributesAsString()
//	{
//		String result = "";
//		for ( Attribute attr : attributes.keySet() )
//			result += ", " + ChatColor.YELLOW + attr.name() + ChatColor.RESET;
//
//		return ChatColor.WHITE + "[" + result.substring(2) + ChatColor.WHITE + "]";
//	}
}
