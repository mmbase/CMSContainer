<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="navigation">
		<xsl:for-each select="description">
			<p class="navigation-description">
				<span class="navigation-description-title">
					<xsl:value-of disable-output-escaping="yes" select="@title"/>
				</span>
				<xsl:value-of disable-output-escaping="yes" select="."/>
			</p>
		</xsl:for-each>
		<fieldset class="lastfieldset">
			<div class="spacer">
				<script type="text/javascript">
				function readonlysubmit(){
					document.document.getElementById('<xsl:value-of disable-output-escaping="yes" select="$NAMESPACE"/>forms').submit();
					return false;
				}
				  function setstepvalue(stepvalue) {
					 document.getElementById('<xsl:value-of disable-output-escaping="yes" select="$NAMESPACE"/>activeStep').value=stepvalue;
					 return true;
				  }
				  function backStepping() {
					 document.getElementById('<xsl:value-of disable-output-escaping="yes" select="$NAMESPACE"/>backStep').value='true';
					 return false;
				  }
				  </script>
				<input type="hidden" name="activeStep" id="{$NAMESPACE}activeStep"/>
				<input type="hidden" name="editpath" id="editpath" value="{//formstep/@editpath}"/>
				<input type="hidden" name="backStep" id="{$NAMESPACE}backStep" value="false"/>
				<xsl:apply-templates select="navitem"/>
			</div>
		</fieldset>
	</xsl:template>
  
	<xsl:template match="navitem[@guitype='backbutton']">
		<div class="navitem-backbutton {@class}">
		  <xsl:choose>
		    <xsl:when test="@imageurl">
			<input name="{@title}" type="image" src="{@imageurl}" alt="{@title}" title="{@title}">
				<xsl:attribute name="onclick">backStepping();setstepvalue('<xsl:value-of select="@step"/>');</xsl:attribute>
			</input>
			</xsl:when>
			<xsl:otherwise>
			<button title="{@title}" name="{@title}" type="submit" value="{@title}" class="submit">
				<xsl:attribute name="onclick">backStepping();setstepvalue('<xsl:value-of select="@step"/>');</xsl:attribute>
				<xsl:value-of select="@title"/>
			</button>
			</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:template match="navitem[not(@guitype) or @guitype='']">
		<div class="navitem-{@step} {@class}">
		  <xsl:choose>
		    <xsl:when test="@imageurl">
			<input name="{@title}" type="image" src="{@imageurl}" alt="{@title}" title="{@title}">
				<xsl:attribute name="onclick">setstepvalue('<xsl:value-of select="@step"/>');</xsl:attribute>
			</input>
			</xsl:when>
			<xsl:otherwise>
			<button title="{@title}" name="{@title}" type="submit" value="{@title}" class="submit">
				<xsl:attribute name="onclick">setstepvalue('<xsl:value-of select="@step"/>');</xsl:attribute>
				<xsl:value-of select="@title"/>
			</button>
			</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>
	
</xsl:stylesheet>
