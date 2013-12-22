package net.dandielo.citizens.traders_v3.bukkit;

import net.minecraft.server.v1_7_R1.NBTTagCompound;

import org.bukkit.Bukkit;

/*
 * Information 
 * 
 * All credits to PexAuthors got this chunk of code from them, i hope it's ok :)
 */

/**
 * 
 * 
 * Interface to get versioned obfuscation of CraftBukkit classes
 */
@SuppressWarnings("rawtypes")
public class CraftBukkitInterface {
	private static final String CRAFTBUKKIT_PREFIX = "org.bukkit.craftbukkit";
	private static final String MINECRAFT_NET_PREFIX = "net.minecraft.server";
	private static final String VERSION;

	static {
		Class serverClass = Bukkit.getServer().getClass();
		if (!serverClass.getSimpleName().equals("CraftServer")) {
			VERSION = null;
		} else if (serverClass.getName().equals("org.bukkit.craftbukkit.CraftServer")) {
			VERSION = ".";
		} else {
			String name = serverClass.getName();
			name = name.substring("org.bukkit.craftbukkit".length());
			name = name.substring(0, name.length() - "CraftServer".length());
			VERSION = name;
		}
	}

	private CraftBukkitInterface() {
	}

	/**
	 * Get the versioned class name from a class name without the o.b.c prefix.
	 *
	 * @param simpleName The name of the class without the "org.bukkit.craftbukkit" prefix
	 * @return The versioned class name, or {@code null} if not CraftBukkit.
	 */
	public static String getCBClassName(String simpleName) {
		if (VERSION == null) {
			return null;
		}

		return CRAFTBUKKIT_PREFIX + VERSION + simpleName;
	}

	/**
	 * Get the class from the name returned by passing {@code name} into {@link #getCBClassName(String)}
	 *
	 * @param name The name of the class without the "org.bukkit.craftbukkit" prefix
	 * @return The versioned class, or {@code null} if not CraftBukkit
	 */
	public static Class getCBClass(String name) {
		if (VERSION == null) {
			return null;
		}

		try {
			return Class.forName(getCBClassName(name));
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * Get the versioned class name from a class name without the o.b.c prefix.
	 *
	 * @param simpleName The name of the class without the "org.bukkit.craftbukkit" prefix
	 * @return The versioned class name, or {@code null} if not CraftBukkit.
	 */
	public static String getMNClassName(String simpleName) {
		if (VERSION == null) {
			return null;
		}

		return MINECRAFT_NET_PREFIX + VERSION + simpleName;
	}

	/**
	 * Get the class from the name returned by passing {@code name} into {@link #getCBClassName(String)}
	 *
	 * @param name The name of the class without the "org.bukkit.craftbukkit" prefix
	 * @return The versioned class, or {@code null} if not CraftBukkit
	 */
	public static Class getNMClass(String name) {
		if (VERSION == null) {
			return null;
		}

		try {
			return Class.forName(getMNClassName(name));
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
}