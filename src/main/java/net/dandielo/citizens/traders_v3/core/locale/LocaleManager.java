package net.dandielo.citizens.traders_v3.core.locale;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LocaleManager {
	// current locale version
	public final static String pver = "1.0.6"; 
	
	// cache
	private Map<LocaleEntry, String> cache;
	private Map<LocaleEntry, String> keywords;
	private Map<LocaleEntry, ItemLocale> lores;
	private LocaleUpdater updater;
	
	// locale file and yaml config
	protected final static char PATH_SEPARATOR = '/';
	
	protected FileConfiguration locale;
	protected File file;
	
	
	// methods	
	public LocaleManager()
	{
		cache = new HashMap<LocaleEntry, String>(); 
		keywords = new KeywordMap<LocaleEntry, String>();
		lores = new HashMap<LocaleEntry, ItemLocale>(); 
		updater = new LocaleUpdater(defaultLocale().getDefaults());
		
		loadFile();
	}
	
	public void loadFile()
	{
		ConfigurationSection config = DtlTraders.getInstance().getConfig();
		
		String name = config.getString("locale.file");
		if ( name == null ) 
		{
			name = "locale.en";
			config.set("locale.file", name);
			DtlTraders.getInstance().saveConfig();
		}
		
		String path = config.getString("locale.path", "plugins/DtlCitizensTrader/locale");
		if ( path.contains("\\") && !"\\".equals(File.separator) ) 
		{
			path = path.replace("\\", File.separator);
		}
		
		File baseDirectory = new File(path);
		if ( !baseDirectory.exists() ) 
			baseDirectory.mkdirs();

		
		file = new File(path, name);
		
		if ( !file.exists() )
		{
			try 
			{
				file.createNewFile();
				
				// Look for defaults in the jar
			    InputStream stream = DtlTraders.getInstance().getResource("locale.en");
			    
			    if (stream != null)
			    {
			        YamlConfiguration yconfig = YamlConfiguration.loadConfiguration(stream);
					locale = new YamlConfiguration();
			        locale.setDefaults(yconfig);
			        locale.options().copyDefaults(true);
			    }
				
			    save();
			} 
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		load();
	}

	public YamlConfiguration defaultLocale()
	{
		// Look for defaults in the jar
	    InputStream stream = DtlTraders.getInstance().getResource("locale.changes");
	    YamlConfiguration locale = null;
	    
	    if (stream != null)
	    {
	        YamlConfiguration yconfig = YamlConfiguration.loadConfiguration(stream);
			locale = new YamlConfiguration();
	        locale.setDefaults(yconfig);
	        locale.options().copyDefaults(true);
	    }
		locale.options().pathSeparator(PATH_SEPARATOR);
	    return locale;
	}
	
	public void load()
	{
		load(true);
	}
	
	public void load(boolean update)
	{
		locale = new YamlConfiguration();
		locale.options().pathSeparator(PATH_SEPARATOR);
				
		try 
		{
			locale.load(file);
			String ver = locale.getString("ver");
			
			cache.clear();
			ConfigurationSection section = locale.getConfigurationSection("messages");
			if ( section != null )
			for ( String key : section.getKeys(false) )
				cache.put(new LocaleEntry(key, ver), section.getString(key));
			
			keywords.clear();
			section = locale.getConfigurationSection("keywords");
			if ( section != null )
			for ( String key : section.getKeys(false) )
				keywords.put(new LocaleEntry("#"+key, ver), section.getString(key));
			
			lores.clear();
			section = locale.getConfigurationSection("lores");
			if ( section != null )
			for ( String key : section.getKeys(false) )
				lores.put(new LocaleEntry(key, ver), new ItemLocale(section.getString(buildPath(key, "name")), section.getStringList(buildPath(key, "lore"))));
			
			if ( ( ver == null || !ver.equals(pver) ) && update )
			{
				locale = updater.update(cache, lores, keywords, file);
				load(false);
			}
			
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void save()
	{
		try {
			locale.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(CommandSender sender, String key, Object... obj)
	{
		if ( !cache.containsKey(new LocaleEntry(key, pver)) )
		{
			locale.set(buildPath("messages", key), "^3Check the locale, this message is not set!");
			cache.put(new LocaleEntry(key, pver), "^3Check the locale, this message is not set!");
			save();
		}
		
		String msg = cache.get(new LocaleEntry(key, pver));
		for ( int i = 0 ; i < obj.length ; )
		{
			if ( obj[i] instanceof String )
			{
				if ( !keywords.containsKey(new LocaleEntry((String) obj[i+1], pver)) && ((String) obj[i+1]).startsWith("#") )
				{
					locale.set(buildPath("keywords", ((String) obj[i+1]).substring(1)), "^3Invalid keyword!");
					keywords.put(new LocaleEntry((String) obj[i+1], pver), "^3Invalid keyword!");
					save();
				}
				
				msg = msg.replaceAll("\\{" + (String) obj[i] + "\\}", keywords.get(new LocaleEntry((String) obj[i+1], pver)));
				i += 2;
			} else
				++i;
		}
		sender.sendMessage(msg.replace('^', '§'));
	}
	
	//gets the required message
	public String message(String key, Object... obj)
	{
		if ( !cache.containsKey(new LocaleEntry(key, pver)) )
		{
			locale.set(buildPath("messages", key), "^3Check the locale, this message is not set!");
			cache.put(new LocaleEntry(key, pver), "^3Check the locale, this message is not set!");
			save();
		}
		
		String msg = cache.get(new LocaleEntry(key, pver));
		for ( int i = 0 ; i < obj.length ; )
		{
			if ( obj[i] instanceof String )
			{
				if ( !keywords.containsKey(new LocaleEntry((String) obj[i+1], pver)) && ((String) obj[i+1]).startsWith("#") )
				{
					locale.set(buildPath("keywords", ((String) obj[i+1]).substring(1)), "^3Invalid keyword!");
					keywords.put(new LocaleEntry((String) obj[i+1], pver), "^3Invalid keyword!");
					save();
				}
				
				msg = msg.replaceAll("\\{" + (String) obj[i] + "\\}", keywords.get(new LocaleEntry((String) obj[i+1], pver)));
				i += 2;
			} else
				++i;
		}
		return msg.replace('^', '§');
	}
	
	
	//gets the required message
	public List<String> lore(String key)
	{
		List<String> list = new ArrayList<String>();
		if ( lores.containsKey(new LocaleEntry(key, pver)) )
			for ( String l : lores.get(new LocaleEntry(key, pver)).lore() )
				list.add(l.replace('^', '§'));
		return list;
	}

	public String name(String key) {
		String name = "";
		if ( lores.containsKey(new LocaleEntry(key, pver)) )
			name = lores.get(new LocaleEntry(key, pver)).name();
		return name.replace('^', '§');
	}
	
	// helper tools
	public static String buildPath(String... path) 
	{
		StringBuilder builder = new StringBuilder();

		boolean first = true;
		char separator = PATH_SEPARATOR; 

		for ( String node : path ) 
		{
			if ( !first ) 
			{
				builder.append(separator);
			}

			builder.append(node);

			first = false;
		}

		return builder.toString();
	}
	
	protected class KeywordMap<K extends LocaleEntry, V extends String> extends HashMap<K, V>
	{
		private static final long serialVersionUID = -3449939627787377766L;
		
		@SuppressWarnings("unchecked")
		@Override
		public V get(Object key)
		{			
			return (V) ( ((LocaleEntry)key).key().startsWith("#") ? super.get(key) : ((LocaleEntry)key).key() );
		}
	}
	
}
