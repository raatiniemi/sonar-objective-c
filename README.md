# SonarQube Plugin for Objective-C

[![pipeline status](https://gitlab.com/raatiniemi/sonar-objective-c/badges/master/pipeline.svg)](https://gitlab.com/raatiniemi/sonar-objective-c/commits/master)
[![quality gate](https://sonarcloud.io/api/project_badges/measure?project=me.raatiniemi.sonarqube%3Aobjective-c&metric=alert_status)](https://sonarcloud.io/dashboard?id=me.raatiniemi.sonarqube%3Aobjective-c)
[![code test coverage](https://sonarcloud.io/api/project_badges/measure?project=me.raatiniemi.sonarqube%3Aobjective-c&metric=coverage)](https://sonarcloud.io/dashboard?id=me.raatiniemi.sonarqube%3Aobjective-c)
[![technical dept](https://sonarcloud.io/api/project_badges/measure?project=me.raatiniemi.sonarqube%3Aobjective-c&metric=sqale_index)](https://sonarcloud.io/dashboard?id=me.raatiniemi.sonarqube%3Aobjective-c)

This repository is a fork of the open source [SonarQube Plugin for Objective-C](https://github.com/Backelite/sonar-objective-c). It provides modifications and extra features needed for our internal use.

*Releases available from this repository are compatible with SonarQube 7.1, and above.*

## Features

| Feature | Supported | Details |
|---|---|:---:|
| Complexity | YES | Uses [Lizard](https://github.com/terryyin/lizard) |
| Design | NO | |
| Documentation | YES | |
| Duplications | NO | |
| Issues | YES | Uses [OCLint](http://docs.oclint.org/en/dev/intro/installation.html) |
| Size | YES | |
| Tests | YES | |
| Code coverage | YES | Uses [slather](https://github.com/SlatherOrg/slather) |

## Installation

It's recommended to install all of the dependencies without root access for
security reasons. Also, install necessary gems for each project, via `bundler`,
to reduce version conflicts, etc.

### Prerequisites

* A Mac with Xcode installed.
* [HomeBrew](http://brew.sh)
* [PyPi](https://pypi.org/)
* [SonarQube](https://www.sonarqube.org/) (either local or remote installation)
* [SonarQube Scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner)
  (`brew install sonar-scanner`)
* [OCLint](http://oclint.org/) (`brew cask install oclint`)
* [Lizard](https://github.com/terryyin/lizard) (`pip install --user lizard`)
* [Bundler](https://bundler.io/) (`gem install --user-install bundler`)

*Both OCLint and Lizard are optional, however, if the dependency is not
installed data will be missing from the generated analysis.*

#### Configure Bundler

The default behaviour for Bundler is to install gems in a system-wide directory,
i.e. root access is required.

In order to get Bundler to install dependencies without root access, it have to
be configured with a new path, `bundle config --global path ~/.bundle/gems`.

### Install plugin on server
* Download the [release from bintray](https://bintray.com/raatiniemi/sonar-objective-c-plugin)
* Copy the `jar` into the `$SONARQUBE_HOME/extensions/plugins` directory
* Restart the SonarQube server

### Project configuration
* Copy [sonar-project.properties](sample/sonar-project.properties) into your project root folder
* Modify the `sonar-project.properties` file to match your project setup
* Configure your project according to the [`Fastfile` example](sample/Fastfile) and [`Gemfile` example](sample/Gemfile)

## Release history

### 0.7.5
* Categorize OCLint rules to code smell, bug, and vulnerability
* Move Cobertura, Lizard, OCLint, and surefire into separate modules
* Support SonarQube 7.3

### 0.7.4
* [Publish packaged plugin release to bintray](https://bintray.com/raatiniemi/sonar-objective-c-plugin)

### 0.7.3
* Include resources when building plugin, was not included since converting to Gradle
* Use [SonarCloud](https://sonarcloud.io) to analyse project

### 0.7.2
* Include dependencies when building plugin, was not included since converting to Gradle
* Reduce technical dept in Squid sensor
* Automate update of version when building release
* Improve language filter to reduce warnings

### 0.7.1
* Migrate project to Gradle
* Ensure OCLint rule exist before creating issue
* Improve documentation for installation

### 0.7.0
* Remove support for FauxPas
* Drop support for SonarQube versions prior to 7.1
* Improve configuration key and default report path for Lizard
* Improve configuration key and default report path for Cobertura
* Improve configuration key and default report path for OCLint
* Use distinct configuration key for Surefire reports
* Allow Surefire default report path to be specified from web interface
* Improve default reports path for Surefire

### 0.6.4
* Replace `run-sonar.sh` with preferred `fastlane` configuration with dependencies via `Gemfile`
* Only include Objective-C language files from Lizard, Cobertura, Surefire, and OCLint
* Include test coverage for project to SonarQube analysis

### 0.6.3 (detached from backelite project)
* Detached from backelite project (no active maintainers)
* Ensure compatibility with SonarQube 7.0
* Update Lizard complexity report parsing to use new API
* Remove use of deprecated metrics from Lizard complexity reports
* Warn instead of throwing uncatched exception if Lizard complexity report XML
  file is not available
* Ensure OCLint violation reports are properly parsed and sent to SonarQube
* Update available rules for OCLint
* Include description from OCLint rules
* Update Surefire report parsing to use new API
* Update Cobertura report parsing to use new API
* Remove support for legacy code coverage

## Contributing

Feel free to contribute to this plugin by issuing pull requests to this repository or to the [original one](https://github.com/Backelite/sonar-objective-c).

## License

SonarQube Plugin for Objective-C is released under the [GNU LGPL 3 license](http://www.gnu.org/licenses/lgpl.txt).
