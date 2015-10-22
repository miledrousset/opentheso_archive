<?xml version="1.0"?>
 
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>

<xsl:template match="/">
<xsl:apply-templates select="grs" />
</xsl:template>

<xsl:template match="grs">
Title : <xsl:apply-templates select="GRSTag[@type=2 and @value=1]"/>
Date of last Modification : <xsl:apply-templates select="GRSTag[@type=1 and @value=16]"/>
</xsl:template>


</xsl:stylesheet>
