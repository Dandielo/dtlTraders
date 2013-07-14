package net.dandielo.citizens.traders_v3.statistics;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class StatisticManager {
	public static StatisticManager instance = new StatisticManager();	
	
	Map<String, StatElement> stats = new HashMap<String, StatElement>();
	
	private StatisticManager()
	{
	}
	
	public void registerListener(String plugin, StatListener listener)
	{
		Class<? extends StatListener> clazz = listener.getClass();
		
		for ( Method method : clazz.getMethods() )
		{
			if ( method.isAnnotationPresent(Stat.class) )
			{
				Stat stat = method.getAnnotation(Stat.class);
				
				stats.put(plugin + "." + stat.name(), new StatElement(listener, method));
			}
		}
		
		for ( Field field : clazz.getFields() )
		{
			if ( field.isAnnotationPresent(Stat.class) )
			{
				Stat stat = field.getAnnotation(Stat.class);
				
				stats.put(plugin + "." + stat.name(), new StatElement(listener, field));
			}
		}
	}
	
	public static Object getStat(String plugin, String stat)
	{
		return instance.stats.get(plugin + "." + stat).stat();
	}
	
	class StatElement 
	{
		private Method method;
		private Field field;
		private StatListener listener;
		
		public StatElement(StatListener listener, Method method)
		{
			this.method = method;
			this.listener = listener; 
		}
		
		public StatElement(StatListener listener, Field field)
		{
			this.field = field;
			this.listener = listener; 
		}
		
		public Object run() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
		{
			Object val = null;
			if ( field != null )
			{
				field.setAccessible(true);
				val = field.get(listener);
				field.setAccessible(false);
			}
			else
			if ( method != null )
			{
				val = method.invoke(listener);
			}
			return val;
		}
		
		public Object stat()
		{
			try
			{
				return run();
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
			return null;
		}
	}
}
