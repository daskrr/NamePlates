package com.daskrr.nameplates.core.serialize;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import io.netty.buffer.ByteBuf;

public class ByteDataSerializer{

	private ByteBuf byteBuf;
	
	public ByteDataSerializer(ByteBuf byteBuf) {
		this.byteBuf = byteBuf;
	}
	
	public ByteBuf byteBuf() {
		return this.byteBuf;
	}

	
	// custom
	public ByteDataSerializer writeString(String string) {
		this.writeShort(string.length());
		this.writeBytes(string.getBytes());
		return this;
	}
	
	public ByteDataSerializer writeEnum(Enum<?> enumeration) {
		this.writeInt(enumeration.ordinal());
		return this;
	}
	
	public ByteDataSerializer writeUUID(UUID uuid) {
		this.writeLong(uuid.getLeastSignificantBits());
		this.writeLong(uuid.getMostSignificantBits());
		return this;
	}
	
	public ByteDataSerializer writeListSerializeable(List<?> list) {
		this.writeInt(list.size());
		for (Object item : list)
			if (item instanceof Serializeable)
				((Serializeable) item).serialize(this);
		
		return this;
	}
	
	public <T> ByteDataSerializer writeList(List<T> list, BiConsumer<T, ByteDataSerializer> each) {
		this.writeInt(list.size());
		for (T item : list)
			each.accept(item, this);
		
		return this;
	}
	
    public String readString() {
		short size = this.readShort();
		return new String(this.readBytes(size).array());
	}
	
	public int readEnum() {
		return this.readInt();
	}
	
	public UUID readUUID() {
		long least = this.readLong();
		long most = this.readLong();
		return new UUID(least, most);
	}
    
	@SuppressWarnings("unchecked")
	public <T> List<T> readListSerializeable(List<T> list, Function<Integer, Serializeable[]> serializers) {
		int size = this.readInt();
        Serializeable[] serializersArray = serializers.apply((Integer) size);
		for (int i = 0; i < size; i++) {
			serializersArray[i].deserialize(this);
            list.add((T) serializersArray[i]);
		}
           
		return list;
	}
	
	public <T> List<T> readList(List<T> list, Function<ByteDataSerializer, T> each) {
		int size = this.readInt();
		for (int i = 0; i < size; i++)
            list.add(each.apply(this));
           
		return list;
	}
	
	
	// default
	
	public ByteDataSerializer writeBoolean(boolean arg0) {
		this.byteBuf.writeBoolean(arg0);
        return this;
	}

	
	public ByteDataSerializer writeByte(int arg0) {
		this.byteBuf.writeByte(arg0);
        return this;
	}

	
	public ByteDataSerializer writeBytes(ByteBuf arg0) {
		this.byteBuf.writeBytes(arg0);
        return this;
	}

	
	public ByteDataSerializer writeBytes(byte[] arg0) {
		this.byteBuf.writeBytes(arg0);
        return this;
	}

	
	public ByteDataSerializer writeBytes(ByteBuffer arg0) {
		this.byteBuf.writeBytes(arg0);
        return this;
	}

	
	public ByteDataSerializer writeChar(int arg0) {
		this.byteBuf.writeChar(arg0);
        return this;
	}

	
	public ByteDataSerializer writeDouble(double arg0) {
		this.byteBuf.writeDouble(arg0);
        return this;
	}

	
	public ByteDataSerializer writeFloat(float arg0) {
		this.byteBuf.writeFloat(arg0);
        return this;
	}

	
	public ByteDataSerializer writeInt(int arg0) {
		this.byteBuf.writeInt(arg0);
        return this;
	}

	
	public ByteDataSerializer writeLong(long arg0) {
		this.byteBuf.writeLong(arg0);
        return this;
	}

	
	public ByteDataSerializer writeMedium(int arg0) {
		this.byteBuf.writeMedium(arg0);
        return this;
	}

	
	public ByteDataSerializer writeShort(int arg0) {
		this.byteBuf.writeShort(arg0);
        return this;
	}
	
	public boolean readBoolean() {
		return this.byteBuf.readBoolean();
	}


	public byte readByte() {
		return this.byteBuf.readByte();
	}


	public ByteBuf readBytes(int size) {
		return this.byteBuf.readBytes(size);
	}


	public char readChar() {
		return this.byteBuf.readChar();
	}


	public double readDouble() {
		return this.byteBuf.readDouble();
	}


	public float readFloat() {
		return this.byteBuf.readFloat();
	}


	public int readInt() {
		return this.byteBuf.readInt();
	}


	public long readLong() {
		return this.byteBuf.readLong();
	}


	public int readMedium() {
		return this.byteBuf.readMedium();
	}


	public short readShort() {
		return this.byteBuf.readShort();
	}
	
	public byte[] array() {
		return this.array();
	}

}
