package net.dandielo.citizens.traders_v3.utils.items;

public @interface DataNode {
    public String name();
    public String saveKey();
    public Class<? extends ItemData>[] required() default { };
}
