package net.dandielo.citizens.traders_v3.utils.items.attributes;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.tNpcStatus;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeInvalidValueException;
import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeValueNotFoundException;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.traders.wallet.TransactionHandler;
import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemAttr;

@Attribute(
		name = "Player Resources Currency", 
		key = "p", sub = {"h", "f", "e", "l"}, 
		standalone = true, priority = 0,
		status = {tNpcStatus.BUY, tNpcStatus.SELL, tNpcStatus.SELL_AMOUNTS, tNpcStatus.MANAGE_PRICE})
public class PlayerResourcesCurrency extends ItemAttr implements TransactionHandler {
	private int experience;
	private double health; 
	private int level;
	private int food;

	public PlayerResourcesCurrency(String key, String sub) {
		super(key, sub);
	}

	@Override
	public boolean onCompleteTransaction(Player player, StockItem item, String stock, int amount) {
		if (stock.equals("sell"))
		{
			if (getSub().equals("h"))
				player.setHealth(player.getHealth() - health * ((double)amount));
			if (getSub().equals("f"))
				player.setFoodLevel(player.getFoodLevel() - food * amount);
			if (getSub().equals("e"))
				giveSilentExperience(player, -experience * amount);
			if (getSub().equals("l"))
				player.setLevel(player.getLevel() - level * amount);
		}
		else if (stock.equals("buy"))
		{
			if (getSub().equals("h"))
			{
				double hp = health * amount + player.getHealth();
				player.setHealth(hp > player.getMaxHealth() ? player.getMaxHealth() : hp);
			}
			if (getSub().equals("f"))
			{
				int fd = food * amount + player.getFoodLevel();
				player.setFoodLevel(fd > 20 ? 20 : fd);
			}
			if (getSub().equals("e"))
			{
				giveSilentExperience(player, experience * amount);
			}
			if (getSub().equals("l"))
			{
				player.setLevel(player.getLevel() + level * amount);
			}
		}
		return true;
	} 

	@Override
	public boolean onCheckTransaction(Player player, StockItem item, String stock, int amount) {
		if (stock.equals("sell"))
		{
			if (getSub().equals("h"))
				return player.getHealth() > (((double)amount) * health);
			if (getSub().equals("f"))
				return player.getFoodLevel() >= food * amount;
			if (getSub().equals("e"))
				return getTotalExperience(player) >= amount * experience;	
			if (getSub().equals("l"))
				return player.getLevel() >= amount * level;					
		}
		else if (stock.equals("buy")) 
		{
			return true;
		}
		return false;
	}

	@Override
	public void onPriceLoreRequest(Player player, StockItem item, String stock, int amount, List<String> lore) {
		LocaleManager lm = LocaleManager.locale; 
		for (String lLine : lm.getLore("item-currency-price"))
		{
			if (getSub().equals("h"))
			{
				lore.add(
						lLine
						.replace("{amount}", String.valueOf(amount * health))
						.replace("{text}", " ").replace("{currency}", lm.getKeyword("health-points"))
						);
			}
			if (getSub().equals("f"))
			{
				lore.add(
						lLine
						.replace("{amount}", String.valueOf(amount * food))
						.replace("{text}", " ").replace("{currency}", lm.getKeyword("food-level"))
						);
			}
			if (getSub().equals("e"))
			{
				lore.add(
						lLine
						.replace("{amount}", String.valueOf(amount * experience))
						.replace("{text}", " ").replace("{currency}", lm.getKeyword("experience"))
						);
			}
			if (getSub().equals("l"))
			{
				lore.add(
						lLine
						.replace("{amount}", String.valueOf(amount * level))
						.replace("{text}", " ").replace("{currency}", lm.getKeyword("level"))
						);
			}
		}
	}

	@Override
	public void onLoad(String data) throws AttributeInvalidValueException {
		if (data == "") throw new AttributeInvalidValueException(getInfo(), data);
		if (getSub().equals("h"))
			health = Double.parseDouble(data);
		if (getSub().equals("f"))
			food = Integer.parseInt(data);
		if (getSub().equals("e"))
			experience = Integer.parseInt(data);
		if (getSub().equals("l"))
			level = Integer.parseInt(data);
	}

	@Override
	public String onSave() {
		if (getSub().equals("h"))
			return String.valueOf(health);
		if (getSub().equals("f"))
			return String.valueOf(food);
		if (getSub().equals("e"))
			return String.valueOf(experience);
		if (getSub().equals("l"))
			return String.valueOf(level);
		return "";
	}

	@Override
	public void onFactorize(ItemStack item)
			throws AttributeValueNotFoundException {
		throw new AttributeValueNotFoundException();
	}
	
	//Experience helpers
	// with help from Denizen authors
	// https://github.com/DenizenScript/Denizen-For-Bukkit/blob/master/src/main/java/net/aufdemrand/denizen/scripts/commands/player/ExperienceCommand.java
	public static int getTotalExperience(Player p) {
		return getTotalExperience(p.getLevel(), p.getExp());
	}
	
	public static int getTotalExperience(int level, double bar) {
		return getTotalExpToLevel(level) + (int) (getExpToLevel(level + 1) * bar + 0.5);
	}

	public static int getExpToLevel(int level) {
		if (level < 16) {
			return 17;
		}
		else if (level < 31) {
			return 3 * level - 31;
		}
		else {
			return 7 * level - 155;
		}
	}
	
	public static int getTotalExpToLevel(int level) {
		if (level < 16) {
			return 17 * level;
		}
		else if (level < 31) {
			return (int) (1.5 * level * level - 29.5 * level + 360 );
		}
		else {
			return (int) (3.5 * level * level - 151.5 * level + 2220);
		}
	}

	public static void resetExperience(Player player) {
		player.setTotalExperience(0);
		player.setLevel(0);
		player.setExp(0);
	}

	/* Tail recursive way to count the level for the given exp, maybe better with iteration */
	public static int countLevel(int exp, int toLevel, int level) {
		if (exp < toLevel) {
			return level;
		}
		else {
			return countLevel(exp - toLevel, getTotalExpToLevel(level + 2) - getTotalExpToLevel(level + 1), ++level);
		}
	}

	/* Adding experience using the setExp and setLevel methods, should be soundless (not tested) */
	public static void giveSilentExperience(Player player, int exp) {
		final int currentExp = getTotalExperience(player);
		resetExperience(player);
		final int newexp = currentExp + exp;
		
		if (newexp > 0) {
			final int level = countLevel(newexp, 17, 0);
			player.setLevel(level);
			final int epxToLvl = newexp - getTotalExpToLevel(level);
			player.setExp(epxToLvl < 0 ? 0.0f : (float)epxToLvl / (float)getExpToLevel(level + 1));
		}
	}
}
