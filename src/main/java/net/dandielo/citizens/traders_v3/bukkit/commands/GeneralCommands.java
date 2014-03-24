package net.dandielo.citizens.traders_v3.bukkit.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.PluginSettings;
import net.dandielo.citizens.traders_v3.core.commands.Command;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.patterns.PatternManager;
import net.dandielo.citizens.traders_v3.traders.setting.GlobalSettings;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * General commands for this plugin. These commands are not really linked with any tNpc type or it's settings. 
 * @author dandielo
 *
 */
public class GeneralCommands {
	/**
	 * locale manager static instance
	 */
	private static LocaleManager locale = LocaleManager.locale;
	
	@Command(
	name = "trader",
	syntax = "",
	perm = "dtl.trader.commands",
	desc = "shows plugin version, and trader statistics if any is selected",
	npc = false)
	public void trader(DtlTraders plugin, CommandSender sender, Trader npc, Map<String, String> args)
	{
		//send the plugin name and version
		locale.sendMessage(sender, "plugin-command-message", "version", plugin.getDescription().getVersion(), "name", plugin.getName());
		
		//if an NPC is selected
		if ( npc != null )
		{
			//Send trader information
			locale.sendMessage(sender, "key-value", "key", "#type", "value", "#trader-" + npc.getSettings().getType());
			locale.sendMessage(sender, "key-value", "key", "#owner", "value", npc.getSettings().getOwner());
			locale.sendMessage(sender, "key-value", "key", "#stock-name", "value", npc.getSettings().getStockName());
			locale.sendMessage(sender, "key-value", "key", "#stock-start", "value", npc.getSettings().getStockStart());
			locale.sendMessage(sender, "key-value", "key", "#stock-size", "value", String.valueOf(npc.getSettings().getStockSize()));	
		}
	}
	
	@Command(
	name = "trader",
	syntax = "reload",
	perm = "dtl.trader.commands.reload",
	desc = "reloads the locale and all global settings",
	npc = false)
	public void traderReload(DtlTraders plugin, CommandSender sender, Trader npc, Map<String, String> args)
	{
		//reload the general config file
		plugin.reloadConfig();
		
		//reload global settings
		PluginSettings.initPluginSettings();
		GlobalSettings.initGlobalSettings();
		
		//reload all patterns
		PatternManager.instance.reload();
		
		//reload the locale
		locale.load();
		locale.sendMessage(sender, "plugin-reload");
	}
	
	//Commands description holder
	private static List<Command> commands = new ArrayList<Command>(); 
	
	/**
	 * Registers a command for the help command 
	 * @param type
	 * @param command
	 */
	public static void registerCommandInfo(Command command)
	{
		List<Command> list = commands;
		if ( list == null )
			list = new ArrayList<Command>();
		list.add(command);
	}
	
	@Command(
	name = "trader",
	syntax = "help",
	desc = "allows to get information about all trader commands",
	perm = "dtl.trader.commands.help",
	npc = false)
	public void traderHelpBasic(DtlTraders plugin, CommandSender sender, Trader npc, Map<String, String> args)
	{
		List<Command> cmds = commands;
		
	    if ( cmds == null )
			dB.high("Command informations are not loaded");
		
		sender.sendMessage(ChatColor.GOLD + "== " + ChatColor.YELLOW + "Trader commands" + ChatColor.GOLD + " ==");
		sender.sendMessage("");
		
		for ( Command cmd : cmds )
			sender.sendMessage(nameAndSyntax(cmd));
	}
/*	
	//Type help command
	@Command(
	name = "trader",
	syntax = "help --all (page)",
	desc = "allows to get information about all trader commands",
	perm = "dtl.trader.commands.help",
	npc = false)
	public void traderHelp(DtlTraders plugin, CommandSender sender, NPC npc, Map<String, String> args)
	{
		List<Command> cmds = commands.get("trader");
		
		if ( cmds == null )
			sender.sendMessage(ChatColor.RED + "No commands are registered for this type");

		//Getting the page
		int page = 1;
		try
		{
			if ( args.containsKey("page") )
				page = Integer.parseInt(args.get("page"));
		} 
		catch (NumberFormatException e)
		{
			page = 1;
		}
		int overall = ( cmds.size() / 4 ) + (cmds.size() % 4 == 0 ? 0 : 1);
		
		if ( page < 1 || page > overall )
			page = 1;
		
		
		sender.sendMessage(ChatColor.GOLD + "== " + ChatColor.YELLOW + "Trader commands, page: " + ChatColor.AQUA + page + ChatColor.YELLOW + "/" + ChatColor.DARK_AQUA + overall + ChatColor.GOLD + " ==");
		sender.sendMessage("");
		
		int i = 0;
		for ( Command cmd : cmds )
		{
			if ( i >= (page-1)*4 && i < (page)*4 )
			{
				sender.sendMessage(nameAndSyntax(cmd));
				sender.sendMessage(perm(cmd));
				sender.sendMessage(description(cmd));
				if ( !cmd.usage().isEmpty() )
					sender.sendMessage(usage(cmd));
				sender.sendMessage(npc(cmd));
				sender.sendMessage("");
			}
			++i;
		}
	}*/
	
/*
	private static String npc(Command cmd)
	{
		return ChatColor.GOLD + "Npc required: " + ChatColor.YELLOW + String.valueOf(cmd.npc());
	}
	
	private static String perm(Command cmd)
	{
		return ChatColor.GOLD + "Permission: " + ChatColor.YELLOW + cmd.perm();
	}

	private static String usage(Command cmd) {
		return cmd.usage().isEmpty() ? "" : ChatColor.GOLD + "Usage: " + ChatColor.YELLOW + cmd.usage();
	}
	
	private static String description(Command cmd) {
		return ChatColor.GOLD + "Description: " + ChatColor.YELLOW + ( cmd.desc().isEmpty() ? ChatColor.RED + "none" : cmd.desc() );
	}*/

	private static String nameAndSyntax(Command cmd)
	{
		return ChatColor.GOLD + "Command: " + ChatColor.YELLOW + cmd.name() + " " + cmd.syntax();
	}
}
