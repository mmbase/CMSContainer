<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="field[@guitype = 'data' or @guitype = 'bolddata']">
		<tr>
			<td>
				<label>
					<xsl:value-of select="@title" disable-output-escaping="yes"/>
				</label>
			</td>
			<td class="field">
				<xsl:choose>
					<xsl:when test="@guitype = 'bolddata'"><b><xsl:value-of select="@value" disable-output-escaping="yes"/></b></xsl:when>
					<xsl:otherwise><xsl:value-of select="@value" disable-output-escaping="yes"/></xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
		<tr>
			<td class="field" colspan="2">
				<xsl:value-of select="description" disable-output-escaping="yes"/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="field[@guitype = 'datatext' or @guitype = 'text' ]" mode="readonly">
		<tr>
			<td>
				<label>
					<xsl:value-of select="@title" disable-output-escaping="yes"/>
				</label>
			</td>
			<td class="field">
				<xsl:value-of select="@value" disable-output-escaping="yes"/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="field[@guitype = 'edit' or @guitype = 'data']"
					mode="readonly">
		<tr>
			<td>
				<label>
					<xsl:value-of select="@title" disable-output-escaping="yes"/>
				</label>
			</td>
			<td class="field">
				<xsl:value-of select="@value" disable-output-escaping="yes"/>
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template match="field[@guitype = 'checkbox' or @guitype = 'leftcheckbox' ]" mode="readonly">
		<tr>
			<td>
				<label>
					<xsl:value-of select="@title" disable-output-escaping="yes"/>
				</label>
			</td>
			<td class="field">
				<xsl:if test="@value = 'true'">
					<input type="checkbox" class="{@class} checkbox" value="true" checked="checked" disable="true" />
				</xsl:if>
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template match="field[@guitype = 'dataselect' ]" mode="readonly">
		<tr>
			<td>
				<label>
					<xsl:value-of select="@title" disable-output-escaping="yes"/>
				</label>
			</td>
			<td class="field">
				<xsl:variable name="fieldvalue" select="@value"/>
				<xsl:choose>
					<xsl:when test="@value = '0'"></xsl:when>
					<xsl:when test="@guitype = 'bolddata'">
						<b>
							<xsl:value-of select="optionlist/option[@value = $fieldvalue]/text()" disable-output-escaping="yes"/>
						</b>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="optionlist/option[@value = $fieldvalue]/text()" disable-output-escaping="yes"/>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="field[@guitype = 'horizontalradio' ]" mode="readonly">
		<tr>
			<td>
				<label>
					<xsl:value-of select="@title" disable-output-escaping="yes"/>
				</label>
			</td>
			<td class="field">
				<xsl:variable name="fieldvalue" select="@value"/>
				<xsl:choose>
					<xsl:when test="@value = '0'"></xsl:when>
					<xsl:when test="@guitype = 'bolddata'">
						<b>
							<xsl:value-of select="optionlist/option[@value = $fieldvalue]/text()" disable-output-escaping="yes"/>
						</b>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="optionlist/option[@value = $fieldvalue]/text()" disable-output-escaping="yes"/>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="field[@guitype = 'datagroupedcheckbox']" mode="readonly">
		<tr>
			<td>
				<label>
					<xsl:value-of select="@title" disable-output-escaping="yes"/>
				</label>
			</td>
			<td class="field">
				<xsl:for-each select="optionlist/option[contains(@selected,'true')]">
					<xsl:value-of select="text()"/><br />
				</xsl:for-each>
			</td>
		</tr>
	</xsl:template>

</xsl:stylesheet>
