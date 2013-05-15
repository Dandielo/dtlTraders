package net.dandielo.citizens.traders_v3.core.locale;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class LocaleUpdater {
	private Map<LocaleEntry, String> cache;
	private Map<LocaleEntry, String> keywords;
	private Map<LocaleEntry, ItemLocale> lores;
	
	private static final String ver = LocaleManager.pver;
	
	public LocaleUpdater(Configuration configuration) 
	{
		configuration.options().pathSeparator(LocaleManager.PATH_SEPARATOR);
		
		cache = new HashMap<LocaleEntry, String>();
		keywords = new HashMap<LocaleEntry, String>();
		lores = new HashMap<LocaleEntry, ItemLocale>();
		
		ConfigurationSection section = configuration.getConfigurationSection(LocaleManager.buildPath("messages"));
		if ( section != null )
		for ( String key : section.getKeys(false) )
			cache.put(new LocaleEntry(key, section.getString(LocaleManager.buildPath(key, "new"), ""), ver), section.getString(LocaleManager.buildPath(key, "message")));
		
		section = configuration.getConfigurationSection(LocaleManager.buildPath("keywords"));
		if ( section != null )
		for ( String key : section.getKeys(false) )
			keywords.put(new LocaleEntry("#" + key, section.getString(LocaleManager.buildPath(key, "new"), ""), ver), section.getString(LocaleManager.buildPath(key, "keyword")));
		
		section = configuration.getConfigurationSection(LocaleManager.buildPath("lores"));
		if ( section != null )
		for ( String key : section.getKeys(false) )
			lores.put(new LocaleEntry(key, section.getString(LocaleManager.buildPath(key, "new"), ""), ver), new ItemLocale(section.getString(LocaleManager.buildPath(key, "name")), section.getStringList(LocaleManager.buildPath(key, "lore"))));
			
	}

	public YamlConfiguration update(Map<LocaleEntry, String> cache, Map<LocaleEntry, ItemLocale> lores, Map<LocaleEntry, String> keywords, File file) 
	{
		YamlConfiguration loc = new YamlConfiguration();
		loc.options().pathSeparator(LocaleManager.PATH_SEPARATOR);
		
		loc.set("ver", ver);
		
		for ( Map.Entry<LocaleEntry, String> entry : this.cache.entrySet() )
		{
			String key = entry.getKey().key();
			if ( cache.containsKey(entry.getKey()) )
			{
				loc.set(LocaleManager.buildPath("backup","messages", key), cache.get(entry.getKey()));
				
				cache.put(new LocaleEntry(entry.getKey().newkey(), ver), entry.getValue());
				
				if ( entry.getKey().hasNewkey() )
					cache.remove(entry.getKey());
			}
			else
			{
				loc.set(LocaleManager.buildPath("messages", key), entry.getValue());
			}
		}

		for ( Entry<LocaleEntry, String> entry : this.keywords.entrySet() )
		{
			String key = entry.getKey().key();
			if ( keywords.containsKey(entry.getKey()) )
			{
				loc.set(LocaleManager.buildPath("backup","keywords", key.substring(1)), keywords.get(entry.getKey()));

				String n = entry.getKey().newkey();
				if ( n.startsWith("#") )
					n = n.substring(1);
					
				keywords.put(new LocaleEntry("#" + n, ver), entry.getValue());
				
				if ( entry.getKey().hasNewkey() )
					cache.remove(entry.getKey());
				
			}
			else
			{
				loc.set(LocaleManager.buildPath("keywords", key.substring(1)), entry.getValue());
			}
		}
		
		for ( Map.Entry<LocaleEntry, ItemLocale> entry : this.lores.entrySet() )
		{
			String key = entry.getKey().key();
			if ( lores.containsKey(entry.getKey()) )
			{
				loc.set(LocaleManager.buildPath("backup","lores", key, "name"), lores.get(entry.getKey()).name());
				loc.set(LocaleManager.buildPath("backup","lores", key, "lore"), lores.get(entry.getKey()).lore());
			
				lores.put(new LocaleEntry(entry.getKey().newkey(), ver), entry.getValue());
				

				if ( entry.getKey().hasNewkey() )
					cache.remove(entry.getKey());
			}
			else
			{
				loc.set(LocaleManager.buildPath("lores",entry.getKey().key(), "name"), entry.getValue().name());
				loc.set(LocaleManager.buildPath("lores",entry.getKey().key(), "lore"), entry.getValue().lore());
			}
		}
		
		for ( Map.Entry<LocaleEntry, String> entry : cache.entrySet() )
			loc.set(LocaleManager.buildPath("messages",entry.getKey().key()), entry.getValue());

		for ( Map.Entry<LocaleEntry, String> entry : keywords.entrySet() )
			loc.set(LocaleManager.buildPath("keywords",entry.getKey().key().substring(1)), entry.getValue());
		
		for ( Entry<LocaleEntry, ItemLocale> entry : lores.entrySet() )
		{
			loc.set(LocaleManager.buildPath("lores", entry.getKey().key(), "name"), entry.getValue().name());
			loc.set(LocaleManager.buildPath("lores", entry.getKey().key(), "lore"), entry.getValue().lore());
		}
		
		//save the new config
		try {
			loc.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return loc;
	}

}
