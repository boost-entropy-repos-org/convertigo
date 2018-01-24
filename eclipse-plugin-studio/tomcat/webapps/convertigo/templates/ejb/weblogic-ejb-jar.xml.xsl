<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output encoding="ISO-8859-1" media-type="text/xml" indent="yes"/>
	<xsl:include href="./EJBUtils.xsl"/>
	
	<xsl:template match="/child::*[local-name()='definitions']">	
	
<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE  weblogic-ejb-jar PUBLIC "-//BEA Systems, Inc.//DTD WebLogic 6.0.0 EJB//EN" "http://www.bea.com/servers/wls600/dtd/weblogic-ejb-jar.dtd""&gt;</xsl:text>

<weblogic-ejb-jar>
 <description><![CDATA[Generated by Convertigo]]></description>
   <weblogic-enterprise-bean>
      <ejb-name><xsl:call-template name="getClassNameOfCurrent"/>EJB</ejb-name>
      <stateful-session-descriptor>
      </stateful-session-descriptor>
      <reference-descriptor>
      </reference-descriptor>
      <jndi-name><xsl:call-template name="getClassNameOfCurrent"/>Bean</jndi-name>
      <local-jndi-name><xsl:call-template name="getClassNameOfCurrent"/>EJBLocal</local-jndi-name>
   </weblogic-enterprise-bean>
</weblogic-ejb-jar>
	</xsl:template>
</xsl:stylesheet>