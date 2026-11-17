# Building the current EBLA checkout

The original [README](README.md) documents an earlier distributed JAR and
scripts workflow. In the current Git repository, the `pom.xml` is at the
root and those scripts are kept for historical reference under
`archive/scripts/`. They should **not** be treated as the supported build
instructions for this checkout.

## Maven baseline

From the repository root, use:

```sh
mvn -B -ntp clean verify
```

The POM currently specifies Java 8 as the `release` target and has
dependencies on jEDISON 1.1.0, SwingSet 4.0.12, JMF 2.1.1e, H2 2.2.224,
and Log4j 2.26.1. The proposed GitHub Actions CI matrix initially checks
compilation on JDK 11/21/25. **Java 8 bytecode output does not prove all
dependencies or GUI/video functionality run on Java 8.**

Do not invoke `-Prelease` just for ordinary builds: that profile performs
signing and legacy publishing-related packaging.

## Running EBLA

The main entry point is `com.greatmindsworking.ebla.ui.EBLAGui`.
Runtime use requires the dataset, database configuration, video libraries,
and a GUI environment. Consult the historical README and included manual
for dataset layout and original EBLA workflow, but review old commands
for compatibility with the current Maven-produced artifacts.

GitHub Actions compile/package checks are **not** equivalent to processing
the research videos or exercising EBLA's database/UI end-to-end.

## Security and maintenance

See [SECURITY.md](.github/SECURITY.md) for private reporting and
[TODO.md](TODO.md) for deferred dependency, publishing and runtime testing
work.
