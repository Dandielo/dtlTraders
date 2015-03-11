package net.dandielo.api.traders;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.MobType;
import net.dandielo.citizens.traders_v3.tNpcManager;
import net.dandielo.citizens.traders_v3.TEntityStatus;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.setting.GlobalSettings;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;

public class TraderAPI {
	private static tNpcManager manager = tNpcManager.instance(); 
	
	private TraderAPI() { };
	
	//trader type related
	public static <T extends Trader> TraderTrait createTrader(Location loc, String name, String type)
	{
		return createTrader(loc, name, type, EntityType.PLAYER);
	}
	public static <T extends Trader> TraderTrait createTrader(Location location, String name, String type, EntityType entity)
	{
		//add Traits
		NPC npc = CitizensAPI.getNPCRegistry().createNPC(entity, name);
				
		npc.addTrait(TraderTrait.class);
		npc.addTrait(WalletTrait.class);

		//set the walet trait
		WalletTrait wallet = npc.getTrait(WalletTrait.class);
		wallet.setType(GlobalSettings.getDefaultWallet());
		wallet.setMoney(GlobalSettings.getWalletStartBalance());

		//set the mob type
		npc.addTrait(MobType.class);
		npc.getTrait(MobType.class).setType(entity);

		//spawn and the location of the sender
		npc.spawn(location);
		
		TraderTrait trader = npc.getTrait(TraderTrait.class);
		trader.getSettings().setType(type);
		
		return trader;
	}
	public static boolean removeTrader(int id)
	{
		NPC npc = CitizensAPI.getNPCRegistry().getById(id);
		if ( npc == null )
			return false;
		npc.destroy();
		return true;
	}
	public static boolean toggleStatus(Player player, TEntityStatus status)
	{
		Trader trader = tNpcManager.instance().getTraderRelation(player);
		if ( trader == null )
			return false;
		trader.parseStatus(status);
		return true;
	}
	
	public static boolean openTrader(Player player, TraderTrait trait, TEntityStatus status, boolean openInv)
	{
		//check for a relation
		Trader trader = manager.getTraderRelation(player);
		//if found then close it and create a new one
		if ( trader != null && !trader.equals(trait.getNPC()) )
		{
			//close the inventory and remove the relation
			player.closeInventory();
			manager.removeRelation(player);
			//create a new relation
			try
			{
				trader = (Trader) tNpcManager.create_tNpc(trait.getNPC(), trait.getType(), player, TraderTrait.class);
			} catch( Exception e ) { return false; }
		}
		
		//parse the status
		trader.parseStatus(status);
		//open the traders inventory
		if ( openInv )
		{
			//get the inventory ready to display
			Inventory inventory = trader.getStock().getInventory(status);
			//update the players inventory
			trader.updatePlayerInventory();
		    player.openInventory(inventory);
			//register the inventory as a traderInventory
			tNpcManager.instance().registerOpenedInventory(player, inventory);
		}
		return true;
	}
	public static boolean closeTrader(Player player)
	{
		//check for a relation
		Trader trader = manager.getTraderRelation(player);
		//if found then close it
		if ( trader == null ) return false;

		//close the inventory and remove the relation
		player.closeInventory();
		manager.removeRelation(player);
		return true;
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
