package com.xyrality.wotter.rest.v1.api;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOKeyGlobalID;
import com.webobjects.foundation.NSRange;
import com.webobjects.foundation.NSTimestamp;
import com.xyrality.wotter.eo.Account;
import com.xyrality.wotter.eo.Post;
import com.xyrality.wotter.rest.v1.model.ErrorDTO;
import com.xyrality.wotter.rest.v1.model.Helper;
import com.xyrality.wotter.rest.v1.model.WotDTO;

import er.extensions.eof.ERXEC;
import er.extensions.eof.ERXFetchSpecification;
import er.extensions.eof.ERXS;
import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.jaxrs.PATCH;

@Path("/v1/wots")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(tags = {"public"})
public class WotsApi {
	private final Assembler dtoWotAssembler;

	@Context
	private ERXEC editingContext;

	public WotsApi() {
		dtoWotAssembler = DTOAssembler.newAssembler(WotDTO.class, Post.class);
	}

	@GET
	@Path("{wotID}")
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "the wot", response = WotDTO.class),
					@ApiResponse(code = 404, message = "Not found", response = ErrorDTO.class)
			}
	)
	@ApiOperation("show a single wot")
	public WotDTO showWot(
			@PathParam("wotID")
			@NotNull
			final Integer wotID,

			@Context
			final Request request
	) {
		final Post post = (Post)editingContext.faultForGlobalID(EOKeyGlobalID.globalIDWithEntityName(Post.ENTITY_NAME, new Object[] { wotID }), editingContext);
		checkETagForPost(request, post);

		final WotDTO wot = new WotDTO();
		dtoWotAssembler.assembleDto(wot, post, Helper.getGedaAdapters(), null);
		return wot;
	}

	private void checkETagForPost(final @Context Request request, final Post post) {
		final Response.ResponseBuilder preconditionResponse = request.evaluatePreconditions(post.geteTag());
		if (preconditionResponse != null) {
			throw new WebApplicationException(preconditionResponse.build());
		}
	}

	@GET
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "the wots", response = WotDTO.class, responseContainer = "list"),
					@ApiResponse(code = 404, message = "Not found", response = ErrorDTO.class)
			}
	)
	@ApiOperation("list multiple wots at once")
	public List<WotDTO> listWots(
			@QueryParam("timeOrder")
			@ApiParam(
					value = "if wots should be sorted by date ascending or descending",
					allowableValues = "asc,desc")
			@DefaultValue("desc")
			@Pattern(regexp = "(asc|desc)")
			final String orderDirection,

			@QueryParam("offset")
			@ApiParam("offset in the list of wots")
			@DefaultValue("0")
			@Min(0)
			final Integer offset,

			@QueryParam("limit")
			@ApiParam("maximum number of wots to display")
			@DefaultValue("30")
			@Min(1)
			@Max(50)
			final Integer limit
	) {
		final ERXFetchSpecification<Post> fetchSpec = new ERXFetchSpecification<>(Post.ENTITY_NAME);
		fetchSpec.setSortOrderings(orderDirection.equals("asc") ? ERXS.ascs(Post.POSTED_AT_KEY) : ERXS.descs(Post.POSTED_AT_KEY));
		fetchSpec.setFetchRange(new NSRange(offset, limit));
		final List<Post> posts = fetchSpec.fetchObjects(editingContext);

		final List<WotDTO> wots = new ArrayList<>();
		dtoWotAssembler.assembleDtos(wots, posts, Helper.getGedaAdapters(), null);
		return wots;
	}

	@PUT
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "the newly created wot", response = WotDTO.class),
					@ApiResponse(code = 403, message = "not allowed", response = ErrorDTO.class)
			}
	)
	@ApiOperation(
			value = "posts a new wot",
			authorizations = { @Authorization(value = "credentialsAuth") }
	)
	public WotDTO postNewWot(
			@ApiParam("new wot content to post")
			final WotDTO wot,

			@Auth
			@ApiParam(hidden = true)
			final Account poster
	) {
		final Post post = (Post)EOUtilities.createAndInsertInstance(editingContext, Post.ENTITY_NAME);
		dtoWotAssembler.assembleEntity(wot, post, Helper.getGedaAdapters(), null);
		if (wot.getPostTime() == null) {
			post.setPostedAt(new NSTimestamp());
		}
		post.setAccountRelationship(poster);
		editingContext.saveChanges();

		final WotDTO storedWot = new WotDTO();
		dtoWotAssembler.assembleDto(storedWot, post, Helper.getGedaAdapters(), null);
		return storedWot;
	}

	@PATCH
	@ApiResponses(
			value = {
					@ApiResponse(code = 200, message = "the edited wot", response = WotDTO.class),
					@ApiResponse(code = 403, message = "not allowed", response = ErrorDTO.class)
			}
	)
	@ApiOperation(
			value = "edits a wot",
			notes = "only text and id of the incoming object are relevant",
			authorizations = { @Authorization(value = "credentialsAuth") }
	)
	public WotDTO editWot(
			@ApiParam("edited wot content")
			final WotDTO wot,

			@Context
			final Request request,

			@Auth
			@ApiParam(hidden = true)
			final Account poster
	) {
		final Post post = (Post)editingContext.faultForGlobalID(EOKeyGlobalID.globalIDWithEntityName(Post.ENTITY_NAME, new Object[] { wot.getId() }), editingContext);
		checkETagForPost(request, post);
		if (!post.getAccount().equals(poster)) {
			throw new WebApplicationException(Response
					.status(Response.Status.FORBIDDEN)
					.entity(new ErrorDTO("You may only edit your own wots"))
					.build());
		}

		post.setContent(wot.getText());
		editingContext.saveChanges();

		final WotDTO storedWot = new WotDTO();
		dtoWotAssembler.assembleDto(storedWot, post, Helper.getGedaAdapters(), null);
		return storedWot;
	}

	@DELETE
	@Path("{wotID}")
	@ApiResponses(
			value = {
					@ApiResponse(code = 204, message = "successfully deleted"),
					@ApiResponse(code = 404, message = "Not found", response = ErrorDTO.class)
			}
	)
	@ApiOperation(
			value = "deletes a wot",
			authorizations = { @Authorization(value = "credentialsAuth") }
	)
	public void deleteWot(
			@PathParam("wotID")
			@NotNull
			final Integer wotID,

			@Auth
			@ApiParam(hidden = true)
			final Account poster
	) {
		final Post post = (Post)editingContext.faultForGlobalID(EOKeyGlobalID.globalIDWithEntityName(Post.ENTITY_NAME, new Object[] { wotID }), editingContext);
		if (!post.getAccount().equals(poster)) {
			throw new WebApplicationException(Response
					.status(Response.Status.FORBIDDEN)
					.entity(new ErrorDTO("You may only delete your own wots"))
					.build());
		}

		editingContext.deleteObject(post);
		editingContext.saveChanges();
	}
}
