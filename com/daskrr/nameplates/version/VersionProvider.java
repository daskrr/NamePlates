package com.daskrr.nameplates.version;

import com.daskrr.nameplates.core.NamePlatesPlugin;
import org.bukkit.Bukkit;

import com.daskrr.nameplates.version.NMSRegistry.VersionRegistry;
import com.daskrr.nameplates.version.NMSRegistry.WrappedItem;

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
	}
	
	
	public Version getVersion() {
		return this.version;
	}
	
	public <T> T getItem(WrappedItem<T> item) {
		return this.registry.get(item);
	}

	public static VersionProvider getInstance() {
		return NamePlatesPlugin.instance().getVersionProvider();
	}
}
