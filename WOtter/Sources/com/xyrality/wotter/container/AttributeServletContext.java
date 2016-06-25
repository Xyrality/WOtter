package com.xyrality.wotter.container;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.google.common.collect.Maps;

@SuppressWarnings("deprecation")
class AttributeServletContext implements ServletContext {
	private Map<String, Object> attributes = Maps.newHashMap();
	
	@Override
	public ServletContext getContext(String paramString) {
		return this;
	}

	@Override
	public int getMajorVersion() {
		return 0;
	}

	@Override
	public int getMinorVersion() {
		return 0;
	}

	@Override
	public String getMimeType(String paramString) {
		return paramString;
	}

	@Override
	public Set getResourcePaths(String paramString) {
		return null;
	}

	@Override
	public URL getResource(String paramString) throws MalformedURLException {
		return null;
	}

	@Override
	public InputStream getResourceAsStream(String paramString) {
		return null;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String paramString) {
		return null;
	}

	@Override
	public RequestDispatcher getNamedDispatcher(String paramString) {
		return null;
	}

	@Override
	public Servlet getServlet(String paramString) throws ServletException {
		return null;
	}

	@Override
	public Enumeration getServlets() {
		return null;
	}

	@Override
	public Enumeration getServletNames() {
		return null;
	}

	@Override
	public void log(String paramString) {
		
	}

	@Override
	public void log(Exception paramException, String paramString) {
	}

	@Override
	public void log(String paramString, Throwable paramThrowable) {
	}

	@Override
	public String getRealPath(String paramString) {
		return null;
	}

	@Override
	public String getServerInfo() {
		return null;
	}

	@Override
	public String getInitParameter(String paramString) {
		return null;
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return null;
	}

	@Override
	public Object getAttribute(String paramString) {
		return attributes.get(paramString);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration<String> getAttributeNames() {
		return new Vector<>(attributes.keySet()).elements();
	}

	@Override
	public void setAttribute(String paramString, Object paramObject) {
		attributes.put(paramString, paramObject);
	}

	@Override
	public void removeAttribute(String paramString) {
		attributes.remove(paramString);
	}

	@Override
	public String getServletContextName() {
		return this.getClass().getName();
	}
	
}
