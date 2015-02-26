package net.dandielo.citizens.traders_v3.traders.wallet;

import java.util.List;

import net.dandielo.citizens.traders_v3.traders.stock.StockItem;

import org.bukkit.entity.Player;

public interface TransactionHandler {
	public boolean onCompleteTransaction(Player player, StockItem item, String stock, int amount);
	public boolean onCheckTransaction(Player player, StockItem item, String stock, int amount);
	public void onPriceLoreRequest(Player player, StockItem item, String stock, int amount, List<String> lore);
}
