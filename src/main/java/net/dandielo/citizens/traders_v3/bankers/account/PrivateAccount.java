package net.dandielo.citizens.traders_v3.bankers.account;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;

import net.dandielo.citizens.traders_v3.bankers.tabs.BankItem;
import net.dandielo.citizens.traders_v3.bankers.tabs.Tab;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;

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
			if ( j < settings.getMaxVisibleTabs() )
			    inventory.setItem(i++, tab.getIcon());
			++j;
		}

		if ( j + 1 < maxSize && j + 1 < settings.getMaxVisibleTabs() )
		{
			//check the players permission
			if ( tabCountPermCheck(j+i) )
			    inventory.setItem(i++, new BankItem("35 a:1 s:0 .lore n:" + LocaleManager.locale.getName("tab-unowned"), 
			    		/* the lore to set */ LocaleManager.locale.getLore("tab-unowned")).getItem());
		}
	}

	private boolean tabCountPermCheck(int tabId)
	{
		boolean result = false;
		for ( int i = tabId ; i < maxSize && !result ; ++i )
		{
			result = perms.has(viewer, "dtl.banker.tabs.count." + (i+1));
		}
		return result;
	}
}
