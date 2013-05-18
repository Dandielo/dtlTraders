package net.dandielo.citizens.traders_v3.core.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
	String name();
	String syntax() default "";
	String desc() default "";
	String usage() default "";
	String perm() default "";
	boolean npc() default true;
}
