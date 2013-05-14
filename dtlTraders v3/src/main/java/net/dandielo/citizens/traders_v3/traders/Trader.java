package net.dandielo.citizens.traders_v3.traders;

import org.bukkit.entity.Player;

import net.citizensnpcs.api.npc.NPC;
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
		status = getDefaultStatus();
		settings = trader.getSettings();
		stock = trader.getStock();
		this.wallet = wallet.getWallet();
		this.player = player;
	}
	
	//current trader status
	public Status getStatus() {
		return status;
	}
	
	public boolean equals(NPC npc)
	{
		return settings.getNPC().getId() == npc.getId();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	//Trader status enum
	public static enum Status
	{
		SELL, BUY, SELL_AMOUNTS, MANAGE_SELL, MANAGE_BUY, MANAGE_PRICE, MANAGE_AMOUNTS, MANAGE_LIMITS;
		
		public boolean inManagementMode()
		{
			return !( this.equals(SELL) || this.equals(BUY) || this.equals(SELL_AMOUNTS) ); 
		}
		
		public static Status baseManagementStatus(String status)
		{
			if ( MANAGE_SELL.name().toLowerCase().contains(status) )
				return MANAGE_SELL;
			return MANAGE_BUY;
		}
		
		public static Status baseStatus(String status)
		{
			if ( SELL.name().toLowerCase().equals(status) )
				return SELL;
			return BUY;
		}
	}
	
	public Status getDefaultStatus()
	{
		return Status.baseStatus(settings.getStockDefault());
	}
	
	public Status getDefaultManagementStatus()
	{
		return Status.baseManagementStatus(settings.getStockDefault());
	}
	
	
}
