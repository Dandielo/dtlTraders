package net.dandielo.citizens.traders_v3.core;

import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;

import org.bukkit.configuration.file.FileConfiguration;

public class PluginSettings {	
	//plugin config file
	protected static FileConfiguration config = DtlTraders.getInstance().getConfig();
	
	//locale settings, cached
	private static String locale;
	private static boolean localeAutoUpdate;
	
	//statistic settings
	private static boolean statistics = false;
	
	//load plugin settings
	public static void initPluginSettings()
	{
		locale = config.getString("locale.load", "en");
		localeAutoUpdate = config.getBoolean("locale.auto-update", true);
		
		//unused
		statistics = config.getBoolean("general.statistics", false);
	}
	
	//getters
	public static String getLocale()
	{
		return locale;
	}
	
	public static boolean autoUpdateLocale()
	{
		return localeAutoUpdate;
	}
	
	public static boolean statisticsEnabled()
	{
		return statistics;
	}
}
