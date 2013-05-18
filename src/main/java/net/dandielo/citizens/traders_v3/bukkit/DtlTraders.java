package net.dandielo.citizens.traders_v3.bukkit;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.dandielo.citizens.traders_v3.tNpcListener;
import net.dandielo.citizens.traders_v3.tNpcManager;
import net.dandielo.citizens.traders_v3.core.Debugger;
import net.dandielo.citizens.traders_v3.core.PluginSettings;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.setting.TGlobalSettings;
import net.dandielo.citizens.traders_v3.traders.types.Server;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;
import net.dandielo.citizens.traders_v3.utils.items.ItemFlag;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class DtlTraders extends JavaPlugin {
	//console prefix
	public static final String PREFIX = "[dtlTraders]" + ChatColor.WHITE; 
	
	//bukkit resources
	//private static Logger logger = Logger.getLogger("Minecraft");
	private static ConsoleCommandSender console;
	
	//plugin instance
	private static DtlTraders instance;
	
	//plugin resources
	
	@Override
	public void onLoad()
	{
	}
	
	@Override
	public void onEnable()
	{
		//set the plugin instance
		instance = this;
		
		//set the console sender
		console = getServer().getConsoleSender();
		
		//init plugin settings
		saveDefaultConfig();
		PluginSettings.initPluginSettings();
		
		Debugger.info("Enabling plugin");
		
		//init Vault
		if ( !initVault() )
		{
			this.setEnabled(false);
			this.getPluginLoader().disablePlugin(this);
			
			//Severe message
			severe("Vault plugin not found, disabling plugin");
			return;
		}
		
		info("Loading config files");
		//init global settings
		TGlobalSettings.initGlobalSettings();
		
		//register traits
		CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(TraderTrait.class).withName("trader"));
		CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(WalletTrait.class).withName("wallet"));
		
		//registering core extensions
		ItemAttr.registerCoreAttributes();
		ItemFlag.registerCoreFlags();
		tNpcManager.registerTraderTypes();
		
		//register type handlers
		Trader.registerHandlers(Server.class);
		
		//register events
		getServer().getPluginManager().registerEvents(tNpcListener.instance(), this);
		
	    //init Traders
		//init Bankers
		
		//init Denizen
		initDenizens();
		//init Wallets
		initWallets();
		
		//enabled info
		info("Enabled");
	}
	
	private void initDenizens()
	{	
	}
	
	private void initWallets()
	{
	}
	
	private boolean initVault()
	{
		if ( getServer().getPluginManager().getPlugin("Vault") == null ) 
		{
			warning("Vault plugin not found! Disabling plugin");
			return false;
		}
		return Econ.econ.isEnabled();
	}
	
	//static methods
	public static DtlTraders getInstance() 
	{
		return instance;
	}

	//static logger warning
	public static void info(String message)
	{
		console.sendMessage(PREFIX + "[INFO] " + message);
	//	logger.info("["+instance.getDescription().getName()+"] " + message);
	}
	
	//static logger warning
	public static void warning(String message)
	{
		console.sendMessage(PREFIX + ChatColor.GOLD + "[WARNING] " + ChatColor.RESET + message);
	//	logger.warning("["+instance.getDescription().getName()+"] " + message);
	}
	
	//static logger severe
	public static void severe(String message)
	{
		console.sendMessage(PREFIX + ChatColor.RED + "[SEVERE] " + ChatColor.RESET + message);
	//	logger.severe("["+instance.getDescription().getName()+"] " + message);
	}
}
 