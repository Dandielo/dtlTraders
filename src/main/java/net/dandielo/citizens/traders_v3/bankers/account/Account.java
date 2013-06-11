package net.dandielo.citizens.traders_v3.bankers.account;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import net.dandielo.citizens.traders_v3.bankers.setting.Settings;
import net.dandielo.citizens.traders_v3.bankers.tabs.Tab;

public abstract class Account implements InventoryHolder {
	/**
	 * Settings from the banker that will handle our account
	 */
	private Settings settings;

	/**
	 * The account type used for loading and saving
	 */
	protected AccountType type;
	
	/*
	 * Account related settings	
	 */
	
	//the account owner
	protected String owner;
	
	//the account name (stock name)
	//protected String name;
	
	//list of all tabs the player has in his account
	protected List<Tab> tabs;
	protected int maxSize;

	protected int tabSize;
	protected int lastTab;
	
	/**
	 * Create a new Account, this will load it and cache in memory
	 */
	public Account(String owner, AccountType type)
	{
		//set the type
		this.type = type; 
		
		//set the account owner
		this.owner = owner;
		
		//set the default account name format
	//	name = Settings.getDefaultAccountNameFormat();
		
		//tab defaults, create list
		tabs = new ArrayList<Tab>();
		
		//how many tabs the user can have
		maxSize = Settings.getDefaultMaxTabs();
		
		//how big is each tab?
		tabSize = Settings.getDefaultTabSize();
		
		//the last opened tab, first tab as default
		lastTab = 0;
		
		//number default tabs
		for ( int i = 0 ; i < Settings.getStartTabCount() ; ++i )
		{
			tabs.add(Tab.createNewTab(type));
		}
	}
	
	/**
	 * @return
	 * the account owner
	 */
	public String getOwner()
	{
		return owner;
	}
	
	/**
	 * @return
	 * the account name format (stock name)
	 */
/*	public String getName()
	{
		return name;
	}*/
	
	/**
	 * @return
	 * all bank tabs from the account
	 */
	public List<Tab> getTabs()
	{
		return tabs;
	}
	
	/**
	 * @return
	 * the amount of tabs the player has unlocked
	 */
	public int tabCount()
	{
		return tabs.size();
	}
	
	/**
	 * @return
	 * maximum tabs the account can have (it's max size)
	 */
	public int getMaxTabs()
	{
		return maxSize;
	}
	
	/**
	 * @return
	 * the size of each tab (as inventory)
	 */
	public int getTabSize()
	{
		return tabSize;
	}
	
	/**
	 * @return
	 * the account type
	 */
	public AccountType getType()
	{
		return type;
	}
	
	/**
	 * @param settings
	 * the settings that will be used to manage the account
	 */
	public void applySettings(Settings settings)
	{
		this.settings = settings; 
	}
	
	/**
	 * @return
	 * true if the given slot is in the UI row
	 */
	public boolean isUIRow(int slot)
	{
		return slot >= (tabSize-1)*9 && slot < (tabSize*9);
	}
	
	/**
	 * Returns the new created inventory that fits the account and banker settings
	 */
	@Override
	public Inventory getInventory()
	{
		Inventory inventory = Bukkit.createInventory(this, tabSize*9, 
				settings.getAccountNameFormat().replace("{player}", owner).replace("{npc}", settings.getNpcName())
				);
		return inventory;
	}
	
	public abstract void tabSwitch(Tab tab, Inventory inventory);
	
	/**
	 * Sets the UI part of the bankers inventory, is different for guild and private bankers. 
	 * Guild bankers UI depends on a members status.
	 */
	public abstract void setTabUI(Inventory inventory);
	
	/**
	 * Adds a new tab to the account
	 * @param tab
	 * that will be added
	 */
	public void addTab(Tab tab)
	{
		tabs.add(tab);
	}
	
	/**
	 * Looks through all tabs in the account for the tab with the given name
	 * @param name 
	 * that will be used to find the tab
	 * @return
	 * the related tab, or null if no found
	 */
	public Tab getTab(String name)
	{
		Tab tab = null;
		int i = 0;
		
		//find the tab
		while(tab == null && tabs.size() < i)
		{
			if ( tabs.get(i).getName().equals(name) )
				tab = tabs.get(i);
		}
		
		return tab;
	}
	
	/**
	 * Returns the tab at the i'th place
	 * @param i the tab index
	 * @return
	 * the tab at the given index
	 */
	public Tab getTab(int i)
	{
		return tabs.size() <= i ? null : tabs.get(i);  
	}
	
	public abstract void onLoad(ConfigurationSection data);
	public abstract void onSave(ConfigurationSection data);

	
	public static enum AccountType
	{
		PRIVATE, GUILD
	}
}
