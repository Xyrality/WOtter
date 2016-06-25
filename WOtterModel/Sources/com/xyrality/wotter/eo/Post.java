package com.xyrality.wotter.eo;

import javax.ws.rs.core.EntityTag;

import com.webobjects.foundation.NSTimestamp;

public class Post extends _Post {
	public String getContent() {
		return content();
	}

	public Account getAccount() {
		return account();
	}

	public NSTimestamp getPostedAt() {
		return postedAt();
	}

	public int getID() {
		return Integer.parseInt(primaryKeyInTransaction());
	}

	public EntityTag geteTag() {
		final String etag = String.format("%s/%08x/%016x", primaryKey(), getContent().hashCode(), getPostedAt().getTime());
		return new EntityTag(etag);
	}
}
