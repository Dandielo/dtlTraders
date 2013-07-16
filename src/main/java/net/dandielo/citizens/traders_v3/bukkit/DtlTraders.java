package net.dandielo.citizens.traders_v3.bukkit;

import java.io.IOException;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.dandielo.citizens.traders_v3.tNpcListener;
import net.dandielo.citizens.traders_v3.tNpcManager;
import net.dandielo.citizens.traders_v3.bankers.backend.AccountLoader;
import net.dandielo.citizens.traders_v3.bankers.setting.BGlobalSettings;
import net.dandielo.citizens.traders_v3.bukkit.commands.BankerCommands;
import net.dandielo.citizens.traders_v3.bukkit.commands.GeneralCommands;
import net.dandielo.citizens.traders_v3.bukkit.commands.TraderCommands;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.PluginSettings;
import net.dandielo.citizens.traders_v3.core.commands.CommandManager;
import net.dandielo.citizens.traders_v3.statistics.StatisticManager;
import net.dandielo.citizens.traders_v3.statistics.StatisticServer;
import net.dandielo.citizens.traders_v3.statistics.TraderStats;
import net.dandielo.citizens.traders_v3.traders.setting.TGlobalSettings;
import net.dandielo.citizens.traders_v3.traits.BankerTrait;
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
	AccountLoader accLoader;
	
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
		
		info("Loading config files");
		//init global settings
		TGlobalSettings.initGlobalSettings();
		BGlobalSettings.initGlobalSettings();
		
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
		CommandManager.manager.registerCommands(BankerCommands.class);
		
	    //init Traders
		//init Bankers
		
		//init Denizen
		initDenizens();
		//init Wallets
		initWallets();
		
		//load all accounts
		accLoader = AccountLoader.accLoader;
		info("Accounts loaded: " + ChatColor.YELLOW + accLoader.accountsLoaded());
		
		//enable statistic server
		try
		{
			s = new StatisticServer();
			server = new Thread(s);
			server.start();
			
			stats = new TraderStats();
			logs = new Thread(stats);
			logs.start();
			info("Statistic server enabled");
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		
		StatisticManager.instance.registerListener("dtlTraders", new TraderStats());
		
		
		//enabled info
		info("Enabled");
	}
	
	Thread server, logs;
	TraderStats stats;
	StatisticServer s;
	
	@Override
	public void onDisable()
	{
		try
		{
			s.stop();
			server.join(1000);
			stats.stop();
			logs.join(1000);
		}
		catch( InterruptedException e )
		{
			e.printStackTrace();
		}
		
		
		accLoader.save();
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
	}
}
 