# IDE setup

The current Maven POM is the source of truth. Import the project as
**Existing Maven Projects** in Eclipse/m2e, or open the POM as a Maven
project in NetBeans/IntelliJ.

- The POM currently declares Java 8 as its compiler release target. The
  actual Java runtime requirements of legacy JMF and H2 dependencies must
  be validated independently (see [TODO.md](TODO.md)).
- Eclipse `.project`, `.classpath`, and all `.settings/` directories are
  local generated metadata and should not be committed. Maven/m2e is the
  source of compiler configuration; verify the Java release matches the POM.
  Custom Eclipse diagnostic preferences are local to each developer.
- After pulling this cleanup, use **Maven → Update Project** if necessary.
  If Eclipse project registration is stale, remove it from the workspace
  without deleting project contents on disk, then re-import as a Maven project.
  Do not commit generated `bin/` or `target/` output.
- `.editorconfig` establishes UTF-8, LF and two-space soft indentation.
  Install an EditorConfig plugin if the editor requires one.
- This is a historical GUI/video application, not a conventional headless
  service. Some end-to-end tests require the legacy AVI dataset/GUI setup.
