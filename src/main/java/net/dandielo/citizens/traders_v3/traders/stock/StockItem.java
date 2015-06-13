package net.dandielo.citizens.traders_v3.traders.stock;

import java.util.ArrayList;
import java.util.List;

import net.dandielo.citizens.traders_v3.TEntityStatus;
import net.dandielo.citizens.traders_v3.utils.items.StockItemAttribute;
import net.dandielo.citizens.traders_v3.utils.items.StockItemFlag;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Amount;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Limit;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Multiplier;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Price;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Slot;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.ItemAttribute;
import net.dandielo.core.items.serialize.ItemFlag;
import net.dandielo.core.items.serialize.flags.Lore;

import org.bukkit.inventory.ItemStack;

/**
 * Item structure using in each trader stock, this structure allows to save and store more data than the commom ItemStack bukkit structure.
 * @author dandielo
 */
public final class StockItem extends dItem {
	/**
	 * Pattern used for loading data form strings
	 */
	//public static final String ITEM_PATTERN = "(([^ :]+):([^ :]+))|([^ :]*)";

	/**
	 * The bukkit representation of the item, this item is always saved without any data assigned. Assigning is done on a copy when needed.
	 */
	//private ItemStack item;
	
	/**
	 * All additional data set for the item
	 */
	//private Set<ItemAttribute> attr = new HashSet<ItemAttribute>();
	
	/**
	 * Flags for checking some things fast
	 */
	//private Map<Class<? extends ItemFlag>, ItemFlag> flags = new HashMap<Class<? extends ItemFlag>, ItemFlag>();

	/**
	 * Creates a stock item base on the bukkit item provided. It only saves the id and data, nothing else.
	 * @param item
	 *     bukkit item stack item
	 */
	public StockItem(ItemStack item)
	{
		super(item);
		//this.item = new ItemStack(item.getType(), 1, ItemUtils.itemHasDurability(item) ? 0 : item.getDurability()); 
	}

	/**
	 * Creates a stock item by using the given format string. It will also initialize with all attributes found in the format string.
	 * Default attributes will also apply
	 * @param format
	 *     the formated save string
	 */
	public StockItem(String format)
	{
		super(format);
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
		super(format, list);
	}
	
	/**
	 * Uses the serialization method
	 */
	@Override 
	public String toString()
	{
		return serialize();
	}

	/**
	 * Returns a list of temporary lore strings that should be applied depending on the traders status. 
	 * @param status
	 *     Status that is checked
	 * @return
	 *     List of lore strings
	 */ 
	public List<String> getDescription(TEntityStatus status)
	{
		//create a new list
		List<String> lore = new ArrayList<String>();
		
		//check and add if there is already lore attached
		if ( this.hasFlag(Lore.class) )
			lore.addAll(this.getLore());
		
		//for each attribute
		for (ItemAttribute itemAttr : this.attributes)
		{
			if (itemAttr instanceof StockItemAttribute)
			{
				StockItemAttribute siAttr = (StockItemAttribute) itemAttr;

				//for (TEntityStatus attrStatus : siAttr.getShopStatus().status())
				//	if (attrStatus.equals(status))
				        siAttr.getDescription(status, lore);
			}
		}
		
		//for each flag
		for (ItemFlag itemFlag : flags)
		{
			if (itemFlag instanceof StockItemFlag)
			{
				StockItemFlag siFlag = (StockItemFlag) itemFlag;

				//for (TEntityStatus attrStatus : siFlag.getShopStatus().status())
				//	if ( attrStatus.equals(status))
						siFlag.getDescription(status, lore);
			}
		}
		
		//return the lore
		return lore;
	}

	/**
	 * @return
	 *     true if the price attribute is present
	 */
	public boolean hasPrice()
	{
		return hasAttribute(Price.class);
	}
	
	/**
	 * Gets the items price if the attribute is present.
	 * @return
	 *     the price set, or <b>-1.0</b> instead
	 */
	public double getPrice()
	{
		return hasAttribute(Price.class) ? getAttribute(Price.class, false).getPrice() : -1.0;
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
		return getAttribute(Price.class, true);
	}

	/**
	 * Gets the items multiplier value
	 * @return
	 * the multiplier set, or -1.0 instead
	 */
	public double getMultiplier()
	{
		return hasAttribute(Multiplier.class) ? this.getAttribute(Multiplier.class, false).getMultiplier() : -1.0;
	}
	
	/**
	 * Checks if the item has a multiplier assigned
	 * @return
	 * true if a the multiplier attribute is set
	 */
	public boolean hasMultiplier()
	{
		return hasAttribute(Multiplier.class);
	}
	
	/**
	 * @return
	 *     the limit attribute, if there is no attrib it will create one.
	 */
	public Limit getLimitAttr()
	{
		return getAttribute(Limit.class, true);
	}
	
	/**
	 * Gets the items slot attribute, always a valid value.
	 * @return
	 *     the items slot in stock. 
	 */
	public int getSlot()
	{
		return hasAttribute(Slot.class) ? getAttribute(Slot.class, false).getSlot() : -1;
	}

	/**
	 * Checks if the given slot is equal to the items one.
	 * @return
	 *     true if slots are equal
	 */
	public boolean checkSlot(int slot)
	{
		return getAttribute(Slot.class, true).getSlot() == slot;
	}
	
	/**
	 * Sets the slot attribute for the item
	 * @param slot
	 */
	public void setSlot(int slot)
	{
		getAttribute(Slot.class, true).setSlot(slot);
	}
	
	/**
	 * @return
	 *     true if more than 1 amount is set
	 */
	public boolean hasMultipleAmounts()
	{
		return getAttribute(Amount.class, true).hasMultipleAmounts();
	}

	/**
	 * @return
	 *    the first set amount, considered as the major one
	 */
	public int getAmount()
	{
		return getAttribute(Amount.class, true).getAmount();
	}

	/**
	 * @param i
	 *     index of the amount to get
	 * @return
	 *     amount under the given index 
	 */
	public int getAmount(int i)
	{
		return getAttribute(Amount.class, true).getAmount(i);
	}
	
	/**
	 * @return
	 *     all saved amounts
	 */
	public List<Integer> getAmounts()
	{
		return getAttribute(Amount.class, true).getAmounts();
	}
	
	/**
	 * Adds a new amount to the stock item
	 * @param a
	 * amount that should be added
	 */
	public void addAmount(int a)
	{ 
		getAttribute(Amount.class, true).addAmount(a);
	}
	
	/**
	 * @return
	 *    the assigned lore, or null otherwise
	 */
	public List<String> getLore()
	{
		return hasFlag(Lore.class) ? getFlag(Lore.class, false).getLore() : null;
	}
	
	/**
	 * @return
	 *    the assigned lore, or null otherwise
	 */
	public List<String> getRawLore()
	{
		return hasFlag(Lore.class) ? getFlag(Lore.class, false).getRawLore() : null;
	}

//	/**
//	 * holds info if the lore was managed by another plugin
//	 */
//	private boolean loreManaged = false;
//	
//	/**
//	 * This function tells the item factorizer should load the lore or if it skip it because some other attribute has managed it. 
//	 * @param managed
//	 * true if the lore loading should be skipped
//	 */
//	public void loreManaged(boolean managed)
//	{
//		loreManaged = managed;
//	}
	
//	private boolean standaloneAttrCheck(StockItem item)
//	{
//		boolean containsAll = true;
//		for ( ItemAttribute key : item.attr )
//			containsAll = containsAll && !key.getInfo().standalone() ? this.attr.contains(key) : containsAll;
//		for ( ItemAttribute key : this.attr)
//			containsAll = containsAll && !key.getInfo().standalone() ? item.attr.contains(key) : containsAll;
//		return containsAll;
//	}
//	private boolean standaloneFlagCheck(StockItem item)
//	{
//		boolean containsAll = true;
//		for ( ItemFlag key : item.flags.values() )
//			containsAll = containsAll && !key.getInfo().standalone() ? this.flags.containsKey(key.getClass()) : containsAll;
//		for ( ItemFlag key : this.flags.values() )
//			containsAll = containsAll && !key.getInfo().standalone() ? item.flags.containsKey(key.getClass()) : containsAll;
//		return containsAll;
//	}
//	private boolean attributeMissmatch(StockItem item)
//	{
//		return !(standaloneAttrCheck(item) && standaloneFlagCheck(item));
//	}
//	
//	
//	
//	private boolean patternStandaloneAttrCheck(StockItem item)
//	{
//		boolean containsAll = true;
//		for ( ItemAttribute key : this.attr )
//			containsAll = containsAll && !key.getInfo().standalone() ? item.attr.contains(key) : containsAll;
//		return containsAll;
//	}
//	private boolean patternStandaloneFlagCheck(StockItem item)
//	{
//		boolean containsAll = true;
//		for ( ItemFlag key : this.flags.values() )
//			containsAll = containsAll && !key.getInfo().standalone() ? item.flags.containsKey(key.getClass()) : containsAll;
//		return containsAll;
//	}
//	private boolean patternAttributeMissmatch(StockItem item)
//	{
//		return !(patternStandaloneAttrCheck(item) && patternStandaloneFlagCheck(item));
//	}
//	
//	/**
//	 * Strong equality is needed when we need precise information if an item is equal, example: Stock placement
//	 * @param item
//	 *     item to compare against
//	 * @return
//	 *     true if equal 
//	 */
//	public final boolean equalsStrong(StockItem item)
//	{
//		boolean equals;
//		//check Item materials
//		equals = item.item.getType().equals(this.item.getType());
//		//if equals check data, if not durability
//		equals = equals && !ItemUtils.itemHasDurability(item.item) ? item.item.getDurability() == this.item.getDurability() : equals; 
//		
//		//checking attribute missmatching
//		equals = equals ? !attributeMissmatch(item) : equals;
//		
//	//	dB.low("After ID and data check: ", equals);
//		
//		//now a if block to not make thousands of not needed checks 
//		if ( equals )
//		{
//			//for each attribute in this item
//			for ( ItemAttribute tAttr : attr )
//			{
//				//if only once is false then return false
//				if ( !equals ) break;
//				
//				//temporary false
//				equals = tAttr.getInfo().standalone();
//				
//				//debug low
//				dB.low("Before ", tAttr.getInfo().name() ," check: ", equals, ", with: ", tAttr.onSave());
//				
//				//check each item in the second item, if the attribute is found and strong equal continue
//				for ( ItemAttribute iAttr : item.attr )
//				{
//					//debug low
//		//			dB.info("Checking ", iAttr.getInfo().name() ," with: ", iAttr.onSave());
//					
//					//same attributes
//					if ( tAttr.getClass().equals(iAttr.getClass()) )
//						equals = tAttr.equalsStrong(iAttr);
//				}
//				
//				//debug low
//				dB.low("After ", tAttr.getInfo().name() ," check: ", equals);
//			}
//			
//			//for each attribute in this item
//			for ( ItemFlag tFlag : flags.values() )
//			{
//				//if only one is false then return false
//				if ( !equals ) break;
//				
//				//temporary false
//				equals = tFlag.getInfo().standalone();
//				
//				//check each item in the second item, if the attribute is found and strong equal continue
//				for ( ItemFlag iFlag : item.flags.values() )
//					//same attributes
//					if ( tFlag.getClass().equals(iFlag.getClass()) )
//						equals = tFlag.equalsStrong(iFlag);
//				
//			}
//		}
//		return equals;
//	}
//	
//	/**
//	 * Weak equality is needed when we need only some informations about the equality, example: adding items to players inventory does not need amount equality.
//	 * @param item
//	 *     item to compare against
//	 * @return
//	 *     true if equal 
//	 */
//	public final boolean equalsWeak(StockItem item)
//	{
//		boolean equals;
//		//check item materials
//		equals = item.item.getType().equals(this.item.getType());
//		//if equals check data, if not durability
//		equals = equals && !ItemUtils.itemHasDurability(item.item) ? item.item.getDurability() == this.item.getDurability() : equals; 
//
//		//checking attribute missmatching
//		equals = equals ? !attributeMissmatch(item) : equals;
//
//		dB.low("After ID and data check: ", equals);
//		
//		//now a if block to not make thousands of not needed checks 
//		if ( equals )
//		{
//			//for each attribute in this item
//			for ( ItemAttribute tAttr : attr )
//			{
//				//if only once is false then return false
//				if ( !equals ) break;
//				
//				//temporary false (or true)
//				equals = tAttr.getInfo().standalone();
//
//				//debug low
//				dB.low("Before ", tAttr.getInfo().name() ," check: ", equals, ", with: ", tAttr.onSave());
//				
//				//check each item in the second item, if the attribute is found and strong equal continue
//				for ( ItemAttribute iAttr : item.attr )
//				{
//					//debug low
//			//		dB.info("Checking ", iAttr.getInfo().name() ," with: ", iAttr.onSave());
//					
//					//same attributes
//					if ( tAttr.getClass().equals(iAttr.getClass()) )
//						equals = tAttr.equalsWeak(iAttr);
//				}
//				
//				//debug low
//		//		dB.low("After ", tAttr.getInfo().name() ," check: ", equals);
//			}
//			
//			//for each attribute in this item
//			for ( ItemFlag tFlag : flags.values() )
//			{				
//				//if only one is false then return false
//				if ( !equals ) break; 
//				
//				//temporary false
//				equals = tFlag.getInfo().standalone();
//				
//				//check each item in the second item, if the attribute is found and strong equal continue
//				for ( ItemFlag iFlag : item.flags.values() )
//					//same attributes
//					if ( tFlag.getClass().equals(iFlag.getClass()) )
//						equals = tFlag.equalsWeak(iFlag);
//			}
//		}
//		return equals;
//	}

//	/**
//	 * uses strong equality
//	 */
//	@Override
//	public final boolean equals(Object object)
//	{
//		return (object instanceof StockItem && equalsStrong((StockItem)object));
//	}
	
	//TO-DO: Patterns
//	public final int priorityMatch(StockItem that)
//	{
//		
//		int priority = 0;
//		
//		Material mat = getMaterial();
//		if (mat.getMaxDurability() == 0)
//		{
//			if (mat.equals(Material.AIR))
//			{
//				priority += getMaterialData().equals(that.getMaterialData()) ? 120 : -2;
//			}
//			else 
//			{
//				priority += getMaterialData().equals(that.getMaterialData()) &&
//						mat.equals(that.getMaterial()) ? 140 : -2; 
//			}
//		}
//		else
//		{
//			if (!mat.equals(Material.AIR))
//				priority += mat.equals(that.getMaterial()) ? 130 : -2;	
//		}
//		
////
////		//id and data check
////		if ( this.hasFlag(DataCheck.class) )
////		{
////			if ( !this.item.getType().equals(Material.AIR) )
////			{
////				if ( this.item.getType().equals(that.item.getType()) &&
////					 this.item.getDurability() == that.item.getDurability() )
////					priority += 140;
////				else
////					priority = -2;
////			}
////			else
////			{
////				if ( this.item.getType().equals(Material.AIR) && this.item.getDurability() == that.item.getDurability() )
////					priority += 120;
////				else
////					priority = -2;
////			}
////		}
////		else
////		{
////			if ( !this.item.getType().equals(Material.AIR) )
////			{
////				if ( this.item.getType().equals(that.item.getType()) )
////				{
////					priority = 130;
////				}
////				else priority = -2;
////			}
////			else
////			    priority = 0;
////		}
//
////		if ( patternAttributeMissmatch(that) ) return -2;
//		
//		
//		//now a if block to not make thousands of not needed checks 
//		if ( priority < 0 ) return priority;
//
//		
//		for (ItemAttribute thisItemAttr : attributes)
//		{
//			if (!thisItemAttr.getInfo().standalone())
//			{
//				for (ItemAttribute thatItemAttr : that.attributes)
//				{
//					if (thisItemAttr.getClass().equals(thatItemAttr.getClass()) && thisItemAttr.equals(thatItemAttr))
//						priority += thisItemAttr.getInfo().priority();
//				}
//			}
//		}
//		
////		//for each attribute in this item
////		for ( ItemAttribute tAttr : attr )
////		{
////			//check each item in the second item, if the attribute is found and strong equal continue
////			for ( ItemAttribute iAttr : that.attr )
////			{
////				//debug low
////		//		dB.info("Checking ", iAttr.getInfo().name() ," with: ", iAttr.onSave());
////
////				//same attributes
////				if ( tAttr.getClass().equals(iAttr.getClass()) && tAttr.equalsStrong(iAttr) )
////					priority += tAttr.getInfo().priority();
////			}
////			//debug low
////		//	dB.low("After ", tAttr.getInfo().name() ," check: ", String.valueOf(priority));
////		}
//
//		//for each attribute in this item
//		for (ItemFlag thisItemFlag : flags)
//		{
//			if (!thisItemFlag.getInfo().standalone())
//			{
//				for (ItemFlag thatItemFlag : that.flags)
//				{
//					if (thisItemFlag.getClass().equals(thatItemFlag.getClass()) && thisItemFlag.equals(thatItemFlag))
//						priority += thisItemFlag.getInfo().priority();
//				}
//			}
//		}
////		
////		//for each attribute in this item
////		for ( ItemFlag tFlag : flags.values() )
////		{
////			//check each item in the second item, if the attribute is found and strong equal continue
////			for ( ItemFlag iFlag : that.flags.values() )
////			{
////				//same attributes
////				if ( tFlag.getClass().equals(iFlag.getClass()) && tFlag.equalsStrong(iFlag) )
////					priority += tFlag.getInfo().priority();
////			}
////		}
////		
//	//	dB.info("Priority result: ", priority);
//		return priority;
//	}
}
