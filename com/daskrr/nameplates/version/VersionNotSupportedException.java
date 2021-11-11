package com.daskrr.nameplates.version;

import javax.annotation.Nullable;

public class VersionNotSupportedException extends Exception {

	private static final long serialVersionUID = 3537774251257068042L;
	
	private String bukkitVersion;
	private Version version;
	
	public VersionNotSupportedException (String message, @Nullable String bukkitVersion, @Nullable Version version) {
		super(message);
		
		this.bukkitVersion = bukkitVersion;
		this.version = version;
	}
	
	@Nullable
	public String getBukkitVersion () {
		return this.bukkitVersion;
	}
	
	@Nullable
	public Version getVersion () {
		return this.version;
	}
}
