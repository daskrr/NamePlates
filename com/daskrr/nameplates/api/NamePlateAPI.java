package com.daskrr.nameplates.api;

import com.daskrr.nameplates.api.nameplate.NamePlate;
import com.daskrr.nameplates.core.NamePlates;
import com.daskrr.nameplates.core.NamePlatesPlugin;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NamePlateAPI extends NamePlates {
	
	private NamePlateAPI(NamePlatesPlugin plugin) {
		super(plugin);
	}

	public int addToPlayer(NamePlate plate, Player player) {
		return this.addToPlayer(plate, player.getUniqueId());
	}
	public int addToPlayer(NamePlate plate, OfflinePlayer player) {
		return this.addToPlayer(plate, player.getUniqueId());
	}

	public int addToPlayers(NamePlate plate, Player... players) {
		UUID[] uuids = new UUID[players.length];
		for (int i = 0; i < players.length; i++) uuids[i] = players[i].getUniqueId();
		return this.addToPlayers(plate, uuids);
	}
	public int addToPlayers(NamePlate plate, OfflinePlayer... players) {
		UUID[] uuids = new UUID[players.length];
		for (int i = 0; i < players.length; i++) uuids[i] = players[i].getUniqueId();
		return this.addToPlayers(plate, uuids);
	}

	public int addToEntities(NamePlate plate, EntityType... types) {
		return super.addToEntities(plate, types);
	}

	public static NamePlateAPI getInstance() {
		if (NamePlatesPlugin.instance().apiHandler != null)
			return NamePlatesPlugin.instance().apiHandler;
		
		return new NamePlateAPI(NamePlatesPlugin.instance());
	}
}
