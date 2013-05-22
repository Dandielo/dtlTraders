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
	
	//wallet object
	private Wallet wallet;
	
	public WalletTrait()
	{
		super("wallet");
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
	}
	
	//events
	@Override
	public void load(DataKey data)
	{
		wallet = new Wallet(type, money);
	}
	
	@Override
	public void save(DataKey data)
	{
		money = wallet.getMoney();
	}
}
