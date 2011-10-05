<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="field[@guitype = 'checkbox' ]">
		<tr>
			<xsl:call-template name="field_label">
				<xsl:with-param name="labelClass">label-checkbox</xsl:with-param>
			</xsl:call-template>
			<td class="checkbox">
				<xsl:call-template name="field_checkbox_input"/>
			</td>
		</tr>
		<tr>
			<td>
				<xsl:if test="description">
				<span class="greytext"><xsl:value-of select="description" disable-output-escaping="yes"/></span>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template match="field[@guitype = 'leftcheckbox' ]">
		<tr>
			<td class="checkbox">
				<xsl:call-template name="field_checkbox_input"/>
			</td>
			<xsl:call-template name="field_label">
				<xsl:with-param name="labelClass">label-checkbox</xsl:with-param>
			</xsl:call-template>
		</tr>
		<tr>
			<td>
				<xsl:if test="description">
				<span class="greytext"><xsl:value-of select="description" disable-output-escaping="yes"/></span>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template name="field_checkbox_input">
		<input type="checkbox" id="{@name}" name="{@name}" class="{@class} checkbox" value="true">
				<xsl:if test="'true' = @value">
				<xsl:attribute name="checked">checked</xsl:attribute>
				</xsl:if>
		</input>
	</xsl:template>
</xsl:stylesheet>
