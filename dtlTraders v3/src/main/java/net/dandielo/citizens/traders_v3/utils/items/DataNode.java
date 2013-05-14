package net.dandielo.citizens.traders_v3.utils.items;

import net.dandielo.citizens.traders_v3.traders.Trader.Status;

public @interface DataNode {
    public String name();
    public String saveKey();
    public boolean assignLore() default false;
    public Status[] assignStatus() default { };
}
