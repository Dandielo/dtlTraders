package net.dandielo.citizens.traders_v3;

import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.bukkit.Perms;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.dB.DebugLevel;
import net.dandielo.citizens.traders_v3.core.events.trader.TraderPrepareEvent;
import net.dandielo.citizens.traders_v3.core.exceptions.InvalidTraderTypeException;
import net.dandielo.citizens.traders_v3.core.exceptions.TraderTypeNotFoundException;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.utils.ItemUtils;
import net.dandielo.core.bukkit.NBTUtils;
import net.dandielo.core.items.serialize.flags.Lore;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class TEntityListener implements Listener {
	/**
	 * Permissions manager instance
	 */
	Perms perms = Perms.perms;
	
	/**
	 * Locale manager instance
	 */
	private LocaleManager locale = LocaleManager.locale;
	private static TEntityListener instance = new TEntityListener();
	
	
	public static TEntityListener instance()
	{
		return instance;
	}
	
	
	//class definition
	tNpcManager manager = tNpcManager.instance();

	public TEntityListener()
	{
	}

	//don't allow consuming marked items
	@EventHandler
	public void playerItemConsumeEvent(PlayerItemConsumeEvent e)
	{
		//cancel the event if the item is marked or has a trader lore
		if ( NBTUtils.isMarked(e.getItem()) )// || NBTUtils.hasTraderLore(e.getItem()) )
		{
			e.setItem(null);
			e.setCancelled(true);
		}
	}
	
	//general events
	@EventHandler
	public void inventoryClickEvent(final InventoryClickEvent e)
	{
		TradingEntity trader = manager.getRelation(e.getWhoClicked().getName(), TradingEntity.class);
		
		if ( trader != null )
		{
			if ( trader.getStatus().inManagementMode() )
				trader.onManageInventoryClick(e);
			else
			    trader.onInventoryClick(e);
		}
	}
	
	@EventHandler
	public void onLogoutRemoving(PlayerQuitEvent e)
	{
		int i = 0;
		for ( ItemStack item : e.getPlayer().getInventory().getContents() )
		{
			if ( item != null )
			{
				if ( NBTUtils.isMarked(item) )
				{
					//send specific debug messages
					dB.spec(DebugLevel.S1_ADONDRIEL, "Marked item found on player quit event");
					dB.spec(DebugLevel.S1_ADONDRIEL, "Item: ", item);
					
					//remove the item
					e.getPlayer().getInventory().setItem(i, null);
				}
			}
			++i;
		}
	}
	
	@EventHandler
	public void onLoginRemoving(PlayerJoinEvent e)
	{
		int i = 0;
		for ( ItemStack item : e.getPlayer().getInventory().getContents() )
		{
			if ( item != null )
			{
				if ( Lore.hasTraderLore(item) )
				{
					//send specific debug messages
					dB.spec(DebugLevel.S1_ADONDRIEL, "Item with trader price lore found on player join event");
					dB.spec(DebugLevel.S1_ADONDRIEL, "Item: ", item);
					
					//remove the item
					e.getPlayer().getInventory().setItem(i, null);
				}
			}
			++i;
		}
	}

	//remove marked items on inventory click events
	@EventHandler
	public void markedItemRemoval(InventoryClickEvent e)
	{		
		int i = 0;
		for ( ItemStack item : e.getWhoClicked().getInventory().getContents() )
		{
			if ( item != null )
			{
				if ( NBTUtils.isMarked(item) ) //||						( !tNpcAPI.isTNpcInventory((Player) e.getWhoClicked()) && NBTUtils.hasTraderLore(item) ) )
				{
					//send specific debug messages
					dB.spec(DebugLevel.S1_ADONDRIEL, "Marked item found on player inventory click");
					dB.spec(DebugLevel.S1_ADONDRIEL, "Marked: ", NBTUtils.isMarked(item));
					dB.spec(DebugLevel.S1_ADONDRIEL, "Lore: ", Lore.hasTraderLore(item));
					dB.spec(DebugLevel.S1_ADONDRIEL, "Item: ", item);
					
					//remove the item
					e.getWhoClicked().getInventory().setItem(i, null);
				}
			}
			++i;
		}
	}
	
	//remove marked items on inventory open events
	@EventHandler
	public void inventoryOpenEvent(InventoryOpenEvent e)
	{
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void inventoryCloseEvent(InventoryCloseEvent e)
	{
		TradingEntity npc = manager.getRelation(e.getPlayer().getName(), TradingEntity.class);
		if ( npc != null )
		{
			//unregister the inventory as a traderInventory
			manager.removeOpenedInventory((Player) e.getPlayer());

			//if the trader is not in mm, remove the relation too
		    if ( !npc.getStatus().inManagementMode() )
		    {
		    	//remove the relation
		    	manager.removeRelation((Player) e.getPlayer());

		    	//send specific debug info
		    	dB.spec(DebugLevel.S1_ADONDRIEL, "Adding player inventory to the cleaning querry");
		    	
		    	//clean his inventory
		    	//cleaner.addPlayer((Player) e.getPlayer());
		    	new InventoryCleaner((Player) e.getPlayer());
		    }
		    //in the mode is MANAGE_UNLOCKED, lock it and save items
		    else
		    {
		    	if ( npc.getStatus().equals(TEntityStatus.MANAGE_UNLOCKED) )
		    	{
		    		//lock and save the inventory
		    		npc.lockAndSave();
		    	}
		    	if ( npc.getStatus().equals(TEntityStatus.MANAGE_PRICE) )
		    	{
		    		//remove all special block lores 
		    		((Trader)npc).setSpecialBlockValues();
		    	}
		    }
		}
	}
	
	/*
	 * Handle the NPCDamageByEntityEvent to force the NPCLeftClickEvent lol...
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void npcDamageEvent(NPCDamageByEntityEvent e)
	{
		//check trait if this NPC is a trader and if the entity is a player
		if ( !e.getNPC().hasTrait(TraderTrait.class) ) return;
		if ( !(e.getDamager() instanceof Player) ) return;
		
		Player damager = (Player) e.getDamager();
		//if damage is enabled and the player is not sneaking
		if ( Settings.mmEnableDamage() && !damager.isSneaking() ) return;
		
		//set cancelled
		e.setCancelled(true);
	}

	//npc events for traders
	@EventHandler(priority=EventPriority.HIGHEST)
	public void npcLeftClickEvent(NPCLeftClickEvent e)
	{
		//check trait
		if ( !e.getNPC().hasTrait(TraderTrait.class) ) return;
		
		//check permission
		if ( !perms.has(e.getClicker(), "dtl.trader.use") ) 
		{
			locale.sendMessage(e.getClicker(), "error-nopermission");
			return;
		}
		
		//dont allow creative to open traders
		if ( e.getClicker().getGameMode().equals(GameMode.CREATIVE)
				&& !perms.has(e.getClicker(), "dtl.trader.bypass.creative") )
		{
			locale.sendMessage(e.getClicker(), "error-nopermission-creative");
			return;
		}
		
		TraderTrait traderTrait = e.getNPC().getTrait(TraderTrait.class);
		Trader trader;
		try 
		{

		    if ( !manager.inRelation(e.getClicker()) )
			{
				trader = (Trader) tNpcManager.create_tNpc(e.getNPC(), traderTrait.getType(), e.getClicker(), TraderTrait.class);
				manager.registerRelation(e.getClicker(), trader);
			}
			else
			{
				trader = manager.getTraderRelation(e.getClicker());
				if ( !trader.equals(e.getNPC()) )
				{
					manager.removeRelation(e.getClicker());
					trader = (Trader) tNpcManager.create_tNpc(e.getNPC(), traderTrait.getType(), e.getClicker(), TraderTrait.class);
					manager.registerRelation(e.getClicker(), trader);
				}
			}

			trader.onLeftClick(e.getClicker().getItemInHand());
			
			if ( !trader.getStatus().inManagementMode() )
				manager.removeRelation(e.getClicker());
			
			e.setCancelled(true);
		}
		catch (TraderTypeNotFoundException e1) 
		{
			//debug critical
			dB.critical("Trader type was not found, type: ", traderTrait.getType());
			dB.critical("Did you changed the save file?");
		} 
		catch (InvalidTraderTypeException e1) 
		{
			//debug critical
			dB.critical("Trader type is invalid, type: ", traderTrait.getType());
			dB.critical("Contact the dev to fix this!");
		}
	}

	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
	public void npcRightClickEvent(NPCRightClickEvent e) 
	{
		//check trait
		if ( !e.getNPC().hasTrait(TraderTrait.class) ) return;

		//check permission
		if ( !perms.has(e.getClicker(), "dtl.trader.use") )
		{
			locale.sendMessage(e.getClicker(), "error-nopermission");
			return;
		}
		
		//dont allow creative to open traders
		if ( e.getClicker().getGameMode().equals(GameMode.CREATIVE)
				&& !perms.has(e.getClicker(), "dtl.trader.bypass.creative") ) 
		{
			locale.sendMessage(e.getClicker(), "error-nopermission-creative");
			return;
		}

		TraderTrait traderTrait = e.getNPC().getTrait(TraderTrait.class);
		Trader trader;
		try 
		{
			if ( !manager.inRelation(e.getClicker()) )
			{
				trader = (Trader) tNpcManager.create_tNpc(e.getNPC(), traderTrait.getType(), e.getClicker(), TraderTrait.class);
				manager.registerRelation(e.getClicker(), trader);
			}
			else
			{
				trader = manager.getTraderRelation(e.getClicker());
				
				if ( !trader.getStatus().inManagementMode() )
				{
					//close inventory before opening
					e.getClicker().closeInventory();

					//make a new relation
					manager.removeRelation(e.getClicker());
					trader = (Trader) tNpcManager.create_tNpc(e.getNPC(), traderTrait.getType(), e.getClicker(), TraderTrait.class);
					manager.registerRelation(e.getClicker(), trader);
				}
				
				//check if its the same NPC if not then close the old manager mode and open the next NPC i normal mode
				if ( !trader.equals(e.getNPC()) )
				{
					manager.removeRelation(e.getClicker());
					trader = (Trader) tNpcManager.create_tNpc(e.getNPC(), traderTrait.getType(), e.getClicker(), TraderTrait.class);
					manager.registerRelation(e.getClicker(), trader);
				}
			}
			
			//preparation event, called before the trader right click, allows to change the stock or cancel the event totally
			TraderPrepareEvent event = new TraderPrepareEvent(trader, e.getClicker());
			event.callEvent();
			
			//event cancelled, now you can do your own code
			if (event.isCancelled()) return;

			//event not cancelled
			if ( !trader.onRightClick(e.getClicker().getItemInHand()) )
				//check the mode 
				if ( !trader.getStatus().inManagementMode() )
					manager.removeRelation(e.getClicker());


		}
		catch (TraderTypeNotFoundException e1) 
		{
			//debug critical
			dB.critical("Trader type was not found, type: ", traderTrait.getType());
			dB.critical("Did you changed the save file?");
		} 
		catch (InvalidTraderTypeException e1) 
		{
			//debug critical
			dB.critical("Trader type is invalid, type: ", traderTrait.getType());
			dB.critical("Contact the dev to fix this!");
		}
	}

	/**
	 * Cleans the players inventory from any "dtltrader" lore that is applied to ease the way of using traders. 
	 * @author dandielo
	 */
	static class InventoryCleaner implements Runnable
	{		
		private final Player player;
		
		public InventoryCleaner(final Player player)
		{
			//set the player
			this.player = player;
			
			//set the task for schedule
			Bukkit.getScheduler().scheduleSyncDelayedTask(DtlTraders.getInstance(), this, Settings.cleaningTimeout());
		}
		
		@Override
		public void run()
		{
			//clean the player
			clean(player);
			
			//specific debug info
			dB.spec(DebugLevel.S1_ADONDRIEL, "Removed from the cleaning querry");
		}

		public static void clean(Player thisPlayer)
		{
			int i = 0;
			//search for items
			for ( ItemStack item : thisPlayer.getInventory().getContents() )
			{
				if ( item != null )
				{
					if ( NBTUtils.isMarked(item) )
					{
//						dB.high("Marked item found, remove it");
//						//specific debug info
//						dB.spec(DebugLevel.S1_ADONDRIEL, "Marked item found, remove it");
//						dB.spec(DebugLevel.S1_ADONDRIEL, "Item: ", item);
				    	
						//remove item
						thisPlayer.getInventory().setItem(i, null);
					}
					else
					{		
//						dB.spec(DebugLevel.S1_ADONDRIEL, "Item: ", item);	
//						dB.spec(DebugLevel.S1_ADONDRIEL, "Has trader lore: ", Lore.hasTraderLore(item));
//						dB.spec(DebugLevel.S1_ADONDRIEL, "Cleaned Item: ", ItemUtils.createStockItem(item).getItem(true));	
//						
						//clean transaction lores 
						thisPlayer.getInventory().setItem(i, ItemUtils.createStockItem(item).getItem(true));
					}
				}
				++i;
			}
		}
	}
}
