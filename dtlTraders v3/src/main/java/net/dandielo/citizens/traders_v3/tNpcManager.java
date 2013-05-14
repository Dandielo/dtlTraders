package net.dandielo.citizens.traders_v3;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import net.citizensnpcs.api.npc.NPC;
import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.core.exceptions.InvalidTraderTypeException;
import net.dandielo.citizens.traders_v3.core.exceptions.TraderTypeNotFoundException;
import net.dandielo.citizens.traders_v3.core.exceptions.TraderTypeRegistrationError;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.TraderType;
import net.dandielo.citizens.traders_v3.traders.types.Server;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;

public class tNpcManager {
	//instance
	private static tNpcManager instance = new tNpcManager();
	
	public static tNpcManager instance()
	{
		return instance;
	}
	
	//current trader registry
	private Map<String, Trader> transactions = new HashMap<String, Trader>();

	public void openTransaction(Player clicker, Trader trader) {
		transactions.put(clicker.getName(), trader);
	}

	public boolean inTransaction(Player player)
	{
		return transactions.containsKey(player.getName());
	}
	
	public Trader getTransactionTrader(Player player)
	{
		return transactions.get(player.getName());
	}
	
	public boolean closeTransaction(Player player)
	{
		return transactions.containsKey(player.getName());
	}
	//current banker registry
	//private Map<String, Trader> traders = new HashMap<String, Trader>();
	
	//class definition
	private tNpcManager()
	{
		registerTraderTypes();
	}
	
	private void registerTraderTypes()
	{
		try
		{
			registerType(Server.class);
		} 
		catch (TraderTypeRegistrationError e) 
		{
			e.printStackTrace();
		}
	}
	
	
	

	//Type management
	private final static Map<TraderType, Class<? extends Trader>> types = new HashMap<TraderType, Class<? extends Trader>>();
	
	public final static void registerType(Class<? extends Trader> clazz) throws TraderTypeRegistrationError
	{
		if ( !clazz.isAnnotationPresent(TraderType.class) ) throw new TraderTypeRegistrationError();
		
		TraderType typeInfo = clazz.getAnnotation(TraderType.class);
		types.put(typeInfo, clazz);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final static Trader createTarder(NPC npc, String type, Player player) throws TraderTypeNotFoundException, InvalidTraderTypeException
	{
		TraderType typeInfo = null;
		for ( TraderType info : types.keySet() )
			if ( info.name().equals(type) )
				typeInfo = info;
		
		//if the types is not registered throw an exception
		if ( typeInfo == null ) throw new TraderTypeNotFoundException(type);
		
		//get the class of the type 
		Class clazz = types.get(typeInfo);
		Trader trader = null;
		try
		{
			if ( clazz.getConstructor(TraderTrait.class, WalletTrait.class, Player.class) != null )
			{
			    trader = (Trader) clazz
			        .getConstructor(TraderTrait.class, WalletTrait.class, Player.class)
					.newInstance(npc.getTrait(TraderTrait.class), npc.getTrait(WalletTrait.class), player);
			}
			else
			if ( clazz.getConstructor(TraderTrait.class, WalletTrait.class) != null )
			{
				trader = (Trader) clazz
						.getConstructor(TraderTrait.class, WalletTrait.class)
						.newInstance(npc.getTrait(TraderTrait.class), npc.getTrait(WalletTrait.class));
			}
			else throw new Exception();	
		}
		catch (Exception e)
		{
			DtlTraders.severe("The following trader type is invalid: " + typeInfo.name() + ", author: " + typeInfo.author());
			throw new InvalidTraderTypeException(type);
		}
		return trader;
	}
}
