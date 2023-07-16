
<html lang="en">

<head>
  <title>${className}</title>
  <style>
    body {
      padding: 0;
      margin: 0;
      font-family: Arial, Helvetica, sans-serif;
      background-color: whitesmoke;
    }

    header {
      color: rgb(31, 35, 40);
      background-color: rgb(246, 248, 250);
      box-shadow: rgb(208, 215, 222) 0px -1px 0px 0px inset;
      box-sizing: border-box;
      background-origin: padding-box;
      font-size: 10px;
      line-height: 21px;
      padding: 16px;
      ;
    }

    section#page-content {
      margin: 20px;
    }

    ul#method-list {
      padding: 0;
      margin: 0;
      list-style: none;
    }

    div.method {
      background-color: white;
      margin: 16px 16px 16px 32px;
      padding: 8px 16px;
      box-shadow: inset;
      box-shadow: rgba(0, 0, 0, 0.16) 0px 1px 4px;
    }

    div.item {
      margin: 5px;
      font-size: 90%;
    }

    div.item .item-content {
      padding: 8px 8px;
    }

    div.item .summary {
      border-radius: 6px;
      box-shadow: rgba(0, 0, 0, 0.02) 0px 1px 3px 0px, rgba(27, 31, 35, 0.15) 0px 0px 0px 1px;
      padding: 3px 6px;
    }

    div.item h3 {
      padding: 5px 0;
      border-bottom: 0.9px solid lightgray;
    }

    code {
      word-wrap: break-word;
    }
  </style>
</head>

<body>
  <header>
    <h1>${className}</h1>
  </header>
  <section id="page-content">
    <ul id="method-list">
      <li>
        <div class="method">
          <#list methods as method>
            <h2><code>${method.method.declaration}</code></h2>
            <div class="item">
              <div class="summary">
                <p>
                  This method is <strong>${method.classification}</strong>.
                  <#if method.classification=="error">
                    An internal error prevented this method from being analysed.
                  <#elseif method.classification=="partially-tested">
                    It is exercised by the test suite, some body transformations are not
                    detected by the test suite while others are detected.
                  <#elseif method.classification=="pseudo-tested">
                    It is exercised by the test suite, but when the code is modified no test case fails.
                    <#elseif method.classification=="pseudo-tested">
                  </#if>
                </p>
              </div>
            </div>
            <div class="item">
              <h3>Details</h3>
                <#assign non_detected_mutations = method.nonDetectedMutations>
                <#if non_detected_mutations??>
                  <p>No test case failed when the entire body of the method was replaced by:</p>
                  <ul>
                  <#list non_detected_mutations as mutation>
                    <li>${mutation.description}</li>
                  </#list>
                  </ul>
                </#if>
                <#assign detected_mutations = method.detectedMutations>
                <#if detected_mutations??>
                  <p>At least one test case failed when the entire body of the method was replaced by:</p>
                  <ul>
                  <#list detected_mutations as mutation>
                    <li>
                      <p>${mutation.description}</p>
                      <p>This transformation was detected by the following test case(s):</p>
                      <ul>
                      <#list mutation.detectingTests as test>
                        <li><code>${test}</code></li>
                      </#list>
                    </li>
                  </#list>
                  </ul>
                </#if>
                <#assign error_mutations = method.mutationsWithError>
                <#if error_mutations??>
                <div>
                  There was an error in the analysis of this method.
                  This signals a internal problem in the mutation process.
                  Please report the issue in Github if you think it is a bug.
                  The following mutation operators resulted in error:
                  <ul>
                    <#list error_mutations as mutation>
                      <li><code>${mutation.operator}</code></li>
                    </#list>
                  </ul>
                </div>
                </#if>
              <div>
                <p>The method is executed by the following tests:</p>
                <ul class="test-list">
                  <#list method.coveringTests as test>
                    <li><code>${test}</code></li>
                  </#list>
                </ul>
              </div>
            </div>
          </#list>
        </div>
        </div>
      </li>
    </ul>
  </section>
</body>

</html>