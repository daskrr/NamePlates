package com.daskrr.nameplates.api.nameplate;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.daskrr.nameplates.api.NamePlateAPI;
import com.daskrr.nameplates.api.NamePlateAPIOptions;
import com.daskrr.nameplates.core.EntityGroup;
import com.daskrr.nameplates.core.ContextNamePlate;
import com.daskrr.nameplates.core.NamePlateHandler;
import com.daskrr.nameplates.core.NamePlatesPlugin;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.daskrr.nameplates.core.serialize.ByteDataSerializer;
import com.daskrr.nameplates.core.serialize.Serializeable;
import com.google.common.collect.Lists;

public class NamePlate extends ContextNamePlate implements Serializeable {

	// modifications applied to this, will require manual API#update or this#update
	private final NamePlateTextBuilder builder;
	
	private double marginBottom;
	private boolean resourceFriendly;
	private int viewDistance;
	private boolean renderBehindWalls;
	private boolean renderPermanently;
	
	private int id = -1;
	private EntityGroup<?> group = null;
	private final Set<UUID> viewers = Sets.newHashSet();
	protected List<UUID> disallowedPlayers = Lists.newArrayList();
	
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
		this.update();
	    return this;
	}
	    
	public NamePlate setResourceFriendly(boolean resourceFriendly) {
	    this.resourceFriendly = resourceFriendly;
		this.update();
	    return this;
	}
	    
	public NamePlate setViewDistance(int viewDistance) {
	    this.viewDistance = viewDistance;
		this.update();
	    return this;
	}
	    
	public NamePlate setRenderBehindWalls(boolean renderBehindWalls) {
	    this.renderBehindWalls = renderBehindWalls;
		this.update();
	    return this;
	}

	public NamePlate setRenderPermanently(boolean renderPermanently) {
		this.renderPermanently = renderPermanently;
		this.update();
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
	public boolean getRenderPermanently() {
		return this.renderPermanently;
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
		if (!this.inContext())
			return null;

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
	public EntityGroup<?> getSharedGroup() {
		return this.group;
	}

	@Override
	protected void setId(int id) {
		this.id = id;
		this.builder.setContext(id);
	}

	@Override
	protected void setGroup(EntityGroup<?> group) {
		this.group = group;
	}

	@Override
	protected Set<UUID> getViewersSet() {
		return this.viewers;
	}

	// updates render
	public void update() {
		if (this.inContext())
			NamePlatesPlugin.instance().plateHandler.updater.update(this.id);
	}

	// IN CONTEXT END

	public void disableView(Player... players) {
		for (Player player : players) this.disallowedPlayers.add(player.getUniqueId());
		this.update();
	}
	public void enableView(Player... players) {
		for (Player player : players) this.disallowedPlayers.remove(player.getUniqueId());
		this.update();
	}

	public List<UUID> getDisabledViewPlayers() {
		return Lists.newArrayList(this.disallowedPlayers);
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
