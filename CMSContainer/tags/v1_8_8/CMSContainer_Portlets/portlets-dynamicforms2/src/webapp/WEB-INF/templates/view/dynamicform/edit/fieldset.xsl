<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="fieldset[@guitype = 'extrainfo']">
		<xsl:apply-templates select="field[@guitype = 'horizontalradio' ]"/>
		<xsl:apply-templates select="field[@guitype = 'text' ]"/>
	</xsl:template>
	
	<xsl:template match="fieldset[@guitype = 'horizontaldata']" mode="readonly">
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
	
	<xsl:template match="fieldset[@guitype = 'horizontaledit']">
		<tr>
			<td colspan="3">
				<table cellpadding="0" cellspacing="0" border="0">
					<tr>
						<td>
							<label>
								<xsl:value-of select="@title" disable-output-escaping="yes"/>
							</label>
						</td>
					</tr>
					<tr>
						<xsl:for-each select="field">
							<td>
								<xsl:if test="@title">
									<span>
										<xsl:value-of select="@title" disable-output-escaping="yes"/>
									</span>
								</xsl:if>
								<input type="text" maxlength="{@maxlength}" id="{@name}" name="{@name}" value="{@value}" class="{@class} horizontaledit"/>
								<xsl:if test="description">
									<span class="greytext"><xsl:value-of select="description" disable-output-escaping="yes"/></span>
								</xsl:if>
							</td>
						</xsl:for-each>
					</tr>
				</table>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>
