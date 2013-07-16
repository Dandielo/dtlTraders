package net.dandielo.citizens.traders_v3.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.permission.Permission;

public class Perms {
	public final static Perms perms = new Perms(); 
	
	//class definition
	private Permission permission = null;
	
	private Perms()
	{
		initPerms();
	}
	
	private void initPerms()
	{
		RegisteredServiceProvider<Permission> permissionProvider = DtlTraders.getInstance().getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null)
        {
            permission = permissionProvider.getProvider();
        	DtlTraders.info("Permission plugin: " + ChatColor.YELLOW + permission.getName());
        }
        else
        {
        	//lacks some essential functions for permissions
        	DtlTraders.info("Permission plugin not found! Not all functions will be available");
        }
	}
	
	public boolean has(CommandSender sender, String perm)
	{
		return permission != null ? permission.has(sender, perm) : sender.hasPermission(perm);
	}
	
	public static boolean hasPerm(CommandSender sender, String perm)
	{
		return perms.has(sender, perm);
	}
}
