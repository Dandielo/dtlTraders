package net.dandielo.citizens.traders_v3.traders.transaction.participants;

import java.util.UUID;

import org.bukkit.entity.Player;

import net.dandielo.citizens.traders_v3.bukkit.Econ;
import net.dandielo.citizens.traders_v3.traders.transaction.Participant;

public class PlayerParticipant implements Participant {
	private static Econ econ = Econ.econ;
	private Player player;
	
	public PlayerParticipant(Player player)
	{
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	@Override
	public boolean isPlayer() {
		return true;
	}

	@Override
	public UUID getUUID() {
		return player.getUniqueId();
	}

	@Override
	public boolean check(double amount) {
		return econ.check(getUUID(), amount);
	}

	@Override
	public boolean withdraw(double amount) {
		return econ.withdraw(getUUID(), amount);
	}

	@Override
	public boolean deposit(double amount) {
		return econ.deposit(getUUID(), amount);
	}
}
