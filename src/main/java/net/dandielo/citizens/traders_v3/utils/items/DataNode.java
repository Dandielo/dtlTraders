package net.dandielo.citizens.traders_v3.utils.items;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.dandielo.citizens.traders_v3.traders.Trader.Status;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataNode {
    public String name();
    public String saveKey();
    public boolean byDefault();
    public boolean assignLore() default false;
    public Status[] assignStatus() default { };
}
