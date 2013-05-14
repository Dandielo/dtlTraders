package net.dandielo.citizens.traders_v3.traders.setting;

public class GlobalSettings {
	//pattern settings
    protected static String pattern;
    
    //stock settings
    protected static int stockSize = 6;
    protected static String stockNameFormat = "{name}'s shop";
	protected static String stockDefault = "sell";
    
    //event settings
    protected static boolean doubleClicks;
    
    //static methods
    public static String getGlobalStockNameFormat()
    {
    	return stockNameFormat;
    }
    
    public static int getGlobalStockSize()
    {
    	return stockSize;
    }
}
