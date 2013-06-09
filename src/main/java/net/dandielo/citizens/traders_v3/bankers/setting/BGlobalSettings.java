package net.dandielo.citizens.traders_v3.bankers.setting;

import org.bukkit.configuration.ConfigurationSection;

import net.dandielo.citizens.traders_v3.core.Debugger;
import net.dandielo.citizens.traders_v3.core.PluginSettings;

public class BGlobalSettings extends PluginSettings {
	//the banker config section
	protected static ConfigurationSection bConfig;

	private static String accountNameFormat;
	private static int maxTabs;
	private static int tabSize;
	
	private static int startTabCount = 1;

	public static void initGlobalSettings()
	{
		//debug info
		Debugger.info("Loading general trader configuration");
		
		//get trader section
		bConfig = config.getConfigurationSection("banker");

		//load stock settings
		accountNameFormat = bConfig.getString("account.format", "{npc}\'s bank!");
		maxTabs = bConfig.getInt("account.max-tabs", 9);
		tabSize = bConfig.getInt("account.tabs-size", 3);
	}
	
	public static String getDefaultAccountNameFormat()
	{
		return accountNameFormat;
	}

	public static int getDefaultMaxTabs()
	{
		return maxTabs;
	}

	public static int getDefaultTabSize()
	{
		return tabSize;
	}

	public static int getStartTabCount()
	{
		return startTabCount;
	}

}
