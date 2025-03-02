<#function toDurationFormat duration>
    <#return "${duration.toHours()?string('0')}:${duration.toMinutesPart()?string('00')}:${duration.toSecondsPart()?string('00')}.${duration.toMillisPart()?string('000')}"/>
</#function>

<html lang="en">
  <head>
    <title>Testing issues report</title>
    <link href="style.css" rel="stylesheet" />
  </head>
  <body>
    <header>
      <h1>Testing Issues Report</h1>
    </header>
    <section id="page-content">
      <div class="item">
        <h2>Summary</h2>
        <div id="index-report-summary" class="item-content">
          <table>
            <tr>
              <th class="right">Total time</th>
              <td class="left">${toDurationFormat(duration)}</td>
            </tr>
            <tr>
              <th class="right">Issues</th>
              <td class="left">${totalNumberOfIssues}</td>
            </tr>
            <tr>
              <th class="right">Operators</th>
              <td class="left">
                <#list operators as operator>
                  <code>${operator}</code>
                <#else>
                  No mutation operator was found. This could be a configuration issue. Please check your PIT configuration.
                </#list>
              </td>
            </tr>
          </table>
        </div>
      </div>
      <div class="item">
        <h2>Classes with testing issues</h2>
        <div class="item-content">
          <#if findings?size == 0>
            No test issue was found.
          <#else>
          <table>
            <thead>
              <tr><th class="left">Class</th><th class="right">Issues</th></tr>
            </thead>
             <#list findings as finding>
               <tr><td class="left"><code><a href="${finding.className}.html">${finding.className}</a></code></td><td class="right">${finding.numberOfIssues}</td></tr>
             </#list>
          </table>
          </#if>
        </div>
      </div>
    </section>
  </body>
</html>
