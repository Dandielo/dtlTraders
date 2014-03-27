package net.dandielo.citizens.traders_v3.core.events.trader;

import net.dandielo.citizens.traders_v3.core.events.TraderEvent;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.traders.stock.Stock;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class TraderPrepareEvent extends TraderEvent implements Cancellable 
{
	public TraderPrepareEvent(Trader npc, Player player) {
		super(npc, player);
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

	/**
	 * Methods
	 */;

	 public Stock getStock() {
		 return getTrader().getStock();
	 }

	 public Settings getSettings() {
		 return getTrader().getSettings();
	 }

	 /**
	  * Allows to cancel the event and events opening the traders window
	  */
	 boolean cancelled = false;

	 @Override
	 public boolean isCancelled() {
		 return cancelled;
	 }

	 @Override
	 public void setCancelled(boolean val) {
		 cancelled = val;
	 }
}
