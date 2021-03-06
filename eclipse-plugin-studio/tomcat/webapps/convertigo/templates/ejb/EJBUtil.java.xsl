<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output encoding="ISO-8859-1" media-type="text/plain" indent="yes"/>
	<xsl:include href="./EJBUtils.xsl"/>
	<xsl:template match="/child::*[local-name()='definitions']">	
package com.twinsoft.convertigo.ejb;

import javax.rmi.PortableRemoteObject;
import javax.naming.NamingException;
import javax.naming.InitialContext;

import java.util.Hashtable;

import java.net.InetAddress;
import java.security.SecureRandom;

import com.twinsoft.convertigo.ejb.<xsl:call-template name="getClassNameOfCurrent"/>EJB;
import com.twinsoft.convertigo.ejb.<xsl:call-template name="getClassNameOfCurrent"/>EJBHome;
import com.twinsoft.convertigo.ejb.<xsl:call-template name="getClassNameOfCurrent"/>EJBLocal;
import com.twinsoft.convertigo.ejb.<xsl:call-template name="getClassNameOfCurrent"/>EJBLocalHome;

/**
 * Utility class for <xsl:call-template name="getClassNameOfCurrent"/>EJB.
 * Automatically generated by twinsoft Convertigo.
 */
public class <xsl:call-template name="getClassNameOfCurrent"/>EJBUtil{
	/**
	 * Cached remote home (EJBHome).
	 * Uses lazy loading to obtain its value (loaded by getHome() methods).
	 */
	private static <xsl:call-template name="getClassNameOfCurrent"/>EJBHome cachedRemoteHome = null;
	
	/**
	 * Cached local home (EJBLocalHome).
	 * Uses lazy loading to obtain its value (loaded by getLocalHome() methods).
	 */
	private static <xsl:call-template name="getClassNameOfCurrent"/>EJBLocalHome cachedLocalHome = null;
	
	// Home interface lookup methods
	
	/**
	 * Obtain remote home interface from default initial context
	 * @return Home interface for <xsl:call-template name="getClassNameOfCurrent"/>EJB. Lookup using JNDI_NAME
	 */
	public static <xsl:call-template name="getClassNameOfCurrent"/>EJBHome getHome() throws NamingException {
		if (cachedRemoteHome == null) {
			// Obtain initial context
			InitialContext initialContext = new InitialContext();
			try {
				java.lang.Object objRef = initialContext.lookup(<xsl:call-template name="getClassNameOfCurrent"/>EJBHome.JNDI_NAME);
				cachedRemoteHome = (<xsl:call-template name="getClassNameOfCurrent"/>EJBHome) PortableRemoteObject.narrow(objRef, <xsl:call-template name="getClassNameOfCurrent"/>EJBHome.class);
			}
			finally {
				initialContext.close();
			}
		}
		return cachedRemoteHome;
	}
	
	/**
	 * Obtain remote home interface from parameterised initial context
	 * @param environment Parameters to use for creating initial context
	 * @return Home interface for <xsl:call-template name="getClassNameOfCurrent"/>EJB. Lookup using JNDI_NAME
	 */
	public static <xsl:call-template name="getClassNameOfCurrent"/>EJBHome getHome( Hashtable environment ) throws NamingException{
		// Obtain initial context
		InitialContext initialContext = new InitialContext(environment);
		try {
			java.lang.Object objRef = initialContext.lookup(<xsl:call-template name="getClassNameOfCurrent"/>EJBHome.JNDI_NAME);
			return (<xsl:call-template name="getClassNameOfCurrent"/>EJBHome) PortableRemoteObject.narrow(objRef, <xsl:call-template name="getClassNameOfCurrent"/>EJBHome.class);
		}
		finally {
			initialContext.close();
		}
	}
	
	/**
	 * Obtain local home interface from default initial context
	 * @return Local home interface for <xsl:call-template name="getClassNameOfCurrent"/>EJB. Lookup using JNDI_NAME
	 */
	public static <xsl:call-template name="getClassNameOfCurrent"/>EJBLocalHome getLocalHome() throws NamingException{
		// Local homes shouldn't be narrowed, as there is no RMI involved.
		if (cachedLocalHome == null) {
			// Obtain initial context
			InitialContext initialContext = new InitialContext();
			try {
				cachedLocalHome = (<xsl:call-template name="getClassNameOfCurrent"/>EJBLocalHome) initialContext.lookup(<xsl:call-template name="getClassNameOfCurrent"/>EJBLocalHome.JNDI_NAME);
			}
			finally {
				initialContext.close();
			}
		}
		return cachedLocalHome;
	}
}
	</xsl:template>
</xsl:stylesheet>