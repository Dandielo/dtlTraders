package net.dandielo.citizens.traders_v3.traits;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
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
	
	//get the actual wallet object
	public Wallet getWallet()
	{
		return wallet;
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
		
	}
}
