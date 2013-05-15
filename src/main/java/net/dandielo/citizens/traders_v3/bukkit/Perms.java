package net.dandielo.citizens.traders_v3.bukkit;

import org.bukkit.entity.Player;
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
        	DtlTraders.info("Permission plugin: " + permission.getName());
        }
        else
        {
        	//lacks some essential functions for permissions
        	DtlTraders.info("Permission plugin not found! Not all functions will be available");
        }
	}
	
	public boolean has(Player player, String perm)
	{
		return permission != null ? permission.has(player, perm) : player.hasPermission(perm);
	}
}
