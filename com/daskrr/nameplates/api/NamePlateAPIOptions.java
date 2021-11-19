package com.daskrr.nameplates.api;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class NamePlateAPIOptions
{
	private final Map<Key<?>, Option<?>> options;
	
	public NamePlateAPIOptions() {
		this.options = Maps.newHashMap();
		
		Key.values().forEach((key) -> {
			this.options.put(key, key.getDefaultValue());
		});
	}
	
	public List<Pair<Key<?>, Option<?>>> getOptions() {
		List<Pair<Key<?>, Option<?>>> options = Lists.newArrayList();
		
		this.options.forEach((key, opt) -> options.add(Pair.of(key, opt)));
		
		return options;
	}
	
	@SuppressWarnings("unchecked")
	public <T> Option<T> getOption(Key<T> key) {
		return (Option<T>) options.get(key);
	}
	
	public <T> void setOption(Key<T> key, Option<T> option) {
		this.options.put(key, option);
	}
	
	public static class Key<T> {

		private static final List<Key<?>> keys = Lists.newArrayList();

		public static final Key<Integer> VIEW_DISTANCE = makeKey(new Key<Integer>(160)); // THIS SHOULD NOT BE BIGGER THAN ANY SPECIFIC VIEW DISTANCES
		public static final Key<Boolean> RESOURCE_FRIENDLY = makeKey(new Key<Boolean>(false)); // TODO
		public static final Key<Integer> POSITION_UPDATE_TIME = makeKey(new Key<Integer>(2));
		public static final Key<Double> MARGIN_BOTTOM = makeKey(new Key<Double>(.1D));
		public static final Key<Double[]> MARGIN = makeKey(new Key<Double[]>(new Double[] {0D, 0D}));
		public static final Key<PassengerPlateOverlapScenario> PASSENGER_PLATE_OVERLAP = makeKey(new Key<PassengerPlateOverlapScenario>(PassengerPlateOverlapScenario.LEFT));
		public static final Key<Boolean> RENDER_BEHIND_WALLS = makeKey(new Key<Boolean>(true));
		
		private final T defaultValue;
		
		private Key (T defaultValue) {
			this.defaultValue = defaultValue;
		}
		
		public Option<T> getDefaultValue() {
			return new Option<T>(this.defaultValue);
		}
		
		private static <T> Key<T> makeKey(Key<T> key) {
			keys.add(key);
			return key;
		}
		
		public static List<Key<?>> values() {
			return Lists.newArrayList(keys);
		}
	}
	
	public static class Option<T> {
		
		private T value;
		
		public Option(T value) {
			this.value = value;
		}
		
		public T getValue() {
			return this.value;
		}
		
		public void setValue(T value) {
			this.value = value;
		}
	}
}
