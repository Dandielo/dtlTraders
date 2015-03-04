package net.dandielo.citizens.traders_v3.traders.transaction;

import java.util.List;

/** Handles a currency used by shops.
 * 
 * This interface should be used for EACH attribute which extends the "p" key. It allows to hook
 * into the traders pricing system and allow to set custom currency prices for items.
 * 
 * @author dandielo
 * @since dtlTraders 3.3.0
 */
public interface CurrencyHandler {
	/** Called when a transaction should be finalized.
	 * 
	 * This function is called when an item has meet all requirements to be sold by a player or trader, 
	 * and the transaction should be finalized. If you want to change a players currency amount, you 
	 * should do this here.
	 * 
	 * This function should return false ONLY then, when a transaction wasn't properly finalized. This will
	 * occur in an error on the server letting the server owner know that something bad happend.
	 * 
	 * @param tinfo
	 *   Contains all vital informations about a transaction that should be finalized. 
	 * @return
	 *   <strong>TRUE</strong> on success, <strong>FALSE</strong> on heavy errors.
	 */
	public boolean finalizeTransaction(TransactionInfo tinfo);
	
	/** Called to check if the requirements or this currency are meet.
	 * 
	 * This function is always called before a transaction is finalized, it should check the players 
	 * account with the price multiplier by the amount requested, unless the <strong>.stack-price</strong> flag is set.
	 * 
	 * @param tinfo
	 *   Contains all vital informations about a transaction that will be performed. If something 
	 *   should cancel the transaction <strong>FALSE</strong> will be returned.
	 * @return
	 *   <strong>TRUE</strong> if the players account has enough "value", <strong>FALSE</strong> otherwise.
	 */
	public boolean allowTransaction(TransactionInfo tinfo);
	
	/** Called to display the items currency value.
	 * 
	 * This  function is called when a player opens a trader inventory. Each item will then request a 
	 * description that shows the requirements to buy an item.  
	 * 
	 * @param tinfo
	 *   Contains all vital informations about a transaction item that should be described.
	 * @param lore
	 *   A list of strings that will create the items requirement description. It's good practice to 
	 *   also check if the player meets the requirements, returning the amount 
	 *   as green <i>(requirement meet)</i> or red <i>(requirement not meet)</i>. 
	 */
	public void getDescription(TransactionInfo tinfo, List<String> result);
	
	public double getTotalPrice(TransactionInfo info);
	
	/** The currency name
	 * 
	 * @return
	 *   The name of the currency. 
	 */
	public String getName();
	
	
}
