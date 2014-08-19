package net.dandielo.citizens.traders_v3.traders.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Owner;
import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.traders_v3.core.dB;

public class Settings extends GlobalSettings {	
    //the Npc associated with these settings
	private final NPC npc;
	private OfflinePlayer owner = null;
	private String type = "server"; //needs to be here when some1 will apply it with the /trait command
	
	//npc related settings
	private int stockSize = GlobalSettings.stockSize;
	private String stockNameFormat = GlobalSettings.stockNameFormat;
	private String stockStart = GlobalSettings.stockStart;
	
	//pattern settings
	private List<String> patterns = new ArrayList<String>();
	
	public Settings(NPC npc) {
		this.npc = npc;
	}
	
	public NPC getNPC()
	{
		return npc;
	}
	
	//trader type
	public String getType()
	{
		return type;
	}
	
	/**
	 * Should be used only when creating a new NPC and setting default values. 
	 * Sets the traders type
	 */
	public void setType(String type)
	{
		this.type = type;
	}
	
	//trader owner
	public OfflinePlayer getOwner()
	{
		return owner;
	}
	public void setOwner(OfflinePlayer owner)
	{
		this.owner = owner;
	}
	
	//npc owner
	public String getNpcOwner()
	{
		return npc.getTrait(Owner.class).getOwner();
	}
	
	//stock size
	public int getStockSize() {
		return stockSize;
	}
	
	public void setStockSize(int size)
	{
		stockSize = size;
	}
	
	//stock name format
	public String getStockName() {
		//get the npc name and change all abstract color codes into real ones
		//#request 3 on PiratePad 
		return stockNameFormat.replace("{npc}", npc.getName()).replace('&', '§').replace('^', '§');
	}
	
	public String getStockFormat() {
		return stockNameFormat;
	}
	
	public void setStockFormat(String format)
	{
		stockNameFormat = format;
	}
	
	//start stock
	public String getStockStart()
	{
		return stockStart;
	}
	
	public void setStockStart(String stock)
	{
		stockStart = stock;
	}
	
	//other
	public String getManagerStockStart()
	{
		return mmStockStart;
	}

	//loading and saving
	@SuppressWarnings("unchecked")
	public void load(DataKey data) 
	{
		//debug info
		dB.info("Loading trader settings for: ", this.npc.getName());
		
		//load trader settings
		type = data.getString("type");
		
		//load the owner from UUID String
		String ownerUUID = data.getString("owner-uuid", "none");
		owner = ownerUUID.equals("none") ? null : Bukkit.getOfflinePlayer(UUID.fromString(ownerUUID));
		
		/* compatibility start */
		if ( type.equals("trader") )
			type = data.getString("trader");
	    /* compatibility end */
		
		//load stock settings
		stockSize = data.getInt("stock.size", GlobalSettings.stockSize);
		stockNameFormat = data.getString("stock.format", GlobalSettings.stockNameFormat);
		stockStart = data.getString("stock.default", GlobalSettings.stockStart);
		
		//load pattern settings 
		if ( data.getRaw("patterns") != null )
		    patterns.addAll((List<String>)data.getRaw("patterns"));
	}
	
	public void save(DataKey data) 
	{
		//debug info
		dB.info("Saving trader settings for:", this.npc.getName());
		
		//save trader settings
		data.setString("type", type);
		data.setString("owner-uuid", owner == null ? "none" : owner.getUniqueId().toString());
		data.setRaw("stock", null);
		
		//save patterns
		data.setRaw("patterns", patterns);
		
		//save stock settings
		if ( stockSize != GlobalSettings.stockSize )
			data.setInt("stock.size", stockSize);
		if ( !stockNameFormat.equals(GlobalSettings.stockNameFormat) )
			data.setString("stock.format", stockNameFormat);
		if ( !stockStart.equals(GlobalSettings.stockStart) )
			data.setString("stock.default", stockStart);
			
	} 

	public List<String> getPatterns()
	{
		return patterns;
	}

}
