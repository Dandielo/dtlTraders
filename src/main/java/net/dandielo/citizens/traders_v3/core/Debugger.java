package net.dandielo.citizens.traders_v3.core;

import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

/**
 * dTrader Debugger 
 * @author dandielo
 */
public class Debugger {
	/** Debugger prefix */
	private static final String DEBUG;
	
	/** Debugger settings */
	private static DebugLevel debugLevel;
	
	/** ConsoleSender where whe should display those messages */
	private static ConsoleCommandSender sender;
	
	// Static "constructor"
	static
	{
		DEBUG = ChatColor.DARK_PURPLE + "[DEBUG]" + ChatColor.RESET;
		debugLevel = DebugLevel.valueOf(PluginSettings.debugLevel());
		sender = DtlTraders.getInstance().getServer().getConsoleSender();
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
	public static boolean levelEnabled(DebugLevel level)
	{
		return debugLevel.levelEnabled(level);
	}

	/**
	 * Sends a <strong>critical</strong> message to the console
	 */
	public static void critical(Object... args)
	{
		if ( !levelEnabled(DebugLevel.CRITICAL) ) return;
		
		sendMessage(ChatColor.RED, "CRITICAL", ChatColor.GOLD, args);
	}

	/**
	 * Sends a <strong>severe</strong> message to the console
	 */
	public static void high(Object... args)
	{
		if ( !levelEnabled(DebugLevel.HIGH) ) return;
		
		sendMessage(ChatColor.GOLD, "SEVERE", ChatColor.BOLD, args);
	}

	/**
	 * Sends a <strong>normal priority</strong> message to the console
	 */
	public static void normal(Object... args)
	{
		if ( !levelEnabled(DebugLevel.NORMAL) ) return;
		
		sendMessage(ChatColor.YELLOW, "NORMAL", args);
	}

	/**
	 * Sends a <strong>low priority</strong> message to the console
	 */
	public static void low(Object... args)
	{
		if ( !levelEnabled(DebugLevel.LOW) ) return;
		
		sendMessage(ChatColor.AQUA, "LOW", args);
	}

	/**
	 * Sends a <strong>information</strong> message to the console
	 */
	public static void info(Object... args)
	{
		if ( !levelEnabled(DebugLevel.INFO) ) return;
		
		sendMessage(ChatColor.GREEN, "INFO", args);
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

		for( Object arg : args )
		{
			if( arg.getClass().isArray() )
			{
				for( Object o : (Object[]) arg )
				{
					if( o.getClass().isArray() )
					{
						for( Object co : (Object[]) o )
						{
							builder.append(co);
						}
					}
					else
						builder.append(o);
				}

				continue;
			}

			builder.append(arg);
		}

		return builder.toString();
	}
	
	/** Helper function to send messages to console */
	private static void sendMessage(ChatColor color, String prefix, Object... args)
	{
		sender.sendMessage(mergeArgs(DtlTraders.PREFIX, DEBUG, color, "[" + prefix + "] ", ChatColor.RESET, args));
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
