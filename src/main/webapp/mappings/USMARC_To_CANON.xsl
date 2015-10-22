<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:marc="http://www.loc.gov/MARC21/slim" 
                              xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
                              xmlns:dc="http://purl.org/dc/elements/1.1/" 
                              xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                              exclude-result-prefixes="marc">

<xsl:output method="html"/>

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="marc:record">
  <xsl:variable name="leader" select="marc:leader"/>
  <xsl:variable name="leader6" select="substring($leader,7,1)"/>
  <xsl:variable name="leader7" select="substring($leader,8,1)"/>
  <xsl:variable name="controlField008" select="marc:controlfield[@tag=008]"/>

  <xsl:apply-templates select="marc:datafield[@tag=35]"/>
  <xsl:apply-templates select="marc:datafield[@tag=37]"/>
  <xsl:apply-templates select="marc:datafield[@tag=40]"/>
  <xsl:apply-templates select="marc:datafield[@tag=245]"/>
  <xsl:apply-templates select="marc:datafield[@tag=246]"/>
  <xsl:apply-templates select="marc:datafield[@tag=300]"/>
  <xsl:apply-templates select="marc:datafield[@tag=506]"/>
  <xsl:apply-templates select="marc:datafield[@tag=513]"/>
  <xsl:apply-templates select="marc:datafield[@tag=520]"/>
  <xsl:apply-templates select="marc:datafield[@tag=555]"/>
  <xsl:apply-templates select="marc:datafield[@tag=650]"/>
  <xsl:apply-templates select="marc:datafield[@tag=785]"/>
</xsl:template>

<xsl:template match="marc:datafield[@tag=35]">
System control number : <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="marc:datafield[@tag=37]">
Source of Acquisition : <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="marc:datafield[@tag=40]">
Cataloging Source : <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="marc:datafield[@tag=245]">
Title : <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="marc:datafield[@tag=246]">
Subtitle : <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="marc:datafield[@tag=300]">
Pub info : <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="marc:datafield[@tag=506]">
	Restrictions on Access : <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="marc:datafield[@tag=513]">
	Type of report and Period Covered : <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="marc:datafield[@tag=520]">
	Summary : <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="marc:datafield[@tag=555]">
	Summary : <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="marc:datafield[@tag=650]">
	Subject : <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="marc:datafield[@tag=785]">
	Lookup 785 : <xsl:value-of select="."/>
</xsl:template>

</xsl:stylesheet>
