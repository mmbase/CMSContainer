<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:variable name="REASON-WORKFLOW">Cet objet est dans le workflow et est accepté. Vous n'avez pas la permission à la publication.</xsl:variable>
  <xsl:variable name="REASON-PUBLISH">Cet objet est dans la publication. Cet objet sera inaccessible jusqu'à ce qu'il soit publié.</xsl:variable>

  <xsl:variable name="tooltip_finish">Stocker toutes les modifications pour l'acceptation.</xsl:variable>
  <xsl:variable name="tooltip_no_finish">
    Les modifications ne peuvent être validées tant que certaines données ne sont pas renseignées correctement.
  </xsl:variable>

  <xsl:variable name="tooltip_accept">Accepter toutes les modifications.</xsl:variable>
  <xsl:variable name="tooltip_no_accept">
     Les modifications ne peuvent être validées tant que certaines données ne sont pas renseignées correctement.
  </xsl:variable>

  <xsl:variable name="tooltip_reject">Rejeter.</xsl:variable>
  <xsl:variable name="tooltip_no_reject">
     Les modifications ne peuvent être rejetées tant que certaines données ne sont pas renseignées correctement.
  </xsl:variable>

  <xsl:variable name="tooltip_publish">Publier toutes les modifications.</xsl:variable>
  <xsl:variable name="tooltip_no_publish">
    Les modifications ne peuvent être publiées tant que certaines données ne sont pas renseignées correctement.
  </xsl:variable>

  <xsl:template name="prompt_finish">
    finir
  </xsl:template>

  <xsl:template name="prompt_accept">
    accepter
  </xsl:template>

  <xsl:template name="prompt_reject">
    rejeter
  </xsl:template>
  
  <xsl:template name="prompt_publish">
    publier
  </xsl:template>
</xsl:stylesheet>