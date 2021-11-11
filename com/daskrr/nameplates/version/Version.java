package com.daskrr.nameplates.version;

import com.daskrr.nameplates.core.NamePlatesPlugin;
import com.daskrr.nameplates.version.NMSRegistry.VersionRegistry;

public enum Version {
		v1_8_R1("v1_8_R1", NMSRegistry.v1_8_R1),
		v1_8_R2("v1_8_R2"),
		v1_8_R3("v1_8_R3"),
		v1_9_R1("v1_9_R1"),
		v1_9_R2("v1_9_R2"),
		v1_10_R1("v1_10_R1"),
		v1_11_R1("v1_11_R1"),
		v1_12_R1("v1_12_R1"),
		v1_13_R1("v1_13_R1"),
		v1_13_R2("v1_13_R2"),
		v1_14_R1("v1_14_R1"),
		v1_15_R1("v1_15_R1"),
		v1_16_R1("v1_16_R1"),
		v1_16_R2("v1_16_R2"),
		v1_16_R3("v1_16_R3"),
		v1_17_R1("v1_17_R1");
		
		private final String version;
		private VersionRegistry registry;
		private boolean implemented = true;
		
		private Version (String version) {
			this.version = version;
		}
		
		private Version (String version, VersionRegistry registry) {
			this.version = version;
			this.registry = registry;
		}
		
		private Version (String version, boolean implemented) {
			this.version = version;
			this.implemented = implemented;
		}
		
		public String getVersion() {
			return this.version;
		}
		
		public VersionRegistry getRegistry() {
			return this.registry;
		}
		
		public static Version fromString(String version) throws VersionNotSupportedException {
			for (Version v : values())
				if (v.getVersion().equalsIgnoreCase(version)) {
					if (!v.implemented)
						throw new VersionNotSupportedException(
										"The version of your server is not yet supported by "+ NamePlatesPlugin.PLUGIN_NAME,
										version,
										v
								  );
					return v;
				}
					
			return null;
		}
	}