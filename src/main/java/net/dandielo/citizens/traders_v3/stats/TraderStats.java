package net.dandielo.citizens.traders_v3.stats;

import net.dandielo.stats.api.Listener;
import net.dandielo.stats.api.Stat;
import net.dandielo.stats.api.Stat.RequestType;
import net.dandielo.stats.api.Updater;

@Stat(name = "trader/{id}")
public class TraderStats implements Listener, Updater {
	
	@Stat(name="check", requestType = RequestType.GET)
	public Object isTrader(String id)
	{
		return false;
	//	return CitizensAPI.getNPCRegistry().getById(Integer.parseInt(id)).hasTrait(TraderTrait.class);
	} 
	
	@Stat(name="name", requestType = RequestType.UPDATE)
	public void setName(String id, String value)
	{
	//	CitizensAPI.getNPCRegistry().getById(Integer.parseInt(id)).setName(value);
	}
}
