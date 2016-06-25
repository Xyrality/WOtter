package com.xyrality.wotter.filters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.tuple.Pair;

import com.xyrality.wotter.container.ERXECRequestContextHandler;
import com.xyrality.wotter.eo.Account;
import com.xyrality.wotter.rest.v1.model.ErrorDTO;

import er.extensions.eof.ERXEC;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.basic.BasicCredentials;

/**
 * Filter to parse base authentication and retrieve the account. This is not the way
 * to implement this as in this case the password is just the account name.
 */
@Priority(Priorities.AUTHENTICATION)
public class AccountAuthenticationFilter extends AuthFilter<BasicCredentials, Account> {

	public AccountAuthenticationFilter() {
		this.realm = "WOtter";
		this.authorizer = (principal, role) -> principal.getName().equals(role);
		this.unauthorizedHandler = (prefix, realm) -> Response
				.status(Response.Status.UNAUTHORIZED)
				.header(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"" + realm + "\"")
				.entity(new ErrorDTO("Unauthorized"))
				.build();
	}

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {
		final String authenticationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		if (authenticationHeader != null) {
			final Pair<String, String> userPasswordCombination = extractBasicAuthentication(authenticationHeader);
			final String username = userPasswordCombination.getLeft();
			final String password = userPasswordCombination.getRight();
			if (username.equals(password)) {
				final ERXEC editingContext = ERXECRequestContextHandler.ecForContext(requestContext);
				final Account account = Account.fetchAccount(editingContext, Account.NAME_KEY, username);
				if (account != null) {
					requestContext.setSecurityContext(new AccountSecurityContext(requestContext, account));
					return;
				}
			}
		}

		throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
	}

	private class AccountSecurityContext implements SecurityContext {
		private final Account account;
		private final boolean isSecure;

		AccountSecurityContext(final ContainerRequestContext requestContext, final Account account) {
			this.account = account;
			this.isSecure = requestContext.getSecurityContext().isSecure();
		}

		@Override
		public Principal getUserPrincipal() {
			return account;
		}

		@Override
		public boolean isUserInRole(final String role) {
			return authorizer.authorize(account, role);
		}

		@Override
		public boolean isSecure() {
			return isSecure;
		}

		@Override
		public String getAuthenticationScheme() {
			return SecurityContext.BASIC_AUTH;
		}
	}

	private Pair<String, String> extractBasicAuthentication(final String authenticationHeader) {
		if (!authenticationHeader.startsWith("Basic ")) {
			throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
		}
		final String actualAuth = authenticationHeader.substring(6);
		final String authValueString = new String(DatatypeConverter.parseBase64Binary(actualAuth), StandardCharsets.UTF_8);
		final String[] userPasswordCombo = authValueString.split(":", 2);
		if (userPasswordCombo.length < 2) {
			throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
		}

		return Pair.of(userPasswordCombo[0], userPasswordCombo[1]);
	}
}
