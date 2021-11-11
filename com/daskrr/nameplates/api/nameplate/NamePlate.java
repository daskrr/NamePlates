package com.daskrr.nameplates.api.nameplate;

import java.util.List;
import java.util.UUID;

import com.daskrr.nameplates.api.NamePlateAPI;
import com.daskrr.nameplates.api.NamePlateAPIOptions;
import com.daskrr.nameplates.core.EntityGroup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.daskrr.nameplates.core.serialize.ByteDataSerializer;
import com.daskrr.nameplates.core.serialize.Serializeable;
import com.google.common.collect.Lists;

public class NamePlate implements Serializeable {
	
	private NamePlateTextBuilder builder;
	
	private double marginBottom;
	private boolean resourceFriendly;
	private int viewDistance;
	private boolean renderBehindWalls;
	
	private int id = -1;
	private EntityGroup group;
	private List<UUID> viewers = Lists.newArrayList();
	private List<UUID> disallowedPlayers = Lists.newArrayList();
	
	public NamePlate(String... lines) {
		this(new NamePlateTextBuilder(lines));
	}
	
	public NamePlate(NamePlateTextBuilder builder) {
		this.builder = builder;
		
		// set defaults from API Options
		NamePlateAPIOptions options = NamePlateAPI.getInstance().getOptions();
		this.marginBottom = options.getOption(NamePlateAPIOptions.Key.MARGIN_BOTTOM).getValue();
		this.resourceFriendly = options.getOption(NamePlateAPIOptions.Key.RESOURCE_FRIENDLY).getValue();
		this.viewDistance = options.getOption(NamePlateAPIOptions.Key.VIEW_DISTANCE).getValue();
		this.renderBehindWalls = options.getOption(NamePlateAPIOptions.Key.RENDER_BEHIND_WALLS).getValue();
	}
	
	public NamePlate setMarginBottom(double margin) {
	    this.marginBottom = margin;
	    // TODO: trigger change
	    return this;
	}
	    
	public NamePlate setResourceFriendly(boolean resourceFriendly) {
	    this.resourceFriendly = resourceFriendly;
	    // TODO: trigger change
	    return this;
	}
	    
	public NamePlate setViewDistance(int viewDistance) {
	    this.viewDistance = viewDistance;
	    // TODO: trigger change
	    return this;
	}
	    
	public NamePlate setRenderBehindWalls(boolean renderBehindWalls) {
	    this.renderBehindWalls = renderBehindWalls;
	    // TODO: trigger change
	    return this;
	}
	
	
	public double getMarginBottom() {
	    return this.marginBottom;
	}
	public boolean getResourceFriendly() {
	    return this.resourceFriendly;
	}
	public double getViewDistance() {
	    return this.viewDistance;
	}
	public boolean getRenderBehindWalls() {
	    return this.renderBehindWalls;
	}
	
	public NamePlateTextBuilder getBuilder() {
		return this.builder;
	}
	
	
	// IN CONTEXT BEGIN
	
	public int getId() {
	    return this.id;
	}
	public boolean inContext() {
		return this.id != -1;
	}
	public Player[] getViewers() {
		List<Player> viewers = Lists.newArrayList();
		
		for (UUID playerUUID : this.viewers) {
			Player player;
			if ((player = Bukkit.getPlayer(playerUUID)) != null)
				viewers.add(player);
		}
		
	    return viewers.toArray(new Player[viewers.size()]);
	}
	public boolean isShared() {
		return this.group != null;
	}
	public EntityGroup getSharedGroup() {
		return this.group;
	}

	// IN CONTEXT END

	public void disableView(Player... players) {
	    for (int i = 0; i < players.length; i++) this.disallowedPlayers.add(players[i].getUniqueId());
	    // TODO: trigger change ?
	}
	public void enableView(Player... players) {
	    for (int i = 0; i < players.length; i++) this.disallowedPlayers.remove(players[i].getUniqueId());
	    // TODO: trigger change ?
	}
	
	
	// SERIALIZATION
	@Override
	public void serialize(ByteDataSerializer buffer) { // TODO: add option for YAMLConfiguration		
		buffer.writeDouble(this.marginBottom);
		buffer.writeBoolean(this.resourceFriendly);
		buffer.writeInt(this.viewDistance);
		buffer.writeBoolean(this.renderBehindWalls);
		
		buffer.writeList(disallowedPlayers, (uuid, serializer) -> serializer.writeUUID(uuid));
	}
	
	@Override
	public void deserialize(ByteDataSerializer buffer) {
		this.marginBottom = buffer.readDouble();
		this.resourceFriendly = buffer.readBoolean();
		this.viewDistance = buffer.readInt();
		this.renderBehindWalls = buffer.readBoolean();
		
		this.disallowedPlayers = buffer.readList(this.disallowedPlayers, (serializer) -> serializer.readUUID());
	}
	    
}
