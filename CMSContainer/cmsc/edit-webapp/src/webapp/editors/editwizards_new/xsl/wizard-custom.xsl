<?xml version="1.0" encoding="utf-8"?>
<!--
   Stylesheet for customizing the edit wizards.
-->
<xsl:stylesheet 
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:node="org.mmbase.bridge.util.xml.NodeFunction"
  xmlns:date="org.mmbase.bridge.util.xml.DateFormat"
  extension-element-prefixes="node date">

  <xsl:template name="extrajavascript-custom">
  </xsl:template>

  <xsl:template match="command[@name=&apos;pagegroupselector&apos;]" mode="listnewbuttons">
    <td class="listnew">
      <a href="#" onclick="select_fid='{../@fid}';select_did='{../command[@name=&apos;add-item&apos;]/@value}';window.open('../../../../editors/secure/selectGroup.jsp', 'pagegroupselector', 'width=350,height=500,status=yes,toolbar=no,titlebar=no,scrollbars=yes,resizable=yes,menubar=no');" class="button">
        <xsl:call-template name="prompt_search"/>
      </a>
    </td>
  </xsl:template>
</xsl:stylesheet>