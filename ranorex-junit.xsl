<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes"/>
  <xsl:template match="/">
    <xsl:element name="testsuite">
      <xsl:attribute name="errors"><xsl:value-of select="count(//activity[(@testcasename and @result='Error')])"/></xsl:attribute>
      <xsl:attribute name="failures"><xsl:value-of select="count(//activity[(@testcasename and @result='Failed')])"/></xsl:attribute>
      <xsl:attribute name="hostname"><xsl:value-of select="//activity/@host"/></xsl:attribute>
      <xsl:attribute name="name"><xsl:value-of select="//activity/@testsuitename"/></xsl:attribute>
      <!-- <xsl:attribute name="tests"><xsl:value-of select="sum(//activity/@maxchildren)"/></xsl:attribute> -->
      <xsl:attribute name="tests"><xsl:value-of select="count(//activity[(@testcasename and @result!='Ignored')])"/></xsl:attribute>
      <xsl:attribute name="time">3.215</xsl:attribute>
      <xsl:for-each select="//activity[@testcasename]">
        <xsl:element name="testcase">
          <xsl:attribute name="time">0</xsl:attribute>
          <xsl:attribute name="classname"><xsl:value-of select="@testcasename"/></xsl:attribute>
          <xsl:attribute name="name"><xsl:value-of select="@testcasename"/></xsl:attribute>
          <xsl:choose>
            <xsl:when test="@result='Failed'">
              <xsl:element name="failure">
                <xsl:attribute name="message">
                  <xsl:for-each select=".//activity[@result='Failed']">
                    <xsl:value-of select="errmsg"></xsl:value-of>
                  </xsl:for-each>
                </xsl:attribute>
                <!--<xsl:attribute name="type">someFailureType</xsl:attribute>-->
              </xsl:element>
            </xsl:when>
            <xsl:when test="@result='Error' or @result='Ignored'">
              <xsl:element name="error">
                  <xsl:attribute name="message"><xsl:value-of select=".//activity[@result='Error' or @result='Ignored']/errmsg"></xsl:value-of></xsl:attribute>
                  <!--<xsl:attribute name="type">someErrorType</xsl:attribute>-->
              </xsl:element>
            </xsl:when>
            <xsl:otherwise>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:element>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>
</xsl:stylesheet>