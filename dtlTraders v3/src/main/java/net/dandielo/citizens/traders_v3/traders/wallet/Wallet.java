package net.dandielo.citizens.traders_v3.traders.wallet;

import org.bukkit.entity.Player;

import net.dandielo.citizens.traders_v3.traders.Trader;

public class Wallet {

	private double money;
	private Type type;
	
	public Wallet(String type, double money) {
		this.money = money;
		this.type = Type.fromString(type);
	}
	
	//deposit to player or trader
	public boolean deposit(Player player)
	{
		return false;
	}
	public boolean deposit(Trader trader)
	{
		return false;
	}

	//withdraw from player or trader
	public boolean withdraw(Player Player)
	{
		return true;
	}
	public boolean withdraw(Trader trader)
	{
		return true;
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
