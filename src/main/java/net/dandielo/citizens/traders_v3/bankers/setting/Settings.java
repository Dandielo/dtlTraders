package net.dandielo.citizens.traders_v3.bankers.setting;

import org.bukkit.ChatColor;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.util.DataKey;

public class Settings extends BGlobalSettings {
	
	private String accountName = getDefaultAccountNameFormat();
	private int maxVisibleTabs = getDefaultMaxTabs();

//	private double withdrawFee;
//	private double depositFee;
	
	private NPC npc;
	private String type = "private";
	
	public Settings(NPC npc)
	{
		this.npc = npc;
	}

	public NPC getNPC()
	{
		return npc;
	}
	
	public String getNpcName()
	{
		return ChatColor.RESET + npc.getName().replace('&', '§') + ChatColor.RESET;
	}

	public String getType()
	{
		return type;
	}
	
	public String getAccountNameFormat()
	{
		return accountName;
	}
	
	public int getMaxVisibleTabs()
	{
		return maxVisibleTabs;
	}

	public void load(DataKey data)
	{
		type = data.getString("type");
		
		accountName = data.getString("account-format", getDefaultAccountNameFormat());
		maxVisibleTabs = data.getInt("visible-tabs", getDefaultMaxTabs());
	}

	public void save(DataKey data)
	{
		data.setString("type", type);
		data.setRaw("account", null);
		
		if ( !accountName.equals(getDefaultAccountNameFormat()) )
			data.setString("account.format", accountName);
		if ( maxVisibleTabs != getDefaultMaxTabs() )
			data.setInt("account.visible-tabs", maxVisibleTabs);
	}
	
	
}
