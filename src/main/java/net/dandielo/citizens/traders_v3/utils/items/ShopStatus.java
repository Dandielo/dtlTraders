package net.dandielo.citizens.traders_v3.utils.items;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.dandielo.citizens.traders_v3.TEntityStatus;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ShopStatus {
    /**
     * Determines when should be additional lore information be assigned to an item in stock.
     * This lore will not persist, it's added only temporary
     * <br><br>
     * <strong>Default: empty</strong> 
     * @return
     *     Status array when it should appear
     */
    public TEntityStatus[] status() default { };
}
