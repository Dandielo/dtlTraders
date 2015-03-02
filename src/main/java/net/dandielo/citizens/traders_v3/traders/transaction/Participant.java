package net.dandielo.citizens.traders_v3.traders.transaction;

import java.util.UUID;

public interface Participant {
	public boolean isPlayer();
	public UUID getUUID();
	
	boolean check(double amount);
	boolean withdraw(double amount);
	boolean deposit(double amount);
}
