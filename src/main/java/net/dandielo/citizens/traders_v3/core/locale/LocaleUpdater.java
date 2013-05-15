package net.dandielo.citizens.traders_v3.core.locale;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Updater class, this allows to update the locale file using locale.changes resource
 * <br><br>
 * <Strong>Deprecated:</strong> This updater is quite deprecated it will be exchanged with one that will update the locale from the drlTraders repo, depending on the localization
 * @author dandielo
 */
public class LocaleUpdater {
	//cached changes from the locale.changes file
	private Map<LocaleEntry, String> messages;
	private Map<LocaleEntry, String> keywords;
	private Map<LocaleEntry, LocaleItem> ui;
	
	//private static final String ver = LocaleManager.pver;
	
	/**
	 * Creates a updater instance with all chached changes 
	 * 
	 * @author dandielo
	 */
	LocaleUpdater(Configuration config) 
	{
		config.options().pathSeparator(LocaleManager.PATH_SEPARATOR);
		
		//create cache holders
		messages = new HashMap<LocaleEntry, String>();
		keywords = new HashMap<LocaleEntry, String>();
		ui = new HashMap<LocaleEntry, LocaleItem>();
		
		//load messages
		loadMessages(LocaleManager.localeVersion, config.getConfigurationSection(buildPath("messages")));
			
		//load keywords
		loadKeywords(LocaleManager.localeVersion, config.getConfigurationSection(buildPath("keywords")));
		
		//load keywords
		loadUIConfigs(LocaleManager.localeVersion, config.getConfigurationSection(buildPath("kui")));
				
			
	}

	/**
	 * Caches all message changes
	 */
	protected void loadMessages(String currentVersion, ConfigurationSection config)
	{
		//TODO add debug information (this is not normal)
		if ( config == null ) return;
		
		for ( String key : config.getKeys(false) )
			messages.put(
					new LocaleEntry(key, config.getString(buildPath(key, "new"), ""), currentVersion), 
					config.getString(buildPath(key, "message"))
					);
	}
	
	/**
	 * Caches all keyword chanegs
	 */
	protected void loadKeywords(String currentVersion, ConfigurationSection config)
	{
		//TODO add debug information (this is not normal)
		if ( config == null ) return;

		for ( String key : config.getKeys(false) )
			keywords.put(
					new LocaleEntry("#"+key, config.getString(buildPath(key, "new"), ""), currentVersion), 
					config.getString(LocaleManager.buildPath(key, "keyword"))
					);
	}

	/**
	 * Caches all UI locale changes
	 */
	protected void loadUIConfigs(String currentVersion, ConfigurationSection config)
	{
		//TODO add debug information (this is not normal)
		if ( config == null ) return;

		for ( String key : config.getKeys(false) )
			ui.put(
					new LocaleEntry(key, config.getString(buildPath(key, "new"), ""), currentVersion),
					new LocaleItem(
							config.getString(buildPath(key, "name")),
							config.getStringList(buildPath(key, "lore"))
							)
					);		
	}

	/**
	 * Checks cached data for changes between locale versions.    
	 * It can add, remove or update any entry, its value, key or both.
	 * 
	 * @param messages
	 *     a map containing all cached <strong>messages</strong> from the actual loaded locale file
	 * @param keywords
	 *     a map containing all cached <strong>keywords</strong> from the actual loaded locale file
	 * @param uiSettings
	 *     a map containing all cached <strong>ui item settings</strong> from the actual loaded locale file
	 * @param file
	 *     the file that will be altered
	 * 
	 * @author dandielo
	 */
	public void update(Map<LocaleEntry, String> messages, Map<LocaleEntry, String> keywords, Map<LocaleEntry, LocaleItem> uiSettings, File file) 
	{
		//create the new yaml configuration
		YamlConfiguration newLocaleYaml = new YamlConfiguration();
		
		//set the default locale format?
		newLocaleYaml.set("messages", "");
		newLocaleYaml.set("keywords", "");
		newLocaleYaml.set("ui", "");
		
		//set the path separator we are using
		newLocaleYaml.options().pathSeparator(LocaleManager.PATH_SEPARATOR);
		//set the version to the latest one
		newLocaleYaml.set("ver", LocaleManager.localeVersion);
		
		//altering messages
		for ( Map.Entry<LocaleEntry, String> entry : this.messages.entrySet() )
		{
			//the message key
			String messageKey = entry.getKey().key();
			
			//if cached messages have the same key, we need to update those messages
			if ( messages.containsKey(entry.getKey()) )
			{
				//set the old message to the new yaml configuration (as backup)
				newLocaleYaml.set(
						buildPath("backup", "messages", messageKey),
						messages.get(entry.getKey())
						);
				
				//update cached messages 
				messages.put(
						new LocaleEntry(entry.getKey().newkey(), LocaleManager.localeVersion), 
						entry.getValue()
						);
				
				//if the updated message is saved under a new key, remove the old one
				if ( entry.getKey().hasNewkey() )
					messages.remove(entry.getKey());
			}
			else
			{
				//if the message is new, add it just to the yaml configuration
				newLocaleYaml.set(buildPath("messages", messageKey), entry.getValue());
			}
		}

		//updating keywords
		for ( Entry<LocaleEntry, String> entry : this.keywords.entrySet() )
		{
			//the keyword key
			String keywordKey = entry.getKey().key();
			
			//if there is a keyword to updated it do it ;) 
			if ( keywords.containsKey(entry.getKey()) )
			{
				//save the old keyword (as backup) 
				newLocaleYaml.set(
						buildPath("backup", "keywords", keywordKey.substring(1)), 
						keywords.get(entry.getKey())
						);

				//prepare the newKey (it might be the old key too)
				String newKey = entry.getKey().newkey();
				if ( newKey.startsWith("#") )
					newKey = newKey.substring(1);
				
				//update cached keywords
				keywords.put(
						new LocaleEntry("#" + newKey, LocaleManager.localeVersion),
						entry.getValue()
						);
				
				//remove the old keyword if its key has changed
				if ( entry.getKey().hasNewkey() ) 
					keywords.remove(entry.getKey());
				
			}
			else
			{
				//if the keyword is new, add it just to the yaml configuration
				newLocaleYaml.set(
						buildPath("keywords", keywordKey.substring(1)), 
						entry.getValue()
						);
			}
		}

		//updating UI settings
		for ( Map.Entry<LocaleEntry, LocaleItem> entry : this.ui.entrySet() )
		{
			//get the UI setting key
			String key = entry.getKey().key();
			
			//if that key exists in the cached data, then update data
			if ( uiSettings.containsKey(entry.getKey()) )
			{
				//save both old item name and lore in the backup section
				newLocaleYaml.set(
						buildPath("backup", "lores", key, "name"), 
						uiSettings.get(entry.getKey()).name()
						);
				newLocaleYaml.set(
						buildPath("backup", "lores", key, "lore"), 
						uiSettings.get(entry.getKey()).lore()
						);
			
				//update cached UI settings 
				uiSettings.put(
						new LocaleEntry(entry.getKey().newkey(), LocaleManager.localeVersion),
						entry.getValue()
						);
				
				//if the setting key changed, remove the old key
				if ( entry.getKey().hasNewkey() )
					messages.remove(entry.getKey());
			}
			else
			{
				//if these ui settings are new just add them to the yaml config
				newLocaleYaml.set(
						buildPath("lores",entry.getKey().key(), "name"), 
						entry.getValue().name()
						);
				newLocaleYaml.set(
						buildPath("lores",entry.getKey().key(), "lore"), 
						entry.getValue().lore()
						);
			}
		}
		
		//save all cached messages to the yaml
		for ( Map.Entry<LocaleEntry, String> entry : messages.entrySet() )
			newLocaleYaml.set(
					buildPath("messages",entry.getKey().key()), 
					entry.getValue()
					);

		//save all cached keywords to the yaml
		for ( Map.Entry<LocaleEntry, String> entry : keywords.entrySet() )
			newLocaleYaml.set(
					buildPath("keywords",entry.getKey().key().substring(1)), 
					entry.getValue()
					);

		//save all cached UI settings to the yaml
		for ( Entry<LocaleEntry, LocaleItem> entry : uiSettings.entrySet() )
		{
			newLocaleYaml.set(
					buildPath("lores", entry.getKey().key(), "name"), 
					entry.getValue().name()
					);
			newLocaleYaml.set(
					buildPath("lores", entry.getKey().key(), "lore"), 
					entry.getValue().lore()
					);
		}
		
		//save the new yaml to the file
		try 
		{
			newLocaleYaml.save(file);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
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
		char separator = LocaleManager.PATH_SEPARATOR; 

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

}
