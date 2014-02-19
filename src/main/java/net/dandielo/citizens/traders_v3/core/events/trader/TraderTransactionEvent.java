package net.dandielo.citizens.traders_v3.core.events.trader;

import net.dandielo.citizens.traders_v3.core.events.TraderEvent;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class TraderTransactionEvent extends TraderEvent
{
	
	public TraderTransactionEvent(Trader npc, Player player, StockItem item, TransactionResult result)
	{
		super(npc, player);
		this.item = item;
		this.result = result;
	}

	/**
	 * Handlers
	 */
	private static final HandlerList handlers = new HandlerList();
	 
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	/*
	 * Custom Event
	 */
	private StockItem item;
	private TransactionResult result;
	
	private boolean saveToInv = true;

	public boolean isSaveToInv()
	{
		return saveToInv;
	}

	public void setSaveToInv(boolean saveToInv)
	{
		this.saveToInv = saveToInv;
	}

	public Player getCustomer()
	{
		return player;
	}
	
	public StockItem getItem()
	{
		return item;
	}

	public TransactionResult getResult()
	{
		return result;
	}
	
	public static enum TransactionResult
	{
		SUCCESS_PLAYER_BUY, SUCCESS_PLAYER_SELL, LIMIT_REACHED, INVENTORY_FULL, PLAYER_LACKS_MONEY, TRADER_LACKS_MONEY;
		
		public boolean success()
		{
			return equals(SUCCESS_PLAYER_BUY);
		}
		
		public boolean falied()
		{
			return !equals(SUCCESS_PLAYER_BUY);
		}
	}
}
