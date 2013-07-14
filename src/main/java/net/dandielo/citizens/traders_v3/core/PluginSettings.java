package net.dandielo.citizens.traders_v3.core;

import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;

import org.bukkit.configuration.file.FileConfiguration;

public class PluginSettings {	
	//plugin config file
	protected static FileConfiguration config;

	//debug settings
	private static String debugLevel;
	
	//locale settings, cached
	private static String locale;
	private static boolean localeAutoUpdate;
	
	//Online logs password and user
	private static String user = "";
	private static String pass = "";
	
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

		user = config.getString("logging.web-account");
		pass = config.getString("logging.web-pass");
		
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

	public static String debugLevel() {
		return debugLevel.toUpperCase();
	}

	public static String getLogUser()
	{
		return user;
	}

	public static String getLogPass()
	{
		return pass;
	}

	public static int logUpdateCounter()
	{
		return 5;
	}
}
