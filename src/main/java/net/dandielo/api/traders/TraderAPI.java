package net.dandielo.api.traders;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.MobType;
import net.dandielo.citizens.traders_v3.tNpcManager;
import net.dandielo.citizens.traders_v3.tNpcStatus;
import net.dandielo.citizens.traders_v3.core.exceptions.TraderTypeRegistrationException;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.setting.TGlobalSettings;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;

public class TraderAPI {
	
	//Core related
	public boolean registerTraderType(Class<? extends Trader> type)
	{
		try
		{
			tNpcManager.registerType(type);
		}
		catch( TraderTypeRegistrationException e )
		{
			return false;
		}
		return true;
	}
	
	//trader type related
	public <T extends Trader> TraderTrait createTrader(Location loc, String name, String type)
	{
		return createTrader(loc, name, type, EntityType.PLAYER);
	}
	public <T extends Trader> TraderTrait createTrader(Location location, String name, String type, EntityType entity)
	{
		//add Traits
		NPC npc = CitizensAPI.getNPCRegistry().createNPC(entity, name);
				
		npc.addTrait(TraderTrait.class);
		npc.addTrait(WalletTrait.class);

		//set the walet trait
		WalletTrait wallet = npc.getTrait(WalletTrait.class);
		wallet.setType(TGlobalSettings.getDefaultWallet());
		wallet.setMoney(TGlobalSettings.getWalletStartBalance());

		//set the mob type
		npc.addTrait(MobType.class);
		npc.getTrait(MobType.class).setType(entity);

		//spawn and the location of the sender
		npc.spawn(location);
		
		TraderTrait trader = npc.getTrait(TraderTrait.class);
		trader.getSettings().setType(type);
		
		return trader;
	}
	
	public boolean removeTrader(int id)
	{
		NPC npc = CitizensAPI.getNPCRegistry().getById(id);
		if ( npc == null )
			return false;
		npc.destroy();
		return true;
	}
	
	public boolean toggleStatus(Player player, tNpcStatus status)
	{
		Trader trader = tNpcManager.instance().getTraderRelation(player);
		if ( trader == null )
			return false;
		trader.parseStatus(status);
		return true;
	}
	
	public boolean openTrader(Player player, TraderTrait trader)
	{
		return false;
	}
	public boolean closeTrader(Player player, TraderTrait trader)
	{
		return false;
	}
	
	//transaction related
	public boolean sellItem(Player player, Trader trader, StockItem item)
	{
		return false;
	}

	public boolean buyItem(Player player, Trader trader, StockItem item)
	{
		return false;
	}
	
	//Stock related
	public void removeItem(Trader trader, String stock, ItemStack item)
	{
		
	}
	public void removeItem(Trader trader, String stock, StockItem item)
	{
		
	}
	public void removeItem(Trader trader, String stock, String item)
	{
		
	}
	
	public void addItem(Trader trader, String stock, ItemStack item)
	{
		
	}
	public void addItem(Trader trader, String stock, StockItem item)
	{
		
	}
	public void addItem(Trader trader, String stock, String item)
	{
		
	}
	
	public boolean hasItem(Trader trader, String stock, ItemStack item)
	{
		return false;
	}
	public boolean hasItem(Trader trader, String stock, StockItem item)
	{
		return false;
	}
	public boolean hasItem(Trader trader, String stock, String item)
	{
		return false;
	}
	
	public StockItem getItem(Trader trader, String stock, ItemStack item)
	{
		return null;
	}
	public StockItem getItem(Trader trader, String stock, StockItem item)
	{
		return null;
	}
	public StockItem getItem(Trader trader, String stock, String item)
	{
		return null;
	}
}
