package com.daskrr.nameplates.version;

import com.daskrr.nameplates.core.NamePlatesPlugin;
import com.daskrr.nameplates.version.wrapped.WrappedItem;
import org.bukkit.Bukkit;

import com.daskrr.nameplates.version.NMSRegistry.VersionRegistry;
import org.bukkit.entity.EntityType;

public class VersionProvider {

	private final Version version;
	private final VersionRegistry registry;
	
	public VersionProvider() throws VersionNotSupportedException {
		String bukkitVersion;

        try {
            bukkitVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            throw new VersionNotSupportedException("There was a problem extracting the server version.", null, null);
        }
        
        this.version = Version.fromString(bukkitVersion);
        this.registry = this.version.getRegistry();

		if (this.registry == null)
			throw new VersionNotSupportedException("The version of your server is not yet supported by "+ NamePlatesPlugin.PLUGIN_NAME, bukkitVersion, null);
	}
	
	
	public Version getVersion() {
		return this.version;
	}
	
	public static <T> T getItem(WrappedItem<T> item) {
		return getInstance().registry.get(item);
	}

	public static EntityType entityType(String id) {
		return getInstance().registry.getEntityType(id);
	}

	public static VersionProvider getInstance() {
		return NamePlatesPlugin.instance().getVersionProvider();
	}
}
