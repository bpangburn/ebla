# IDE setup

The current Maven POM is the source of truth. Import the project as
**Existing Maven Projects** in Eclipse/m2e, or open the POM as a Maven
project in NetBeans/IntelliJ.

- The POM currently declares Java 8 as its compiler release target. The
  actual Java runtime requirements of legacy JMF and H2 dependencies must
  be validated independently (see [TODO.md](TODO.md)).
- The tracked customized Java 8 compiler preference file
  `.settings/org.eclipse.jdt.core.prefs` is intentionally retained.
- Generated Eclipse `.classpath`, `.project`, generic encoding/m2e
  settings, plus `target/` and `bin/`, should not be tracked.
- `.editorconfig` establishes UTF-8, LF and two-space soft indentation.
  Install an EditorConfig plugin if the editor requires one.
- This is a historical GUI/video application, not a conventional headless
  service. Some end-to-end tests require the legacy AVI dataset/GUI setup.
