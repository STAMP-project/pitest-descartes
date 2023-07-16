<#function toDurationFormat duration>
    <#return "${duration.toHours()?string('0')}:${duration.toMinutesPart()?string('00')}:${duration.toSecondsPart()?string('00')}.${duration.toMillisPart()?string('000')}"/>
</#function>

<html lang="en">
  <head>
    <title>Testing issues report</title>
    <style>
      body { padding: 0; margin: 0; font-family: Arial, Helvetica, sans-serif; }
      header
      {
        color: rgb(31, 35, 40);
        background-color: rgb(246, 248, 250);
        box-shadow: rgb(208, 215, 222) 0px -1px 0px 0px inset;
        box-sizing: border-box;
        background-origin: padding-box;
        font-size: 10px;
        line-height: 21px;
        padding: 16px;;
      }
      section#page-content { margin: 16px; }
      div.item .item-content { border: 0.8px solid rgb(208, 215, 222); border-radius: 6px; padding: 8px 8px; }
      table { font-size: 90%; width: 100%; border-collapse: collapse; }
      tr:last-child { border-bottom: none; }
      tr { border-bottom: solid .8px rgb(211, 214, 218); }
      thead { vertical-align: middle; }
      td, th { padding: 10px; margin: 0; }
      .left { text-align: left; }
      .right { text-align: right; }
    </style>
  </head>
  <body>
    <header>
      <h1>Testing Issues Report</h1>
    </header>
    <section id="page-content">
      <div class="item">
        <h2>Summary</h2>
        <div class="item-content">
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
               <tr><td class="left"><code><a href="${finding.className}.html">${finding.className}</a></code></td><td class="right">${finding.issues}</td></tr>
             </#list>
          </table>
          </#if>
        </div>
      </div>
    </section>
  </body>
</html>
