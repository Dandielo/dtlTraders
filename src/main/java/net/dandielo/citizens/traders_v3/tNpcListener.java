package net.dandielo.citizens.traders_v3;

import java.util.Timer;
import java.util.TimerTask;

import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.dandielo.citizens.traders_v3.bukkit.Perms;
import net.dandielo.citizens.traders_v3.core.Debugger;
import net.dandielo.citizens.traders_v3.core.exceptions.InvalidTraderTypeException;
import net.dandielo.citizens.traders_v3.core.exceptions.TraderTypeNotFoundException;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.utils.NBTUtils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class tNpcListener implements Listener {
	/**
	 * Permissions manager instance
	 */
	Perms perms = Perms.perms;
	
	InventoryCleaner cleaner = new InventoryCleaner();
	
	private static tNpcListener instance = new tNpcListener();
	
	public static tNpcListener instance()
	{
		return instance;
	}
	
	//class definition
	tNpcManager manager = tNpcManager.instance();

	public tNpcListener()
	{
		cleaner.start();
	}
	
	//general events
	@EventHandler
	public void inventoryClickEvent(InventoryClickEvent e)
	{
		Trader trader = manager.getTraderRelation(e.getWhoClicked());

		if ( trader != null )
		{
			if ( trader.getStatus().inManagementMode() )
				trader.onManageInventoryClick(e);
			else
			    trader.onInventoryClick(e);
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
				if ( NBTUtils.isMarked(item) )
				{
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
/*		int i = 0;
		for ( ItemStack item : e.getPlayer().getInventory().getContents() )
		{
			if ( item != null )
			{
				if ( NBTUtils.isMarked(item) )
				{
					e.getPlayer().getInventory().setItem(i, null);
				}
			}
			++i;
		}*/
	}
	
	@EventHandler
	public void inventoryCloseEvent(InventoryCloseEvent e)
	{
		Trader trader = manager.getTraderRelation(e.getPlayer());
		if ( trader != null )
		{
			//unregister the inventory as a traderInventory
			manager.removeInventory(e.getInventory());
			
			//if the trader is not in mm, remove the relation too
		    if ( !trader.getStatus().inManagementMode() )
		    {			
		    	//remove the relation
		    	manager.removeRelation((Player) e.getPlayer());

		    	//clean his inventory
		    	cleaner.addPlayer((Player) e.getPlayer());
		    }
		}
	}

	//npc events
	@EventHandler
	public void npcLeftClickEvent(NPCLeftClickEvent e)
	{
		//check trait
		if ( !e.getNPC().hasTrait(TraderTrait.class) ) return;
		
		//check permission
		if ( !perms.has(e.getClicker(), "dtl.trader.use") ) return;

		TraderTrait traderTrait = e.getNPC().getTrait(TraderTrait.class);
		Trader trader;
		try 
		{

		    if ( !manager.inRelation(e.getClicker()) )
			{
				trader = (Trader) tNpcManager.create_tNpc(e.getNPC(), traderTrait.getType(), e.getClicker());
				manager.registerRelation(e.getClicker(), trader);
			}
			else
			{
				trader = manager.getTraderRelation(e.getClicker());
				if ( !trader.equals(e.getNPC()) )
				{
					manager.removeRelation(e.getClicker());
					trader = (Trader) tNpcManager.create_tNpc(e.getNPC(), traderTrait.getType(), e.getClicker());
					manager.registerRelation(e.getClicker(), trader);
				}
			}

			trader.onLeftClick(e.getClicker().getItemInHand());
			
			if ( !trader.getStatus().inManagementMode() )
				manager.removeRelation(e.getClicker());
		}
		catch (TraderTypeNotFoundException e1) 
		{
			//debug critical
			Debugger.critical("Trader type was not found, type: ", traderTrait.getType());
			Debugger.critical("Did you changed the save file?");
		} 
		catch (InvalidTraderTypeException e1) 
		{
			//debug critical
			Debugger.critical("Trader type is invalid, type: ", traderTrait.getType());
			Debugger.critical("Contact the dev to fix this!");
		}
	}

	@EventHandler
	public void npcRightClickEvent(NPCRightClickEvent e) 
	{
		//check trait
		if ( !e.getNPC().hasTrait(TraderTrait.class) ) return;

		//check permission
		if ( !perms.has(e.getClicker(), "dtl.trader.use") ) return;
		

		TraderTrait traderTrait = e.getNPC().getTrait(TraderTrait.class);
		Trader trader;
		try 
		{
			if ( !manager.inRelation(e.getClicker()) )
			{
				trader = (Trader) tNpcManager.create_tNpc(e.getNPC(), traderTrait.getType(), e.getClicker());
				manager.registerRelation(e.getClicker(), trader);
			}
			else
			{
				trader = manager.getTraderRelation(e.getClicker());
			}

			if ( !trader.onRightClick(e.getClicker().getItemInHand()) )
				//check the mode 
				if ( !trader.getStatus().inManagementMode() )
					manager.removeRelation(e.getClicker());
			
			
		}
		catch (TraderTypeNotFoundException e1) 
		{
			//debug critical
			Debugger.critical("Trader type was not found, type: ", traderTrait.getType());
			Debugger.critical("Did you changed the save file?");
		} 
		catch (InvalidTraderTypeException e1) 
		{
			//debug critical
			Debugger.critical("Trader type is invalid, type: ", traderTrait.getType());
			Debugger.critical("Contact the dev to fix this!");
		}
	}

	/**
	 * Cleans the players inventory from any "dtltrader" lore that is applied to ease the way of using traders. 
	 * @author dandielo
	 */
	static class InventoryCleaner 
	{		
		/**
		 * The timer that will shoot ech TimedTask 
		 */
		private Timer timer;
		
		/**
		 * The list that contains players that should be cleaned on the next tick
		 */
	//	private List<Player> players = Collections.synchronizedList(new ArrayList<Player>());
		
		/**
		 * Adds a player to the list, that will clean his inventory for dtltrader lores
		 * @param player
		 */
		public void addPlayer(final Player player)
		{
		//	players.listIterator().add(player);
			
			TimerTask task = new TimerTask()
			{
				private Player thisPlayer = player;
				
				@Override
				public void run()
				{
					int i = 0;
					for ( ItemStack item : thisPlayer.getInventory().getContents() )
					{
						if ( item != null )
						{
							//if the item is marked then some1 could only steal it ;) 
							//so we are cleaning the inventory from marked items
							thisPlayer.getInventory().setItem(i, NBTUtils.cleanItem(item));
						}
						++i;
					}
				}
			};

			//so this one for a fast cleanup
			timer.schedule(task, 100);
			
			//the second one if the first fails
		//	timer.schedule(task, 1500);
		}
		
		public void start()
		{
			if ( timer != null ) 
			{
				timer.cancel();
			}
			
			timer = new Timer("DtlDescription-Cleaner");
		}
	}
}
