package net.dandielo.citizens.traders_v3.core;

import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;

import org.bukkit.configuration.file.FileConfiguration;

public class PluginSettings {	
	//plugin config file
	protected static FileConfiguration config;

	//debug settings
	private static String debugLevel = "none";
	
	//locale settings, cached
	private static String locale;
	private static boolean localeAutoUpdate;
	
	//core settings (should be changed only by advanced users)
	private static int cleaningTimeout = 4;
	
	//statistic settings
	private static boolean statistics = false;
	
	//load plugin settings
	public static void initPluginSettings()
	{
		//set the config file
		config = DtlTraders.getInstance().getConfig();
		
		//debug settings
		debugLevel = config.getString("debug", "normal");
		
		//debug info
		dB.info("Loading plugin settings");
		
		//locale settings
		locale = config.getString("locale.load", "en");
		localeAutoUpdate = config.getBoolean("locale.auto-update", true);

		//core settings
		cleaningTimeout = config.getInt("trader.transaction.cleaning", 4);
		
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
	
	public static int cleaningTimeout()
	{
		return cleaningTimeout;
	}

	public static String debugLevel() {
		return debugLevel.toUpperCase();
	}
}
