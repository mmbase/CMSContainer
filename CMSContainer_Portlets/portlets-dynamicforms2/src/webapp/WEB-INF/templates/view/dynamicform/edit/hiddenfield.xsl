<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="field[@guitype = 'hidden' ]">
				<input type="hidden" id="{@name}" name="{@name}" value="{@value}"/>
	</xsl:template>
</xsl:stylesheet>
