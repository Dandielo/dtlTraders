package net.dandielo.citizens.traders_v3.traders.clicks;

public enum InventoryType {
	TRADER, PLAYER;
	
	public boolean equals(boolean b)
	{
		return b ? equals(TRADER) : equals(PLAYER);
	}
}
