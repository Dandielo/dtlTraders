package net.dandielo.citizens.traders_v3.bankers.tabs;

import java.util.ArrayList;
import java.util.List;

import net.dandielo.citizens.traders_v3.utils.ItemUtils;

import org.bukkit.configuration.ConfigurationSection;

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
		data.set("icon", ItemUtils.createBankItem(icon).toString());
		data.set("desc", desc);
		
		List<String> list = new ArrayList<String>();
		for ( BankItem item : items )
			list.add(item.toString());
		
		data.set("items", list);
	}

	@Override
	public void onLoad(ConfigurationSection data)
	{
		BankItem iconItem = new BankItem(data.getString("icon", "35 a:1 s:0 n:Bank tab"));
		
		name = iconItem.getName();
		icon = iconItem.getItem();
		desc = data.getStringList("desc");
		
		//load all items for this tab
		for ( String item : data.getStringList("items") )
		{
			//create a new bank item
			items.add(new BankItem(item));
		}
	}

}
