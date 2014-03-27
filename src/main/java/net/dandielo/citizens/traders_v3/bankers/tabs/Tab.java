package net.dandielo.citizens.traders_v3.bankers.tabs;

import java.util.ArrayList;
import java.util.List;

import net.dandielo.citizens.traders_v3.bankers.account.Account.AccountType;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public abstract class Tab {

	protected String name = "Bank tab";
	protected BankItem icon = new BankItem("35 a:1 s:0 n:Bank Tab");
	protected List<String> desc;
	
	protected List<BankItem> items = new ArrayList<BankItem>();	
	
	public void setIcon(ItemStack icon)
	{
	}
	
	public void setDescription(List<String> desc)
	{
		this.desc = desc;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		//replace color codes
		this.name = name.replace('^', '§');
		
		
		//new bank item
	}
	
	public int getUsedSize()
	{
		return items.size();
	}
	
	public List<BankItem> getItems()
	{
		return items;
	}
	
	public abstract void addItem(BankItem item);
//	public abstract void removeItem(BankItem item);

	public abstract void onSave(ConfigurationSection data);
	public abstract void onLoad(ConfigurationSection data);

	public static Tab createNewTab(AccountType type, ConfigurationSection configurationSection)
	{
		Tab tab = null;
		if ( type.equals(AccountType.PRIVATE) )
			tab = new PrivateTab();
		else
		if ( type.equals(AccountType.GUILD) ) { }
		
		if ( tab != null )
			tab.onLoad(configurationSection);
		
		return tab;
	}
	
	public static Tab createNewTab(AccountType type)
	{
		Tab tab = null;
		if ( type.equals(AccountType.PRIVATE) )
			tab = new PrivateTab();
		else
		if ( type.equals(AccountType.GUILD) ) { }

		return tab;
	}
}
