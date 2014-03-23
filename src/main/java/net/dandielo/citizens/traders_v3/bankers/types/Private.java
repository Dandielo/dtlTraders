package net.dandielo.citizens.traders_v3.bankers.types;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.tNpcManager;
import net.dandielo.citizens.traders_v3.tNpcStatus;
import net.dandielo.citizens.traders_v3.tNpcType;
import net.dandielo.citizens.traders_v3.bankers.Banker;
import net.dandielo.citizens.traders_v3.bankers.account.Account.AccountType;
import net.dandielo.citizens.traders_v3.bankers.backend.AccountLoader;
import net.dandielo.citizens.traders_v3.bankers.tabs.Tab;
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
		account = AccountLoader.accLoader.getAccount(AccountType.PRIVATE, player);
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
		
		//set the start status
		this.status = tNpcStatus.ACCOUNT_LOCKED;
		this.baseStatus = tNpcStatus.ACCOUNT_LOCKED;
		
		tab = account.getTab(0);
		account.tabSwitch(tab, inventory);
		
		//open it ;P
		player.openInventory(inventory);
		return true;
	}

	
	@ClickHandler(inventory = InventoryType.TRADER, shift = true,
	status = { tNpcStatus.ACCOUNT_LOCKED, tNpcStatus.ACCOUNT_UNLOCKED, tNpcStatus.ACCOUNT_MANAGE })
	public void tabClick(InventoryClickEvent e)
	{
		if ( !account.isUIRow(e.getSlot()) ) return;
		e.setCancelled(true);
		
		//if air or null then return
		if ( e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR) )
			return;
		
		if ( e.isShiftClick() )
		{
			if ( e.isRightClick() )
			{
				Tab tab = account.getTab(e.getSlot()%9);

				//end if its the same tab
				if ( tab != null ) return;
				
				//check permission if some can but a tab ;P
				if ( !perms.has(player, "dtl.banker.buy-tab") ) return;
				
				//now we maybe can buy the tab :P
				if ( !tabTransaction(e.getSlot()%9) )
				{
					//send message
					locale.sendMessage(player, "banker-tab-buy-fail");
					return;
				}
				//send message
				locale.sendMessage(player, "banker-tab-buy-success");

				//add the new tab
				tab = Tab.createNewTab(AccountType.PRIVATE);
				//success
				account.addTab(tab);
				
				//save and lock the previous opened tab
				if ( status.equals(tNpcStatus.ACCOUNT_UNLOCKED) )
				{
					//send the message
					locale.sendMessage(player, "banker-tab-locked", "tab", this.tab.getName());
					this.saveItemsUpponLocking();
				}

				//switch to the new tab
				this.tab = tab;			
				account.tabSwitch(tab, inventory);
				status = tNpcStatus.ACCOUNT_LOCKED;

				//send the message
				locale.sendMessage(player, "banker-tab-switch", "tab", tab.getName());
			}
			//left click
			//new item icon
			else
			{
			}
		}
		else
		{
			if ( e.isLeftClick() )
			{
				Tab tab = account.getTab(e.getSlot()%9);

				//end if its the same tab
				if ( tab == null || this.tab.equals(tab) ) return;

				if ( status.equals(tNpcStatus.ACCOUNT_UNLOCKED) )
				{
					//send the message
					locale.sendMessage(player, "banker-tab-locked", "tab", this.tab.getName());
					this.saveItemsUpponLocking();
				}

				this.tab = tab;			
				account.tabSwitch(tab, inventory);
				status = tNpcStatus.ACCOUNT_LOCKED;

				//send the message
				locale.sendMessage(player, "banker-tab-switch", "tab", tab.getName());
			}
			else 
			{
				Tab tab = account.getTab(e.getSlot()%9);

				//end if its the same tab
				if (!this.tab.equals(tab) )
				{
					if ( status.equals(tNpcStatus.ACCOUNT_UNLOCKED) )
					{
						//send the message
						locale.sendMessage(player, "banker-tab-locked", "tab", this.tab.getName());
						this.saveItemsUpponLocking();
					}

					this.tab = tab;			
					account.tabSwitch(tab, inventory);
					
					status = tNpcStatus.ACCOUNT_LOCKED;

					//send the message
					locale.sendMessage(player, "banker-tab-switch", "tab", tab.getName());
				}
				
				if ( status.equals(tNpcStatus.ACCOUNT_LOCKED) )
				{
					status = tNpcStatus.ACCOUNT_UNLOCKED;

					//send the message
					locale.sendMessage(player, "banker-tab-unlocked", "tab", tab.getName());
				}
				else
				{
					status = tNpcStatus.ACCOUNT_LOCKED;
					this.saveItemsUpponLocking();

					//send the message
					locale.sendMessage(player, "banker-tab-locked", "tab", tab.getName());
				}
			}
		}
	}
	
	@ClickHandler(inventory = InventoryType.TRADER, 
	status = { tNpcStatus.ACCOUNT_LOCKED, tNpcStatus.ACCOUNT_MANAGE })
	public void tabEventCancel(InventoryClickEvent e)
	{
		e.setCancelled(true);
	}
	
	@ClickHandler(inventory = InventoryType.TRADER, 
	status = { tNpcStatus.ACCOUNT_UNLOCKED})
	public void onUnlocked(InventoryClickEvent e)
	{//nothing to do here
		
	}
}
