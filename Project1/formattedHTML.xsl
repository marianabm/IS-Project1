<?xml version="1.0" encoding="UTF-8"?>
<html xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsl:version="1.0">
	<head>
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous"/>
		<title> Researcher Profile </title>
	</head>
	<body>
		<div class="container">
			<div>
				<h1 class="bg-info"><b> <xsl:value-of select="//name"/> </b></h1>
			</div>
			<div>
				<h2><u> Citations </u></h2>
				<table class="table table-hover table-bordered" border="1" cellspacing="0" cellpadding="2" bordercolor="666633">
					<tr>
						<th><b> Year </b></th>
						<th><b> Number of Citations </b></th>
					</tr>
					<xsl:for-each select="//citations/numberOfCitations">
						<tr>
							<td> <xsl:value-of select="@year"/> </td>
							<td> <xsl:value-of select="."/> </td>
						</tr>
					</xsl:for-each>
					<tr class="active">
						<td> <b> Total of citations: </b> </td>
						<td> <xsl:value-of select="//citations/total"/> </td>
					</tr>
				</table>
			</div>
			<div>
				<h2><u> Publications </u></h2>
				<ol style="size:20px">
				<xsl:for-each select="//publications/publication">
					<!-- year part -->
					<xsl:sort select="substring-before(date,'/')" data-type="number" order="descending" />
					<!-- month part -->
         			<xsl:sort select="substring-before(substring-after(date,'/'),'/')" data-type="number" order="descending"/>	
         			<!-- day part -->
         			<xsl:sort select="substring-after(date,'/')" data-type="number" order="descending"/>									
						<li>
							<div>
								<h3><xsl:value-of select="title"/></h3>
							
								<p> Authors: 
									<xsl:for-each select="./author">
										<xsl:value-of select="."/>, 
									</xsl:for-each>
								</p>
								<p>Publication Date: <xsl:value-of select="date"/></p>
								<p><xsl:value-of select="additionalInformation"/></p>
								<p>Publisher: <xsl:value-of select="publisher"/></p>
								<p>Description: <xsl:value-of select="description"/></p>
								<p><b>Total of citations: </b> <xsl:value-of select="citationInformation/totalCitations"/></p>
								<ul>
									<xsl:for-each select="citationInformation/citation">
										<xsl:sort select="@year" data-type="number" order="descending" />
										<li><xsl:value-of select="."/></li>
									</xsl:for-each>
								</ul>
							</div>
						</li>					
				</xsl:for-each>
				</ol>
			</div>
		</div>
	</body>
</html>