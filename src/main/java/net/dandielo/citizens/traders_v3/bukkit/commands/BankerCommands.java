package net.dandielo.citizens.traders_v3.bukkit.commands;

import java.util.Map;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.MobType;
import net.dandielo.citizens.traders_v3.tNpcManager;
import net.dandielo.citizens.traders_v3.bankers.Banker;
import net.dandielo.citizens.traders_v3.bankers.account.Account.AccountType;
import net.dandielo.citizens.traders_v3.bankers.backend.AccountLoader;
import net.dandielo.citizens.traders_v3.bankers.tabs.BankItem;
import net.dandielo.citizens.traders_v3.bankers.tabs.Tab;
import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.commands.Command;
import net.dandielo.citizens.traders_v3.core.exceptions.InvalidTraderTypeException;
import net.dandielo.citizens.traders_v3.core.exceptions.TraderTypeNotFoundException;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.traders.setting.TGlobalSettings;
import net.dandielo.citizens.traders_v3.traits.BankerTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Name;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class BankerCommands {
	
	private static AccountLoader accounts = AccountLoader.accLoader;
	private static LocaleManager locale = LocaleManager.locale;

	/**
	 * Creates a new banker on the senders position. For creating a banker this command needs to have specified his name. The name should be parsed as a entry in the args Map, under the "free" key.
	 * @param plugin
	 * This plugin
	 * @param sender
	 * The command sender
	 * @param banker
	 * that took part when the command was executed
	 * @param args
	 * additional arguments, like name, type and entity type
	 * @throws TraderTypeNotFoundException
	 * @throws InvalidTraderTypeException
	 */
	@Command(
	name = "banker",
	syntax = "create {args}",
	perm = "dtl.banker.commands.create",
	desc = "creates a new banker with the given arguments | 'e:', 't:'",
	usage = "- /trader create James the Banker e:sheep t:private",
	npc = false)
	public void bankerCreate(DtlTraders plugin, Player sender, Banker banker, Map<String, String> args) throws TraderTypeNotFoundException, InvalidTraderTypeException
	{
		//get the traders name
		String name = args.get("free");
		
		//get the entity
		EntityType entity = EntityType.fromName(args.get("e") == null ? "player" : args.get("e"));

		//the the type
		String type = args.get("t") == null ? "private" : args.get("t");
		
		if ( name == null )
		{
			locale.sendMessage(sender, "error-argument-missing", "argument", "#name");
			return;
		}
		
		//if entity is still null set it to player
		if(entity == null) entity = EntityType.PLAYER;
		
		//Create the npc
		NPC npc = CitizensAPI.getNPCRegistry().createNPC(entity, name);
		
		//add Traits
		npc.addTrait(BankerTrait.class);
		npc.addTrait(WalletTrait.class);
		
		//set the walet trait
		WalletTrait wallet = npc.getTrait(WalletTrait.class);
		wallet.setType(TGlobalSettings.getDefaultWallet());
		wallet.setMoney(TGlobalSettings.getWalletStartBalance());
		
		//set the mob type
		npc.addTrait(MobType.class);
		npc.getTrait(MobType.class).setType(entity);

		//spawn and the location of the sender
		npc.spawn(sender.getLocation());

		//create a test Trader Npc
		Banker nBanker = (Banker) tNpcManager.create_tNpc(npc, type, sender, BankerTrait.class);
		nBanker.getSettings().setType(type);
		
		//register the relation
		tNpcManager.instance().registerRelation(sender, nBanker); 
		
		//send messages
		locale.sendMessage(sender, "banker-created", "player", sender.getName(), "banker", name);
		
		//send the Trader Create event
		//TODO new BankerCreateEvent(nBanker, sender).callEvent();
	}
	
	/**
	 * Allows to change the tab name for a players bank account
	 * @param plugin
	 * This plugin
	 * @param sender
	 * The command sender
	 * @param banker
	 * that took part when the command was executed
	 * @param args
	 * the new tab name 
	 * @throws TraderTypeNotFoundException
	 * @throws InvalidBankerTypeException
	 */
	@Command(
	name = "banker",
	syntax = "tabname set <tabID> {args}",
	perm = "dtl.banker.commands.tabname",
	desc = "Changes a tabs name",
	usage = "- /banker tabname 0 set Weapons Tab",
	npc = false)
	public void settingTabName(DtlTraders plugin, Player sender, Banker banker, Map<String, String> args)
	{
		//check the argument
		if ( args.get("free") == null )
		{
			locale.sendMessage(sender, "error-argument-missing", "argument", "{args}");
			return;
		}

		int tabId = -1;
		try
		{
		    tabId = Integer.parseInt(args.get("tabID") );
		}
		catch (NumberFormatException e)
		{
			locale.sendMessage(sender, "error-argument-invalid", "argument", "<tabID>");
			return;
		}
		
		//get the tab
		Tab tab = accounts.getAccount(AccountType.PRIVATE, sender).getTab(tabId);
		
		if ( tab == null )
		{
			locale.sendMessage(sender, "error-argument-invalid", "argument", "<tabID>");
			return;
		}
		
		tab.setName(args.get("free"));
		
		//send a message
		locale.sendMessage(sender, "key-change", 
				"key", "#tab-name", 
				"value", tab.getName());
	}
	
	/**
	 * Allows to change the tab name for a players bank account
	 * @param plugin
	 * This plugin
	 * @param sender
	 * The command sender
	 * @param banker
	 * that took part when the command was executed
	 * @param args
	 * the new tab name 
	 * @throws TraderTypeNotFoundException
	 * @throws InvalidBankerTypeException
	 */
	@Command(
	name = "banker",
	syntax = "tabicon set <tabID> {args}",
	perm = "dtl.banker.commands.tabicon",
	desc = "Changes a tabs icon",
	usage = "- /banker tabname set 0 33 a:2 n:Name",
	npc = false)
	public void settingTabIcon(DtlTraders plugin, Player sender, Banker banker, Map<String, String> args)
	{
		//check the argument
		if ( args.get("free") == null )
		{
			locale.sendMessage(sender, "error-argument-missing", "argument", "{args}");
			return;
		}

		int tabId = -1;
		try
		{
		    tabId = Integer.parseInt(args.get("tabID") );
		}
		catch (NumberFormatException e)
		{
			locale.sendMessage(sender, "error-argument-invalid", "argument", "<tabID>");
			return;
		}
		
		//get the tab
		Tab tab = accounts.getAccount(AccountType.PRIVATE, sender).getTab(tabId);
		
		if ( tab == null )
		{
			locale.sendMessage(sender, "error-argument-invalid", "argument", "<tabID>");
			return;
		}
		
		BankItem bItem = new BankItem(args.get("free"));
		tab.setIcon( bItem.getItem() );
		
		if ( bItem.hasAttr(Name.class) )
		    tab.setName(bItem.getName());

		//send a message
		locale.sendMessage(sender, "key-change", 
				"key", "#tab-icon", 
				"value", args.get("free"));
		
		//send a message
		locale.sendMessage(sender, "key-change", 
				"key", "#tab-name", 
				"value", tab.getName());
	}
}
