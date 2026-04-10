<img src="common/src/main/resources/pinkstone-icon.png" width="128">

# Pinkstone

[![Commit Build](https://github.com/Orapdoo/Pinkstone/actions/workflows/build-commit.yml/badge.svg)](https://github.com/Orapdoo/Pinkstone/actions/workflows/build-commit.yml)
[![Tag Release Build](https://github.com/Orapdoo/Pinkstone/actions/workflows/build-tag.yml/badge.svg)](https://github.com/Orapdoo/Pinkstone/actions/workflows/build-tag.yml)

Pinkstone is a high-performance Minecraft rendering and optimization mod based on Sodium, focused on smoother frame pacing, lower rebuild stutter, and lower runtime allocation pressure.

## Public Release Status

- Fabric builds are the only published target.
- CI workflows build, package, and publish artifact bundles.
- Every commit push creates one GitHub Fabric release build.
- Tag pushes automatically compile and publish a Fabric release build with artifacts.
- Public-facing metadata and branding are configured for Pinkstone.

## Automated Releases

Pinkstone uses two release channels:

- Commit channel: every commit on branches produces an auto-generated Fabric release.
	- `Pinkstone Fabric CI <short_sha>`
- Tag channel: tagged builds produce versioned release artifacts.

This means you always get ready-to-use Fabric jar files without manual packaging.

## Downloads

- Stable releases: [GitHub Releases](https://github.com/Orapdoo/Pinkstone/releases)
- Development builds: [GitHub Actions](https://github.com/Orapdoo/Pinkstone/actions)

## Installation

Install Pinkstone the same way as other client performance mods:

1. Install Fabric Loader for your Minecraft version.
2. Download the Pinkstone jar from releases.
3. Place the jar into your `.minecraft/mods` folder.
4. Launch the game.

## Reporting Issues

Use [Issues](https://github.com/Orapdoo/Pinkstone/issues) for bug reports and feature requests. When reporting performance issues, include:

- `latest.log`
- a crash report (if present)
- GPU + driver version
- reproduction steps and mod list

## Building From Source

Pinkstone uses Gradle.

```powershell
.\gradlew.bat build
```

Build outputs are placed in `build/mods`.

### Build Requirements

- OpenJDK 25
- Gradle Wrapper (included)

## Attribution

Pinkstone is an independent fork built on top of the Sodium codebase. This project is not affiliated with the official Sodium maintainers.

## License

Except where otherwise stated (see [third-party license notices](thirdparty/NOTICE.txt)), repository content is provided under the [Polyform Shield 1.0.0](LICENSE.md).
