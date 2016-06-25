package com.xyrality.wotter.rest.v1.model;

import com.inspiresoftware.lib.dto.geda.annotations.DtoField;

import io.swagger.annotations.ApiModelProperty;

public class ErrorDTO {
	@DtoField(value = "message", readOnly = true)
	@ApiModelProperty(value = "error message", readOnly = true)
	private String message;

	public ErrorDTO() {
	}

	public ErrorDTO(final String message) {
		this.message = message;
	}

	public ErrorDTO(final String formatString, final Object... args) {
		this.message = String.format(formatString, args);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}
}
