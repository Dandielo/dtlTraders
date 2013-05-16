package net.dandielo.citizens.traders_v3.utils.items.data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.core.exceptions.InvalidDataAssignmentException;
import net.dandielo.citizens.traders_v3.core.exceptions.ItemDataNotFoundException;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.DataNode;
import net.dandielo.citizens.traders_v3.utils.items.ItemData;

@DataNode(name="Amount", saveKey="a")
public class Amount extends ItemData {

	List<Integer> amounts = new ArrayList<Integer>();
	
	public Amount(String key) {
		super(key);
		amounts.add(1);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getValue(StockItem stockItem) {
		return (T) amounts;
	}
	
	public int getAmount()
	{
		return amounts.get(0);
	}
	
	public void addAmount(int a)
	{
		amounts.add(a);
	}
	
	public List<Integer> getAmounts()
	{
		return amounts;
	}
	
	public boolean hasMultipleAmounts()
	{
		return amounts.size() > 1;
	}

	@Override
	public void assing(ItemStack item) {
		item.setAmount(getAmount());
	}

	@Override
	public void checkItemCompatibility(ItemStack item)
			throws InvalidDataAssignmentException {
	}

	@Override
	public void load(String value) {
		amounts.clear();
		for ( String amout : value.split(",") )
			amounts.add( Integer.parseInt(amout) < 1 ? 1 : Integer.parseInt(amout) );
	}

	@Override
	public String save() {
		String result = "";
		for ( int i = 0 ; i < amounts.size() ; ++i )
			result += amounts.get(i) + ( i + 1 < amounts.size() ? "," : "" );
		return result;
	}

	@Override
	public void peek(ItemStack item) throws ItemDataNotFoundException {
		amounts.clear();
		amounts.add(item.getAmount());
	}

	public boolean equals(Amount amount)
	{
		return amount.getAmount() == getAmount();
	}
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof Amount ? equals((Amount)o) : false;
	}
}
