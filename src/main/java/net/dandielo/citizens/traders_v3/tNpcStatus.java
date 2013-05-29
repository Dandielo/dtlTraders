package net.dandielo.citizens.traders_v3;

import static net.dandielo.citizens.traders_v3.tNpcStatus.StatusType.*;

public enum tNpcStatus
{
	/*
	 * Trader status declarations
	 */
	SELL(TRADER, "sell"), 
	BUY(TRADER, "buy"), 
	SELL_AMOUNTS(TRADER, "sAmounts"),
	
	MANAGE_SELL(TRADER, "mSell"), 
	MANAGE_BUY(TRADER, "mBuy"), 
	MANAGE_AMOUNTS(TRADER, "mAmounts"),
	MANAGE_PRICE(TRADER, "mPrice"),
	MANAGE_LIMIT(TRADER, "mLimit"), 
	MANAGE_UNLOCKED(TRADER, "mUnlocked")
	
	/*
	 * Banker status declarations
	 */
	
	
	/*
	 * Auction status declarations
	 */
	;
	
	
	StatusType type;
	String statusName;
	
	/**
	 * Status constructor for each core tNpc  
	 * @param type
	 * the status type
	 * @param statusName
	 * the status name
	 */
	tNpcStatus(StatusType type, String statusName)
	{
		
	}
	
	/**
	 * as to string just return the status name
	 */
	@Override
	public String toString()
	{
		return statusName;
	}
    
	/**
	 * The type of a tNpc status determines what more specific tNpc type uses what status.  
	 * @author dandielo
	 *
	 */
	public static enum StatusType
    {
    	TRADER, BANKER, AUCTION
    }
	
	/*
	 * Trader methods
	 */
	
	public boolean inManagementMode()
	{
		return !( this.equals(SELL) || this.equals(BUY) || this.equals(SELL_AMOUNTS) ); 
	}
	
	public static tNpcStatus parseBaseManageStatus(tNpcStatus oldStatus, tNpcStatus newStatus)
	{
		return newStatus.equals(MANAGE_SELL) || newStatus.equals(MANAGE_BUY) ||
				newStatus.equals(SELL) || newStatus.equals(BUY) ? newStatus : oldStatus;
	}
	
	public static tNpcStatus baseManagementStatus(String status)
	{
		if ( MANAGE_SELL.name().toLowerCase().contains(status) )
			return MANAGE_SELL;
		return MANAGE_BUY;
	}
	
	public static tNpcStatus baseStatus(String status)
	{
		if ( SELL.name().toLowerCase().equals(status) )
			return SELL;
		return BUY;
	}

	public String asStock() {
		return this.equals(BUY) || this.equals(MANAGE_BUY) ? "buy" : "sell";
	}
}