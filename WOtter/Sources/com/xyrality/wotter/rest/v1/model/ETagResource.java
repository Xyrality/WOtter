package com.xyrality.wotter.rest.v1.model;

/**
 * Marks a resource supporting etag-based caching
 */
public interface ETagResource {
	String geteTag();
}
