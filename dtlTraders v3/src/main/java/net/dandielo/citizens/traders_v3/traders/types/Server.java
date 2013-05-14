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
		if ( status.inManagementMode() )
		    inventory = stock.getManagementInventory(status);
		else
			inventory = stock.getInventory(status);
		player.openInventory(inventory);
	}

	@Override
	public void onInventoryClick() {
	}

	@Override
	public void onManageInventoryClick() {
		return;
	}

}
