package net.dandielo.citizens.traders_v3.core.commands;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dandielo.citizens.traders_v3.tNpc;
import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.bukkit.Perms;
import net.dandielo.citizens.traders_v3.bukkit.commands.GeneralCommands;
import net.dandielo.citizens.traders_v3.core.Debugger;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.core.tools.StringTools;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Manages all incoming command requests for this plugin. Automatically sending basic warnings or errors when requirements for the called command are not meet.
 * @author dandielo
 */
public class CommandManager {
	/**
	 * Singleton instance
	 */
	public static final CommandManager manager = new CommandManager();
	
	/**
	 * Required instances
	 */
	private static LocaleManager locale = LocaleManager.locale;
	private static Perms perms = Perms.perms;
	
	/**
	 * Command executor
	 */
	private DtlCommandExecutor executor;
	
	/**
	 * All plugin commands
	 */
	private Map<CommandSyntax, CommandBinding> commands;
	
	/**
	 * Objects that are used to call these commands
	 */
	private Map<Class<?>, Object> objects = new HashMap<Class<?>, Object>();
	
	/**
	 * Set defaults and executor for <b>trader</b> and <b>banker</b> commands
	 */
	private CommandManager()
	{
		commands = new HashMap<CommandSyntax, CommandBinding>();
		executor = new DtlCommandExecutor(this);

		DtlTraders.getInstance().getCommand("trader").setExecutor(executor);
	//	plugin.getCommand("banker").setExecutor(executor);
	}
	
	/**
	 * Creates a new object instance for all commands it adds to the overall command container.
	 * @param clazz
	 * the class which will be initialized.
	 */
	protected void newInstance(Class<?> clazz)
	{
		try
		{
			objects.put(clazz, clazz.newInstance());
		}
		catch (Exception e)
		{
			//debug critical
			Debugger.critical("Command class could not be initialized!");
			Debugger.critical("Stack trace: ", StringTools.stackTrace(e.getStackTrace()));
		}
	}
	
	/**
	 * Registers all commands that the given class has. A command is a method with a valid "Command" annotation.
	 * @param clazz
	 * Class with commands to register
	 */
	public void registerCommands(Class<?> clazz)
	{
		//if it has been initialized once, do not do it a second time, its pointless...
		if ( objects.containsKey(clazz) ) 
			return;
		
		//create a new instance
		newInstance(clazz);
		
		//check each method in the given class
		for ( Method method : clazz.getMethods() )
		{
			//get the annotation
			Command command = method.getAnnotation(Command.class);
			
			//if it exists then we can continue
			if ( command != null )
			{
				//register information about the command (This is used by the "help command")
				GeneralCommands.registerCommandInfo(command.name(), command);
				
				//debug info
			    Debugger.info("Registered command: '", ChatColor.GREEN, command.name(), " ", command.syntax(), ",");
			    
				CommandSyntax syntax = new CommandSyntax(command.name(), command.syntax());
				commands.put(syntax, new CommandBinding(clazz, method, syntax, command));
			}
		}
	}
	
	/**
	 * Tries to execute the given command name with all its arguments
	 * @param name
	 * command name
	 * @param args
	 * supplied command arguments 
	 * @param sender
	 * the player who send the command
	 * @param tNPC
	 * the tNPC that is selected (manager mode)
	 * @return
	 * always so all command messages are handler byt this manager
	 */
	public boolean execute(String name,  String[] args, CommandSender sender, tNpc tNPC)
	{
		for ( Map.Entry<CommandSyntax, CommandBinding> command : commands.entrySet() )
			if ( new CommandSyntax(name, args).equals(command.getKey()) )
			{
				if ( command.getValue().requiresNpc() && tNPC == null )
				{
					locale.sendMessage(sender, "error-npc-not-selected");
					return true;
				}
				else
					return command.getValue().execute(sender, tNPC, args);
			}
		locale.sendMessage(sender, "error-command-invalid");
		return true;
	}
	
	/**
	 * Anlyzes and prepares the commands syntax
	 * @author dandielo
	 *
	 */
	private static class CommandSyntax
	{
		private static final Pattern commandPattern = Pattern.compile("(<([^<>]*)>)|([ ]*\\(([^\\(\\)]*)\\))|([ ]*\\{([^\\{\\}]*)\\})");
		
		private List<String> argumentNames = new ArrayList<String>();
		private String name;
		private String originalSyntax;
		private Pattern syntax;
				
		public CommandSyntax(String name, String[] args) 
		{
			this.name = name;
			originalSyntax = name + " " + toString(args);
		}

		public CommandSyntax(String name, String args) 
		{
			this.name = name;
			originalSyntax = args;
			String syntax = name + " " + originalSyntax;
			
			Matcher matcher = commandPattern.matcher(originalSyntax);
			while(matcher.find())
			{
				if ( matcher.group(1) != null )
				{
					argumentNames.add(matcher.group(2));
					syntax = syntax.replace(matcher.group(1), "(\\S+)");
				}
				if ( matcher.group(3) != null )
				{
					argumentNames.add(matcher.group(4));
					syntax = syntax.replace(matcher.group(3), "( [\\S]*){0,1}");
				}
				if ( matcher.group(5) != null )
				{
					argumentNames.add(matcher.group(6));
					syntax = syntax.replace(matcher.group(5), "( [\\S\\s]*){0,}");
				}
			}
			this.syntax = Pattern.compile(syntax);
		}
		
		public Map<String, String> listArgs(String group)
		{
			Map<String, String> map = new HashMap<String, String>();
			
			String[] args = group.split(" ", 2);
			
			String free = "";
			for ( String arg : args )
				if ( arg.contains(":") )
					map.put(arg.split(":")[0], arg.split(":")[1]);
				else if ( arg.startsWith("--" ))
					map.put(arg.substring(2), "");
				else
					free += " " + arg;
			
			if ( !free.isEmpty() )
				map.put("free", free.trim());
			
			return map;
		}
		
		public Map<String, String> commandArgs(String[] args)
		{
			Map<String, String> map = new HashMap<String, String>();
			Matcher matcher = syntax.matcher(name + " " + toString(args));
			int max = matcher.groupCount();
			
			matcher.find();
			for ( int i = 0 ; i < max ; ++i )
				if ( matcher.group(i+1) != null && !matcher.group(i+1).trim().isEmpty() )
					if ( argumentNames.get(i).equals("args") )
						map.putAll(listArgs(matcher.group(i+1).trim()));
					else
						map.put(argumentNames.get(i), matcher.group(i+1).trim());
			
			return map;
		}
		
		@Override
		public int hashCode()
		{
			return originalSyntax.hashCode();
		}
		
		@Override 
		public boolean equals(Object o)
		{
			if ( !(o instanceof CommandSyntax) )
				return false;
			return Pattern.matches(((CommandSyntax)o).syntax.pattern(), originalSyntax);
		}
		
		//Utils
		public static String toString(String[] args)
		{
			if ( args.length < 1 )
				return "";
			
			String res = args[0];
			for ( int i = 1 ; i < args.length ; ++i )
				res += " " + args[i];
			return res;
		}
	}
	
	private class CommandBinding
	{
		private Method method; 
		private CommandSyntax syntax;
		private Class<?> clazz;
		private String perm;
		private boolean req;
		
		public CommandBinding(Class<?> clazz, Method method, CommandSyntax syntax, Command cmd) 
		{
			this.clazz = clazz;
			this.method = method;
			this.syntax = syntax;
			this.perm = cmd.perm();
			this.req = cmd.npc(); 
		}
		
		public boolean requiresNpc() {
			return req;
		}
		
		/**
		 * Executes the command.
		 * @param sender
		 * sender who wants to execute
		 * @param tNPC
		 * npc assigned to the sender
		 * @param args
		 * command arguments
		 * @return
		 * always true
		 */
		public Boolean execute(CommandSender sender, tNpc tNPC, String[] args)
		{
			if ( !perms.has(sender, perm) )
			{
				locale.sendMessage(sender, "error-nopermission-command");
				return true;
			}
			try 
			{
				method.invoke(objects.get(clazz), DtlTraders.getInstance(), sender, tNPC, syntax.commandArgs(args));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return true;
		}
	}
	
	final class ObjectEntry<K, V> implements Map.Entry<K, V> {
	    private final K key;
	    private V value;

	    public ObjectEntry(K key, V value) {
	        this.key = key;
	        this.value = value;
	    }

	    @Override
	    public K getKey() {
	        return key;
	    }

	    @Override
	    public V getValue() {
	        return value;
	    }

	    @Override
	    public V setValue(V value) {
	        V old = this.value;
	        this.value = value;
	        return old;
	    }
	}
}
