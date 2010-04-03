<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="field[@guitype = 'text' ]">
		<tr>
			<xsl:call-template name="field_label" />
			<td class="field">
				<textarea rows="{rows}" cols="{cols}" id="{@name}" name="{@name}" class="{@class} text">
				    <xsl:choose>
				        <xsl:when test="not(@value) or @value = ''"><xsl:text>&#x0A;</xsl:text></xsl:when>
				        <xsl:otherwise><xsl:value-of select="@value"/></xsl:otherwise>
				    </xsl:choose>
				</textarea>
				<xsl:if test="description">
					<span class="greytext"><xsl:value-of select="description" disable-output-escaping="yes"/></span>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>
