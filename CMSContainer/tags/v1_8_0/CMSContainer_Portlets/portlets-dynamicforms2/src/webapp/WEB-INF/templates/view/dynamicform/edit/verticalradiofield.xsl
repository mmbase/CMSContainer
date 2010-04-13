<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="field[@guitype = 'verticalradio' ]">
		<xsl:variable name="fieldvalue" select="@value"/>
		<xsl:variable name="fieldname" select="@name"/>
		<xsl:variable name="fieldtitle" select="@title"/>
		<xsl:variable name="fieldrequired" select="@required"/>
		<xsl:variable name="errorMsg" select="@error"/>
		<xsl:for-each select="optionlist/option">
		<tr>
			<xsl:choose>
				<xsl:when test="position() = 1">
					<xsl:call-template name="field_label">
						<xsl:with-param name="fieldtitle" select="$fieldtitle"/>
						<xsl:with-param name="fieldname" select="$fieldname"/>
						<xsl:with-param name="fieldrequired" select="$fieldrequired"/>
						<xsl:with-param name="errorMsg" select="$errorMsg"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<td class="label"></td>
				</xsl:otherwise>
			</xsl:choose>
			<td class="field">
				<input name="{$fieldname}" id="{$fieldname}" type="radio" value="{@value}" >
					<xsl:if test="@value=$fieldvalue">
						<xsl:attribute name="checked">checked</xsl:attribute>
					</xsl:if>
					<xsl:value-of select="."/>
				</input><xsl:text disable-output-escaping="yes"></xsl:text>
			</td>
		</tr>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="field[@guitype = 'foldverticalradio' ]">
		<xsl:variable name="fieldvalue" select="@value"/>
		<xsl:variable name="fieldname" select="@name"/>
		<xsl:variable name="fieldtitle" select="@title"/>
		<xsl:variable name="fieldrequired" select="@required"/>
		<xsl:variable name="errorMsg" select="@error"/>
		<script type="text/javascript">
		function change(state) {
			if(state) {
				document.getElementById('<xsl:value-of select="../field[contains(@name,'date')]/@name"/>').value='';
				document.getElementById('<xsl:value-of select="../field[contains(@name,'date')]/@name"/>').readOnly = true;			
				} else {
				document.getElementById('<xsl:value-of select="../field[contains(@name,'date')]/@name"/>').readOnly = false;			
				document.getElementById('<xsl:value-of select="../field[contains(@name,'date')]/@name"/>').focus();
			}
		}
		</script>
		<xsl:for-each select="optionlist/option">
		<tr>
			<xsl:choose>
				<xsl:when test="position() = 1">
					<xsl:call-template name="field_label">
						<xsl:with-param name="fieldtitle" select="$fieldtitle"/>
						<xsl:with-param name="fieldname" select="$fieldname"/>
						<xsl:with-param name="fieldrequired" select="$fieldrequired"/>
						<xsl:with-param name="errorMsg" select="$errorMsg"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<td class="label"></td>
				</xsl:otherwise>
			</xsl:choose>
			<td class="field">
				<input name="{$fieldname}" id="{$fieldname}" type="radio" value="{@value}" >
						<xsl:choose>
							<xsl:when test="position() = 1"><xsl:attribute name="onclick">change(true)</xsl:attribute></xsl:when>
							<xsl:when test="position() = 2"><xsl:attribute name="onclick">change(false)</xsl:attribute></xsl:when>
						</xsl:choose>
					<xsl:if test="@value=$fieldvalue">
						<xsl:attribute name="checked">checked</xsl:attribute>
					</xsl:if>
					<xsl:value-of select="."/>
				</input><xsl:text disable-output-escaping="yes"></xsl:text>
			</td>
		</tr>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
