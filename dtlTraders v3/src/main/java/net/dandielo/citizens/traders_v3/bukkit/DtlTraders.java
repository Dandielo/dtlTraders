package net.dandielo.citizens.traders_v3.bukkit;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class DtlTraders extends JavaPlugin {
	//plugin instance
	private static DtlTraders instance;

	//bukkit resources
	private static Logger logger = Logger.getLogger("Minecraft");
	private static CommandSender console;
	
	
	@Override
	public void onLoad()
	{
	}
	
	@Override
	public void onEnable()
	{
		//set the plugin instance
		instance = this;
		
		//init Vault
		
	    //init Traders
		//init Bankers
		
		//init Denizen
		//init Wallets
	}
	
	private void initDenizens()
	{	
	}
	
	private void initWallets()
	{
	}
	
	private void initVault()
	{
	}
	
	//static methods
	public static DtlTraders getInstance() 
	{
		return instance;
	}

	//static logger warning
	public static void info(String message)
	{
		logger.info("["+instance.getDescription().getName()+"] " + message);
	}
	
	//static logger warning
	public static void warning(String message)
	{
		logger.warning("["+instance.getDescription().getName()+"] " + message);
	}
	
	//static logger severe
	public static void severe(String message)
	{
		logger.severe("["+instance.getDescription().getName()+"] " + message);
	}
}
 