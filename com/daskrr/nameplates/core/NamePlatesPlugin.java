package com.daskrr.nameplates.core;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.daskrr.nameplates.api.NamePlateAPI;
import com.daskrr.nameplates.version.VersionNotSupportedException;
import com.daskrr.nameplates.version.VersionProvider;

public class NamePlatesPlugin extends JavaPlugin 
{
	public static final String PLUGIN_NAME = "EntityNamePlatesAPI";
	
	private VersionProvider versionProvider;
	public NamePlateHandler plateHandler;
	public NamePlateAPI apiHandler;
	
	@Override
	public void onEnable() {
		// Check nms version
		try {
			this.versionProvider = new VersionProvider();
			getLogger().info("Your server is running on version "+ this.versionProvider.getVersion().getVersion());
		} catch (VersionNotSupportedException e) {
			getLogger().severe("There was an issue with your server version: " + e.getMessage());
		}

		this.plateHandler = new NamePlateHandler(this);
		try {
			this.apiHandler = new NamePlateAPI(this.plateHandler);
		}
		catch (InstantiationException e) {
			; // this will never happen
		}
	}
	
	@Override
	public void onDisable() {
		// TODO: remove all nameplates, stop tickers
		this.plateHandler.updater.ticker.cancel();
	}
	
	public VersionProvider getVersionProvider() {
		return this.versionProvider;
	}
	
	public static NamePlatesPlugin instance() {
		return (NamePlatesPlugin) Bukkit.getServer().getPluginManager().getPlugin(PLUGIN_NAME);
	}
}
