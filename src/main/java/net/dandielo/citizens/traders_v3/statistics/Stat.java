package net.dandielo.citizens.traders_v3.statistics;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
public @interface Stat {
	public String name();
	public UpdatePolicy update() default UpdatePolicy.ReadOnly;
	public String callback() default "";
	
	public static enum UpdatePolicy
	{
		Hard, Custom, ReadOnly
	}
}
