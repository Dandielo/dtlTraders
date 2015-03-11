package net.dandielo.citizens.traders_v3.core.commands;

import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.dandielo.citizens.traders_v3.TradingEntity;
import net.dandielo.citizens.traders_v3.tNpcManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DtlCommandExecutor implements CommandExecutor {
	public static CommandManager cManager;
	public static Citizens citizens;
	
	public DtlCommandExecutor(CommandManager manager)
	{
		cManager = manager;
		citizens = (Citizens) CitizensAPI.getPlugin();
	}
	
	private static tNpcManager manager = tNpcManager.instance();
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
		
		if ( sender instanceof Player )
		{
	    	TradingEntity npc = manager.getRelation(sender.getName(), TradingEntity.class);
			return cManager.execute(name, args, sender, npc);
		}
		return true;
	}
}
