package com.xyrality.wotter.rest.v1.model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import com.xyrality.wotter.container.NSTimestampInstantConverter;

import io.swagger.annotations.ApiModelProperty;

@Dto
public class WotDTO implements ETagResource {
	@DtoField(value = "content")
	@ApiModelProperty(value = "content of the wot", readOnly = true)
	private String text;

	@DtoField(value = "account.name", readOnly = true)
	@ApiModelProperty(value = "name of the author", readOnly = true)
	private String author;

	@DtoField(value = "postedAt", converter = NSTimestampInstantConverter.NAME)
	@ApiModelProperty(value = "post time of the wot", readOnly = true, dataType = "dateTime")
	private Instant postTime;

	@DtoField(value = "ID", readOnly = true)
	@ApiModelProperty(value = "identifier of the wot", readOnly = true)
	private int id;

	@DtoField(value = "eTag.value", readOnly = true)
	@JsonIgnore
	private String eTag;

	public String getText() {
		return text;
	}

	public void setText(final String text) {
		this.text = text;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(final String author) {
		this.author = author;
	}

	public Instant getPostTime() {
		return postTime;
	}

	public void setPostTime(final Instant postTime) {
		this.postTime = postTime;
	}

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String geteTag() {
		return eTag;
	}

	public void seteTag(final String eTag) {
		this.eTag = eTag;
	}
}
