<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="fieldset[@guitype = 'horizontaledit']" mode="readonly">
		<tr>
			<td>
				<xsl:value-of select="@title" disable-output-escaping="yes"/>
			</td>
			<td class="field">
				<xsl:for-each select="field">
					<xsl:value-of select="@value" disable-output-escaping="yes"/>
					<xsl:text> </xsl:text>
				</xsl:for-each>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="fieldset[@guitype = 'verticalradioanddate']"
		mode="readonly">
		<tr>
			<td>
				<label>
					<xsl:value-of select="@title" disable-output-escaping="yes" />
				</label>
			</td>
			<td class="field">
				<xsl:variable name="fieldvalue" select="field[1]/@value" />
				<xsl:choose>
					<xsl:when test="field[1]/optionlist/option[1]/@value = $fieldvalue">
						<xsl:value-of select="field[1]/optionlist/option[1]//text()"
							disable-output-escaping="yes" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="field[contains(@name,'date')]/@value"
							disable-output-escaping="yes" />
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>
  
</xsl:stylesheet>