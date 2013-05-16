package net.dandielo.citizens.traders_v3.core;

import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

/**
 * dTrader Debugger 
 * @author dandielo
 */
public class Debugger {
	//private singleton instance
	private static Debugger debugger = new Debugger();
	
	//debugger settings
	private DebugLevel debugLevel = DebugLevel.valueOf(PluginSettings.debugLevel());
	
	//ConsoleSender where whe should display those messages
	private ConsoleCommandSender sender = DtlTraders.getInstance().getServer().getConsoleSender();
	
	/**
	 * Debugger instance
	 * @author dandielo
	 */
	private Debugger()
	{
	}
	
	/**
	 * Checks if the provided debug level is enabled for showing. 
	 * @param level
	 *     level to check
	 * @return
	 *     true if the level can be showed, false otherwise
	 *     
	 * @author dandielo
	 */
	public boolean levelEnabled(DebugLevel level)
	{
		return debugLevel.levelEnabled(level);
	}

	/**
	 * Sends a <strong>critical</strong> message to the console
	 */
	public static void critical(Object... args)
	{
		if ( debugger.levelEnabled(DebugLevel.CRITICAL) )
		{
		//	debugger.sender.sendMessage(mergeArgs(ChatColor.RED, DtlTraders.PREFIX, "[CRITICAL]", ChatColor.GOLD, "-----------------"));
			debugger.sender.sendMessage(mergeArgs(ChatColor.RED, DtlTraders.PREFIX, "[CRITICAL]", ChatColor.GOLD, args));
		//	debugger.sender.sendMessage(mergeArgs(ChatColor.RED, DtlTraders.PREFIX, "[CRITICAL]", ChatColor.GOLD, "-----------------"));
		}
	}

	/**
	 * Sends a <strong>severe</strong> message to the console
	 */
	public static void high(Object... args)
	{
		if ( debugger.levelEnabled(DebugLevel.HIGH) )
		{
			debugger.sender.sendMessage(mergeArgs(ChatColor.GOLD, DtlTraders.PREFIX, "[SEVERE]", ChatColor.WHITE, args));
		}
	}

	/**
	 * Sends a <strong>normal priority</strong> message to the console
	 */
	public static void normal(Object... args)
	{
		if ( debugger.levelEnabled(DebugLevel.NORMAL) )
		{
			debugger.sender.sendMessage(mergeArgs(ChatColor.YELLOW, DtlTraders.PREFIX, "[NORMAL]", ChatColor.WHITE, args));
		}
	}

	/**
	 * Sends a <strong>low priority</strong> message to the console
	 */
	public static void low(Object... args)
	{
		if ( debugger.levelEnabled(DebugLevel.LOW) )
		{
			debugger.sender.sendMessage(mergeArgs(ChatColor.AQUA, DtlTraders.PREFIX, "[LOW]", ChatColor.WHITE, args));
		}
	}

	/**
	 * Sends a <strong>information</strong> message to the console
	 */
	public static void info(Object... args)
	{
		if ( debugger.levelEnabled(DebugLevel.INFO) )
		{
			debugger.sender.sendMessage(mergeArgs(ChatColor.GREEN, DtlTraders.PREFIX, "[INFO]", ChatColor.WHITE, args));
		}
	}
	
	/**
	 * Merges the given arguments into one string, this allows to add any Object to the string without any problems. 
	 * @param args
	 *     given arguments
	 * @return
	 *     merged string
	 * @author dandielo
	 */
	private static String mergeArgs(Object... args)
	{
		StringBuilder builder = new StringBuilder();
		for ( Object arg : args )
		{
			builder.append(arg);
			if ( !arg.equals(args[args.length-1]) )
			    builder.append(" ");
		}
		return builder.toString();
	}
	
	/**
	 * DebugLevel enum, allows easily to control debug information
	 * @author dandielo
	 */
	public static enum DebugLevel
	{
		 NONE, CRITICAL, HIGH, NORMAL, LOW, INFO;
		 
		 boolean showCritical()
		 {
			 return showHigh() || equals(CRITICAL);
		 }
		 
		 boolean showHigh()
		 {
			 return showNormal() || equals(HIGH);
		 }
		 
		 boolean showNormal()
		 {
			 return showLow() || equals(NORMAL);
		 }
		 
		 boolean showLow()
		 {
			 return showInfo() || equals(LOW);
		 }
		 
		 boolean showInfo()
		 {
			 return equals(INFO);
		 }
		 
		 boolean levelEnabled(DebugLevel level)
		 {
			 switch(level)
			 {
			 case NONE:
				 return false;
			 case CRITICAL:
				 return showCritical();
			 case HIGH:
				 return showHigh();
			 case NORMAL:
				 return showNormal();
			 case LOW:
				 return showLow();
			 case INFO:
				 return showInfo();
			 }
			 return false;
		 }
	}
}
