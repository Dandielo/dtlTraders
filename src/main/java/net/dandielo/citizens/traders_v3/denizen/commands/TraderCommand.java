package net.dandielo.citizens.traders_v3.denizen.commands;

import net.aufdemrand.denizen.exceptions.CommandExecutionException;
import net.aufdemrand.denizen.exceptions.InvalidArgumentsException;
import net.aufdemrand.denizen.interfaces.dExternal;
import net.aufdemrand.denizen.scripts.ScriptEntry;
import net.aufdemrand.denizen.scripts.commands.AbstractCommand;
import net.aufdemrand.denizen.objects.aH;
import net.aufdemrand.denizen.utilities.debugging.dB;

import net.dandielo.api.traders.TraderAPI;
import net.dandielo.citizens.traders_v3.tNpcStatus;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;

/**
 * Your command!
 * This class is a template for a Command in Denizen.
 *
 * If loading externally, implement dExternal and its load() method.
 *
 * @author Dandielo
 */
public class TraderCommand extends AbstractCommand implements dExternal {

	@Override
	public void load() {
		activate().as("Trader").withOptions("trader [open|close|manage] ({sell}|buy) ({stock}|relation)", 2);
	}

	@Override
	public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {
		//Arguments 
		Action action = null;
		tNpcStatus status = tNpcStatus.SELL;
		Context context = Context.STOCK;
		
		// Interpret arguments
		for (String arg : scriptEntry.getArguments())
		{
			 if ( aH.matchesArg("OPEN, CLOSE, MANAGE", arg) )
				 action = Action.valueOf(arg);

			 if ( aH.matchesArg("SELL, BUY", arg) )
				 status = tNpcStatus.baseStatus(arg);
			 
			 if ( aH.matchesArg("SELL, BUY", arg) )
				 context = Context.valueOf(arg);
		}

		if ( action == null ) 
			throw new InvalidArgumentsException("Must have action specified!");
		if ( !scriptEntry.hasPlayer() ) 
			throw new InvalidArgumentsException("Needs a player for executing!");
		if ( !scriptEntry.hasNPC() ) 
			throw new InvalidArgumentsException("Must be a Trader NPC!");
		if ( !scriptEntry.getNPC().getCitizen().hasTrait(TraderTrait.class) ) 
			throw new InvalidArgumentsException("Must be a Trader NPC!");
		
		//add object to the script entry
		scriptEntry
		    .addObject("action", action)
		    .addObject("status", status)
		    .addObject("context", context);
	}


	@Override
	public void execute(ScriptEntry scriptEntry) throws CommandExecutionException {

		// Fetch required objects
		Action action = (Action) scriptEntry.getObject("action");
		tNpcStatus status = (tNpcStatus) scriptEntry.getObject("status");
		Context context = (Context) scriptEntry.getObject("context");
		TraderTrait trait = scriptEntry.getNPC().getCitizen().getTrait(TraderTrait.class);

		// Debug the execution
		dB.report(scriptEntry, getName(), aH.debugObj("Action", action.toString()));

		// Do the execution
		switch(action)
		{
		case OPEN: 
			TraderAPI.openTrader(scriptEntry.getPlayer().getPlayerEntity(), trait, status, context.equals(Context.STOCK));
			break;
		case CLOSE: 
			TraderAPI.closeTrader(scriptEntry.getPlayer().getPlayerEntity());
			break;
		case MANAGE:
			break;
		default:
			break;
		}
	}

	private enum Context {
		STOCK, RELATION
	}
	private enum Action {
		OPEN, CLOSE, MANAGE
	}
}
