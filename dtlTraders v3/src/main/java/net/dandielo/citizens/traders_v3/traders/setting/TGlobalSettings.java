package net.dandielo.citizens.traders_v3.traders.setting;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.core.PluginSettings;

public class TGlobalSettings extends PluginSettings {
	//trader settings
	protected static Map<ItemStack, Double> specialBlocks = new HashMap<ItemStack, Double>();
	protected static ItemStack mmToogleItem = new ItemStack(Material.STICK);
	protected static boolean leftClickMMToggle = true;
	
	//player trader limits
	protected static int playerTraderLimit = 1;
	 
	//pattern settings
    protected static Map<String, Integer> defaultPatterns = new HashMap<String, Integer>();
    
    //stock settings
    protected static int stockSize = 6;
    protected static String stockNameFormat = "{name}\'s shop";
	protected static String stockDefault = "sell";
    
    //inventory click settings
	protected static Map<String, ItemStack> uiItems = new HashMap<String, ItemStack>();
    protected static boolean doubleClick = true;
    
    //load global settings
    public static void initGlobalSettings()
    {
    	uiItems.put("sell", new ItemStack(Material.WOOL, 1, (short) 1));
    	uiItems.put("buy", new ItemStack(Material.WOOL, 1, (short) 2));
    	uiItems.put("back", new ItemStack(Material.WOOL, 1, (short) 14));
    	uiItems.put("price", new ItemStack(Material.WOOL, 1, (short) 15));
    	uiItems.put("limit", new ItemStack(Material.WOOL, 1, (short) 3));
    }
    
    //static methods
    public static String getGlobalStockNameFormat()
    {
    	return stockNameFormat;
    }
    
    public static int getGlobalStockSize()
    {
    	return stockSize;
    }
    
    public static boolean dClickEvent()
    {
    	return doubleClick;
    }
    
    public static Map<String, ItemStack> getUiItems()
    {
    	return uiItems;
    }
}
