package net.dandielo.citizens.traders_v3.statistics;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import net.dandielo.citizens.traders_v3.tNpcManager;
import net.dandielo.citizens.traders_v3.core.PluginSettings;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;

public class TraderStats implements StatListener, Runnable {
	
	public static int req = 0;
	@Stat(name = "requests")
	public int currentTransactions()
	{
		return req++;
	}
	
	@Stat(name = "invs")
	public int transactions()
	{
		return tNpcManager.instance().getInventories().size();
	}
	
	@Stat(name = "log")
	public String logUpdate()
	{
		StringWriter result = new StringWriter();
	
		result.append("[");
		for ( LogEntry log : logs )
			result.append(log.asJSON()+ ",");
		result.append("[]]");
		
		logs.clear();
		
		return result.toString();
	}
	
	private static List<LogEntry> logs = new ArrayList<LogEntry>();
	
	public static void traderLog(Trader trader, String action, StockItem item, int amount)
	{
		logs.add(new LogEntry(trader, action, item, amount));
		if ( logs.size() >= PluginSettings.logUpdateCounter() )
		{
			StatisticServer.logRequest();
		}
	}
	
	private boolean stop = false;
	
	@Override
	public void run()
	{
		while(!stop)
		{
			try
			{
				//bow we will wait 5 seconds, this should help a bit (I hope so)!
				Thread.sleep(5000);
			}
			catch( InterruptedException e )
			{
				e.printStackTrace();
			}
			if ( logs.size() >= PluginSettings.logUpdateCounter() )
			{
				StatisticServer.logRequest();
			}
		}
	}
	
	public void stop()
	{
		stop = true;
	}
	
	static class LogEntry
	{
		private String player;
		private String trader;
		private String action;
		private String money;
		private String item;
		private String amount;
		
		private Date date;
		
		public LogEntry(Trader trader, String action, StockItem item, int amount)
		{
			this.date = new Date();
			this.trader = trader.getNPC().getName();
			this.player = trader.getPlayer().getName();
			this.action = action;
			this.money = item.getPriceFormated();
			this.amount = String.valueOf(amount);
			this.item = item.toString();
		}
		
		public String asJSON() 
		{
			StringWriter strWriter = new StringWriter();

			strWriter.append("[");
			strWriter.append("{\"date\":\""+date+"\"},");
			strWriter.append("{\"trader\":\""+trader+"\"},");
			strWriter.append("{\"player\":\""+player+"\"},");
			strWriter.append("{\"action\":\""+action+"\"},");
			strWriter.append("{\"money\":\""+money+"\"},");
			strWriter.append("{\"amount\":\""+amount+"\"},");
			strWriter.append("{\"item\":\""+item+"\"}");
			strWriter.append("]");
		/*	new JSONWriter(strWriter).array().array()
			.object().key("date").value(date).endObject()
			.object().key("trader").value(trader).endObject()
			.object().key("player").value(player).endObject()
			.object().key("action").value(action).endObject()
			.object().key("money").value(money).endObject()
			.object().key("amount").value(amount).endObject()
			.object().key("item").value(item).endObject()
			.endArray().endArray();;*/
			
			return strWriter.toString();
		}
	}
}
