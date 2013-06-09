package net.dandielo.citizens.traders_v3.bankers.tabs;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class PrivateTab extends Tab {

	@Override
	public void addItem(BankItem item)
	{
		items.add(item);
	}

	@Override
	public void onSave(ConfigurationSection data)
	{
		//set the data
		data.set("name", name);
		data.set("icon", icon);
		data.set("desc", desc);
		data.set("items", items);
	}

	@Override
	public void onLoad(ConfigurationSection data)
	{
		name = data.getString("name");
		icon = new ItemStack(Material.WOOL);
		desc = data.getStringList("desc");
		
		//load all items for this tab
		for ( String item : data.getStringList("items") )
		{
			//create a new bank item
			items.add(new BankItem(item));
		}
	}

}
