package com.daskrr.nameplates.api.nameplate;

import java.util.*;

import com.daskrr.nameplates.api.NamePlateAPI;
import com.daskrr.nameplates.api.NamePlateAPIOptions;
import com.daskrr.nameplates.api.PassengerPlateOverlapScenario;
import com.daskrr.nameplates.core.EntityGroup;
import com.daskrr.nameplates.core.ContextNamePlate;
import com.daskrr.nameplates.core.NamePlatesPlugin;
import com.daskrr.nameplates.core.PlateRender;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.daskrr.nameplates.core.serialize.ByteDataSerializer;
import com.daskrr.nameplates.core.serialize.Serializeable;
import com.google.common.collect.Lists;

public class NamePlate extends ContextNamePlate implements Serializeable {

	// modifications applied to this, will require manual API#update or this#update
	private final NamePlateTextBuilder builder;

	// TODO make all of these api options (to sync)
	private NamePlateAPIOptions.Option<Double> marginBottom;
	private NamePlateAPIOptions.Option<Boolean> resourceFriendly;
	private NamePlateAPIOptions.Option<Integer> viewDistance;
	private NamePlateAPIOptions.Option<Boolean> renderBehindWalls;
	private NamePlateAPIOptions.Option<PassengerPlateOverlapScenario> overlapScenario;
	private Double setMarginBottom = null;
	private Boolean setResourceFriendly = null;
	private Integer setViewDistance = null;
	private Boolean setRenderBehindWalls = null;
	private PassengerPlateOverlapScenario setOverlapScenario = null;
	
	private int id = -1;
	private EntityGroup<?> group = null;
	private boolean isStatic = false;
	protected List<UUID> disallowedPlayers = Lists.newArrayList();

	protected Map<UUID, PlateRender> renders = Maps.newHashMap();
	protected List<UpdateCriteria> updaters = Lists.newArrayList();
	
	public NamePlate(String... lines) {
		this(new NamePlateTextBuilder(lines));
	}
	
	public NamePlate(NamePlateTextBuilder builder) {
		this.builder = builder;

		NamePlateAPI api = NamePlateAPI.getInstance();
		if (api == null)
			throw new IllegalStateException(String.format("%s has not been instantiated at this time", NamePlatesPlugin.PLUGIN_NAME));
		
		// set defaults from API Options
		NamePlateAPIOptions options = api.getOptions();
		this.marginBottom = options.getOption(NamePlateAPIOptions.Key.MARGIN_BOTTOM);
		this.resourceFriendly = options.getOption(NamePlateAPIOptions.Key.RESOURCE_FRIENDLY);
		this.viewDistance = options.getOption(NamePlateAPIOptions.Key.VIEW_DISTANCE);
		this.renderBehindWalls = options.getOption(NamePlateAPIOptions.Key.RENDER_BEHIND_WALLS);
		this.overlapScenario = options.getOption(NamePlateAPIOptions.Key.PASSENGER_PLATE_OVERLAP);
	}
	
	public NamePlate setMarginBottom(double margin) {
	    this.setMarginBottom = margin;
		this.update();
	    return this;
	}
	    
	public NamePlate setResourceFriendly(boolean resourceFriendly) {
	    this.setResourceFriendly = resourceFriendly;
		this.update();
	    return this;
	}
	    
	public NamePlate setViewDistance(int viewDistance) {
	    this.setViewDistance = viewDistance;
		this.update();
	    return this;
	}
	    
	public NamePlate setRenderBehindWalls(boolean renderBehindWalls) {
	    this.setRenderBehindWalls = renderBehindWalls;
		this.update();
	    return this;
	}

	public NamePlate setOverlapScenario(PassengerPlateOverlapScenario overlapScenario) {
		this.setOverlapScenario = overlapScenario;
		this.update();
		return this;
	}
	
	
	public double getMarginBottom() {
	    return this.setMarginBottom == null ? this.marginBottom.getValue() : this.setMarginBottom;
	}
	public boolean getResourceFriendly() {
	    return this.setResourceFriendly == null ? this.resourceFriendly.getValue() : this.setResourceFriendly;
	}
	public double getViewDistance() {
	    return this.setViewDistance == null ? this.viewDistance.getValue() : this.setViewDistance;
	}
	public boolean getRenderBehindWalls() {
	    return this.setRenderBehindWalls == null ? this.renderBehindWalls.getValue() : this.setRenderBehindWalls;
	}
	public PassengerPlateOverlapScenario getOverlapScenario() {
		return this.setOverlapScenario == null ? this.overlapScenario.getValue() : this.setOverlapScenario;
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
		
		for (UUID playerUUID : this.getViewersSet()) {
			Player player;
			if ((player = Bukkit.getPlayer(playerUUID)) != null)
				viewers.add(player);
		}
		
	    return viewers.toArray(new Player[0]);
	}
	public boolean isShared() {
		return this.group != null;
	}
	public EntityGroup<?> getSharedGroup() {
		return this.group;
	}
	public boolean isStatic() {
		return this.isStatic;
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
	protected void setStatic() {
		this.isStatic = true;
	}

	@Override
	protected Set<UUID> getViewersSet() {
		Set<UUID> allViewers = Sets.newHashSet();
		this.renders.forEach((entityUUID, render) -> allViewers.addAll(render.getViewers()));

		return allViewers;
	}

	@Override
	protected Map<UUID, PlateRender> getRenders() {
		return this.renders;
	}

	@Override
	protected PlateRender getRender(UUID entityUUID) {
		PlateRender render = this.renders.get(entityUUID);
		if (render == null) {
			render = new PlateRender();
			this.renders.put(entityUUID, render);
		}

		return render;
	}

	@Override
	protected void setUpdaters(UpdateCriteria... criteria) {
		this.updaters = Lists.newArrayList(criteria);

		if (this.updaters.size() == 1 && this.updaters.contains(UpdateCriteria.PLAYER_JOIN))
			return;

		Objects.requireNonNull(NamePlatesPlugin.instance()).plateHandler.updater.putNamePlateUpdaters(id, criteria);
	}
	@Override
	protected List<UpdateCriteria> getUpdaters() {
		return Lists.newArrayList(this.updaters);
	}

	// updates render
	public void update() {
		if (!this.inContext())
			return;

		this.checkUpdaters();
		Objects.requireNonNull(NamePlatesPlugin.instance()).plateHandler.updater.update(this.id);
	}

	@Override
	protected void checkUpdaters() {
		Set<ContextNamePlate.UpdateCriteria> updaters = Sets.newHashSet();
		Lists.newArrayList(this.builder.getLines()).forEach(line -> {
			Lists.newArrayList(line.getComponents()).forEach(component -> {
				String format = component.getFormat();
				if (format.contains("{ENTITY_HOLDING}")
				 || format.contains("{ENTITY_OFFHAND}")
				 || format.contains("{ENTITY_HELMET}")
				 || format.contains("{ENTITY_CHESTPLATE}")
				 || format.contains("{ENTITY_LEGGINGS}")
				 || format.contains("{ENTITY_BOOTS}"))
					updaters.add(ContextNamePlate.UpdateCriteria.ENTITY_EQUIPMENT);

				if (format.contains("{ONLINE_PLAYERS}"))
					updaters.add(ContextNamePlate.UpdateCriteria.PLAYER_JOIN);
				if (format.contains("{VIEWING_PLAYER_DISPLAY_NAME}"))
					updaters.add(ContextNamePlate.UpdateCriteria.DISPLAY_NAME_CHANGE);

				if (format.contains("{TIME}"))
					updaters.add(ContextNamePlate.UpdateCriteria.TIME);
				if (format.contains("{DATE}"))
					updaters.add(ContextNamePlate.UpdateCriteria.DATE);
			});
		});

		this.setUpdaters(updaters.toArray(new ContextNamePlate.UpdateCriteria[0]));
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
		int options = 0x7fffff;
		if (this.setMarginBottom == null)
			options &= ~1;
		if (this.setResourceFriendly == null)
			options &= ~(1 << 1);
		if (this.setViewDistance == null)
			options &= ~(1 << 2);
		if (this.setRenderBehindWalls == null)
			options &= ~(1 << 3);
		if (this.setOverlapScenario == null)
			options &= ~(1 << 4);
		if (this.setOverlapScenario == null)
			options &= ~(1 << 5);

		buffer.writeInt(options);

		if ((options & 1) == 1)
			buffer.writeDouble(this.setMarginBottom);
		if (((options >> 1) & 1) == 1)
			buffer.writeBoolean(this.setResourceFriendly);
		if (((options >> 2) & 1) == 1)
			buffer.writeInt(this.setViewDistance);
		if (((options >> 3) & 1) == 1)
			buffer.writeBoolean(this.setRenderBehindWalls);
		if (((options >> 4) & 1) == 1)
			buffer.writeEnum(this.setOverlapScenario);

		buffer.writeList(disallowedPlayers, (uuid, serializer) -> serializer.writeUUID(uuid));
	}
	
	@Override
	public void deserialize(ByteDataSerializer buffer) {
		int options = buffer.readInt();

		if ((options & 1) == 1)
			this.setMarginBottom = buffer.readDouble();
		if (((options >> 1) & 1) == 1)
			this.setResourceFriendly = buffer.readBoolean();
		if (((options >> 2) & 1) == 1)
			this.setViewDistance = buffer.readInt();
		if (((options >> 3) & 1) == 1)
			this.setRenderBehindWalls = buffer.readBoolean();
		if (((options >> 4) & 1) == 1)
			this.setOverlapScenario = buffer.readEnum(PassengerPlateOverlapScenario.class);

		this.disallowedPlayers = buffer.readList(this.disallowedPlayers, ByteDataSerializer::readUUID);
	}
}
