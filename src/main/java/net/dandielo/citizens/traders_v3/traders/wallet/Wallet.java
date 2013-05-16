package net.dandielo.citizens.traders_v3.traders.wallet;

import org.bukkit.entity.Player;

import static net.dandielo.citizens.traders_v3.bukkit.Econ.econ;
import net.dandielo.citizens.traders_v3.core.Debugger;
import net.dandielo.citizens.traders_v3.traders.Trader;

public class Wallet {
	private double money;
	private Type type;
	
	public Wallet(String type, double money) {
		this.money = money;
		this.type = Type.fromString(type);
	}
	
	//deposit to player or trader
	public boolean deposit(Player player, double amount)
	{
		//debug info
		Debugger.info("Deposit money, to: player, name: ", player.getName());
		Debugger.info("Amount: ", amount);
		
		return econ.deposit(player.getName(), amount);
	}
	public boolean deposit(Trader trader, double amount)
	{
		//debug info
		Debugger.info("Deposit money, to: trader, name: ", trader.getSettings().getNPC().getName());
		Debugger.info("Amount: ", amount, ", wallet: ", type.name().toLowerCase());
		
		if ( type.equals(Type.PRIVATE) )
		{
			money += amount;
		}
		else 
		if ( type.equals(Type.OWNER) )
		{
			return econ.deposit(trader.getSettings().getOwner(), amount);
		}
		return true;
	} 

	//withdraw from player or trader
	public boolean withdraw(Player player, double amount)
	{
		//debug info
		Debugger.info("Withdraw money, from: player, name: ", player.getName());
		Debugger.info("Amount: ", amount);
		
		return econ.withdraw(player.getName(), amount);
	}
	public boolean withdraw(Trader trader, double amount)
	{
		//debug info
		Debugger.info("Withdraw money, from: trader, name: ", trader.getSettings().getNPC().getName());
		Debugger.info("Amount: ", amount, ", wallet: ", type.name().toLowerCase(), ", balance: ", money);
		
		if ( type.equals(Type.INFINITE) )
		    return true;
		else 
		if ( type.equals(Type.PRIVATE) )
		{
			return money - amount >= 0 ? (money -= amount) >= 0 : false;
		}
		else
		{
			return econ.withdraw(trader.getSettings().getOwner(), amount);
		}
	}
	
	//Type enum
	static enum Type
	{
		INFINITE, OWNER, PRIVATE;
		
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
