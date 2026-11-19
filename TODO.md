# EBLA final-pass TODO

## Build and compatibility
- [ ] Review the new Maven CI matrix (JDK 11/21/25) and GitHub's
      existing **CodeQL default setup** scans. Do not add an advanced
      CodeQL workflow without first disabling GitHub's default setup.
- [ ] Fix any Maven artifact, plugin, or Java compiler issues that CI
      reveals.
- [x] Add minimal headless JUnit smoke tests for statistics and score ordering.
      These do not exercise the image/video pipeline.
- [ ] Add automated tests for deterministic image segmentation/entity
      extraction/lexical resolution without depending on a graphical desktop.
- [ ] Test the H2 2.5.252 schema migration/runtime behavior against sample
      datasets and EBLA's existing SQL files. Avoid a blind H2 update.
- [ ] Verify the actual **Java runtime** baseline, especially H2, JMF and
      legacy Swing dependencies. The POM's Java 8 release target alone
      does not establish Java 8 runtime compatibility.
- [ ] Audit video/AVI decoding with JMF 2.1.1e, native libraries, and
      GUI/headless behavior under supported Linux/Windows JDKs.
- [ ] Confirm compatibility with published
      `com.greatmindsworking.jedison:jedison:1.1.0` and SwingSet 4.0.13
      before upgrading across major lines.

## Release process and repository hygiene
- [x] Replace retired Sonatype OSSRH endpoints with an opt-in Central
      Publisher Portal release profile. Bundle generation is upload-disabled
      by default; publication remains manual.
- [ ] Validate a signed Central Portal release bundle locally, inspect
      the attached executable JAR and licensing, and test a Portal upload
      using a new, unpublished version when a release is actually needed.
      Do not store Portal credentials or GPG secrets in the repository.
- [ ] Retain `archive/` research artifacts and `experiences/` samples
      intentionally; do not delete these as generated build products.
- [ ] Reconcile the historical README/manual and current Maven installation
      flow after a reproducible runtime test.
- [ ] Review the sample `dbSettings` file before public distribution.
      It currently contains a local H2 URL and the default user name,
      rather than an embedded secret, but deployment-specific credentials
      should never be committed.
- [ ] Assess whether CodeQL and dependency update checks should be required
      before merging once their runs are stable.

The existing `.github/SECURITY.md` already documents the supported
`1.1.x` line on `master`; keep it rather than replacing it with a
generic template.
