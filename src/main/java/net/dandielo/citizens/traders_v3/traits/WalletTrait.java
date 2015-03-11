package net.dandielo.citizens.traders_v3.traits;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.traders.transaction.Wallet;

public class WalletTrait extends Trait {
	
	//saved variables
	@Persist String type = "infinite";
	@Persist double money = 0.0;
	
	private String playerUUID;
	private OfflinePlayer player;
	//@Persist Player player;
	
	//wallet object
	private Wallet wallet;
	
	public WalletTrait()
	{
		super("wallet");
	}
	
	//set wallet settings
	public void setPlayer(OfflinePlayer player2)
	{
		this.player = player2;
		wallet.setPlayer(player2);
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

	public double getBalance()
	{
		return wallet.getMoney();
	}

	public OfflinePlayer getPlayer()
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
		dB.info("Wallet trait attached to: ", npc.getName());
		
		//set the wallet
		wallet = new Wallet(type, money);
		wallet.setPlayer(player);
	}
	
	//events
	@Override
	public void load(DataKey data)
	{
		playerUUID = data.getString("player-uuid", "none");
		player = playerUUID.equals("none") ? null : Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
		
		wallet = new Wallet(type, money);
		wallet.setPlayer(player);
	}
	
	@Override
	public void save(DataKey data)
	{
		playerUUID = player == null ? "none" : player.getUniqueId().toString();
		data.setString("player-uuid", playerUUID);
		
		money = wallet.getMoney();
	}
}
