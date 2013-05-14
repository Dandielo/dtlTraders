package net.dandielo.citizens.traders_v3.traders.setting;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Owner;
import net.citizensnpcs.api.util.DataKey;

public class Settings extends GlobalSettings {	
    //the Npc associated with these settings
	private final NPC npc;
	private String owner;
	private String type;
	
	//npc related settings
	private int stockSize;
	private String stockNameFormat;
	private String stockDefault;
	
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
	
	//trader owner
	public String getOwner()
	{
		return owner;
	}
	public void setOwner(String owner)
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
	
	public String getStockName(String npc) {
		return stockNameFormat.replace("{npc}", npc);
	}

	//loading and saving
	public void load(DataKey data) {
		//load trader settings
		type = data.getString("type");
		
		//load stock settings
		stockSize = data.getInt("stock.size", GlobalSettings.stockSize);
		stockNameFormat = data.getString("stock.format", GlobalSettings.stockNameFormat);
		stockDefault = data.getString("stock.default", GlobalSettings.stockDefault);
	}
	
	public void save(DataKey data) {
		//save trader settings
		data.setString("type", type);
		
		//save stock settings
		if ( stockSize != GlobalSettings.stockSize )
			data.setInt("stock.size", stockSize);
		if ( !stockNameFormat.equals(GlobalSettings.stockNameFormat) )
			data.setString("stock.format", stockNameFormat);
		if ( !stockDefault.equals(GlobalSettings.stockDefault) )
			data.setString("stock.default", stockDefault);
			
	}

}
