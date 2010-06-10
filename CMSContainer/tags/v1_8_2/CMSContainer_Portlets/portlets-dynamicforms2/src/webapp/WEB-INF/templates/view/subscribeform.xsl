<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output
    method="html"
    encoding="utf-8"
    omit-xml-declaration="yes"
    standalone="yes"
    indent="yes" />

  <xsl:include href="dynamicform/form.xsl"/>

	<xsl:template match="/">
		<style type="text/css">
		  @import url('<xsl:value-of select="$URLCONTEXT"/>css/dynamicforms.css');
		</style>
		<xsl:apply-templates select="form"/>
	</xsl:template>

</xsl:stylesheet>