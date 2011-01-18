<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:variable name="REASON-RIGHTS">Vous n'avez pas les droits nécessaires sur cet objet dans le contenu du canal</xsl:variable>

  <xsl:variable name="prompt_search_link">rechercher</xsl:variable>
  <xsl:variable name="prompt_new_link">nouveau</xsl:variable>
  <xsl:variable name="prompt_edit_link">changer</xsl:variable>

  <xsl:template name="prompt_invalid_list">
    <xsl:param name="minoccurs">0</xsl:param>
    <xsl:param name="maxoccurs">*</xsl:param>
    <xsl:choose>
	    <xsl:when test="$minoccurs = '1' and $minoccurs = $maxoccurs">Sélectionner <xsl:value-of select="$maxoccurs"/> élement</xsl:when>
	    <xsl:when test="$minoccurs = $maxoccurs">Sélectionner <xsl:value-of select="$maxoccurs"/> élements</xsl:when>
	    <xsl:when test="not($minoccurs = '0') and not($maxoccurs = '*')">Au moins <xsl:value-of select="minoccurs"/> et au plus <xsl:value-of select="maxoccurs"/> Les éléments doivent être sélectionnés</xsl:when>
	    <xsl:when test="not($minoccurs = '0')">Au moins  <xsl:value-of select="minoccurs"/> élements doivent être sélectionnés</xsl:when>
	    <xsl:when test="not($maxoccurs = '*')">Au plus  <xsl:value-of select="maxoccurs"/> élements doivent être sélectionnés</xsl:when>
    </xsl:choose>
  </xsl:template>
	
  <xsl:template name="prompt_new">nouveau</xsl:template>

</xsl:stylesheet>