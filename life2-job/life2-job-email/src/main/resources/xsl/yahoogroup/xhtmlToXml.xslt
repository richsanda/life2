<xsl:transform
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"

        exclude-result-prefixes="#all"

        version="2.0">

    <xsl:output method="xml"
                omit-xml-declaration="yes"
                indent="yes"/>

    <xsl:strip-space elements="*"/>

    <xsl:param name="yahoogroup"/>
    <xsl:param name="filename"/>

    <xsl:template match="/">

        <xsl:variable name="messageNum" select="html/body//form/input[@name = 'messageNum']/@value"/>

        <xsl:if test="count($messageNum) != 1">
            <xsl:message terminate="yes">
                <xsl:text>found </xsl:text>
                <xsl:value-of select="count($messageNum)"/>
                <xsl:text> message numbers: </xsl:text>
                <xsl:value-of select="$messageNum" separator=", "/>
                <xsl:text> filename: </xsl:text>
                <xsl:value-of select="$filename"/>
            </xsl:message>
        </xsl:if>

        <xsl:variable name="contentDiv" select="(//div[@class = 'msgarea entry-content'])"/>
        <xsl:variable name="contentTable" select="$contentDiv/ancestor::table[1]"/>

        <!--xsl:if test="count($contentDiv) + count($contentTable) != 2">
            <xsl:message>
                <xsl:text>found </xsl:text>
                <xsl:value-of select="count($contentDiv) + count($contentTable)"/>
                <xsl:text> content div and table: </xsl:text>
                <xsl:value-of select="$messageNum" separator=", "/>
            </xsl:message>
        </xsl:if-->

        <xsl:variable name="yahoogroup" select="//span[@class = 'ygrp-pname']"/>

        <xsl:if test="count($yahoogroup) != 1">
            <xsl:message>
                <xsl:text>found </xsl:text>
                <xsl:value-of select="count($yahoogroup)"/>
                <xsl:text> yahoogroup: </xsl:text>
                <xsl:value-of select="$yahoogroup" separator=", "/>
                <xsl:text> (</xsl:text>
                <xsl:value-of select="$messageNum" separator=", "/>
                <xsl:text>)</xsl:text>
            </xsl:message>
        </xsl:if>

        <xsl:variable name="sent" select="$contentTable//abbr[@class = 'updated'][not(ancestor::div[@class = 'msgarea entry-content'])]/@title"/>

        <xsl:if test="count($sent) != 1">
            <xsl:message>
                <xsl:text>found </xsl:text>
                <xsl:value-of select="count($sent)"/>
                <xsl:text> sent: </xsl:text>
                <xsl:value-of select="$sent" separator=", "/>
                <xsl:text> (</xsl:text>
                <xsl:value-of select="$messageNum" separator=", "/>
                <xsl:text>)</xsl:text>
            </xsl:message>
        </xsl:if>

        <xsl:variable name="from" select="//td[@class = 'info env first']//span[@class = 'name']"/>

        <xsl:if test="count($from) != 1">
            <xsl:message>
                <xsl:text>found </xsl:text>
                <xsl:value-of select="count($from)"/>
                <xsl:text> from: </xsl:text>
                <xsl:value-of select="$from" separator=", "/>
                <xsl:text> (</xsl:text>
                <xsl:value-of select="$messageNum" separator=", "/>
                <xsl:text>)</xsl:text>
            </xsl:message>
        </xsl:if>

        <!--xsl:if test="contains($from, '@')">
            <xsl:message>
                <xsl:text>from contains '@': </xsl:text>
                <xsl:value-of select="$from"/>
                <xsl:text> (</xsl:text>
                <xsl:value-of select="$messageNum" separator=", "/>
                <xsl:text>)</xsl:text>
            </xsl:message>
        </xsl:if-->

        <xsl:variable name="topic" select="//td[contains(@class, 'ygrp-topic-title')]"/>

        <xsl:if test="count($topic) != 1">
            <xsl:message>
                <xsl:text>found </xsl:text>
                <xsl:value-of select="count($topic)"/>
                <xsl:text> topic: </xsl:text>
                <xsl:value-of select="$topic" separator=", "/>
                <xsl:text> (</xsl:text>
                <xsl:value-of select="$messageNum" separator=", "/>
                <xsl:text>)</xsl:text>
            </xsl:message>
        </xsl:if>

        <xsl:variable name="subject" select="//div[contains(@class, 'subject')]"/>

        <xsl:if test="count($subject) > 1">
            <xsl:message>
                <xsl:text>found </xsl:text>
                <xsl:value-of select="count($subject)"/>
                <xsl:text> subject: </xsl:text>
                <xsl:value-of select="$subject" separator=", "/>
                <xsl:text> (</xsl:text>
                <xsl:value-of select="$messageNum" separator=", "/>
                <xsl:text>)</xsl:text>
            </xsl:message>
        </xsl:if>

        <GroupMessage id="1">
            <SourceKey type="YAHOOGROUP">
                <Group>
                    <xsl:value-of select="$yahoogroup[1]"/>
                </Group>
                <Number>
                    <xsl:value-of select="$messageNum[1]"/>
                </Number>
            </SourceKey>
            <Sent>
                <xsl:value-of select="normalize-space($sent[1])"/>
            </Sent>
            <From>
                <xsl:variable name="firstFrom" select="$from[1]"/>
                <xsl:value-of select="normalize-space(
                                      if (contains($firstFrom, '@'))
                                      then substring-before($firstFrom, '@')
                                      else $firstFrom)"/>
            </From>
            <Topic>
                <xsl:value-of select="normalize-space($topic[1])"/>
            </Topic>
            <Subject>
                <xsl:value-of select="normalize-space($subject[1])"/>
            </Subject>
            <Body html="true">
                <xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>
                <xsl:apply-templates select="$contentDiv"/>
                <xsl:text disable-output-escaping="yes">]]</xsl:text>
                <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
            </Body>
        </GroupMessage>
    </xsl:template>

    <xsl:template match="*">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="@* | text()">
        <xsl:copy-of select="."/>
    </xsl:template>

</xsl:transform>
