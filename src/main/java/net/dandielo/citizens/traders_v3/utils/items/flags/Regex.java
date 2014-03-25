package net.dandielo.citizens.traders_v3.utils.items.flags;

import net.dandielo.citizens.traders_v3.utils.items.Attribute;
import net.dandielo.citizens.traders_v3.utils.items.ItemFlag;

@Attribute(name = "Regex check", key=".regex", standalone = true)
public class Regex extends ItemFlag {
	public Regex(String key) {
		super(key);
	}
}
