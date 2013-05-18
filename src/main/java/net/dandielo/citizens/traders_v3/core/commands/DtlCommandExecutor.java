package net.dandielo.citizens.traders_v3.core.commands;

import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;

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
	
	//private static NpcManager traders = DtlTraders.getNpcEcoManager();
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
		
		if ( sender instanceof Player )
		{
		//	tNPC npc = traders.tNPC(sender);
		//	return cManager.execute(name, sender, npc, args);
		}
		return true;
	}
}
