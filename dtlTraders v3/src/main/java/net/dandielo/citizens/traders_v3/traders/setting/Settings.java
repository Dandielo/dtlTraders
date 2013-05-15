package net.dandielo.citizens.traders_v3.traders.setting;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Owner;
import net.citizensnpcs.api.util.DataKey;

public class Settings extends TGlobalSettings {	
    //the Npc associated with these settings
	private final NPC npc;
	private String owner;
	private String type = "server";
	
	//npc related settings
	private int stockSize = TGlobalSettings.stockSize;
	private String stockNameFormat = TGlobalSettings.stockNameFormat;
	private String stockDefault = TGlobalSettings.stockDefault;
	
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
	
	public String getStockName() {
		return stockNameFormat.replace("{npc}", npc.getName());
	}
	
	public String getStockDefault()
	{
		return stockDefault;
	}

	//loading and saving
	public void load(DataKey data) {
		//load trader settings
		type = data.getString("type");
		
		//load stock settings
		stockSize = data.getInt("stock.size", TGlobalSettings.stockSize);
		stockNameFormat = data.getString("stock.format", TGlobalSettings.stockNameFormat);
		stockDefault = data.getString("stock.default", TGlobalSettings.stockDefault);
	}
	
	public void save(DataKey data) {
		//save trader settings
		data.setString("type", type);
		
		//save stock settings
		if ( stockSize != TGlobalSettings.stockSize )
			data.setInt("stock.size", stockSize);
		if ( !stockNameFormat.equals(TGlobalSettings.stockNameFormat) )
			data.setString("stock.format", stockNameFormat);
		if ( !stockDefault.equals(TGlobalSettings.stockDefault) )
			data.setString("stock.default", stockDefault);
			
	}

}
