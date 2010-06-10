<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="field[@guitype = 'horizontalradio' ]">
		<tr>
			<xsl:call-template name="field_label" />

			<xsl:variable name="fieldvalue" select="@value"/>
			<xsl:variable name="fielddefaultvalue" select="value"/>
			<xsl:variable name="fieldname" select="@name"/>
			<xsl:variable name="fieldtitle" select="@title"/>
			<td class="field" align="center">
			<xsl:for-each select="optionlist/option">
				<input name="{$fieldname}" id="{$fieldname}" type="radio" value="{@value}"  class="{@class} radio">
					<xsl:if test="position() = last()">
						<xsl:attribute name="align">right</xsl:attribute>
					</xsl:if>
          <xsl:choose>
            <xsl:when test="@value=$fieldvalue">
              <xsl:attribute name="checked">checked</xsl:attribute>
            </xsl:when>
            <xsl:when test="$fieldvalue='' and @value=$fielddefaultvalue">
              <xsl:attribute name="checked">checked</xsl:attribute>              
            </xsl:when>
          </xsl:choose>
					<xsl:if test="@value=$fieldvalue or @value=$fielddefaultvalue">
						<xsl:attribute name="checked">checked</xsl:attribute>
					</xsl:if>
				</input>
				<xsl:value-of select="."/>
				<xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
			</xsl:for-each>
			<xsl:if test="@required = 'true' and @title = '' ">
				<xsl:text> </xsl:text>*
			</xsl:if>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>
