package net.dandielo.citizens.traders_v3.bukkit;

import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public class Econ {
	public final static Econ econ = new Econ();
	
	private Economy economy;
	private boolean enabled = false;
	
	private Econ()
	{
		init();
	}
	
	private void init()
	{
		RegisteredServiceProvider<Economy> rspEcon = DtlTraders.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
		if ( rspEcon != null ) 
        {
        	//economy exists, plugin enabled
        	economy = rspEcon.getProvider();
        	DtlTraders.info("Using " + economy.getName() + " plugin");
        	enabled = true;
        } 
        else 
        {
        	//no economy plugin found disable the plugin
        	DtlTraders.info("Economy plugin not found! Disabling plugin");
		}
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public boolean deposit(String name, double amount)
	{
		return economy.depositPlayer(name, amount).transactionSuccess();
	}
	
	public boolean withdraw(String name, double amount)
	{
		return economy.withdrawPlayer(name, amount).transactionSuccess();
	}
	
}
