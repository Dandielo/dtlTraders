package net.dandielo.citizens.traders_v3.utils.items.data;

import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.core.exceptions.ItemDataNotFoundException;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.DataNode;
import net.dandielo.citizens.traders_v3.utils.items.ItemData;

@DataNode(name="Name", saveKey="n")
public class Name extends ItemData {

	private String name;
	
	public Name(String key) {
		super(key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getValue(StockItem stockItem) {
		return (T) name;
	}

	@Override
	public void assing(ItemStack item) {
		item.getItemMeta().setDisplayName(name);
	}

	@Override
	public void peek(ItemStack item) throws ItemDataNotFoundException {
		if ( !item.getItemMeta().hasDisplayName() )
			throw new ItemDataNotFoundException();
		name = item.getItemMeta().getDisplayName();
	}

	@Override
	public void load(String value) {
		name = value;
	}

	@Override
	public String save() {
		return name;
	}
	
	public boolean equals(Name name)
	{
		return name.name.equals(this.name);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return equals((Name)o);
	}

}
