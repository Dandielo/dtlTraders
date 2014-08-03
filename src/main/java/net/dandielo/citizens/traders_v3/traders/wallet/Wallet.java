package net.dandielo.citizens.traders_v3.traders.wallet;

import org.bukkit.OfflinePlayer;

import static net.dandielo.citizens.traders_v3.bukkit.Econ.econ;
import net.dandielo.citizens.traders_v3.bankers.Banker;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.traders.Trader;

public class Wallet {
	OfflinePlayer player = null;
	
	private double money;
	private Type type;
	
	public Wallet(String type, double money) {
		this.money = money;
		this.type = Type.fromString(type);
	}
	
	//set type
	public void setType(String type)
	{
		this.type = Type.fromString(type);
	}
	
	//set money
	public void setMoney(double money)
	{
		this.money = money;
	}
	
	//get money
	public double getMoney()
	{
		return money;
	}
	
	//set target player
	public void setPlayer(OfflinePlayer player)
	{
		this.player = player;
	}
	
	public OfflinePlayer getPlayer()
	{
		return player;
	}
	
	//deposit to player or trader
	public boolean deposit(OfflinePlayer player, double amount)
	{
		//debug info
		dB.info("Deposit money, to: player, name: ", player.getName());
		dB.info("Amount: ", amount);
		
		return econ.deposit(player.getUniqueId(), amount);
	}
	public boolean deposit(Trader trader, double amount)
	{
		//debug info
		dB.info("Deposit money, to: trader, name: ", trader.getSettings().getNPC().getName());
		dB.info("Amount: ", amount, ", wallet: ", type.name().toLowerCase());
		
		if ( type.equals(Type.PRIVATE) )
		{
			money += amount;
		}
		else 
		if ( type.equals(Type.OWNER) )
		{
			return econ.deposit(trader.getSettings().getOwner().getUniqueId(), amount);
		}
		else
		if ( type.equals(Type.PLAYER) )
		{
			if ( player != null )
			    return econ.deposit(player.getUniqueId(), amount);
			return false;
		}
		return true;
	} 
	public boolean deposit(Banker banker, double amount)
	{
		//debug info
		dB.info("Deposit money, to: trader, name: ", banker.getSettings().getNPC().getName());
		dB.info("Amount: ", amount, ", wallet: ", type.name().toLowerCase());

		if ( type.equals(Type.PRIVATE) )
		{
			money += amount;
		}
		else 
		if ( type.equals(Type.OWNER) )
		{//same as private here
			money += amount;
		}
		else
		if ( type.equals(Type.PLAYER) )
		{//same as private here
			money += amount;
		}
		return true;
	}

	//withdraw from player or trader
	public boolean withdraw(OfflinePlayer player, double amount)
	{
		//debug info
		dB.info("Withdraw money, from: player, name: ", player.getName());
		dB.info("Amount: ", amount);
		
		return econ.withdraw(player.getUniqueId(), amount);
	}
	public boolean withdraw(Trader trader, double amount)
	{
		//debug info
		dB.info("Withdraw money, from: trader, name: ", trader.getSettings().getNPC().getName());
		dB.info("Amount: ", amount, ", wallet: ", type.name().toLowerCase(), ", balance: ", money);
		
		if ( type.equals(Type.INFINITE) )
		    return true;
		else 
		if ( type.equals(Type.PRIVATE) )
		{
			return money - amount >= 0 ? (money -= amount) >= 0 : false;
		}
		else
		if ( type.equals(Type.OWNER) ) 
		{
			return econ.withdraw(trader.getSettings().getOwner().getUniqueId(), amount);
		}
		else
		{
			if ( player != null )
				return econ.withdraw(player.getUniqueId(), amount);
			return false;
		}
	}
	
	//Type enum
	static enum Type
	{
		INFINITE, OWNER, PRIVATE, PLAYER;
		
		@Override
		public String toString()
		{
			return name().toLowerCase();
		}
		
		public static Type fromString(String type)
		{
			return valueOf(type.toUpperCase());
		}
	}
}
