package net.dandielo.citizens.traders_v3.traders.types;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;

public class Private extends Trader {

	public Private(TraderTrait trader, WalletTrait wallet, Player player)
	{
		super(trader, wallet, player);
	}

	@Override
	public void onLeftClick(ItemStack itemInHand)
	{
	}

	@Override
	public boolean onRightClick(ItemStack itemInHand)
	{
		return false;
	}

}
