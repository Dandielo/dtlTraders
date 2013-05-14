package net.dandielo.citizens.traders_v3.traders.types;

import org.bukkit.entity.Player;

import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.TraderType;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;

@TraderType(name="server", author="dandielo")
public class Server extends Trader {

	public Server(TraderTrait trader, WalletTrait wallet, Player player) {
		super(trader, wallet, player);
	}

	@Override
	public void onLeftClick() {
		if ( status.inManagementMode() )
			status = getDefaultStatus();
		else
			status = getDefaultManagementStatus();
	}

	@Override
	public void onRightClick()
	{
		//open the inventory
		player.openInventory(stock.getInventory());
	}

	@Override
	public void onInventoryClick() {
	}

}
