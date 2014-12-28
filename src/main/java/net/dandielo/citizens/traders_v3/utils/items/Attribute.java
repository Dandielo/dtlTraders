package net.dandielo.citizens.traders_v3.utils.items;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.dandielo.citizens.traders_v3.tNpcStatus;

//import net.dandielo.citizens.traders_v3.traders.Trader.Status;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Attribute {
	
	/**
	 * Attribute name, used in exceptions and debug information
	 * @return
	 *     returns the name of the attribute (required)
	 */
    public String name();
    
	/**
	 * Attribute key, used for saving and to comparing with other attributes (unique, required)
	 * @return
	 *     returns the name of the attribute, required
	 */
    public String key();
    
    /**
     * Attribute sub-keys, used to create more attributes based on the same class, later you can check what sub-key was used.
     * @return 
     *     Returns a list of all sub-keys.
     */
    public String[] sub() default { };
    
    /**
     * Priority value used for valid searching of item definitions in price patterns, if negative then it wont use this attribute for the priority check.
     * @return
     *     returns the priority
     */
    public int priority() default -1;
    
    /**
     * Sets if the given attribute is required for each item to work properly.
     * <br><br>
     * <strong>Default: false</strong> 
     * @return
     *     true if required, false otherwise
     */
    public boolean required() default false;
    
    /**
     * Determines if the Attribute needs to be present in both items when a weak or strong check is done.  
     * @return
     *     true if only one instance is needed
     */
    public boolean standalone() default false;
    
    /**
     * Determines when should be additional lore information be assigned to an item in stock.
     * This lore will not persist, it's added only temporary
     * <br><br>
     * <strong>Default: empty</strong> 
     * @return
     *     Status array when it should appear
     */
    public tNpcStatus[] status() default { };
}
