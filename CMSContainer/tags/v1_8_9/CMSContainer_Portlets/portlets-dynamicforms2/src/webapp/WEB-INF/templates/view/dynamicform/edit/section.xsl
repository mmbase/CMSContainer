<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<!-- field includes -->
	<xsl:include href="dynamicform/edit/editfield.xsl"/>
	<xsl:include href="dynamicform/edit/checkboxfield.xsl"/>
	<xsl:include href="dynamicform/edit/datafield.xsl"/>
	<xsl:include href="dynamicform/edit/hiddenfield.xsl"/>
	<xsl:include href="dynamicform/edit/textfield.xsl"/>
	<xsl:include href="dynamicform/edit/selectfield.xsl"/>
	<xsl:include href="dynamicform/edit/horizontalradiofield.xsl"/>
	<xsl:include href="dynamicform/edit/verticalradiofield.xsl"/>
	<xsl:include href="dynamicform/edit/groupedcheckboxfield.xsl"/>
	<xsl:include href="dynamicform/edit/imagefield.xsl"/>
	
	<!-- fieldset includes -->
	<xsl:include href="dynamicform/edit/fieldset.xsl"/>	
	<xsl:include href="dynamicform/edit/sectionnavigation.xsl"/>

	<xsl:template match="section">
		<fieldset>
			<legend>
				<span>
					<xsl:value-of select="@title"/>
				</span>
			</legend>
			<xsl:for-each select="description">
				<p class="section-description">
					<xsl:if test="@title != ''">
						<span class="section-description-title">
							<xsl:value-of disable-output-escaping="yes" select="@title"/>
						</span>
					</xsl:if>
					<xsl:value-of disable-output-escaping="yes" select="."/>
				</p>
			</xsl:for-each>
			<table border="0" cellspacing="0" cellpadding="0">
				<xsl:apply-templates select="field | fieldset"/>
			</table>
		</fieldset>
		<xsl:apply-templates select="navigation"/>
	</xsl:template>
	
	<xsl:template name="field_label">
		<xsl:param name="labelClass">label</xsl:param>
		<xsl:param name="fieldtitle" select="@title"/>
		<xsl:param name="fieldname" select="@name"/>
		<xsl:param name="fieldrequired" select="@required"/>
		<xsl:param name="errorMsg" select="@error"/>
		<xsl:param name="rowspan"></xsl:param>
		<td>
			<xsl:if test="$rowspan != ''">
				<xsl:attribute name="rowspan"><xsl:value-of select="$rowspan" /></xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$errorMsg != ''">
					<xsl:attribute name="class"><xsl:value-of select="$labelClass" /> redtext</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="class"><xsl:value-of select="$labelClass" /></xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="$fieldname != ''">
					<label for="{$fieldname}">
						<xsl:value-of select="$fieldtitle" disable-output-escaping="yes" />
						<xsl:if test="$fieldrequired = 'true' and $fieldtitle != '&#160;'">
							<xsl:text> *</xsl:text>
						</xsl:if>
					</label>
				</xsl:when>
				<xsl:otherwise>
					<label>
						<xsl:value-of select="$fieldtitle" disable-output-escaping="yes" />
						<xsl:if test="$fieldrequired = 'true' and $fieldtitle != '&#160;'">
							<xsl:text> *</xsl:text>
						</xsl:if>
					</label>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="$errorMsg != ''">
					<div class="redtext">
						<xsl:value-of select="$errorMsg" />
					</div>
				</xsl:when>
			</xsl:choose>
		</td>
	</xsl:template>
	
</xsl:stylesheet>
