package net.dandielo.citizens.traders_v3.utils.items.attributes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.tNpcStatus;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

@Attribute(name="Amount", key = "a", required = true, priority = 5,
status = {tNpcStatus.MANAGE_SELL, tNpcStatus.MANAGE_BUY})
public class Amount extends ItemAttr {
	private List<Integer> amounts = new ArrayList<Integer>();
	
	/**
	 * Default constructor, initialize it with right attribute key. 
	 * @param key
	 *     attribute unique key
	 */
	public Amount(String key) {
		super(key);
		//default value
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
	public void onLoad(String data) throws AttributeInvalidValueException 
	{
		amounts.clear();
		try
		{
		    for ( String amout : data.split(",") )
			    amounts.add( Integer.parseInt(amout) < 1 ? 1 : Integer.parseInt(amout) );
		} 
		catch (NumberFormatException e)
		{
			throw new AttributeInvalidValueException(getInfo(), data);
		}
	}

	@Override
	public String onSave() 
	{
		String result = "";
		for ( int i = 0 ; i < amounts.size() ; ++i )
			result += amounts.get(i) + ( i + 1 < amounts.size() ? "," : "" );
		return result;
	}

	@Override
	public void onAssign(ItemStack item)
	{
		item.setAmount(getAmount());
	}

	@Override
	public void onFactorize(ItemStack item)
	{
		amounts.clear();
		amounts.add(item.getAmount());
	}
	
	@Override
	public void onStatusLoreRequest(tNpcStatus status, List<String> lore)
	{
		//TODO implement later
	}

	@Override
	public boolean equalsStrong(ItemAttr amount)
	{
		return ((Amount)amount).getAmount() == getAmount();
	}
}
