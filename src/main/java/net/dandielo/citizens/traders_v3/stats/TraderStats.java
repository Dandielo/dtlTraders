package net.dandielo.citizens.traders_v3.stats;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONWriter;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.stats.api.Listener;
import net.dandielo.stats.api.Stat;
import net.dandielo.stats.api.Stat.RequestType;
import net.dandielo.stats.api.Updater;

@Stat(name = "trader")
public class TraderStats implements Listener, Updater {
	
	@Stat(name="list", requestType = RequestType.GET)
	public Object getList() throws JSONException
	{
		//json writer
		StringWriter writer = new StringWriter();
		JSONWriter json = new JSONWriter(writer);
		
		//itterate through NPC's
		Iterator<NPC> it = CitizensAPI.getNPCRegistry().iterator();
		json.object();
		while(it.hasNext())
		{
			NPC npc = it.next();
			if ( npc.hasTrait(TraderTrait.class) )
				json.key(String.valueOf(npc.getId())).value(npc.getName());
		}
		json.endObject();
		
		//return json string
		return writer.toString();
	}
	
	@Stat(name="{id}/setting/{setting}", requestType = RequestType.GET)
	public Object getSetting(String id, String setting)
	{
		//check if npc is valid
		NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.valueOf(id));
		if ( npc == null || !npc.hasTrait(TraderTrait.class) ) return false;
		
		//return the requested thing
		Settings settings = npc.getTrait(TraderTrait.class).getSettings();
		if ( setting.equalsIgnoreCase("stockformat") )
			return settings.getStockFormat();
		if ( setting.equalsIgnoreCase("stockstart") )
			return settings.getStockStart();
		if ( setting.equalsIgnoreCase("stocksize") )
			return settings.getStockSize();
		return false;
	}
	
	@Stat(name="{id}/setting/{setting}", requestType = RequestType.UPDATE)
	public void setSetting(String id, String setting, String value)
	{
		//check if npc is valid
		NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.valueOf(id));
		if ( npc == null || !npc.hasTrait(TraderTrait.class) ) return;
		
		//return the requested thing
		Settings settings = npc.getTrait(TraderTrait.class).getSettings();
		if ( setting.equalsIgnoreCase("stockformat") )
			settings.setStockFormat(value);
		if ( setting.equalsIgnoreCase("stockstart") )
			settings.setStockStart(value);
		if ( setting.equalsIgnoreCase("stocksize") )
			settings.setStockSize(Integer.valueOf(value));
	}
}
