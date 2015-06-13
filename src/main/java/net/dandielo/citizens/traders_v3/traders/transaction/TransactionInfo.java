package net.dandielo.citizens.traders_v3.traders.transaction;

import org.bukkit.entity.Player;

import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.traders.transaction.participants.PlayerParticipant;
import net.dandielo.citizens.traders_v3.utils.items.flags.StackPrice;

public class TransactionInfo {
	/* Participants */
	private Participant seller;
	private Participant buyer;
	
	/* Transaction details */
	private StockItem item;
	public enum Stock { SELL, BUY }; Stock stock;
	private int amount;
	
	/* Price details */
	private double multiplier;
	
	public TransactionInfo(String stock, StockItem item, int amount)
	{
		this.stock = Stock.valueOf(stock.toUpperCase());
		this.item = item;
		this.amount = amount;
	}

	private void setSeller(Participant seller) {
		this.seller = seller;
	}
	private void setBuyer(Participant buyer) {
		this.buyer = buyer;
	}
	TransactionInfo setMultiplier(double multiplier) {
		this.multiplier = multiplier;
		return this;
	}
	TransactionInfo setParticipants(Participant player, Participant trader) {
		if (stock.equals(Stock.SELL))
		{
			setBuyer(player);
			setSeller(trader);
		}
		else
		{
			setSeller(player);
			setBuyer(trader);
		}
		return this;
	}
	
	/* Get informations */
	public boolean isStackprice() {
		return item.hasFlag(StackPrice.class);
	}
	
	public int getAmount() {
		return isStackprice() ? amount / item.getAmount() : amount;
	}
	
	public double getMultiplier() {
		return multiplier;
	}
	
	public Stock getStock() {
		return stock;
	}
	
	public Participant getBuyer() {
		return buyer;
	}
	
	public Participant getSeller() {
		return seller;
	}
	
	public double getTotalScaling() {
		return multiplier * getAmount();
	}
	
	public Player getPlayerParticipant() {
		return buyer instanceof PlayerParticipant ? 
				((PlayerParticipant)buyer).getPlayer() 
			  : ((PlayerParticipant)seller).getPlayer();
	}
}
