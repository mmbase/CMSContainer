<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="field[@guitype = 'select' ]">
		<tr>
			<xsl:call-template name="field_label" />
			<td class="field">
				<xsl:variable name="fieldvalue" select="@value" />
				<select name="{@name}" id="{@name}" class="{@class} select">
					<xsl:for-each select="optionlist/option">
						<option value="{@value}">
							<xsl:if test="@value = $fieldvalue">
								<xsl:attribute name="selected">selected</xsl:attribute>
							</xsl:if>
							<xsl:value-of select="." />
						</option>
					</xsl:for-each>
				</select>
				<xsl:if test="description">
					<span class="greytext">
						<xsl:value-of select="description" disable-output-escaping="yes" />
					</span>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template match="field[@guitype = 'clientnumberselect' ]">
		<tr>
			<xsl:call-template name="field_label" />
			<td class="field">
				<script type="text/javascript">
				  function setcurrentstep(stepvalue) {
					 document.getElementById('<xsl:value-of disable-output-escaping="yes" select="$NAMESPACE"/>activeStep').value=stepvalue;
					 document.esform.submit();
				  }
				</script>
				<xsl:variable name="fieldvalue" select="@value"/>
				<select name="{@name}" id="{@name}" class="{@class} clientnumberselect">
					<xsl:attribute name="onchange">setcurrentstep('<xsl:value-of select="//formstep/@name"/>')</xsl:attribute>						
					<xsl:for-each select="optionlist/option">
					<option value="{@value}">
						<xsl:if test="@value = $fieldvalue">
							<xsl:attribute name="selected">selected</xsl:attribute>
						</xsl:if><xsl:value-of select="."/></option>
					</xsl:for-each>
				</select>
				<xsl:if test="description">
					<span class="greytext"><xsl:value-of select="description" disable-output-escaping="yes"/></span>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>
