<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:include href="dynamicform/edit/section.xsl"/>
	<xsl:include href="dynamicform/edit/list.xsl"/>
	<xsl:include href="dynamicform/navigation.xsl"/>

	<xsl:template match="formstep[not(@guitype) or @guitype='' ]">
		<xsl:if test="stepinfo/images">
			<ul class="stepinfo-images">
				<xsl:for-each select="stepinfo/images/image">
					<li class="stepimage-{@status}">
						<img alt="{@title}" title="{@title}" src="{@url}"/>
					</li>
				</xsl:for-each>
			</ul>
		</xsl:if>
		<xsl:for-each select="stepinfo/description">
			<p class="stepinfo-description">
				<xsl:if test="@title != ''">
					<span class="stepinfo-description-title">
						<xsl:value-of disable-output-escaping="yes" select="@title"/>
					</span>
				</xsl:if>
				<xsl:value-of disable-output-escaping="yes" select="."/>
			</p>
		</xsl:for-each>
		<xsl:choose>
			<xsl:when test="list and not(section)">
				<form action="{$ACTIONURL}" method="post" id="{$NAMESPACE}form" enctype="multipart/form-data">
					<div class="form-hidden-params">
						<xsl:value-of select="//default-form-params" disable-output-escaping="yes"/>
						<input type="hidden" name="currentStep" id="currentStep" value="{@name}"/>
						<input type="hidden" name="sequence" id="sequence" value="{@sequence}"/>
					</div>
					<xsl:apply-templates select="list"/>
					<div class="cform">
						<xsl:apply-templates select="navigation"/>
					</div>
				</form>
			</xsl:when>
			<xsl:otherwise>
				<xsl:if test="stepinfo/@title and stepinfo/@title !='' ">
					<div class="ctitle">
						<xsl:value-of disable-output-escaping="yes" select="stepinfo/@title"/>
					</div>
				</xsl:if>
				<div class="cform">
					<form action="{$ACTIONURL}" method="post" id="{$NAMESPACE}form" enctype="multipart/form-data">
						<div class="form-hidden-params">
							<xsl:value-of select="//default-form-params" disable-output-escaping="yes"/>
							<input type="hidden" name="currentStep" id="currentStep" value="{@name}"/>
							<input type="hidden" name="sequence" id="sequence" value="{@sequence}"/>
						</div>
						<xsl:apply-templates select="section | list"/>
						<xsl:apply-templates select="navigation"/>
					</form>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

   <xsl:template match="formstep[@guitype='success' and @processor-errors = 'false']">
      <h3 class="stepinfo-title"><xsl:value-of select="stepinfo/@title" /></h3>
      	<xsl:for-each select="stepinfo/description">
			<p class="stepinfo-description">
				<xsl:if test="@title != ''">
					<span class="stepinfo-description-title">
						<xsl:value-of disable-output-escaping="yes" select="@title"/>
					</span>
				</xsl:if>
				<xsl:value-of disable-output-escaping="yes" select="."/>
			</p>
		</xsl:for-each>
   </xsl:template>
	
   <xsl:template match="formstep[@guitype='success' and @processor-errors = 'true']">
      <h3>Er is een fout opgetreden tijdens het verwerken van uw aanvraag. Probeer het
      later nog eens.</h3>
   </xsl:template>
</xsl:stylesheet>
