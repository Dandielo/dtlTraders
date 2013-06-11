package net.dandielo.citizens.traders_v3.bankers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.citizensnpcs.api.npc.NPC;
import net.dandielo.citizens.traders_v3.tNpc;
import net.dandielo.citizens.traders_v3.tNpcStatus;
import net.dandielo.citizens.traders_v3.bankers.account.Account;
import net.dandielo.citizens.traders_v3.bankers.setting.Settings;
import net.dandielo.citizens.traders_v3.bankers.tabs.BankItem;
import net.dandielo.citizens.traders_v3.bankers.tabs.Tab;
import net.dandielo.citizens.traders_v3.bukkit.Perms;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.core.tools.StringTools;
import net.dandielo.citizens.traders_v3.traders.clicks.ClickHandler;
import net.dandielo.citizens.traders_v3.traders.wallet.Wallet;
import net.dandielo.citizens.traders_v3.traits.BankerTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;
import net.dandielo.citizens.traders_v3.utils.ItemUtils;

public abstract class Banker implements tNpc {

	/**
	 * All registered click functions for each trader type
	 */
	private static Map<Class<? extends Banker>, List<Method>> handlers = new HashMap<Class<? extends Banker>, List<Method>>();
	
	/**
	 * Register functions for the specified type
	 * @param clazz
	 * the trader type that will be looked through functions
	 */
	public static void registerHandlers(Class<? extends Banker> clazz)
	{
		//debug info
		dB.info("Registering click handlers for trader type: ", clazz.getSimpleName());
		
		List<Method> methods = new ArrayList<Method>();
		for ( Method method : clazz.getMethods() )
			if ( method.isAnnotationPresent(ClickHandler.class) )
				methods.add(method);
		handlers.put(clazz, methods);
	}

	/**
	 * Permissions manager instance
	 */
	protected Perms perms = Perms.perms;
	
	/**
	 * Locale manager instance
	 */
	protected LocaleManager locale = LocaleManager.locale;
	
	/*
	 * Trader const data, nothing of these changes during the transaction
	 */
	protected Settings settings;
	protected Wallet wallet;
	
	/*
	 * Player related data, includes the player and his base stock that will be used to restore items when switching from buy stock to sell stock, or when closing the inventory when in buy stock
	 */
	protected Player player;
	protected Account account;
	protected Tab tab;
	
	/*
	 * Temporary trader data
	 */
	protected Inventory inventory;
	protected tNpcStatus baseStatus; //TODO?
	protected tNpcStatus status;
	

	/**
	 * Creates a new Banker type based on the banker and wallet trait. It also assigns a player to the new created banker. 
	 * @param banker
	 * the banker trait that holds all settings
	 * @param wallet
	 * the wallet trait that is used to complete transactions
	 * @param player
	 * the assigned player
	 */
	public Banker(BankerTrait banker, WalletTrait wallet, Player player)
	{
		//debug info
		dB.low("Creating a banker, for: ", player.getName());
		
		//set all constant data
		settings = banker.getSettings();
		this.wallet = wallet.getWallet();
		this.player = player;
	}
	
	/**
	 * Get all specified settings for this trader,
	 * @return
	 * Settings applied to the trader
	 */
	public Settings getSettings()
	{
		return settings;
	}
	
	/**
	 * @return
	 * the current status for the trader
	 */
	public tNpcStatus getStatus() 
	{
		return status;
	}
	
	/**
	 * @return
	 * the the traders stock
	 */
	public Account getAccount()
	{
		return account;
	}
	
	/**
	 * @return
	 * the npc that is a trader
	 */
	public NPC getNPC()
	{
		return settings.getNPC();
	}
	
	
	@Override
	public void onManageInventoryClick(InventoryClickEvent e)
	{
		onInventoryClick(e);
	}

	@Override
	public void onInventoryClick(InventoryClickEvent e)
	{
		inventoryClickHandler(e);
	}
	
	/**
	 * Handles the inventory click event by using registered click handlers for the given type
	 * @param e
	 */
	private void inventoryClickHandler(InventoryClickEvent e)
	{		
		//debug info
		dB.low("Handling click event");
		
		//check if the clicked inventoru=y is the tope or the bottom one
        boolean top = e.getView().convertSlot(e.getRawSlot()) == e.getRawSlot();
		
		//get all click handlers for the calling class
		List<Method> methods = handlers.get(getClass());
		for ( Method method : methods )
		{
			//get the handler information
			ClickHandler handler = method.getAnnotation(ClickHandler.class);

			if ( !handler.shift() ? !e.isShiftClick() : true )
			{
				if ( checkStatusWith(handler.status()) && handler.inventory().equals(top) )
				{
					try 
					{
						//debug info
						dB.low("Executing method: ", ChatColor.AQUA, method.getName());
						method.invoke(this, e);
					} 
					catch (Exception ex) 
					{
						//debug info
						dB.critical("While executing inventory click event");
						dB.critical("Exception: ", ex.getClass().getSimpleName());
						dB.critical("Method: ", method.getName());
						dB.critical("Banker: ", this.getSettings().getNPC().getName(), ", player: ", player.getName());
						dB.critical(" ");
						dB.critical("Exception message: ", ex.getMessage());
						dB.high("Stack trace: ", StringTools.stackTrace(ex.getStackTrace()));
						
						//cancel the event because of the exception!
						e.setCancelled(true);
					}
				}
			}
		}
		//debug info, shows if the event was canceled or not
		dB.info("Event cancelled: ", e.isCancelled());
	}
	
	/** 
	 * Checks if the current banker status is present in the given array
	 *
	 * @param stat
	 *     array to check
	 */
	protected boolean checkStatusWith(tNpcStatus[] stat)
	{
		for ( tNpcStatus s : stat )
			if ( s.equals(status) )
				return true;
		return false;
	}
	
	public void saveItemsUpponLocking()
	{
		//debug normal
		dB.normal("Clearing the stock to set it with new items");
		
		//clear
		tab.getItems().clear();
				
		int slot = 0;
		for ( ItemStack vItem : inventory.getContents() )
		{
			//check if the given item is not null
			if ( vItem != null && !account.isUIRow(slot) )
			{
				//to bank item
				BankItem bItem = ItemUtils.createBankItem(vItem);
				
				//set the items new slot 
				bItem.setSlot(slot); 
				
				//add to tab 
				tab.addItem(bItem); 
			}
			
			++slot;
		}
	}
	
	@Override
	public void lockAndSave()
	{
		status = tNpcStatus.ACCOUNT_LOCKED;
		saveItemsUpponLocking();
	}
	
	public boolean tabTransaction(int tabID)
	{
		if ( wallet.withdraw(player, 0.0) )
		{
			wallet.deposit(this, 0.0);
			return true;
		}
		return false;
	}
}
