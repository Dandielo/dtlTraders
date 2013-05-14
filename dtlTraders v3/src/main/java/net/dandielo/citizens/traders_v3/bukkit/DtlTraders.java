package net.dandielo.citizens.traders_v3.bukkit;

import java.util.logging.Logger;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;
import net.dandielo.citizens.traders_v3.utils.items.ItemData;
import net.dandielo.citizens.traders_v3.utils.items.ItemFlag;

import org.bukkit.plugin.java.JavaPlugin;

public class DtlTraders extends JavaPlugin {
	//bukkit resources
	private static Logger logger = Logger.getLogger("Minecraft");
//	private static CommandSender console;
	
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
		
		//register traits
		CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(TraderTrait.class).withName("trader"));
		CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(WalletTrait.class).withName("wallet"));
		
		//init Vault
		initVault();
		
		//registering core extensions
		ItemData.registerCoreData();
		ItemFlag.registerCoreFlags();
		
	    //init Traders
		//init Bankers
		
		//init Denizen
		initDenizens();
		//init Wallets
		initWallets();
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
 