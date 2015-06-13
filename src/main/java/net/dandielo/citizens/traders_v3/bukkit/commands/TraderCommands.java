package net.dandielo.citizens.traders_v3.bukkit.commands;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.MobType;
import net.dandielo.citizens.traders_v3.tNpcManager;
import net.dandielo.citizens.traders_v3.TEntityStatus;
import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.core.commands.Command;
import net.dandielo.citizens.traders_v3.core.events.trader.TraderCreateEvent;
import net.dandielo.citizens.traders_v3.core.exceptions.InvalidTraderTypeException;
import net.dandielo.citizens.traders_v3.core.exceptions.TraderTypeNotFoundException;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.traders.setting.GlobalSettings;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Price;

/**
 * Holds all Trader specific commands as methods
 * @author dandielo
 *
 */
public class TraderCommands {

	private static LocaleManager locale = LocaleManager.locale;

	/**
	 * Creates a new trader on the senders position. For creating the trader this command needs to have specified his name. The name should be parsed as a entry in the args Map, under the "free" key.
	 * @param plugin
	 * This plugin
	 * @param sender
	 * The command sender
	 * @param trader
	 * that took part when the command was executed
	 * @param args
	 * additional arguments, like name, type and entity type
	 * @throws TraderTypeNotFoundException
	 * @throws InvalidTraderTypeException
	 */
	@Command(
			name = "trader",
			syntax = "create {args}",
			perm = "dtl.trader.commands.create",
			desc = "creates a new trader with the given arguments | 'e:', 't:'",
			usage = "- /trader create Wool trader e:sheep t:server",
			npc = false)
	public void traderCreate(DtlTraders plugin, Player sender, Trader trader, Map<String, String> args) throws TraderTypeNotFoundException, InvalidTraderTypeException
	{
		//get the traders name
		String name = args.get("free");

		//get the entity
		EntityType entity = EntityType.valueOf(args.get("e") == null ? "PLAYER" : args.get("e").toUpperCase());

		//the the type
		String type = args.get("t") == null ? "server" : args.get("t");

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
		npc.addTrait(TraderTrait.class);
		npc.addTrait(WalletTrait.class);

		//set the walet trait
		WalletTrait wallet = npc.getTrait(WalletTrait.class);
		wallet.setType(GlobalSettings.getDefaultWallet());
		wallet.setMoney(GlobalSettings.getWalletStartBalance());

		//set the mob type
		npc.addTrait(MobType.class);
		npc.getTrait(MobType.class).setType(entity);

		//spawn and the location of the sender
		npc.spawn(sender.getLocation());

		//create a test Trader Npc
		Trader nTrader = (Trader) tNpcManager.create_tNpc(npc, type, sender, TraderTrait.class);
		nTrader.getSettings().setType(type);

		//start with the unlocked status, to allow fast stock setting 
		nTrader.parseStatus(TEntityStatus.MANAGE_SELL);
		nTrader.parseStatus(TEntityStatus.MANAGE_UNLOCKED);

		//register the relation
		tNpcManager.instance().registerRelation(sender, nTrader);

		//send messages
		locale.sendMessage(sender, "trader-created", "player", sender.getName(), "trader", name);
		locale.sendMessage(sender, "trader-managermode-enabled", "npc", npc.getName());
		locale.sendMessage(sender, "trader-managermode-toggled", "mode", "#stock-sell");

		//send the Trader Create event
		new TraderCreateEvent(nTrader, sender).callEvent();
	}

	/**
	 * Allows to change the stock name for a specified trader
	 * @param plugin
	 * This plugin
	 * @param sender
	 * The command sender
	 * @param trader
	 * that took part when the command was executed
	 * @param args
	 * additional arguments, like name, type and entity type
	 * @throws TraderTypeNotFoundException
	 * @throws InvalidTraderTypeException
	 */
	@Command(
			name = "trader",
			syntax = "stockname <action> {args}",
			perm = "dtl.trader.commands.stockname",
			desc = "Shows/resets or changes the stock name",
			usage = "- /trader stockname set Santas Stock",
			npc = true)
	public void settingStockName(DtlTraders plugin, Player sender, Trader trader, Map<String, String> args)
	{
		String action = args.get("action");

		//if we should set the setting
		if ( action.equals("set") )
		{
			//check the argument
			if ( args.get("free") == null )
			{
				locale.sendMessage(sender, "error-argument-missing", "argument", "{args}");
				return;
			}

			//set the new stock name
			trader.getSettings().setStockFormat(args.get("free"));

			//send a message
			locale.sendMessage(sender, "key-change", 
					"key", "#stock-name", 
					"value", trader.getSettings().getStockFormat());
		}
		else
			//reset the setting to the global default
			if ( action.equals("reset") )
			{
				//set to default
				trader.getSettings().setStockFormat(Settings.getGlobalStockNameFormat());

				//send a message
				locale.sendMessage(sender, "key-change", 
						"key", "#stock-name", 
						"value", trader.getSettings().getStockFormat());
			}
			else
				//show the setting
				if ( action.equals("show") )
				{
					//send the current setting
					locale.sendMessage(sender, "key-value", 
							"key", "#stock-name", 
							"value", trader.getSettings().getStockFormat());
				}
		//send a error message
				else
				{
					locale.sendMessage(sender, "error-argument-invalid", "argument", "<action>");
				}
	}

	/**
	 * Allows to change the stock size for a specified trader 
	 * @param plugin
	 * This plugin
	 * @param sender
	 * The command sender
	 * @param trader
	 * that took part when the command was executed
	 * @param args
	 * additional arguments, like name, type and entity type
	 * @throws TraderTypeNotFoundException
	 * @throws InvalidTraderTypeException
	 */
	@Command(
			name = "trader",
			syntax = "stocksize <action> (size)",
			perm = "dtl.trader.commands.stocksize",
			desc = "Shows/resets or changes the stock size",
			usage = "- /trader stocksize set 5",
			npc = true)
	public void settingStockSize(DtlTraders plugin, Player sender, Trader trader, Map<String, String> args)
	{
		String action = args.get("action");

		//if we should set the setting
		if ( action.equals("set") )
		{
			//check the argument
			if ( args.get("size") == null )
			{
				locale.sendMessage(sender, "error-argument-missing", "argument", "(size)");
				return;
			}

			//is the size valid?
			int size = Integer.parseInt(args.get("size"));
			if ( size < 0 || size > 6 )
			{
				locale.sendMessage(sender, "error-argument-invalid", "argument", "(size)");
				return;
			}

			//set the new stock name
			trader.getSettings().setStockSize( size );

			//send a message
			locale.sendMessage(sender, "key-change", 
					"key", "#stock-size", 
					"value", String.valueOf(trader.getSettings().getStockSize()));
		}
		else
			//reset the setting to the global default
			if ( action.equals("reset") )
			{
				//set to default
				trader.getSettings().setStockSize( Settings.getGlobalStockSize() );

				//send a message
				locale.sendMessage(sender, "key-change", 
						"key", "#stock-size", 
						"value", String.valueOf(trader.getSettings().getStockSize()));
			}
			else
				//show the setting
				if ( action.equals("show") )
				{
					//send the current setting
					locale.sendMessage(sender, "key-value", 
							"key", "#stock-size", 
							"value", String.valueOf(trader.getSettings().getStockSize()));
				}
		//send a error message
				else
				{
					locale.sendMessage(sender, "error-argument-invalid", "argument", "<action>");
				}
	}

	/**
	 * Allows to change the starting stock for the specified trader
	 * @param plugin
	 * This plugin
	 * @param sender
	 * The command sender
	 * @param trader
	 * that took part when the command was executed
	 * @param args
	 * additional arguments, like name, type and entity type
	 * @throws TraderTypeNotFoundException
	 * @throws InvalidTraderTypeException
	 */
	@Command(
			name = "trader",
			syntax = "startstock <action> (stock)",
			perm = "dtl.trader.commands.startstock",
			desc = "Shows/resets or changes the starting stock (buy|sell)",
			usage = "- /trader startstock set buy",
			npc = true)
	public void settingStockStart(DtlTraders plugin, Player sender, Trader trader, Map<String, String> args)
	{
		String action = args.get("action");

		//if we should set the setting
		if ( action.equals("set") )
		{
			//check the argument
			if ( args.get("stock") == null )
			{
				locale.sendMessage(sender, "error-argument-missing", "argument", "(stock)");
				return;
			}

			//set the new stock name
			trader.getSettings().setStockStart( args.get("stock") );

			//send a message
			locale.sendMessage(sender, "key-change", 
					"key", "#stock-start", 
					"value", trader.getSettings().getStockStart());
		}
		else
			//reset the setting to the global default
			if ( action.equals("reset") )
			{
				//set to default
				trader.getSettings().setStockStart( Settings.getGlobalStockStart() );

				//send a message
				locale.sendMessage(sender, "key-change", 
						"key", "#stock-start", 
						"value", trader.getSettings().getStockStart());
			}
			else
				//show the setting
				if ( action.equals("show") )
				{
					//send the current setting
					locale.sendMessage(sender, "key-value", 
							"key", "#stock-start", 
							"value", trader.getSettings().getStockStart());
				}
		//send a error message
				else
				{
					locale.sendMessage(sender, "error-argument-invalid", "argument", "<action>");
				}
	}

	/**
	 * Allows to change the traders wallet and it's settings
	 * @param plugin
	 * This plugin
	 * @param sender
	 * The command sender
	 * @param trader
	 * that took part when the command was executed
	 * @param args
	 * additional arguments, like name, type and entity type
	 * @throws TraderTypeNotFoundException
	 * @throws InvalidTraderTypeException
	 */
	@Command(
			name = "trader",
			syntax = "wallet <option> <action> (value)",
			perm = "dtl.trader.commands.wallet",
			desc = "Shows/sets the wallet type or its settings (infinite|private|owner|player)",
			usage = "- /trader wallet set type player",
			npc = true)
	public void settingWallet(DtlTraders plugin, Player sender, Trader trader, Map<String, String> args)
	{
		String option = args.get("option");
		String action = args.get("action");

		if ( action.equals("set") )
		{
			//get the value
			String value = args.get("value");

			//throw an error if the value is null
			if ( value == null )
			{
				locale.sendMessage(sender, "error-argument-missing", "argument", "(value)");
				return;
			}

			//depending on the option set the new value
			WalletTrait wallet = trader.getNPC().getTrait(WalletTrait.class);
			if ( option.equals("type") )
			{
				//set the new wallet type
				wallet.setType(value);

				//send a message
				locale.sendMessage(sender, "key-change", 
						"key", "#wallet-type", 
						"value", wallet.getType());
			}
			else
				if ( option.equals("player") )
				{
					OfflinePlayer player = null;
					for (OfflinePlayer pl : Bukkit.getOfflinePlayers())
						if (pl.getName().equals(value))
							player = pl;
						
					wallet.setPlayer(player);

					//send a message
					locale.sendMessage(sender, "key-change", 
							"key", "#wallet-player", 
							"value", wallet.getPlayer());
				}
				else
					if ( option.equals("amount") )
					{
						try
						{
							wallet.setMoney(Double.parseDouble(value));

							//send a message
							locale.sendMessage(sender, "key-change", 
									"key", "#wallet-balance", 
									"value", String.valueOf(wallet.getBalance()) );
						}
						catch(NumberFormatException e)
						{
							locale.sendMessage(sender, "error-argument-invalid", "argument", "(value)");
						}
					}
					else
					{
						locale.sendMessage(sender, "error-argument-invalid", "argument", "<option>");
					}
		}
		else 
			if ( action.equals("show") )
			{
				//depending on the option show the value
				WalletTrait wallet = trader.getNPC().getTrait(WalletTrait.class);
				if ( option.equals("type") )
				{
					//send a message
					locale.sendMessage(sender, "key-value", 
							"key", "#wallet-type", 
							"value", wallet.getType());
				}
				else
					if ( option.equals("player") )
					{
						//send a message
						locale.sendMessage(sender, "key-value", 
								"key", "#wallet-player", 
								"value", wallet.getPlayer());
					}
					else
						if ( option.equals("amount") )
						{
							//send a message
							locale.sendMessage(sender, "key-value", 
									"key", "#wallet-balance", 
									"value", String.valueOf(wallet.getBalance()) );
						}
						else
						{
							locale.sendMessage(sender, "error-argument-invalid", "argument", "<option>");
						}
			}
			else
			{
				locale.sendMessage(sender, "error-argument-invalid", "argument", "<action>");
			}
	}

	/**
	 * Allows to change the starting stock for the specified trader
	 * @param plugin
	 * This plugin
	 * @param sender
	 * The command sender
	 * @param trader
	 * that took part when the command was executed
	 * @param args
	 * additional arguments, like name, type and entity type
	 * @throws TraderTypeNotFoundException
	 * @throws InvalidTraderTypeException
	 */
	@Command(
			name = "trader",
			syntax = "sellprice {args}",
			perm = "dtl.trader.commands.sellprice",
			desc = "Sets price to an item that matches the string",
			usage = "- /trader sellprice 33 p:12.11",
			npc = true)
	public void sellprice(DtlTraders plugin, Player sender, Trader trader, Map<String, String> args)
	{
		String itemString = args.get("free");

		int items = 0;

		StockItem price = new StockItem(itemString);
		for ( StockItem item : trader.getStock().getStock("sell") )
		{
			if ( item.priorityMatch(price) >= 0 )
			{
				++items;
				if ( item.hasAttribute(Price.class) )
					item.getPriceAttr().setPrice(price.getPrice());
				else
					item.addAttribute("p", price.getPriceFormated());
			}
		}
		//send message
		locale.sendMessage(sender, "trader-stock-price-set", "items", String.valueOf(items));
	}

	@Command(
			name = "trader",
			syntax = "buyprice {args}",
			perm = "dtl.trader.commands.buyprice",
			desc = "Sets price to an item that matches the string",
			usage = "- /trader buyprice 33 p:12.11",
			npc = true)
	public void buyprice(DtlTraders plugin, Player sender, Trader trader, Map<String, String> args)
	{
		String itemString = args.get("free");

		int items = 0;

		StockItem price = new StockItem(itemString);
		for ( StockItem item : trader.getStock().getStock("buy") )
		{
			if ( price.priorityMatch(item) >= 0 )
			{
				++items;
				if ( item.hasAttribute(Price.class) )
					item.getPriceAttr().setPrice(price.getPrice());
				else
					item.addAttribute("p", price.getPriceFormated());
			}
		}

		//send message
		locale.sendMessage(sender, "trader-stock-price-set", "items", String.valueOf(items));
	}

	@Command(
			name = "trader",
			syntax = "reset <option>",
			perm = "dtl.trader.commands.reset",
			npc = true)
	public void resetSettings(DtlTraders plugin, Player sender, Trader trader, Map<String, String> args)
	{
		if ( args.get("option").equals("prices") )
		{
			//remove all prices attributes
			for ( StockItem item : trader.getStock().getStock("sell") )
				item.removeAttribute(Price.class);

			//send a message
			locale.sendMessage(sender, "trader-stock-price-reset");
		}
		else
		{
			//error action arg invalid
			locale.sendMessage(sender, "error-argument-invalid", "argument", "<action>");
		}
	}

	@Command(
			name = "trader",
			syntax = "pattern <action> (pattern)",
			perm = "dtl.trader.commands.pattern",
			desc = "Allows to manage patterns for a trader",
			usage = "- /trader pattern set pattern_na,e",
			npc = true)
	//Action set/remove/add/clear/reload/show/
	public void setPattern(DtlTraders plugin, Player sender, Trader trader, Map<String, String> args)
	{
		//show all patterns
		if ( args.get("action").equals("show") )
		{
			String result = "";
			for ( String pat : trader.getSettings().getPatterns() )
				result += ChatColor.DARK_AQUA + pat + ", " + ChatColor.RESET;

			//send the message
			locale.sendMessage(sender, "key-value", "key", "#pattern-list", "value", result);
		}
		else
			//add a pattern
			if ( args.get("action").equals("add") )
			{
				if ( !args.containsKey("pattern") )
				{
					locale.sendMessage(sender, "error-argument-missing", "argument", "(pattern)");
					return;
				}

				//add the new pattern
				trader.getSettings().getPatterns().add(args.get("pattern"));

				//added message
				locale.sendMessage(sender, "key-change", "key", "#pattern-add", "value", args.get("pattern"));
			}
			else
				//remove a pattern
				if ( args.get("action").equals("remove") )
				{
					if ( !args.containsKey("pattern") )
					{
						locale.sendMessage(sender, "error-argument-missing", "argument", "(pattern)");
						return;
					}

					//remove the pattern
					trader.getSettings().getPatterns().remove(args.get("pattern"));

					//added message
					locale.sendMessage(sender, "key-change", "key", "#pattern-remove", "value", args.get("pattern"));
				}
				else
					//remove all patterns
					if ( args.get("action").equals("removeall") )
					{			
						//pattern list
						String result = "";
						for ( String pat : trader.getSettings().getPatterns() )
							result += ChatColor.DARK_AQUA + pat + ", " + ChatColor.RESET;

						//clear the pattern list
						trader.getSettings().getPatterns().clear();

						//added message
						locale.sendMessage(sender, "key-change",  "key","#pattern-remove-all", "value", result);
					}
					else
						//set back to default
						if ( args.get("action").equals("default") )
						{
							//set defaults
							trader.getSettings().getPatterns().clear();
							trader.getSettings().getPatterns().addAll(Settings.defaultPatterns());

							//pattern list
							String result = "";
							for ( String pat : trader.getSettings().getPatterns() )
								result += ChatColor.DARK_AQUA + pat + ", " + ChatColor.RESET;

							//added message
							locale.sendMessage(sender, "key-change", "key","#pattern-default", "value", result);
						}
						else
						{
							//error action arg invalid
							locale.sendMessage(sender, "error-argument-invalid", "argument", "<action>");
						}
	}

	/*public static void main(String[] a)
	{
		System.out.print(Pattern.compile("&.").matcher("&3ANPC &7Name").replaceAll("")
	}*/

	@Command(
			name = "trader", syntax = "manage <trader>",
			perm = "dtl.trader.commands.manage",
			usage = "", desc = "", npc = false)
	public void traderManage(DtlTraders plugin, Player sender, Trader trader, Map<String, String> args) throws TraderTypeNotFoundException, InvalidTraderTypeException
	{
		Iterator<NPC> it = CitizensAPI.getNPCRegistry().iterator();
		Pattern pat = Pattern.compile("&.");
		NPC result = null;

		while(it.hasNext() && result == null)
		{
			if ( !pat.matcher((result = it.next()).getName()).replaceAll("").equals(args.get("trader")) )
			{
				try
				{
					if ( result.getId() != Integer.parseInt(args.get("trader")) )
						result = null;

				} catch( Exception e ) { result = null; }
			}

			if ( result != null && !result.hasTrait(TraderTrait.class) )
				result = null;
		}

		if ( result == null ) 
		{
			locale.sendMessage(sender, "error-npc-invalid");
			return;
		}

		if ( !tNpcManager.instance().inRelation(sender) )
		{
			//create a test Trader Npc
			Trader nTrader = (Trader) tNpcManager.create_tNpc(result, result.getTrait(TraderTrait.class).getType(), sender, TraderTrait.class);

			//start with the unlocked status, to allow fast stock setting 
			nTrader.parseStatus(TEntityStatus.MANAGE_SELL);

			//register the relation
			tNpcManager.instance().registerRelation(sender, nTrader);

			//send messages
			locale.sendMessage(sender, "trader-managermode-enabled", "npc", result.getName());
		}
		else
		{
			Trader tr = tNpcManager.instance().getTraderRelation(sender);
			//remove the relation
			tNpcManager.instance().removeRelation(sender);
			sender.closeInventory();
			//send messages
			locale.sendMessage(sender, "trader-managermode-disabled", "npc", tr.getNPC().getName());

			if ( tr.getNPC().getId() != result.getId() )
			{
				//create a test Trader Npc
				Trader nTrader = (Trader) tNpcManager.create_tNpc(result, result.getTrait(TraderTrait.class).getType(), sender, TraderTrait.class);
				//register the relation
				tNpcManager.instance().registerRelation(sender, nTrader);
				//send messages
				locale.sendMessage(sender, "trader-managermode-enabled", "npc", result.getName());
			}
		}
	}

	@Command(
			name = "trader", syntax = "give {args}",
			perm = "dtl.trader.commands.give",
			usage = "", desc = "", npc = false)
	public void traderGive(DtlTraders plugin, Player sender, Trader trader, Map<String, String> args)
	{
		if ( args.get("free") != null )
		{
			try
			{
				StockItem item = new StockItem(args.get("free"));
				sender.setItemInHand(item.getItem(true));
			} catch(NumberFormatException e) { }
		}
	}
	
	@Command(
			name = "trader", syntax = "open",
			perm = "dtl.trader.commands.open",
			usage = "", desc = "", npc = true)
	public void traderOpen(DtlTraders plugin, Player sender, Trader trader, Map<String, String> args)
	{

	}
	/*
	@SerializableAs("dice")
	static class Dice implements ConfigurationSerializable
	{
		int x = 2;
		int y = 3;

		public Dice(int x, int y)
		{

		}

		public Dice(Map<String, Object> m)
		{
			x = (Integer) m.get("x");
			y = (Integer) m.get("y");
		}

		@Override
		public Map<String, Object> serialize()
		{
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("x", x);
			map.put("y", y);
			return map;
		}

		public Dice deserialize(Map<String, Object> m)
		{
			return new Dice(m);
		}

		public Dice valueOf(Map<String, Object> m)
		{
			return new Dice(m);
		}

	}

	public static void main(String[] a)
	{

		YamlConfiguration yaml = new YamlConfiguration();
		ConfigurationSerialization.registerClass(Dice.class, "dice");

		yaml.set("test", new Dice(2, 3));

		System.out.print(yaml.saveToString());
	}*/
}
