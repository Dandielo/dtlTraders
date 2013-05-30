package net.dandielo.citizens.traders_v3.traits;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.traders_v3.core.Debugger;
import net.dandielo.citizens.traders_v3.traders.wallet.Wallet;

public class WalletTrait extends Trait {
	
	//saved variables
	@Persist String type = "infinite";
	@Persist double money = 0.0;
	
	@Persist String player;
	
	//wallet object
	private Wallet wallet;
	
	public WalletTrait()
	{
		super("wallet");
	}
	
	//set wallet settings
	public void setPlayer(String player)
	{
		this.player = player;
		wallet.setPlayer(player);
	}
	
	//set wallet settings
	public void setType(String type)
	{
		this.type = type;
		wallet.setType(type);
	}
	
	//set money
	public void setMoney(double money)
	{
		this.money = money;
		wallet.setMoney(money);
	}

	public  double getBalance()
	{
		return wallet.getMoney();
	}

	public String getPlayer()
	{
		return player;
	}

	public String getType()
	{
		return type;
	}
	
	//get the actual wallet object
	public Wallet getWallet()
	{
		return wallet;
	}
	
	@Override
	public void onAttach()
	{
		//debug info
		Debugger.info("Wallet trait attached to: ", npc.getName());
		
		//set the wallet
		wallet = new Wallet(type, money);
		wallet.setPlayer(player);
	}
	
	//events
	@Override
	public void load(DataKey data)
	{
		wallet = new Wallet(type, money);
		wallet.setPlayer(player);
	}
	
	@Override
	public void save(DataKey data)
	{
		money = wallet.getMoney();
	}
}
