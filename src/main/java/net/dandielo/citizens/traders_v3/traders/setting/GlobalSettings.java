package net.dandielo.citizens.traders_v3.traders.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.PluginSettings;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.core.tools.StringTools;
import net.dandielo.citizens.traders_v3.traders.limits.LimitManager;
import net.dandielo.citizens.traders_v3.utils.ItemUtils;

public class GlobalSettings extends PluginSettings {
	//the trader soncig section
	protected static ConfigurationSection tConfig;

	//transaction settings
	protected static boolean doubleClick;
	
	//manage settings
	protected static Map<ItemStack, Double> specialBlocks = new HashMap<ItemStack, Double>();
	protected static Map<ItemStack, Long> timeoutBlocks = new HashMap<ItemStack, Long>();
	protected static String mmStockStart;
	protected static ItemStack mmItemToggle; 
	protected static boolean mmRightToggle; 
	protected static boolean mmEnableDamage;

	//stock settings
	protected static int stockSize;
	protected static String stockNameFormat;
	protected static String stockStart;

	//wallet settings
	protected static String walletType;
	protected static double walletMoney;

	//pattern settings
	protected static List<String> defaultPatterns;// = new ArrayList<String>();
	protected static String patternFile;

	//player trader settings
	protected static int playerTraderLimit;
	protected static int playerStockSize;
	protected static String playerStockNameFormat;
	
	//UI settings
	protected static Map<String, ItemStack> uiItems = new HashMap<String, ItemStack>();

	//load global settings
	@SuppressWarnings("unchecked")
	public static void initGlobalSettings()
	{
		//debug info
		dB.info("Loading general trader configuration");
		
		//get trader section
		tConfig = config.getConfigurationSection("trader");

		//load transaction settings
		doubleClick = tConfig.getBoolean("transaction.double-click", false);
	//	logEnabled
	//	logFormat
		
		
		//load managing settings
		mmStockStart = tConfig.getString("managing.start-stock", "sell");
		mmItemToggle = ItemUtils.createItemStack(tConfig.getString("managing.item", "air"));
		mmRightToggle = tConfig.getBoolean("managing.right-click", false);

		try
		{
			List<String> specials = (List<String>) tConfig.getList("managing.special-blocks");
			for ( String entry : specials )
			{
				String[] data = entry.split(" ");
				specialBlocks.put(ItemUtils.createItemStack(data[0]), Double.parseDouble(data[1]));
			}
		}
		catch(Exception e)
		{
			//debug high
			dB.high("While loading special blocks, a exception occured");
			dB.high("Exception: ", e.getClass().getSimpleName());
			
			//debug normal
			dB.normal("Exception message: ", e.getMessage());
			dB.normal("StackTrace: ", e.getStackTrace());
		}

		try
		{
			List<String> specials = (List<String>) tConfig.getList("managing.time-blocks");
			for ( String entry : specials )
			{
				String[] data = entry.split(" ", 2);
				timeoutBlocks.put(ItemUtils.createItemStack(data[0]), LimitManager.parseTimeout(data[1]));
			}
		}
		catch(Exception e)
		{
			//debug high
			dB.high("While loading timeout-blocks blocks, a exception occured");
			dB.high("Exception: ", e.getClass().getSimpleName());
			
			//debug normal
			dB.normal("Exception message: ", e.getMessage());
			dB.normal("StackTrace: ", StringTools.stackTrace(e.getStackTrace()));
		}
		
		//load stock settings
		stockStart = tConfig.getString("stock.start-stock", "sell");
		stockSize = tConfig.getInt("stock.size", 6);
		stockNameFormat = tConfig.getString("stock.format", "{npc}\'s shop");

		//load wallet settings
		walletType = tConfig.getString("wallet.type", "infinite");
		walletMoney = tConfig.getDouble("wallet.money", 0.0);

		//load pattern settings
		try
		{
			defaultPatterns = (List<String>) tConfig.getList("pattern.defaults", new ArrayList<String>());
			patternFile = tConfig.getString("pattern.file", "patterns.yml");
		}
		catch(Exception e)
		{
			//debug high
			dB.high("While loading pattern defaults, a exception occured");
			dB.high("Exception: ", e.getClass().getSimpleName());
			
			//debug normal
			dB.normal("Exception message: ", e.getMessage());
			dB.normal("StackTrace: ", StringTools.stackTrace(e.getStackTrace()));
		}
		
		//load books settings
	//	bookFile
	//	bookSaving

		//load player trader settings
		playerTraderLimit = tConfig.getInt("player.limit", 1);
		playerStockSize = tConfig.getInt("player.size", 6);
		playerStockNameFormat = tConfig.getString("player.format", "{npc}\'s shop");

		//load UI settings
		uiItems.put("sell", asUIItem("ui.sell", "wool:1"));
		uiItems.put("buy", asUIItem("ui.buy", "wool:2"));
		uiItems.put("back", asUIItem("ui.back", "wool:14"));
		uiItems.put("price", asUIItem("ui.price", "wool:15"));
		uiItems.put("limit", asUIItem("ui.limit", "wool:3"));
		uiItems.put("plimit", asUIItem("ui.plimit", "wool:6"));
		uiItems.put("lock", asUIItem("ui.lock", "wool:4"));
		uiItems.put("unlock", asUIItem("ui.unlock", "wool:5"));
		
		//load denizen settings
	}
	
	/**
	 * Create as item used for the ui, this item will contain a name and lore with a description for the given option that it handles
	 * @param ID
	 * the item ID 
	 * @param defID
	 * the default ID
	 * @return
	 * the ready UI item
	 */
	private static ItemStack asUIItem(String ID, String defID)
	{
		//create the item
		ItemStack item = ItemUtils.createItemStack(tConfig.getString(ID, defID));
		ItemMeta meta = item.getItemMeta();
		
		//get the name and lore for the item
		meta.setDisplayName(LocaleManager.locale.getName(ID.substring(3)));
		meta.setLore(LocaleManager.locale.getLore(ID.substring(3)));
		
		//set the new meta
		item.setItemMeta(meta);
		
		//return the UI item
		return item;
	}

	/**
	 * @return
	 * the default format for stock names
	 */
	public static String getGlobalStockNameFormat()
	{
		return stockNameFormat;
	}
	
	/**
	 * @return
	 * the default starting stock for each trader
	 */
	public static String getGlobalStockStart()
	{
		return stockStart;
	}

	/**
	 * @return
	 * default stock size
	 */
	public static int getGlobalStockSize()
	{
		return stockSize;
	}
	
	/**
	 * @return
	 * true if toggling manager mode should be done with right click instead of left click
	 */
	public static boolean mmRightToggle()
	{
		return mmRightToggle;
	}
	
	/**
	 * @return
	 * true if the trader can be damaged by players
	 */
	public static boolean mmEnableDamage()
	{
		return mmEnableDamage;
	}
	
	/**
	 * @return
	 * the item that should be used for toggling. If air is set any item is valid.
	 */
	public static ItemStack mmItemToggle()
	{
		return mmItemToggle;
	}

	/**
	 * @return
	 * true if players need to min double click on an item to buy it
	 */
	public static boolean dClickEvent()
	{
		return doubleClick;
	}

	/**
	 * @param item
	 * checks the item if its a special block
	 * @return
	 * the special block value, or 1 if no block was found
	 */
	public static double getBlockValue(ItemStack item)
	{
		ItemStack tempItem = new ItemStack(item.getType());
		tempItem.setAmount(1);
		return specialBlocks.containsKey(tempItem) ? specialBlocks.get(tempItem) : 1.0;
	}
	
	/**
	 * @param item
	 * checks the item if its a special block
	 * @return
	 * the special block value, or 1 if no block was found
	 */
	public static long getBlockTimeoutValue(ItemStack item)
	{
		ItemStack tempItem = new ItemStack(item.getType());
		tempItem.setAmount(1);
		return timeoutBlocks.containsKey(tempItem) ? timeoutBlocks.get(tempItem) : 1;
	}
	
	/**
	 * gets all declared items to use in trader stock UI 
	 * @return
	 */
	public static Map<String, ItemStack> getUiItems()
	{
		return uiItems;
	}

	public static String getDefaultWallet()
	{
		return walletType;
	}
	
	public static double getWalletStartBalance()
	{
		return walletMoney;
	}
	
	public static String getPatternFile()
	{
		return patternFile;
	}
	
	public static List<String> defaultPatterns()
	{
		return defaultPatterns;
	}
}
