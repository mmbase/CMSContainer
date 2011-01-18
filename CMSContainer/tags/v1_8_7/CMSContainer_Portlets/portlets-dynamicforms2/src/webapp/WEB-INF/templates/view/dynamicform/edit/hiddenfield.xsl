<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="field[@guitype = 'hidden' ]">
		<tr>
			<td class="form-hidden-params">
				<input type="hidden" id="{@name}" name="{@name}" value="{@value}" class="{@class}" />
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>
