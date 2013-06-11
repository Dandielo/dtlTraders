package net.dandielo.citizens.traders_v3.traits;

import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.traders_v3.bankers.setting.Settings;
import net.dandielo.citizens.traders_v3.core.dB;

public class BankerTrait extends Trait {

	private Settings settings;
	
	public BankerTrait()
	{
		super("banker");
	}
	
	//get settings for this Trader NPC
	public Settings getSettings() {
		return settings;
	}

	public String getType() {
		return settings.getType();
	}

	//events
	@Override
	public void onRemove()
	{
	}

	@Override
	public void onAttach()
	{
		//debug info
		dB.info("Banker trait attached to:", npc.getName());

		settings = new Settings(this.npc);
	}

	@Override
	public void load(DataKey data)
	{
		//load all settings
		settings.load(data);
	}

	@Override
	public void save(DataKey data)
	{
		//save settings
		settings.save(data);
	}

}
