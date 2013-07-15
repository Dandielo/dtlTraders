package net.dandielo.citizens.traders_v3.statistics;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

import net.dandielo.citizens.traders_v3.core.PluginSettings;
import net.dandielo.citizens.traders_v3.core.dB;

public class StatisticServer implements Runnable {
	
	//the socket listener
	private ServerSocket server; 
	private boolean stop = false;
	
	//statistic server port
	private static int port = 4446;
	
	public StatisticServer() throws IOException
	{
		server = new ServerSocket(port);
	}
	
	/**
	 * Starts listening for statistics request from websites
	 * @throws IOException 
	 */
	public void listen() throws IOException
	{		
		Socket incoming;
		while( !stop )
		{
			try
			{
				incoming = server.accept();
				
				StatisticRequest request = new StatisticRequest(incoming);
				
				//request thread
				Thread t = new Thread(request);
				t.start();
				
			}
			catch( IOException e )
			{
				dB.high("Connection falied");
				e.printStackTrace();
			}
		}
		server.close();
	}
	
	public static void logRequest()
	{
		try
		{
		    URL url = new URL("http://dtltraders.dandielo.net/log/"+PluginSettings.getLogUser());
		    URLConnection con = url.openConnection();
		    con.connect();
		    con.getContent();
		}
		catch( IOException e )
		{
		}
	}

	@Override
	public void run()
	{
		try
		{
			listen();
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stop()
	{
		stop = true;
	}
}
