package net.dandielo.citizens.traders_v3.bankers.account;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.bankers.tabs.BankItem;
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
		
		this.setTabUI(inventory);
		
		for ( BankItem item : tab.getItems() )
		{
			//add the item to the inventory
			inventory.setItem(item.getSlot(), item.getItem());
		}
	}

	@Override
	public void onLoad(ConfigurationSection data)
	{
		maxSize = data.getInt("max-size", maxSize);
	    tabSize = data.getInt("tab-size", tabSize);
		
		tabs.clear();
		
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

		int i = 0;
		for ( Tab tab : tabs )
		{
			//save tabs
			data.set("tabs.tab_" + i, new YamlConfiguration());
			tab.onSave(data.getConfigurationSection("tabs.tab_" + i));
			
			++i;
		}
	}

	@Override
	public void setTabUI(Inventory inventory)
	{
		int i = (tabSize-1)*9, j = 0;
		for ( Tab tab : tabs )
		{
			inventory.setItem(i++, tab.getIcon());
			++j;
		}
		
		//make it bit more nicer ;)
		List<String> unavailable = new ArrayList<String>();
		unavailable.add(ChatColor.RESET + "" + ChatColor.GRAY + "This tab is not available for you");
		unavailable.add(ChatColor.RESET + "" + ChatColor.GRAY + "Shift + right click to buy it");
		//for ( ; j < maxSize ; ++j )
		if ( j + 1 < maxSize )
		{
			inventory.setItem(i++, new BankItem("35 a:1 s:0 .lore n:Bank tab " + (j+1), unavailable).getItem());
		}
	}

}
