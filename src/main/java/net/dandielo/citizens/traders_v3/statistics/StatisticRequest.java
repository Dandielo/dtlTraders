package net.dandielo.citizens.traders_v3.statistics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dandielo.citizens.traders_v3.core.PluginSettings;

public class StatisticRequest implements Runnable {
	private Socket server;
	private String line, input;

	StatisticRequest(Socket server)
	{
		this.server = server;
	}

	public void run()
	{

		input = "";

		try
		{
			// Get input from the client
			BufferedReader in = new BufferedReader( new InputStreamReader(server.getInputStream()));
			PrintWriter out = new PrintWriter(server.getOutputStream(), true);
			
			line = in.readLine();
			Request req = new Request(line);
			
			if ( !req.isValid() || !req.getPlugin().equals("__auth") )
			{
				out.close();
				return;
			}
			
			if ( !req.getStat().equals("login") || !req.getAction().equals("pass") )
			{
				out.println("invalid login");
				out.close(); 
				return;
			}
			
			out.println(PluginSettings.getLogUser() + ":" + PluginSettings.getLogPass());
			
			do
			{
				input = "";
				while( (line = in.readLine()) != null && !line.equals(".") )
				{
					input += line;
				}
				if ( line == null ) break;
				
			    req = new Request(input);
				if ( req.isValid() )
				{
					out.println(StatisticManager.getStat(req.getPlugin(), req.getStat()));
				}
				
			} while(true);
			
			out.close();
		}
		catch( IOException ioe )
		{
			System.out.println("IOException on socket listen: " + ioe);
			ioe.printStackTrace();
		}
	}

	static class Request
	{
		private static Pattern pattern = Pattern.compile("(([^:]+):{0,1})");
		private static String[] bld = { "plugin", "stat", "action", "value" };

		private Map<String, String> data = new HashMap<String, String>();
		private boolean valid = false;

		public Request(String strReq)
		{
			Matcher matcher = pattern.matcher(strReq);

			int i = 0;
			while(matcher.find() && i < 4)
			{
				data.put(bld[i], matcher.group(2));
				++i;
			}
			if ( i >= 3 )
				valid = true;
		}

		public String getPlugin()
		{
			return data.get("plugin");
		}

		public String getAction()
		{
			return data.get("action");
		}

		public String getStat()
		{
			return data.get("stat");
		}

		public String getValue()
		{
			return data.get("value");
		}

		public boolean isValid()
		{
			return valid;
		}

		public static enum Action 
		{
			GET, SET, UPDATE, RESET
		}
	}
}
