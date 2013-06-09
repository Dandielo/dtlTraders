package net.dandielo.citizens.traders_v3.bankers.types;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.tNpcManager;
import net.dandielo.citizens.traders_v3.tNpcStatus;
import net.dandielo.citizens.traders_v3.tNpcType;
import net.dandielo.citizens.traders_v3.bankers.Banker;
import net.dandielo.citizens.traders_v3.bankers.account.Account.AccountType;
import net.dandielo.citizens.traders_v3.bankers.backend.AccountLoader;
import net.dandielo.citizens.traders_v3.traders.clicks.ClickHandler;
import net.dandielo.citizens.traders_v3.traders.clicks.InventoryType;
import net.dandielo.citizens.traders_v3.traits.BankerTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;

@tNpcType(author = "dandielo", name = "private")
public class Private extends Banker {

	public Private(BankerTrait banker, WalletTrait wallet, Player player)
	{
		super(banker, wallet, player);

		//set the account
		account = AccountLoader.accLoader.getAccount(AccountType.PRIVATE, player.getName());
		account.applySettings(settings);
	}

	@Override
	public void onLeftClick(ItemStack itemInHand)
	{
	}

	@Override
	public boolean onRightClick(ItemStack itemInHand)
	{
		//create the inventory
		inventory = account.getInventory();

		//register the inventory as a traderInventory
		tNpcManager.instance().registerOpenedInventory(player, inventory);
		
		this.status = tNpcStatus.ACCOUNT_LOCKED;
		this.baseStatus = tNpcStatus.ACCOUNT_LOCKED;
		
		//open it ;P
		player.openInventory(inventory);
		return true;
	}

	
	@ClickHandler(inventory = InventoryType.TRADER, 
	status = { tNpcStatus.ACCOUNT_LOCKED, tNpcStatus.ACCOUNT_UNLOCKED, tNpcStatus.ACCOUNT_MANAGE })
	public void tabClick(InventoryClickEvent e)
	{
		System.out.print(e.getCurrentItem());
	}
	
}
