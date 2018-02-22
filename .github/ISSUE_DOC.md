#### Characteristics
- **Issue Type**: [bug, feature, test report]
Type of reported issue. It could be:
  - Bug: issue perceived by the reporter as a potential bug, which has to be confirmed by assignee;
  - Feature: issue describing a new requested functionality or a non-functional property to be supported.
  - Test report
Above issue type can be further refined, in the case of bugs, by adopting a number of
predefined tags (taken from a proposed STAMP tag cloud), including REGRESSION, CONFIGURATION, PERFORMANCE
- **Reproducibility**: [always, sometimes, random]
- **Severity**: [feature, minor, major, crash, block]
- **Tool/Service/Component**: [name, version]
e.g. "PIT 1.2.0, Descartes 0.2-SNAPSHOT, PITMP 1.0.1""
- **Execution Environment**: [platform, OS, etc]
Description of the execution environment, e.g "Linux OpenSuse Tumbleweed" or "Linux Ubuntu 16.04.1"
including information about the version of the executed STAMP tools/services and their
local dependencies (in case of standalone execution)
- **Reporter**: [name, mail]
Reference information of the reporter, so the assignee can contact back for further issue refinement, if needed.

#### Description
A detailed description of the issue.
For features, this section should provide a functional description of the required functionality. When describing features formally as user stories, the description can include this formal syntax:
As a <role>, I can <activity> so that <business value>
With this form, all the stakeholders involved in the requirement analysis can understand both the role of the user and the business benefit that the new functionality provides.
For bugs, this section should describe as well:
  - the observed execution behavior and obtained results;
  - the expected execution behavior and results.

#### Steps to reproduce
A detailed description, step-by-step of the procedure followed by the reporter to reproduce the bug reported.

#### Other files and URLs
Additional visual proofs, such as snapshots, providing additional visual information of the bug can be included,
as well as input files required for reproducing the bug or URLs pointed to the sources of such inputs.

#### Relationships
A list of relationships to other issues. In case of features, these relationships can be used to structure them,
grouping related features. Possible relationships:
  - Child of / Parent of
  - Related to
  - Depends on
