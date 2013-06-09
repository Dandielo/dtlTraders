package net.dandielo.citizens.traders_v3.bankers.account;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;

import net.dandielo.citizens.traders_v3.bankers.tabs.Tab;

public class PrivateAccount extends Account {

	public PrivateAccount(String owner)
	{
		super(owner, AccountType.PRIVATE);
	}
	
	public void tabSwitch(Tab tab, Inventory inventory)
	{
		//clear the inventory
		inventory.clear();
	}

	@Override
	public void onLoad(ConfigurationSection data)
	{
		maxSize = data.getInt("max-size", maxSize);
	    tabSize = data.getInt("tab-size", tabSize);
		name = data.getString("name", name);
		
		ConfigurationSection tabs = data.getConfigurationSection("tabs");
		for ( String tabId : tabs.getKeys(false) )
		{
			addTab(Tab.createNewTab(type, tabs.getConfigurationSection(tabId)));
		}
	}

	@Override
	public void onSave(ConfigurationSection data)
	{
		data.set("max-size", maxSize);
		data.set("tab-size", tabSize);
		data.set("name", name);

		int i = 0;
		for ( Tab tab : tabs )
		{
			//save tabs
			data.set("tabs.tab_" + i, new YamlConfiguration());
			tab.onSave(data.getConfigurationSection("tabs.tab_" + i));
			
			++i;
		}
	}

}
