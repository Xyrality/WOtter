package com.xyrality.wotter.rest.v1.model;

import java.util.HashMap;
import java.util.Map;

import com.xyrality.wotter.container.NSTimestampInstantConverter;

public class Helper {
	private static final Map<String, Object> gedaAdapters = new HashMap<>();

	static {
		gedaAdapters.put(NSTimestampInstantConverter.NAME, new NSTimestampInstantConverter());
	}

	public static Map<String, Object> getGedaAdapters() {
		return gedaAdapters;
	}
}
