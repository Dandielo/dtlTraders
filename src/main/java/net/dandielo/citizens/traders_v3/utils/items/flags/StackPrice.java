package net.dandielo.citizens.traders_v3.utils.items.flags;

import java.util.List;

import net.dandielo.citizens.traders_v3.TEntityStatus;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.utils.items.StockItemFlag;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;

@Attribute(name="StackPrice", key = ".sp", standalone = true)
public class StackPrice extends StockItemFlag {
	public StackPrice(dItem item, String key) {
		super(item, key);
	}

	@Override
	public void getDescription(TEntityStatus status, List<String> lore)
	{
		if (!status.inManagementMode()) return;

		//add the lore to the item
		lore.add(LocaleManager.locale.getMessage("key-value", "key", "#stack-price", "value", "enabled"));
	}
	
}
