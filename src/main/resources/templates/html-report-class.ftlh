
<html lang="en">

<head>
  <title>${className}</title>
  <link href="style.css" rel="stylesheet" />
</head>

<body>
  <header>
    <a href="index.html">[Back to Index]</a>
    <h1>${className}</h1>
    <p><span>${numberOfIssues} issue(s)</span></p>
  </header>
  <section id="class-report">
    <ul id="method-list">
      <#list methodsWithIssues as method>
      <li>
        <details class="method-report">
          <summary open><span class="method-classification ${method.classification}">${method.classification}</span><h2 class="method-header"><code>${method.method.declaration}</code></h2></summary>
          <div class="report-item">
            <p class="method-summary">
              <#if method.classification=="error">
                An internal error prevented this method from being analysed.
              <#else>
                This method is <strong class="method-classification ${method.classification}">${method.classification}</strong>.
                <#switch method.classification>
                  <#on "tested">
                    All transformations applied to the body of the method were detected by the test suite.
                  <#on "pseudo-tested">
                    It is exercised by the test suite, but when the code is modified no test case fails.
                  <#on "partially-tested">
                    It is exercised by the test suite, some body transformations are not
                    detected by the test suite while others are detected.
                </#switch>
              </#if>
            </p>
          </div>
          <div class="report-item">
            <h3>Transformations</h3>
              <#assign non_detected_mutations = method.nonDetectedMutations>
              <#if non_detected_mutations?has_content>
                <p>No test case failed when the entire body of the method was replaced by:</p>
                <ul>
                <#list non_detected_mutations as mutation>
                  <li>${mutation.description?no_esc}</li>
                </#list>
                </ul>
              </#if>
                <#assign detected_mutations = method.detectedMutations>
                <#if detected_mutations?has_content>
                  <p>At least one test case failed when the entire body of the method was replaced by:</p>
                  <ul>
                  <#list detected_mutations as mutation>
                    <li>
                      <p>${mutation.description?no_esc}</p>
                      <p>This transformation was detected by the following test case(s):</p>
                      <ul>
                      <#list mutation.detectingTests as test>
                        <li><code>${test}</code></li>
                      </#list>
                      </ul>
                    </li>
                  </#list>
                  </ul>
                </#if>
                <#assign error_mutations = method.mutationsWithError>
                <#if error_mutations?has_content>
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
          </div>
          <div class="report-item">
            <h3>Tests</h3>
            <p>The method is executed by the following tests:</p>
            <ul class="test-list">
              <#list method.coveringTests as test>
                <li><code>${test}</code></li>
              </#list>
            </ul>
          </div>
        </details>
      </li>
      </#list>
    </ul>
  </section>
</body>

</html>