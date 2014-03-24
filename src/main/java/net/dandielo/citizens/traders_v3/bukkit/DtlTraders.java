package net.dandielo.citizens.traders_v3.bukkit;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.dandielo.citizens.traders_v3.tNpcListener;
import net.dandielo.citizens.traders_v3.tNpcManager;
import net.dandielo.citizens.traders_v3.bankers.backend.AccountLoader;
import net.dandielo.citizens.traders_v3.bukkit.commands.GeneralCommands;
import net.dandielo.citizens.traders_v3.bukkit.commands.TraderCommands;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.PluginSettings;
import net.dandielo.citizens.traders_v3.core.commands.CommandManager;
import net.dandielo.citizens.traders_v3.stats.TraderStats;
import net.dandielo.citizens.traders_v3.traders.limits.LimitManager;
import net.dandielo.citizens.traders_v3.traders.setting.GlobalSettings;
import net.dandielo.citizens.traders_v3.traits.BankerTrait;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;
import net.dandielo.citizens.traders_v3.utils.items.ItemFlag;
import net.dandielo.stats.bukkit.Stats;
import net.dandielo.stats.core.Manager;

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
	private static Stats stats;
	
	//plugin resources
	AccountLoader accLoader;
	LimitManager limits;
	
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
		
		dB.info("Enabling plugin");
		
		//init Vault
		if ( !initVault() )
		{
			this.setEnabled(false);
			this.getPluginLoader().disablePlugin(this);
			
			//Severe message
			severe("Vault plugin not found, disabling plugin");
			return;
		}
		
		//check Citizens2 dependency
		if ( !checkCitizens() )
		{
			this.setEnabled(false);
			this.getPluginLoader().disablePlugin(this);
			
			//Severe message
			severe("Vault plugin not found, disabling plugin");
			return;
		}
		
		info("Loading config files");
		//init global settings
		GlobalSettings.initGlobalSettings();
		
		//register traits
		CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(TraderTrait.class).withName("trader"));
		CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(BankerTrait.class).withName("banker"));
		CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(WalletTrait.class).withName("wallet"));
		
		//registering core extensions
		ItemAttr.registerCoreAttributes();
		ItemFlag.registerCoreFlags();
		tNpcManager.registerTypes();
		
		//register events
		getServer().getPluginManager().registerEvents(tNpcListener.instance(), this);
		
		//register commands
		CommandManager.manager.registerCommands(GeneralCommands.class);
		CommandManager.manager.registerCommands(TraderCommands.class);
		
		//init Denizen
		initDenizens();
		//init Wallets
		initStats();
		
		//load all accounts
		accLoader = AccountLoader.accLoader;
		info("Accounts loaded: " + ChatColor.YELLOW + accLoader.accountsLoaded());
		
		//load all limits
		limits = LimitManager.self;
		limits.init();
		
		//enabled info
		info("Enabled");
	}
	
	@Override
	public void onDisable()
	{		
		accLoader.save();
		
		//save all limits
		limits.save();
	}
	
	private void initDenizens()
	{	
	}
	
	private void initStats()
	{
		stats = (Stats) getServer().getPluginManager().getPlugin("dtlStats");
		if ( stats == null ) 
		{
			return;
		}
		Manager.registerListener("dtlTraders", TraderStats.class);
		Manager.registerUpdater("dtlTraders", TraderStats.class);
		info("dtlStats found, web api usable!");
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
	private boolean checkCitizens()
	{
		if ( getServer().getPluginManager().getPlugin("Citizens") == null ) 
		{
			warning("Citizens2 plugin not found! Disabling plugin");
			return false;
		}
		return true;
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
	}
}
 
