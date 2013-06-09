package net.dandielo.citizens.traders_v3.bankers.backend;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.dandielo.citizens.traders_v3.bankers.account.Account;
import net.dandielo.citizens.traders_v3.bankers.account.Account.AccountType;
import net.dandielo.citizens.traders_v3.bankers.account.PrivateAccount;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Loads, saves and holds all private and guild accounts in game
 * @author dandielo
 */
public class AccountLoader {

	public static AccountLoader accLoader = new AccountLoader();
	
	/**
	 * All loaded accounts
	 */
	private Map<String, Account> accounts = new HashMap<String, Account>();
	
	/**
	 * returns a private account for the given player
	 */
	public Account getAccount(AccountType type, String owner)
	{
		//get the account
		Account account = accounts.get(owner);

		//if the account does not exists create a new one
		if ( account == null || !account.getType().equals(type) )
		    account = _createNewAccount(type, owner);
		
		//return the account
		return account;
	}
	
	
	/**
	 * Two files for private and guild accounts
	 */
	private File pAccFile;
	private File gAccFile; 
	
	/**
	 * Two configuration sections for both files
	 */
	private FileConfiguration pAccFC;
	private FileConfiguration gAccFC;
	
	/**
	 * It's a singleton ;)
	 */
	private AccountLoader()
	{
		init();
	}
	
	/**
	 * Runs once on server start loading all existing accounts
	 */
	public void init()
	{
		//path to both files
		String path = "plugins/dtlTraders/accounts";
		
		//create the directory if none exists
		File dir = new File(path);
		if ( !dir.exists() )
			dir.mkdirs();

		//create both files
		pAccFile = new File(path, "private.yml");
		gAccFile = new File(path, "guild.yml");

		//reload the config (load accounts into memory)
		reload();
	}
	
	private FileConfiguration _tryLoad(File file)
	{
		//create a new file
		try
		{
			if ( !file.exists() )
				file.createNewFile();
		}
		catch( IOException e )
		{
			return new YamlConfiguration();
		}
		
		//load its content
		return YamlConfiguration.loadConfiguration(file);	
	}
	
	/**
	 * Reloads the configuration from files (does not save it before!)
	 */
	public void reload()
	{

		//load the configuration
		pAccFC = _tryLoad(pAccFile);

		for ( String owner : pAccFC.getKeys(false) )
		{
			//load each account
			accounts.put(owner, _loadAccount(owner, pAccFC.getConfigurationSection(owner)));
		}
		
		//not need to load atm
		gAccFC = _tryLoad(gAccFile);		
	}
	
	/**
	 * Saves the current configuration to files
	 */
	public void save()
	{
		try
		{
			//save to file
			pAccFC.save(pAccFile);
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Accounts manipulation methods
	 */
	private Account _createNewAccount(AccountType type, String owner)
	{
		Account account = null;
		if ( type.equals(AccountType.PRIVATE) )
			account = new PrivateAccount(owner);
		else 
		if ( type.equals(AccountType.GUILD) )
			account = null;
		
		//set the yaml
		pAccFC.set(owner + ".type", type.name().toLowerCase());

		//save the data
		account.onSave(pAccFC.getConfigurationSection(owner));

		//return the new acc
		return account;
	}
	
	//loads the account from the yaml
	private Account _loadAccount(String owner, ConfigurationSection section)
	{
		String type = section.getString("type");
		
		//temp var
		Account account = null;
		if ( type.equals("private") )
		    account = new PrivateAccount(owner);
		else 
		if ( type.equals("guild") )
			account = null;
		
		//load the account
		if ( account != null )
		    account.onLoad(section);
		
		return account;
	}
}
