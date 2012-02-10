<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="field[@guitype = 'groupedcheckbox' ]">
		<xsl:variable name="fieldvalue" select="@value"/>
		<xsl:variable name="fieldname" select="@name"/>
		<xsl:variable name="fieldtitle" select="@title"/>
		<xsl:variable name="fieldrequired" select="@required"/>
		<xsl:variable name="optioncount" select="count(optionlist/option)"/>
		<xsl:variable name="errorMsg" select="@error"/>
		<xsl:for-each select="optionlist/option">
			<tr>
				<xsl:if test="position() = 1">
					<xsl:call-template name="field_label">
						<xsl:with-param name="fieldtitle" select="$fieldtitle"/>
						<xsl:with-param name="fieldname" select="$fieldname"/>
						<xsl:with-param name="fieldrequired" select="$fieldrequired"/>
						<xsl:with-param name="errorMsg" select="$errorMsg"/>
						<xsl:with-param name="rowspan" select="$optioncount"/>
					</xsl:call-template>
				</xsl:if>
				<td class="field">
					<input name="{$fieldname}" id="{@value}" type="checkbox" value="{@value}">
						<xsl:if test="@selected = 'true' ">
							<xsl:attribute name="checked">checked</xsl:attribute>
						</xsl:if>
						<xsl:value-of select="."/>
					</input>
					<xsl:text disable-output-escaping="yes"/>
				</td>
			</tr>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="field[@guitype = 'groupedcheckbox' ]" mode="readonly">
		<xsl:variable name="fieldvalue" select="@value"/>
		<xsl:variable name="fieldname" select="@name"/>
		<xsl:variable name="fieldtitle" select="@title"/>
		<xsl:variable name="fieldrequired" select="@required"/>
		<xsl:variable name="optioncount" select="count(optionlist/option)"/>
		<xsl:variable name="errorMsg" select="@error"/>
		<xsl:for-each select="optionlist/option">
						<xsl:if test="@selected = 'true' ">
<xsl:value-of select="$fieldtitle" disable-output-escaping="yes"/>
						</xsl:if>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
