package net.dandielo.citizens.traders_v3.traders.wallet;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

public class ItemPricing {
	private StockItem sItem;
	private Player player;
	private String stock;
	
	public ItemPricing(Player p, String stck, StockItem item)
	{
		player = p;
		sItem = item;
		stock = stck;
	}
	
	public List<String> getFullPriceDescription(int amount) {
		List<String> result = new ArrayList<String>();
		for (ItemAttr attr : sItem.getAttribs("p"))
		{
			TransactionHandler tHandler = (TransactionHandler) attr;
			tHandler.onPriceLoreRequest(player, sItem, stock, amount, result);
		}
		return result;
	}
	
	public boolean onPriceCheckRequest(int amount) {
		boolean result = sItem.getAttribs("p").size() > 0;
		for (ItemAttr attr : sItem.getAttribs("p"))
		{
			TransactionHandler tHandler = (TransactionHandler) attr;
			result = result && tHandler.onCheckTransaction(player, sItem, stock, amount);
		}
		return result;
	}
	
	public boolean tryCompleteTransaction(int amount) {
		boolean result = sItem.getAttribs("p").size() > 0;
		for (ItemAttr attr : sItem.getAttribs("p"))
		{
			TransactionHandler tHandler = (TransactionHandler) attr;
			result = result && tHandler.onCompleteTransaction(player, sItem, stock, amount);
		}
		return result;
	}
}
