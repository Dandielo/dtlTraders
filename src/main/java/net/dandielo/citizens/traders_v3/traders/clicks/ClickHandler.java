package net.dandielo.citizens.traders_v3.traders.clicks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.dandielo.citizens.traders_v3.tNpcStatus;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ClickHandler {
	public tNpcStatus[] status();
	public boolean shift() default false;
	public InventoryType inventory(); 
}
