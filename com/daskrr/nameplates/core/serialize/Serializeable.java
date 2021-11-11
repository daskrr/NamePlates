package com.daskrr.nameplates.core.serialize;

public interface Serializeable {
	
	public void serialize(ByteDataSerializer serializer);
	public void deserialize(ByteDataSerializer serializer);
}
