package com.daskrr.nameplates.api.nameplate;

import java.util.List;
import java.util.function.Function;

import com.daskrr.nameplates.api.NamePlateAPI;
import com.daskrr.nameplates.api.NamePlateAPIOptions;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;

import com.daskrr.nameplates.api.nameplate.NamePlateTextBuilder.Component.ComponentType;
import com.daskrr.nameplates.core.serialize.ByteDataSerializer;
import com.daskrr.nameplates.core.serialize.Serializeable;
import com.google.common.collect.Lists;
import org.bukkit.inventory.ItemStack;

public class NamePlateTextBuilder implements Serializeable {

	protected int id = -1;
	private List<Line> lines = Lists.newArrayList();
	
	public NamePlateTextBuilder() {  }
	
	public NamePlateTextBuilder(String... lines) {
		Lists.newArrayList(lines).forEach((line) -> this.lines.add(new Line(line)));
		this.lines = Lists.reverse(this.lines);
	}
	
	public NamePlateTextBuilder addLine (String line) {
	    return this.addLine(new Line(line));
	}
	public NamePlateTextBuilder addLines (String... lines) {
	    Line[] lineObjects = new Line[lines.length];
	    for (int i = 0; i < lines.length; i++) lineObjects[i] = new Line(lines[i]);
	    return this.addLines(lineObjects);
	}
	public NamePlateTextBuilder addLines (int startFromIndex, String... lines) {
	    Line[] lineObjects = new Line[lines.length];
	    for (int i = 0; i < lines.length; i++) lineObjects[i] = new Line(lines[i]);
	    return this.addLines(startFromIndex, lineObjects);
	}
	public NamePlateTextBuilder setLine (int index, String line) {
	    return this.setLine(index, new Line(line));
	}
	
	
	public NamePlateTextBuilder addLine (Line line) {
	    this.lines.add(line);

	    return this;
	}
	public NamePlateTextBuilder addLines (Line... lines) {
	    this.lines.addAll(Lists.newArrayList(lines));

	    return this;
	}
	public NamePlateTextBuilder addLines (int startFromIndex, Line... lines) {
		this.lines.addAll(startFromIndex, Lists.newArrayList(lines));

	    return this;
	}
	public NamePlateTextBuilder setLine (int index, Line line) {
	    this.lines.set(index, line);

	    return this;
	}
	
	public Line getLine(int index) {
	    return this.lines.get(index);
	}
	public Line[] getLines() {
	    return this.lines.toArray(new Line[this.lines.size()]);
	}
	
	@Override
	public void serialize(ByteDataSerializer serializer) {
		serializer.writeListSerializeable(this.lines);
	}
	
	@Override
	public void deserialize(ByteDataSerializer serializer) {
		serializer.readListSerializeable(this.lines, (size) -> {
			Line[] lines = new Line[size];
			for (int i = 0; i < size; i++) lines[i] = new Line();
			return lines;
		});
	}

	protected void setContext(int id) {
		this.id = id;
	}
	
	public static class Line implements Serializeable {
		
		private NamePlateAPIOptions.Option<Double[]> margin;
		private double[] setMargin = null;
		private List<Component> contents = Lists.newArrayList();

		private boolean empty = false;
		private ItemStack item = null;
		
		public Line() {
			this.margin = NamePlateAPI.getInstance().getOptions().getOption(NamePlateAPIOptions.Key.MARGIN);
		}
		
		public Line(String text) {
			this();
			this.contents.add(new Component(ComponentType.VARTEXT, text));
		}
		
		public Line(double margin) {
			this(margin, margin);
		}
		
		public Line(double marginTop, double marginBottom) {
			this.setMargin = new double[] { marginTop, marginBottom };
		}

		public Line(ItemStack item) {
			this();
			this.item = item;
		}

		
		public Line append (String text) {
		    return this.append(new Component(ComponentType.VARTEXT, text));
		}
		public Line append (Component component) {
		    this.contents.add(component);

		    return this;
		}
		public Line prepend (String text) {
		    return this.prepend(new Component(ComponentType.VARTEXT, text));
		}
		public Line prepend (Component component) {
		    this.contents.add(0, component);

		    return this;
		}
		
		
		public Line setMargin(int margin) {
		    return this.setMargin(margin, margin);
		}
		public Line setMargin(int marginTop, int marginBottom) {
		    this.setMargin = new double[] { marginTop, marginBottom };
		    return this;
		}

		public Line setEmpty(boolean empty) {
			this.empty = empty;
			return this;
		}
		public Line setItem(ItemStack item) {
			this.item = item;
			return this;
		}
		
		
		public double[] getMargin() {
		    return this.setMargin == null ? ArrayUtils.toPrimitive(this.margin.getValue()) : this.setMargin;
		}
		public Component getComponent(int index) {
		    return this.contents.get(index);
		}
		public Component[] getComponents() {
		    return this.contents.toArray(new Component[0]);
		}

		public boolean isEmpty() {
			return this.empty;
		}
		public ItemStack getItem() {
			return this.item;
		}

		public boolean hasItem() {
			return this.item != null;
		}
		
		public void serialize(ByteDataSerializer serializer) {
			byte options = 127;
			if (this.setMargin == null)
				options &= ~1;

			serializer.writeByte(options);

			if ((options & 1) == 1) {
				serializer.writeDouble(this.setMargin[0]);
				serializer.writeDouble(this.setMargin[1]);
			}

			serializer.writeListSerializeable(this.contents);
		}
		
		public void deserialize(ByteDataSerializer serializer) {
			int options = serializer.readByte();

			if ((options & 1) == 1) {
				this.setMargin = new double[2];
				this.setMargin[0] = serializer.readDouble();
				this.setMargin[1] = serializer.readDouble();
			}

			this.contents = serializer.readListSerializeable(this.contents, (size) -> {
				Component[] components = new Component[size];
				for (int i = 0; i < size; i++) components[i] = new Component();
				return components;
			});
		}
	}
	
	public static class Component implements Serializeable {
		
		private ComponentType type;
		private ChatColor color;
		private String format;
		
		private Function<Entity, String> function;
		
		private boolean bold = false;
		private boolean underlined = false;
		private boolean strikethrough = false;
		private boolean italic = false;
		private boolean obfuscated = false;
		
		protected Component () {}

		public Component (ComponentType type) {
			this(type, type.color);
		}

		public Component (ComponentType type, ChatColor color) {
			this(type, color, type.format);
		}

		public Component (ComponentType type, String format) {
			this(type, type.color, format);
		}
		
		public Component (ComponentType type, ChatColor color, String format) {
			this.type = type;
			this.color = color;
			this.format = format;
		}
		
		public Component setType (ComponentType type) {
		    this.type = type;
		    return this;
		}
		public Component setColor (ChatColor color) {
		    this.color = color;
		    return this;
		}
		public Component setBold (boolean bold) {
		    this.bold = bold;
		    return this;
		}
		public Component setUnderlined (boolean underlined) {
		    this.underlined = underlined;
		    return this;
		}
		public Component setStrikethrough (boolean strikethrough) {
		    this.strikethrough = strikethrough;
		    return this;
		}
		public Component setItalic (boolean italic) {
		    this.italic = italic;
		    return this;
		}
		public Component setObfuscated (boolean obfuscated) {
		    this.obfuscated = obfuscated;
		    return this;
		}
		public Component setFormat (String format) {
		    this.format = format;
		    return this;
		}
		
		public ComponentType getType() {
		    return this.type;
		}
		public ChatColor getColor() {
		    return this.color;
		}
		public boolean isBold() {
		    return this.bold;
		}
		public boolean isUnderlined() {
		    return this.underlined;
		}
		public boolean isStrikethrough() {
		    return this.strikethrough;
		}
		public boolean isItalic() {
		    return this.italic;
		}
		public boolean isObfuscated() {
		    return this.obfuscated;
		}
		public String getFormat() {
		    return this.format;
		}
		
		public Component (Function<Entity, String> function) {
			this.function = function;
		}
		
		public Component setFunction(Function<Entity, String> function) {
			this.function = function;
			return this;
		}
		
		public Function<Entity, String> getFunction() {
			return this.function;
		}
		
		public boolean hasFunction() {
			return this.function != null;
		}
		
		public void serialize(ByteDataSerializer serializer) {
			serializer.writeEnum(this.type);
			serializer.writeChar(this.color.getChar());
			serializer.writeString(this.format);

			serializer.writeBoolean(this.bold);
			serializer.writeBoolean(this.underlined);
			serializer.writeBoolean(this.strikethrough);
			serializer.writeBoolean(this.italic);
			serializer.writeBoolean(this.obfuscated);
		}
		
		public void deserialize(ByteDataSerializer serializer) {
			this.type = serializer.readEnum(ComponentType.class);
			this.color = ChatColor.getByChar(serializer.readChar());
			this.format = serializer.readString();

			this.bold = serializer.readBoolean();
			this.underlined = serializer.readBoolean();
			this.strikethrough = serializer.readBoolean();
			this.italic = serializer.readBoolean();
			this.obfuscated = serializer.readBoolean();
		}
		
		public enum ComponentType {
			HEALTH_DISPLAY (ChatColor.RED, "{HP}/{HP_MAX}"),
			HEALTH_DISPLAY_SQUARES (ChatColor.RED, "{HP_SQUARES}"),

			HP (ChatColor.WHITE, "{HP}"),
			HP_MAX (ChatColor.WHITE, "{HP_MAX}"),
			NAME (ChatColor.WHITE, "{NAME}"),

			ENTITY_HOLDING (ChatColor.WHITE, "{ENTITY_HOLDING}"),
			ENTITY_HELMET (ChatColor.WHITE, "{ENTITY_HELMET}"),
			ENTITY_CHESTPLATE (ChatColor.WHITE, "{ENTITY_CHESTPLATE}"),
			ENTITY_LEGGINGS (ChatColor.WHITE, "{ENTITY_LEGGINGS}"),
			ENTITY_BOOTS (ChatColor.WHITE, "{ENTITY_BOOTS}"),

			ONLINE_PLAYERS (ChatColor.WHITE, "{ONLINE_PLAYERS}"),
			MAX_PLAYERS (ChatColor.WHITE, "{MAX_PLAYERS}"),
			VIEWING_PLAYER (ChatColor.WHITE, "{VIEWING_PLAYER}"),
			VIEWING_PLAYER_DISPLAY_NAME (ChatColor.WHITE, "{VIEWING_PLAYER_DISPLAY_NAME}"),
			TIME (ChatColor.WHITE, "{TIME}"), // TODO update
			DATE (ChatColor.WHITE, "{DATE}"), // TODO update
//			TIME_MINECRAFT (ChatColor.WHITE, "{TIME_MINECRAFT}"), TODO
			VARTEXT (ChatColor.RESET, "");
			
			private final ChatColor color;
			private final String format;
			
			ComponentType(ChatColor color, String format) {
				this.color = color;
				this.format = format;
			}
			
			public ChatColor getColor() {
				return this.color;
			}
			
			public String getFormat() {
				return this.format;
			}
		}
	}
}
