package com.xyrality.wotter.eo;

import java.security.Principal;

import org.apache.log4j.Logger;

public class Account extends _Account implements Principal {
	private static Logger log = Logger.getLogger(Account.class);

	public String getName() {
		return name();
	}
}
