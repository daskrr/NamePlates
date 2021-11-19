package com.daskrr.nameplates.core;

import com.daskrr.nameplates.api.NamePlateAPIOptions;
import com.daskrr.nameplates.api.PassengerPlateOverlapScenario;
import com.daskrr.nameplates.api.nameplate.NamePlate;
import com.daskrr.nameplates.api.nameplate.NamePlateTextBuilder;
import com.daskrr.nameplates.api.util.BlockLocation;
import com.daskrr.nameplates.util.EntityUtils;
import com.daskrr.nameplates.version.wrapped.WrappedItem;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.daskrr.nameplates.api.NamePlateAPI;
import com.daskrr.nameplates.version.VersionNotSupportedException;
import com.daskrr.nameplates.version.VersionProvider;

import javax.annotation.Nullable;
import java.util.List;

public class NamePlatesPlugin extends JavaPlugin 
{
	public static final String PLUGIN_NAME = "EntityNamePlatesAPI";
	
	private VersionProvider versionProvider;
	public NamePlateHandler plateHandler;
	public NamePlateAPI apiHandler;

	@Override
	public void onLoad() {
		// Check nms version
		try {
			this.versionProvider = new VersionProvider();
			getLogger().info("Your server is running on version "+ this.versionProvider.getVersion().getVersion());
		} catch (VersionNotSupportedException e) {
			getLogger().severe("There was an issue with your server version: " + e.getMessage());
		}

		VersionProvider.getItem(WrappedItem.ENTITY_REGISTRY).addEntities();
		VersionProvider.getItem(WrappedItem.ENTITY_REGISTRY).register();
	}

	@Override
	public void onEnable() {
		this.plateHandler = new NamePlateHandler(this);
		try {
			this.apiHandler = new NamePlateAPI(this.plateHandler);
		}
		catch (InstantiationException e) {
			; // this will never happen
		}

		// DEBUG START
	//		this.apiHandler.getOptions().getOption(NamePlateAPIOptions.Key.RENDER_BEHIND_WALLS).setValue(false);
			this.apiHandler.getOptions().getOption(NamePlateAPIOptions.Key.VIEW_DISTANCE).setValue(30);

			NamePlateTextBuilder builder = new NamePlateTextBuilder();
			builder.addLine(new NamePlateTextBuilder.Line(new ItemStack(Material.EMERALD)));
			builder.addLine(new NamePlateTextBuilder.Line("§2Your Sim"));
			builder.addLine(new NamePlateTextBuilder.Line().setEmpty(true));
			builder.addLine(new NamePlateTextBuilder.Line("§cFrank is hungry"));

			NamePlate plate = new NamePlate(builder);
			plate.setOverlapScenario(PassengerPlateOverlapScenario.RIGHT);

			this.apiHandler.addToEntities(plate, EntityType.VILLAGER);

			NamePlateTextBuilder builder1 = new NamePlateTextBuilder("Players:", "{ONLINE_PLAYERS}/{MAX_PLAYERS}", "{HP}", "{ENTITY_HOLDING}", "{TIME}", "{DATE}");
			builder1.addLine(new NamePlateTextBuilder.Line(new ItemStack(Material.NAME_TAG)));
			builder1.addLine(new NamePlateTextBuilder.Line("items work"));
	//		builder1.addLine(new NamePlateTextBuilder.Line(new ItemStack(Material.STONE)));
	//		builder1.addLine(new NamePlateTextBuilder.Line("items work"));

			NamePlate plate1 = new NamePlate(builder1);

			this.apiHandler.addToWorld(plate1, new BlockLocation(Bukkit.getWorld("world"), 1151, 80, 206));

		// DEBUG END;
	}
	
	@Override
	public void onDisable() {
		this.plateHandler.updater.ticker.cancel();
		Maps.newHashMap(this.plateHandler.namePlates).forEach((id, namePlate) -> {
			Maps.newHashMap(((ContextNamePlate) namePlate).getRenders()).forEach((entityUUID, render) -> {
				Entity entity = EntityUtils.getEntityInLoadedChunks(entityUUID);
				if (entity != null)
					this.plateHandler.updater.renderManager.remove(entity);
			});
		});

		Maps.newHashMap(this.plateHandler.staticNamePlates).forEach((id, namePlate) -> {
			Maps.newHashMap(((ContextNamePlate) namePlate).getRenders()).forEach((entityUUID, render) -> {
				Entity entity = this.plateHandler.updater.renderManager.createEntityAttachment(entityUUID, this.plateHandler.staticLocations.get(id));
				this.plateHandler.updater.renderManager.remove(entity);
			});
		});
	}
	
	public VersionProvider getVersionProvider() {
		return this.versionProvider;
	}

	@Nullable
	public static NamePlatesPlugin instance() {
		return (NamePlatesPlugin) Bukkit.getServer().getPluginManager().getPlugin(PLUGIN_NAME);
	}
}
