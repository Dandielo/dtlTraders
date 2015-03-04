package net.dandielo.citizens.traders_v3.bukkit;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        	DtlTraders.info("Economy plugin: " + ChatColor.YELLOW + economy.getName());
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
	
	public int getBalance(UUID uid) 
	{
		return economy.getBalance(Bukkit.getOfflinePlayer(uid));
	}
	
	public boolean check(UUID uid, double amount) 
	{
		return economy.getBalance(Bukkit.getOfflinePlayer(uid)) >= amount;
	}
	
	public boolean deposit(UUID uid, double amount)
	{
		return economy.depositPlayer(Bukkit.getOfflinePlayer(uid), amount).transactionSuccess();
	}
	
	public boolean withdraw(UUID uid, double amount)
	{
		return economy.withdrawPlayer(Bukkit.getOfflinePlayer(uid), amount).transactionSuccess();
	}
	
}
