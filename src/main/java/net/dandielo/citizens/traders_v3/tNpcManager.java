package net.dandielo.citizens.traders_v3;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.exceptions.InvalidTraderTypeException;
import net.dandielo.citizens.traders_v3.core.exceptions.TraderTypeNotFoundException;
import net.dandielo.citizens.traders_v3.core.exceptions.TraderTypeRegistrationException;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.types.Server;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;

/**
 * Manages all tNPC and player interactions allowing to lets say, toggling manager mode.
 * @author dandielo
 */
public class tNpcManager {
	/**
	 * private singleton instance
	 */
	private final static tNpcManager instance = new tNpcManager();
	
	/**
	 * @return
	 * tNpcManager instance
	 */
	public static tNpcManager instance()
	{
		return instance;
	}
	
	/**
	 * tNpc registry, registers all ongoing tNpc <=> player relations, only one for each player
	 */
	private Map<String, tNpc> relations = new HashMap<String, tNpc>();

	/**
	 * Checks if the following player is in a relation with the given tNpc type.
	 * @param player
	 * player to check against
	 * @param clazz
	 * a valid tNpc type
	 * @return
	 * true if an relation is found with the given type 
	 */
	public <T extends tNpc> boolean checkRelationType(String player, Class<T> clazz)
	{
		return inRelation(player) ? clazz.isInstance(relations.get(player)) : false;
	}
	
	/**
	 * Checks if the player is in any relation with any tNpc
	 * @param player
	 * player to check
	 * @return
	 * true if a relation was found
	 */
	public boolean inRelation(Player player)
	{
		return inRelation(player.getName());
	}
	
	/**
	 * Checks if the player is in any relation with any tNpc
	 * @param player
	 * player to check
	 * @return
	 * true if a relation was found
	 */
	public boolean inRelation(String player)
	{
		return relations.containsKey(player);
	}
	
	/**
	 * Registers a new relation with the given player and tNpc.
	 * @param player
	 * in relation with the following trader
	 * @param trader
	 * a tNpc that is in relation with player
	 */
	public void registerRelation(Player player, tNpc npc) 
	{
		relations.put(player.getName(), npc);
	}
	
	/**
	 * Gets the relation for the given player
	 * @param player
	 * @param clazz
	 * tNpc type
	 * @return
	 * existing relation of exists, null otherwise
	 */
	@SuppressWarnings("unchecked")
	public <T extends tNpc> T getRelation(String player, Class<T> clazz)
	{
		return checkRelationType(player, clazz) ? (T) relations.get(player) : null;
	}


	/**
	 * Gets the trader relation for the given player. This method is a shortcut for <b>getRelation</b> method
	 * @param player
	 * @return
	 * existing relation of exists, null otherwise
	 */
	public Trader getTraderRelation(HumanEntity player)
	{
		return getTraderRelation(player.getName());
	}
	
	/**
	 * Gets the trader relation for the given player. This method is a shortcut for <b>getRelation</b> method
	 * @param player
	 * @return
	 * existing relation of exists, null otherwise
	 */
	public Trader getTraderRelation(Player player)
	{
		return getTraderRelation(player.getName());
	}
	
	/**
	 * Gets the trader relation for the given player. This method is a shortcut for <b>getRelation</b> method
	 * @param player name
	 * @return
	 * existing relation of exists, null otherwise
	 */
	public Trader getTraderRelation(String player)
	{
		return getRelation(player, Trader.class);
	}


	/**
	 * Removes the relation for the given player
	 * @param player
	 */
	public void removeRelation(HumanEntity player)
	{
		removeRelation(player.getName());
	}
	
	/**
	 * Removes the relation for the given player
	 * @param player
	 */
	public void removeRelation(Player player)
	{
		removeRelation(player.getName());
	}

	/**
	 * Removes the relation for the given player
	 * @param player name
	 */
	public void removeRelation(String player)
	{
		relations.remove(player);
	}
	
	/**
	 * Inventory registry, holds all open trader inventories
	 */
	private Map<String, Inventory> tNpcInventories = new HashMap<String, Inventory>();
	
	/**
	 * Checks if the given inventory is a trader, banker or acution npc inventory
	 * @param inventory
	 * the inventory to check
	 * @return
	 * true if the inventory is listed
	 */
	public boolean tNpcInventoryOpened(Player player)
	{
		return tNpcInventories.containsKey(player.getName());
	}
	
	/**
	 * Registers the given inventory as a trader inventory, it will stay as it as long some1 does not unregisters it.
	 * @param inventory
	 * the inventory to register
	 */
	public void registerOpenedInventory(Player player, Inventory inventory)
	{
		tNpcInventories.put(player.getName(), inventory);
	}
	
	/*
	 * Test methods
	 */
	public Collection<Inventory> getInventories()
	{
		return tNpcInventories.values();
	}
	
	/**
	 * Removes the given inventory from the registry if it's there.
	 * @param inventory
	 * the inventory that should be removed
	 */
	public void removeOpenedInventory(Player player)
	{
		tNpcInventories.remove(player.getName());
	}
	
	/**
	 * Disallow creation of this class
	 */
	private tNpcManager()
	{
	}
	
	/**
	 * Registers all core trader types
	 */
	public static void registerTypes()
	{
		try
		{
			//debug info
			dB.info("Register server trader type");
			registerType(Server.class);
			
			dB.info("Register private banker type");
			//registerType(Private.class);
			
			//register type handlers
			Trader.registerHandlers(Server.class);
			//Banker.registerHandlers(Private.class);
			
			//send message with registered types 
			DtlTraders.info("Registered types: " + typesAsString());
		} 
		catch (TraderTypeRegistrationException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Type registry, stores all registered types
	 */
	private final static Map<tNpcType, Class<? extends tNpc>> types = new HashMap<tNpcType, Class<? extends tNpc>>();
	
	/**
	 * Registers the given class as a new type for use by this plugin. The class needs to have the tNpcType addnotation filled.
	 * @param class1
	 * Class to register
	 * @throws TraderTypeRegistrationException
	 * Thrown when the class does not have the tNpcType addnotation
	 */
	public static void registerType(Class<? extends tNpc> class1) throws TraderTypeRegistrationException
	{
		//check for addnotation
		if ( !class1.isAnnotationPresent(tNpcType.class) ) throw new TraderTypeRegistrationException();
		
		//save the class
		types.put(class1.getAnnotation(tNpcType.class), class1);
	}
	
	/**
	 * Creates a tNpc, used later for relation assigning, based on the type name provided. Each created tNpc is only for 1 player created. 
	 * @param npc
	 * the npc that will have tNpc interaction assigned 
	 * @param type
	 * the type name
	 * @param player
	 * player that will be assigned to this tNpc relation
	 * @return
	 * returns the tNpc created if the given type was found
	 * @throws TraderTypeNotFoundException
	 * @throws InvalidTraderTypeException
	 */
	public static tNpc create_tNpc(NPC npc, String type, Player player, Class<? extends Trait> traitClazz) throws TraderTypeNotFoundException, InvalidTraderTypeException
	{
		tNpcType typeInfo = null;
		for ( tNpcType info : types.keySet() )
			if ( info.name().equals(type) )
				typeInfo = info;
		
		//if the types is not registered throw an exception
		if ( typeInfo == null ) throw new TraderTypeNotFoundException(type);
		
		//add the required wallet trait if not set
		if ( !npc.hasTrait(WalletTrait.class) )
			npc.addTrait(WalletTrait.class);
		
		//get the class of the type 
		Class<? extends tNpc> clazz = types.get(typeInfo);
		tNpc resultNpc = null;
		try
		{
			if ( clazz.getConstructor(traitClazz, WalletTrait.class, Player.class) != null )
			{
				resultNpc = clazz
			        .getConstructor(traitClazz, WalletTrait.class, Player.class)
					.newInstance(npc.getTrait(traitClazz), npc.getTrait(WalletTrait.class), player);
			}
		}
		catch (Exception e)
		{
			//debug critical
			dB.critical("Invalid type: " + typeInfo.name() + ", author: " + typeInfo.author());
			throw new InvalidTraderTypeException(type);
		}
		return resultNpc;
	}
	
	/**
	 * Creates a string with all registered core attribute names
	 * @return 
	 *     formated result string
	 */
	private static String typesAsString()
	{
		String result = "";
		//format the string
		for ( tNpcType attr : types.keySet() )
			result += " ," + ChatColor.YELLOW + attr.name() + ChatColor.RESET;
		
		return ChatColor.WHITE + "[" + result.substring(2) + ChatColor.WHITE + "]";
	}
}
