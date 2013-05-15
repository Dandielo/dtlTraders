package net.dandielo.citizens.traders_v3.core.locale;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.core.PluginSettings;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LocaleManager {
	/** Singleton instance */
	public final static LocaleManager locale = new LocaleManager();

	/** The current locale version */
	public final static String localeVersion = "1.1.0"; 
	
	/* cached messages, keywords and lores */
	private Map<LocaleEntry, String> messages;
	private Map<LocaleEntry, String> keywords;
	private Map<LocaleEntry, LocaleItem> ui;
	
	/* locale updater */
	private LocaleUpdater updater;
	
	// locale file and yaml config
	protected final static char PATH_SEPARATOR = '/';
	
	protected FileConfiguration localeYaml;
	protected File localeFile;
	
	
	/** 
	 * Manages all messages and lores that can be changed in the plugin
	 * This class is a singleton, you can get an instance using it's public static field
	 * 
	 * @author dandielo
	 */
	private LocaleManager()
	{
		updater = new LocaleUpdater(localeChangeConfiguration().getDefaults());
		
		messages = new HashMap<LocaleEntry, String>(); 
		keywords = new KeywordMap<LocaleEntry, String>();
		ui = new HashMap<LocaleEntry, LocaleItem>(); 
		
		loadFile();
	}
	
	/**
	 * Loads the locale from file 
	 * 
	 * @author dandielo
	 */
	public void loadFile()
	{
		//get the file name and path
		String name = "locale." + PluginSettings.getLocale();
		String path = "plugins/dtlTraders/locale";
		
		File baseDirectory = new File(path);
		if ( !baseDirectory.exists() ) 
			baseDirectory.mkdirs();

		localeFile = new File(path, name);
		if ( !localeFile.exists() )
		{
			try 
			{
				localeFile.createNewFile();
				
			    InputStream stream = DtlTraders.getInstance().getResource("locale.en");
			    if (stream != null)
			    {
			        YamlConfiguration yconfig = YamlConfiguration.loadConfiguration(stream);
					localeYaml = new YamlConfiguration();
			        localeYaml.setDefaults(yconfig);
			        localeYaml.options().copyDefaults(true);
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

	/** 
	 * Loads locale changes from plugin resource, this will be used later to updated the locale
	 * 
	 * @return the yaml configuration that will be used to update the current locale file
	 * @author dandielo
	 */
	public YamlConfiguration localeChangeConfiguration()
	{
		//Load locale changes
	    InputStream stream = DtlTraders.getInstance().getResource("locale.changes");
	    YamlConfiguration locale = null;
	    
	    //if the stream is not empty (changes are present)
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
	
	/** 
	 * Loads the configuration, caching all messages, keywords and lores. 
	 * It also updates the locale file (depends on the configuration)
	 * 
	 * @author dandielo
	 */
	public void load()
	{
		load(PluginSettings.autoUpdateLocale());
	}
	
	/**
	 * Loads the configuration, caching all messages, keywords and lores.
	 * 
	 * @param update
	 * updates the current locale file using the LocaleUpdater 
	 * 
	 * @author dandielo
	 */
	public void load(boolean update)
	{
		//create a new yaml configuration (old one if set going to be forgot)
		localeYaml = new YamlConfiguration();
		//set the separator
		localeYaml.options().pathSeparator(PATH_SEPARATOR);
				
		try 
		{
			//load yaml from file
			localeYaml.load(localeFile);
			//get the locale version
			String currentVersion = localeYaml.getString("ver");
			
			//clear all cached data
			messages.clear();
			keywords.clear();
			ui.clear();
		
			//load messages
			loadMessages(currentVersion, localeYaml.getConfigurationSection("messages"));

			//load keywords
			loadKeywords(currentVersion, localeYaml.getConfigurationSection("keywords"));

			//load UI configurations
			loadUIConfigs(currentVersion, localeYaml.getConfigurationSection("ui"));
			
			if ( ( currentVersion == null || !currentVersion.equals(localeVersion) ) && update )
			{
				//updates the file
				updater.update(messages, keywords, ui, localeFile);
				
				//after first updated cache needs to be cleared and reloaded, update it just once 
				load(false);
			}
			
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Caches all messages 
	 */
	protected void loadMessages(String currentVersion, ConfigurationSection config)
	{
		//TODO add debug information (this is not normal)
		if ( config == null ) return;
		
		for ( String key : config.getKeys(false) )
			messages.put(
					new LocaleEntry(key, currentVersion), 
					config.getString(key)
					);
	}
	
	/**
	 * Caches all keywords
	 */
	protected void loadKeywords(String currentVersion, ConfigurationSection config)
	{
		//TODO add debug information (this is not normal)
		if ( config == null ) return;

		for ( String key : config.getKeys(false) )
			keywords.put(
					new LocaleEntry("#"+key, currentVersion), 
					config.getString(key)
					);
	}

	/**
	 * Caches all UI locale settings 
	 */
	protected void loadUIConfigs(String currentVersion, ConfigurationSection config)
	{
		//TODO add debug information (this is not normal)
		if ( config == null ) return;

		for ( String key : config.getKeys(false) )
			ui.put(
					new LocaleEntry(key, currentVersion), 
					new LocaleItem( 
							config.getString(buildPath(key, "name")),
							config.getStringList(buildPath(key, "lore"))
							)
					);
	}
	
	/**
	 * Saves the current yaml setting to file
	 * 
	 *  @author dandielo
	 */
	public void save()
	{
		try {
			localeYaml.save(localeFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(CommandSender sender, String key, Object... obj)
	{
		if ( !messages.containsKey(new LocaleEntry(key, localeVersion)) )
		{
			localeYaml.set(buildPath("messages", key), "^3Check the locale, this message is not set!");
			messages.put(new LocaleEntry(key, localeVersion), "^3Check the locale, this message is not set!");
			save();
		}
		
		String msg = messages.get(new LocaleEntry(key, localeVersion));
		for ( int i = 0 ; i < obj.length ; )
		{
			if ( obj[i] instanceof String )
			{
				if ( !keywords.containsKey(new LocaleEntry((String) obj[i+1], localeVersion)) && ((String) obj[i+1]).startsWith("#") )
				{
					localeYaml.set(buildPath("keywords", ((String) obj[i+1]).substring(1)), "^3Invalid keyword!");
					keywords.put(new LocaleEntry((String) obj[i+1], localeVersion), "^3Invalid keyword!");
					save();
				}
				
				msg = msg.replaceAll("\\{" + (String) obj[i] + "\\}", keywords.get(new LocaleEntry((String) obj[i+1], localeVersion)));
				i += 2;
			} else
				++i;
		}
		sender.sendMessage(msg.replace('^', '§'));
	}
	
	//gets the required message
	public String message(String key, Object... obj)
	{
		if ( !messages.containsKey(new LocaleEntry(key, localeVersion)) )
		{
			localeYaml.set(buildPath("messages", key), "^3Check the locale, this message is not set!");
			messages.put(new LocaleEntry(key, localeVersion), "^3Check the locale, this message is not set!");
			save();
		}
		
		String msg = messages.get(new LocaleEntry(key, localeVersion));
		for ( int i = 0 ; i < obj.length ; )
		{
			if ( obj[i] instanceof String )
			{
				if ( !keywords.containsKey(new LocaleEntry((String) obj[i+1], localeVersion)) && ((String) obj[i+1]).startsWith("#") )
				{
					localeYaml.set(buildPath("keywords", ((String) obj[i+1]).substring(1)), "^3Invalid keyword!");
					keywords.put(new LocaleEntry((String) obj[i+1], localeVersion), "^3Invalid keyword!");
					save();
				}
				
				msg = msg.replaceAll("\\{" + (String) obj[i] + "\\}", keywords.get(new LocaleEntry((String) obj[i+1], localeVersion)));
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
		if ( ui.containsKey(new LocaleEntry(key, localeVersion)) )
			for ( String l : ui.get(new LocaleEntry(key, localeVersion)).lore() )
				list.add(l.replace('^', '§'));
		return list;
	}

	public String name(String key) {
		String name = "";
		if ( ui.containsKey(new LocaleEntry(key, localeVersion)) )
			name = ui.get(new LocaleEntry(key, localeVersion)).name();
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
	
	@SuppressWarnings(value = { "all" })
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
