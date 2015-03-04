package net.dandielo.citizens.traders_v3.traders.transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.patterns.Pattern;
import net.dandielo.citizens.traders_v3.traders.patterns.PatternManager;
import net.dandielo.citizens.traders_v3.traders.patterns.types.PricePattern;
import net.dandielo.citizens.traders_v3.traders.patterns.types.PricePattern.ItemCurrencies;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.traders.transaction.participants.PlayerParticipant;
import net.dandielo.citizens.traders_v3.traders.transaction.participants.TraderParticipant;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

public class ShopSession {
	private Trader trader;
	private Settings settings;
	private Player player;
	
	private Map<String, Map<StockItem, ItemCurrencies>> cache = new HashMap<String, Map<StockItem, ItemCurrencies>>();

	/** Create a new session between a player and a trader
	 * 
	 * <p>This session type allows to manipulate on a players and traders.</p> 
	 * @param trader
	 * @param player
	 */
	public ShopSession(Trader trader, Player player)
	{
		this.trader = trader;
		this.settings = trader.getSettings();
		this.player = player;
	}
	
	/** Create a new session between a player and a trader
	 * 
	 * <p>This session allows only to get descriptions from currencies.</p> 
	 * @param settings
	 * @param player
	 */
	public ShopSession(Settings settings, Player player)
	{
		this.trader = null;
		this.settings = settings;
		this.player = player;
	}
	
	public ItemCurrencies getCurrencies(String stock, StockItem item) {
		if (!cache.containsKey(stock))
			cache.put(stock, new HashMap<StockItem, ItemCurrencies>());
		
		//check the cache
		Map<StockItem, ItemCurrencies> cachedStock = cache.get(stock);
		if (cachedStock.containsKey(item))
			return cachedStock.get(item);
		
		//find the currencies
		ItemCurrencies currencies = new ItemCurrencies();
		List<Pattern> patterns = PatternManager.getPatterns(settings.getPatterns());
		
		for (Pattern pattern : patterns)
			if (pattern instanceof PricePattern)
				currencies.merge(((PricePattern) pattern).getItemCurrency(player, stock, item));
		currencies.resetPriorities();
		for (ItemAttr attr : item.getAttribs("p"))
			if (attr instanceof CurrencyHandler)
				currencies.merge((CurrencyHandler) attr, 0);
		
		//set the cache
		cachedStock.put(item, currencies);
		return currencies;
	}
	
	public List<String> getDescription(String stock, StockItem item, int amount) {
		ItemCurrencies currencies = getCurrencies(stock, item);
		List<String> result = new ArrayList<String>();
		for (CurrencyHandler handler : currencies.getCurrencies())
		{
			handler.getDescription(
				new TransactionInfo(stock, item, amount)
					.setParticipants(new PlayerParticipant(player), new TraderParticipant(trader))
					.setMultiplier(currencies.getMultiplier())
			, result);
		}
		if (result.size() > 0)
		{
			if (stock.equals("sell"))
			{
				result.addAll(0, LocaleManager.locale.getLore("item-price-list"));
				result.addAll(LocaleManager.locale.getLore("item-" + stock));
			}
			else
			{
				result.addAll(0, LocaleManager.locale.getLore("item-worth-list"));
				result.addAll(LocaleManager.locale.getLore("item-player-" + stock));
			}
		}
		return result;
	}
	
	public boolean allowTransaction(String stock, StockItem item, int amount) {
		if (trader == null) return false;
		ItemCurrencies currencies = getCurrencies(stock, item);
		boolean result = currencies.getCurrencies().size() > 0;
		for (CurrencyHandler handler : currencies.getCurrencies())
		{
			result &= handler.allowTransaction(
				new TransactionInfo(stock, item, amount)
					.setParticipants(new PlayerParticipant(player), new TraderParticipant(trader))
					.setMultiplier(currencies.getMultiplier())
			);
		}
		return result;
	}
	
	public boolean finalizeTransaction(String stock, StockItem item, int amount) {
		if (trader == null) return false;
		ItemCurrencies currencies = getCurrencies(stock, item);
		boolean result = currencies.getCurrencies().size() > 0;
		for (CurrencyHandler handler : currencies.getCurrencies())
		{
			result &= handler.finalizeTransaction(
				new TransactionInfo(stock, item, amount)
					.setParticipants(new PlayerParticipant(player), new TraderParticipant(trader))
					.setMultiplier(currencies.getMultiplier())
			);
		}
		return result;
	}
	
	public double getCurrencyValue(String stock, StockItem item, int amount, Class<? extends CurrencyHandler> clazz) {
		CurrencyHandler result = null;
		ItemCurrencies currencies = getCurrencies(stock, item);
		Iterator<CurrencyHandler> it = currencies.getCurrencies().iterator();
		while(it.hasNext() && result == null)
		{
			result = it.next();
			if (!clazz.isInstance(result))
				result = null;
		}
		return result != null ? 
				result.getTotalPrice(
					new TransactionInfo(stock, item, amount)
						.setParticipants(new PlayerParticipant(player), new TraderParticipant(trader))
						.setMultiplier(currencies.getMultiplier())
				)
				/* else */ : 0.0;
	}
}
