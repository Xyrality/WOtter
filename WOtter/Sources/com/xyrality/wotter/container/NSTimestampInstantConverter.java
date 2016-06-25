package com.xyrality.wotter.container;

import java.time.Instant;

import com.inspiresoftware.lib.dto.geda.adapter.BeanFactory;
import com.inspiresoftware.lib.dto.geda.adapter.ValueConverter;
import com.webobjects.foundation.NSTimestamp;

public class NSTimestampInstantConverter
		implements
		ValueConverter {

	public static final String NAME = "NSTimestampInstantConverter";

	@Override
	public Object convertToDto(Object object, BeanFactory beanFactory) {
		if (object == null) {
			return null;
		}

		if (object instanceof NSTimestamp) {
			final NSTimestamp ts = (NSTimestamp)object;
			return Instant.ofEpochMilli(ts.getTime());
		}
		throw new UnsupportedOperationException("Cannot convert " + object.getClass().getName() + " to Instant");
	}

	@Override
	public Object convertToEntity(Object object, Object oldEntity, BeanFactory beanFactory) {
		if (object == null) {
			return null;
		}

		if (object instanceof Instant) {
			return new NSTimestamp(((Instant)object).toEpochMilli());
		}

		throw new UnsupportedOperationException("Cannot convert " + object.getClass().getName() + " to NSTimestamp");
	}

}
