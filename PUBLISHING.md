# Publishing EBLA through the Sonatype Central Portal

EBLA is primarily a legacy research project. Publication is optional.
Normal CI and local builds do **not** publish, and the `release` profile
keeps uploads disabled unless explicitly overridden. No publishing secrets
belong in the repository or automated pull-request builds.

## Prerequisites before a real release

1. Sign into [Central Publisher Portal](https://central.sonatype.com/) and
   verify publishing permissions for the existing
   `com.greatmindsworking.ebla` namespace. OSSRH was retired June 30, 2025.
   If access is missing, contact Sonatype Central Support.
2. For uploading, generate a [Portal user token](https://central.sonatype.org/publish/generate-portal-token/).
   Put the token **only** in your local `~/.m2/settings.xml`, using
   Maven server ID `central`:

   ```xml
   <settings>
     <servers>
       <server>
         <id>central</id>
         <username>YOUR_PORTAL_TOKEN_USERNAME</username>
         <password>YOUR_PORTAL_TOKEN_PASSWORD</password>
       </server>
     </servers>
   </settings>
   ```

   Protect the local settings file, e.g. `chmod 600 ~/.m2/settings.xml`.
   Do not commit credentials or set a publishing token in GitHub Actions.
3. Configure a local GPG signing key and publish its public key to a
   publicly accessible key server. Check with
   `gpg --list-secret-keys --keyid-format LONG`.
   Signing uses local GPG/gpg-agent; override the selected signing key
   with `-Dgpg.keyname=KEY_ID` when needed.
4. Use Maven 3.9.x and a supported JDK (JDK 21 is a practical release-build
   choice). Bytecode targets Java 8, but **runtime** compatibility of all
   JMF/H2/GUI/video components has not been independently established.
5. Choose an **unpublished, non-SNAPSHOT** release version in the POM.
   Maven Central releases are immutable. Do not reuse an existing version
   and do not change the POM version solely to test CI.

## Normal builds

```sh
mvn -B -ntp clean verify
```

This executes CI tests without signing, packaging a release bundle, or
uploading artifacts.

## Generate and inspect a signed bundle without uploading

After configuring GPG and preparing a release version:

```sh
mvn -B -ntp -Prelease -Dcentral.skipUpload=true clean deploy
unzip -l target/central-publishing/central-bundle.zip
```

The `central-publishing-maven-plugin` 0.12.0 stages and bundles locally
when `skipUpload=true`. It creates checksums automatically; the release
profile uses the source, Javadoc, and GPG plugins to attach required
artifacts/signatures. Ensure the POM, binary JAR, sources JAR, Javadoc JAR,
and all signatures are present. Bundle generation does **not** demonstrate
that the Central Portal will accept or validate the deployment.

**Review executable packaging:** The existing Maven Assembly plugin also
builds an attached `jar-with-dependencies`. Inspect this extra artifact,
included third-party license notices, file size, and whether the classifier
should be distributed in the next Central release. Do not remove it
without deciding how to preserve EBLA's research-era executable packaging.

## Deliberate upload and manual publication

Only when ready to make a real, immutable release (and with a token stored
locally), run:

```sh
mvn -B -ntp -Prelease -Dcentral.skipUpload=false clean deploy
```

This uploads to the Portal but **does not automatically publish**
(`autoPublish=false`). Check deployment status and validation errors at
[Central Portal Deployments](https://central.sonatype.com/publishing/deployments).
After reviewing the exact coordinates/artifacts, select **Publish**
manually. Finally, verify that the new version is visible in
[Maven Central](https://central.sonatype.com/search?q=com.greatmindsworking.ebla)
and resolves in an independent consumer build.

### SNAPSHOT releases

The current POM version is `1.1.0-SNAPSHOT`. Central Portal supports
SNAPSHOT publishing separately, but it must first be enabled for the
namespace in Portal settings. This project does not need routine SNAPSHOT
deployment. The obsolete OSSRH `distributionManagement` entries have been
removed; avoid trying to deploy to the retired service.

### References

- https://central.sonatype.org/publish/publish-portal-maven/
- https://central.sonatype.org/publish/requirements/
- https://central.sonatype.org/publish/requirements/gpg/
- https://central.sonatype.org/publish/publish-portal-snapshots/
