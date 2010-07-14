<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="xml" version="1.0" encoding="utf-8" indent="yes" 
		doctype-public="-//Mort Bay Consulting//DTD Configure//EN"
		doctype-system="http://www.eclipse.org/jetty/configure.dtd" />

	<xsl:template match="Context">
		<!-- =============================================================== -->
		<!-- Configure the Jetty Server                                      -->
		<!--                                                                 -->
		<!-- Documentation of this file format can be found at:              -->
		<!-- http://docs.codehaus.org/display/JETTY/jetty.xml                -->
		<!--                                                                 -->
		<!-- =============================================================== -->
		<Configure class="org.eclipse.jetty.webapp.WebAppContext">
			<xsl:apply-templates select="Resource" />
			<xsl:apply-templates select="Environment" />

			<!-- Put the mmbase datadir also in the target directory -->			
			<New class="org.eclipse.jetty.plus.jndi.EnvEntry">
				<Arg type="java.lang.String">mmbase/mmbaseroot/datadir</Arg>
				<Arg type="java.lang.String">target/datadir</Arg>
				<Arg type="boolean">true</Arg>
			</New>
		</Configure>
	</xsl:template>

	<xsl:template match="Resource[@type='javax.mail.Session']">
		<New class="org.eclipse.jetty.plus.jndi.Resource">
			<Arg>mail/Session</Arg>
			<Arg>
				<New class="org.eclipse.jetty.jndi.factories.MailSessionReference">
					<Set name="properties">
						<New class="java.util.Properties">
							<Put name="mail.smtp.host">
								<xsl:value-of select="@mail.smtp.host" />
							</Put>
						</New>
					</Set>
				</New>
			</Arg>
		</New>
	</xsl:template>

	<xsl:template match="Resource[@type='javax.sql.DataSource']">
		<New class="org.eclipse.jetty.plus.jndi.Resource">
			<Arg>
				<xsl:value-of select="@name" />
			</Arg>
			<Arg>
				<New class="com.mchange.v2.c3p0.ComboPooledDataSource">
					<Set name="driverClass">
						<xsl:value-of select="@driverClassName" />
					</Set>
					<Set name="jdbcUrl">
						<xsl:value-of select="@url" />
					</Set>
					<Set name="user">
						<xsl:value-of select="@username" />
					</Set>
					<Set name="password">
						<xsl:value-of select="@password" />
					</Set>
				</New>
			</Arg>
		</New>
	</xsl:template>

	<xsl:template match="Environment">
		<New class="org.eclipse.jetty.plus.jndi.EnvEntry">
			<Arg type="java.lang.String">
				<xsl:value-of select="@name" />
			</Arg>
			<Arg type="java.lang.String">
				<xsl:value-of select="@value" />
			</Arg>
			<Arg type="boolean">true</Arg>
		</New>
	</xsl:template>

</xsl:stylesheet>