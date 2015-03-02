package net.dandielo.citizens.traders_v3.traders.transaction.participants;

import java.util.UUID;

import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.transaction.Participant;

public class TraderParticipant implements Participant {
	private Trader trader;
	
	public TraderParticipant(Trader trader) {
		this.trader = trader;
	}
	
	public Trader getTrader() { 
		return trader;
	}
	
	@Override
	public boolean isPlayer() {
		return false;
	}

	@Override
	public UUID getUUID() {
		return trader.getNPC().getUniqueId();
	}

	@Override
	public boolean check(double amount) {
		return trader.getWallet().check(trader, amount);
	}

	@Override
	public boolean withdraw(double amount) {
		return trader.getWallet().withdraw(trader, amount);
	}

	@Override
	public boolean deposit(double amount) {
		return trader.getWallet().deposit(trader, amount);
	}
	
}
