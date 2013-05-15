package net.dandielo.citizens.traders_v3.traders;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import net.citizensnpcs.api.npc.NPC;
import net.dandielo.citizens.traders_v3.tNpc;
import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.traders.clicks.ClickHandler;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.traders.stock.Stock;
import net.dandielo.citizens.traders_v3.traders.wallet.Wallet;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;

public abstract class Trader implements tNpc {
	//Click handlers
	private static Map<Class<? extends Trader>, List<Method>> handlers = new HashMap<Class<? extends Trader>, List<Method>>();
	
	public static void registerHandlers(Class<? extends Trader> clazz)
	{
		List<Method> methods = new ArrayList<Method>();
		for ( Method method : clazz.getMethods() )
			if ( method.isAnnotationPresent(ClickHandler.class) )
				methods.add(method);
		handlers.put(clazz, methods);
	}
	
	//temp data
	private int lastSlot = -1;
	
	//the trader class
	protected Settings settings;
	protected Wallet wallet;
	protected Stock stock;
	protected Player player;
	
	protected Inventory inventory;
	protected Status status;
	
	//constructor
	public Trader(TraderTrait trader, WalletTrait wallet, Player player)
	{
		settings = trader.getSettings();
		status = getDefaultStatus();
		stock = trader.getStock();
		this.wallet = wallet.getWallet();
		this.player = player;
	}
	
	//trader settings
	public Settings getSettings()
	{
		return settings;
	}
	
	//current trader status
	public Status getStatus() {
		return status;
	}
	
	public boolean equals(NPC npc)
	{
		return settings.getNPC().getId() == npc.getId();
	}
	
	@Override
	public void onInventoryClick(InventoryClickEvent e)
	{ 
		boolean top = e.getView().convertSlot(e.getRawSlot()) == e.getRawSlot();
		
		//get all handlers
		List<Method> methods = handlers.get(getClass());
		for ( Method method : methods )
		{
			ClickHandler handler = method.getAnnotation(ClickHandler.class);
			
			if ( !handler.shift() ? !e.isShiftClick() : true )
			{
				if ( checkStatusWith(handler.status()) && handler.inventory().equals(top) )
				{
					try 
					{
						method.invoke(this, e);
					} 
					catch (Exception ex) 
					{
						DtlTraders.severe("Error when handling click event");
						ex.printStackTrace();
					}
				}
			}
		}
	}
	
	
	
	
	
	public boolean checkStatusWith(Status[] stat)
	{
		for ( Status s : stat )
			if ( s.equals(status) )
				return true;
		return false;
	}
	
	public boolean handleClick(int slot)
	{
		if ( Settings.dClickEvent() )
			return lastSlot == (lastSlot = slot); 
		else
			return true;
	}
	
	public boolean hitTest(int slot, String mode)
	{
		return Settings.getUiItems().get(mode).equals(inventory.getItem(slot));
	}

	//Trader status enum
	public static enum Status
	{
		SELL, BUY, SELL_AMOUNTS, MANAGE_SELL, MANAGE_BUY, MANAGE_PRICE, MANAGE_AMOUNTS, MANAGE_LIMITS;
		
		public boolean inManagementMode()
		{
			return !( this.equals(SELL) || this.equals(BUY) || this.equals(SELL_AMOUNTS) ); 
		}
		
		public static Status baseManagementStatus(String status)
		{
			if ( MANAGE_SELL.name().toLowerCase().contains(status) )
				return MANAGE_SELL;
			return MANAGE_BUY;
		}
		
		public static Status baseStatus(String status)
		{
			if ( SELL.name().toLowerCase().equals(status) )
				return SELL;
			return BUY;
		}

		public String asStock() {
			return this.equals(BUY) || this.equals(MANAGE_BUY) ? "buy" : "sell";
		}
	}
	
	public Status getDefaultStatus()
	{
		return Status.baseStatus(settings.getStockDefault());
	}
	
	public Status getDefaultManagementStatus()
	{
		return Status.baseManagementStatus(settings.getStockDefault());
	}
	
	
}
