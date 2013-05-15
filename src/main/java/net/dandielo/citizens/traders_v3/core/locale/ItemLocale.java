package net.dandielo.citizens.traders_v3.core.locale;

import java.util.ArrayList;
import java.util.List;

public class ItemLocale {
	private String name;
	private List<String> lore;
	
	public ItemLocale(String name, List<String> lore)
	{
		this.name = name;
		this.lore = lore;
		if ( this.lore == null )
			this.lore = new ArrayList<String>();
	}
	
	public String name()
	{
		return name;
	}
	
	public List<String> lore()
	{
		return lore;
	}
}
