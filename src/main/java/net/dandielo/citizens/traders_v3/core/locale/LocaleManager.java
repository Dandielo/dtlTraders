package net.dandielo.citizens.traders_v3.core.locale;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.PluginSettings;

import org.apache.commons.io.input.ReaderInputStream;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;

/**
 * Manages all message, and UI requests by using the chosen localization file
 * @author dandielo
 */
public class LocaleManager {
	/** Singleton instance */
	public final static LocaleManager locale = new LocaleManager();

	/** The current locale version */
	public final static String localeVersion = "1.1.3"; 
	
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
		//debug info
		dB.info("Initializing locale manager");
		
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
		//debug info
		dB.info("Loading locale file");
		
		//get the file name and path
		String name = "locale." + PluginSettings.getLocale() + ".yml";
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
				 
			    InputStream stream = DtlTraders.getInstance().getResource("locales/locale." + PluginSettings.getLocale() + ".yml");
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
				//debug high
				dB.critical("While loading locale file, an exception occured");
				dB.normal("Exception message: ", e.getClass().getSimpleName());
				dB.high("Filename: ", name, ", path to file", path);

				//debug high
				dB.normal("Exception message: ", e.getMessage());
				dB.normal("StackTrace: ", e.getStackTrace());
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
		//debug info
		dB.info("Loading locale changes");
		
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
	
	    if(locale != null) locale.options().pathSeparator(PATH_SEPARATOR);
	    
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
		//debug info
		dB.info("Loading yaml configuration");
		
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
		    // Bypass UTF-8 loading issue on windows
		    InputStream inputStream = new ReaderInputStream(new InputStreamReader(new FileInputStream(localeFile), "UTF-8"));
			localeYaml.load(inputStream);
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
			//debug high
			dB.critical("While reading the locale file, an exception occured");
			dB.high("Exception message: ", e.getClass().getSimpleName());
			dB.high("On update: ", update);

			//debug high
			dB.normal("Exception message: ", e.getMessage());
			dB.normal("StackTrace: ", e.getStackTrace());
		}
	}

	/**
	 * Caches all messages 
	 */
	protected void loadMessages(String currentVersion, ConfigurationSection config)
	{
		//debug info
		dB.info("Loading locale messages");
		
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
		//debug info
		dB.info("Loading locale keywords");
		
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
		//debug info
		dB.info("Loading locale UI configs");
		
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
		//debug info
		dB.info("Saving locale YAML configuration to file");
		
		try 
		{
			localeYaml.save(localeFile);
		} 
		catch (IOException e)
		{
			//debug high
			dB.high("While saving the locale YAML configuration, an exception occured");
			dB.high("Exception: ", e.getClass().getSimpleName());
			
			//debug normal
			dB.normal("Exception message: ", e.getMessage());
			dB.normal("Stack trace: ", e.getStackTrace());
		}
	}
	
	/**
	 * Send a message to the specified sender, may be player or console. It can use any number of arguments. If you want to use a cached keyword add a <strong>#</strong> prefix before the key name.
	 * 
	 * @param sender
	 *     the sender where the message is going to be sent
	 * @param key
	 *     the message key, what message should we send
	 * @param args
	 *     additional arguments for the function call
	 *     
	 * @author dandielo   
	 */
	public void sendMessage(CommandSender sender, String key, Object... args)
	{
		//debug low
		dB.low("Preparing message to: ", sender.getName(), ", message key: ", key);
		dB.low("With arguments: ", args);
		
		//check the message key
		checkMessageKey(key);
		
		//get the message
		String message = messages.get(new LocaleEntry(key, localeVersion));
		
		//for each additional argument
		for ( int i = 0 ; i < args.length ; )
		{
			//debug info
			dB.info("Checking ", i + 1, " message argument: ", args[i]);
			dB.info("Checking ", i + 2, " message argument: ", args[i+1]);
			
			//look for string arguments
			if ( args[i] instanceof String )
			{
				//debug info
				dB.info("Valid tag");

				//check if the argument might be a keyword
				checkKeywordKey((String) args[i+1]);
				
				//replace tags in the initial message string
				message = message.replaceAll("\\{" + (String) args[i] + "\\}", keywords.get(new LocaleEntry((String) args[i+1], localeVersion)));
				
				//as because this case uses more than 1 argument, steps are doubled
				i += 2;
			} else
				//this cannot be a tag, look for a valid one
				++i;
		}
		
		//debug low
		dB.low("Sending message to: ", sender.getName(), ", message key: ", key);
		
		//send the prepared message
		sender.sendMessage(message.replace('^', '§'));
	}
	
	/**
	 * Gets a prepared message that can be used further in any way. It can use any number of arguments. If you want to use a cached keyword add a <strong>#</strong> prefix before the key name.
	 * 
	 * @param key
	 *     the message key, what message should we send
	 * @param args
	 *     additional arguments for the function call
	 *     
	 * @author dandielo   
	 */
	public String getMessage(String key, Object... args)
	{
		//debug info
		dB.low("Preparing message, key: ", key);
		dB.low("With arguments: ", args);
		
		//check the message key
		checkMessageKey(key);

		//get the message
		String message = messages.get(new LocaleEntry(key, localeVersion));

		//for each additional argument
		for ( int i = 0 ; i < args.length ; )
		{
			//look for string arguments
			if ( args[i] instanceof String )
			{
				//check if the argument might be a keyword
				checkKeywordKey((String) args[i+1]);

				//replace tags in the initial message string
				message = message.replaceAll("\\{" + (String) args[i] + "\\}", keywords.get(new LocaleEntry((String) args[i+1], localeVersion)));

				//as because this case uses more than 1 argument, steps are doubled
				i += 2;
			} else
				//this cannot be a tag, look for a valid one
				++i;
		}
		//return the prepared message
		return message.replace('^', '§');
	}
	
	/**
	 * Checks the given key if it's present in the cached message data.
	 * If it's not present it saves it into the locale with the following string <strong>"^3Check the locale, this message is not set!"</strong>, and adds to the cached data.
	 * @param key
	 *     the key that will be checked
	 *     
	 * @author dandielo
	 */
	public void checkMessageKey(String key)
	{
		//debug info
		dB.info("Checking message key: ",key);
		
		//it might be I've forgot to add a message to the locale, add it then with a warning string
		if ( !messages.containsKey(new LocaleEntry(key, localeVersion)) )
		{
			//debug low
			dB.low("Message key not found: ", key);
			
			//add to the yaml config
			localeYaml.set(
					buildPath("messages", key), 
					"^3Check the locale, this message is not set!"
					);
			
			//add to cached data 
			messages.put(
					new LocaleEntry(key, localeVersion), 
					"^3Check the locale, this message is not set!"
					);
			save();
		}
	}
	
	/**
	 * Checks the given key if it's present in the cached keyword data.
	 * If it's not present it saves it into the locale with the following string <strong>"^3Invalid keyword!"</strong>, and adds to the cached data.
	 * @param key
	 *     the key that will be checked
	 *     
	 * @author dandielo
	 */
	public void checkKeywordKey(String key)
	{
		//debug info
		dB.info("Checking keyword key: ", key);
		
		
		//it might be I've forgot to add a message to the locale, add it then with a warning string
		if ( !keywords.containsKey(new LocaleEntry(key, localeVersion)) && key.startsWith("#") )
		{
			//debug low
			dB.low("Keyword key not found: ", key);
			
			//add to the yaml config
			localeYaml.set(
					buildPath("keywords", key.substring(1)), 
					"^3Invalid keyword!"
					);
			
			//add to cached data 
			keywords.put(
					new LocaleEntry(key, localeVersion), 
					"^3Invalid keyword!"
					);
			save();
		}
	}
	
	/**
	 * @param key
	 *     the key where the lore is saved
	 * @return
	 *     the requested UI lore
	 * @author dandielo
	 */
	public List<String> getLore(String key)
	{
		//debug info
		dB.info("Getting lore for UI item: ", key);
		
		List<String> list = new ArrayList<String>();
		if ( ui.containsKey(new LocaleEntry(key, localeVersion)) ) {
			for ( String l : ui.get(new LocaleEntry(key, localeVersion)).lore() )
				list.add(l.replace('^', '§'));
		} else {
		    // The Lore-List may be empty, but if the ui-entry is missing, the locale is incomplete
		    dB.high("Missing Locale: " + key);
		}
		return list;
	}

	/**
	 * @param key
	 *     the key where the name is saved
	 * @return
	 *     the requested UI name
	 * @author dandielo
	 */
	public String getName(String key) {
		//debug info
		dB.info("Getting name for UI item: ", key);
		
		String name = "";
		if ( ui.containsKey(new LocaleEntry(key, localeVersion)) ) {
			name = ui.get(new LocaleEntry(key, localeVersion)).name();
		} else {
            // The Name may be empty, but if the ui-entry is missing, the locale is incomplete
            dB.high("Missing Locale: " + key);
        }
		return name.replace('^', '§');
	}

	/**
	 * Creates valid yaml paths for the given arguments using the specified path separator
	 * @return
	 *     valid yaml path
	 * 
	 * @author dandielo
	 */
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
	
	/**
	 * A special prepared HashMap extension, this allows to handle keyword searches in a specific way
	 * @author dandielo
	 *
	 * @param <K> final type LocaleEntry
	 * @param <V> final type String
	 */
	@SuppressWarnings({ "all" })
	protected class KeywordMap<K extends LocaleEntry, V extends String> extends HashMap<K, V>
	{
		private static final long serialVersionUID = -3449939627787377766L;

		/**
		 * Gets the given value for the given keyword if a <strong>#</strong> character is present at the keywords beginning, otherwise it will return the key supplied.
		 * @param key
		 *     given key to get value from
		 */
		@Override
		public V get(Object key)
		{			
			return (V) ( ((LocaleEntry)key).key().startsWith("#") ? super.get(key) : ((LocaleEntry)key).key() );
		}
	}
	
}
