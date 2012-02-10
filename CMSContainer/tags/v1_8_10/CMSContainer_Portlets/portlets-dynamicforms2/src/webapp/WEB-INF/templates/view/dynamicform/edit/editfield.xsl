<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="field[@guitype = 'edit' ]">
	<xsl:variable name="maxlen">
		<xsl:choose>
			<xsl:when test="@maxlength > 1000000">256</xsl:when>
			<xsl:otherwise><xsl:value-of select="@maxlength"/></xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
		<tr>
			<xsl:call-template name="field_label" />
			<td class="field">
				<input type="text" id="{@name}" name="{@name}" class="{@class} edit" value="{@value}" maxlength="{$maxlen}"/>
				<xsl:if test="description">
    				<span class="greytext"><xsl:value-of select="description" disable-output-escaping="yes"/></span>
    			</xsl:if>
			</td>
			<xsl:if test="tooltip">
				<td class="infobtn">
					<img src="{$URLCONTEXT}gfx/dynamicforms/i_moreinfo_grey.gif" width="15" height="15" border="0" alt="">
						<xsl:attribute name="title"><xsl:value-of select="tooltip"/></xsl:attribute>
					</img>
				</td>
			</xsl:if>
		</tr>
	</xsl:template>
</xsl:stylesheet>
