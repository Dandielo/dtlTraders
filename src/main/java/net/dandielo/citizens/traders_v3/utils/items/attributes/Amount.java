package net.dandielo.citizens.traders_v3.utils.items.attributes;

import java.util.ArrayList;
import java.util.List;

import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.ShopStatus;
import net.dandielo.citizens.traders_v3.utils.items.StockItemAttribute;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;

import org.bukkit.inventory.ItemStack;


@ShopStatus
@Attribute(name="ShopAmount", key = "a", required = true, priority = 5)
public class Amount extends StockItemAttribute {
	private List<Integer> amounts = new ArrayList<Integer>();
	
	/**
	 * Default constructor, initialize it with right attribute key. 
	 * @param key
	 *     attribute unique key
	 */
	public Amount(StockItem item, String key) {
		super(item, key);
		amounts.add(1);
	}
	
	/**
	 * @return
	 *     the first amount saved (major one)
	 */
	public int getAmount()
	{
		return amounts.get(0);
	}
	
	/**
	 * @param i amount to get
	 * @return
	 *     returns 'i' amount
	 */
	public int getAmount(int i)
	{
		return amounts.get(i);
	}

	/**
	 * adds an amount to the amounts list
	 * @param a
	 * amount that should be added
	 */
	public void addAmount(int a)
	{
		amounts.add(a);
	}
	
	/**
	 * @return
	 *     returns amount list
	 */
	public List<Integer> getAmounts()
	{
		return amounts;
	}
	
    /**
     * @return
     * True if list has more than 1 amount, false otherwise
     */
	public boolean hasMultipleAmounts()
	{
		return amounts.size() > 1;
	}

	@Override
	public void onAssign(ItemStack item, boolean unused)
	{
		item.setAmount(getAmount());
	}

	@Override
	public boolean onRefactor(ItemStack item)
	{
		amounts.clear();
		amounts.add(item.getAmount());
		return true;
	}

	@Override
	public boolean similar(ItemAttribute that)
	{
		return equals(that);
	}

	@Override
	public boolean equals(ItemAttribute that)
	{
		return ((Amount)that).getAmount() == getAmount();
	}

	@Override
	public String serialize() {
		String result = "";
		for ( int i = 0 ; i < amounts.size() ; ++i )
			result += amounts.get(i) + ( i + 1 < amounts.size() ? "," : "" );
		return result;
	}

	@Override
	public boolean deserialize(String data) {
		amounts.clear();
		try
		{
		    for ( String amout : data.split(",") )
			    amounts.add( Integer.parseInt(amout) < 1 ? 1 : Integer.parseInt(amout) );
		} 
		catch (NumberFormatException e)
		{
			return false;
		}
		return true;
	}
}
