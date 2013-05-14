package net.dandielo.citizens.traders_v3.traders;

import org.bukkit.entity.Player;

import net.dandielo.citizens.traders_v3.tNpc;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.traders.stock.Stock;
import net.dandielo.citizens.traders_v3.traders.wallet.Wallet;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;

public abstract class Trader implements tNpc {
	
	//the trader class
	protected Settings settings;
	protected Wallet wallet;
	protected Stock stock;
	protected Player player;
	
	protected Status status;
	
	//constructor
	public Trader(TraderTrait trader, WalletTrait wallet, Player player)
	{
		settings = trader.getSettings();
		stock = trader.getStock();
		this.wallet = wallet.getWallet();
		this.player = player;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public static enum Status
	{
		SELL, BUY, SELL_AMOUNTS, MANAGE_SELL, MANAGE_BUY, MANAGE_PRICE, MANAGE_AMOUNTS, MANAGE_LIMITS;
	}
	
	
}
