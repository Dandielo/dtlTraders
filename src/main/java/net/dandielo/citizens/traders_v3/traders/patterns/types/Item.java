package net.dandielo.citizens.traders_v3.traders.patterns.types;

import org.bukkit.configuration.ConfigurationSection;

import net.dandielo.citizens.traders_v3.traders.patterns.Pattern;

public class Item extends Pattern {

	protected Item(String name, Type type)
	{
		super(name, type);
	}

	@Override
	public void loadItems(ConfigurationSection data)
	{
	}

}
