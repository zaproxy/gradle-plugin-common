# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
- Configure Spotless' Java extension with the ZAP license and Google Java Format (AOSP).
- Configure the `JavaCompile` tasks to use UTF-8, enable all warnings, and handle warnings as errors.

### Changed
- Recommended minimum Gradle version is now 8.2.

## [0.1.0] - 2023-03-21
### Added
- A Spotless formatter step that formats Java properties files.


[Unreleased]: https://github.com/zaproxy/gradle-plugin-common/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/zaproxy/gradle-plugin-common/compare/ca4e8161eb1ebe85f68cb78de2e26a1cd887732b...v0.1.0
