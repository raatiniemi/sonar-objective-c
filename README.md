SonarQube Plugin for Objective-C
================================

This repository is a fork of the open source [SonarQube Plugin for Objective-C](https://github.com/Backelite/sonar-objective-c). It provides modifications and extra features needed for our internal use.

### Features

| Feature | Supported | Details |
|---|---|:---:|
| Complexity | YES | Uses [Lizard](https://github.com/terryyin/lizard) |
| Design | NO | |
| Documentation | YES | |
| Duplications | YES | |
| Issues | YES | Uses [OCLint](http://docs.oclint.org/en/dev/intro/installation.html): 71 rules |
| Size | YES | |
| Tests | YES | |
| Code coverage | YES | Uses [slather](https://github.com/SlatherOrg/slather) |

### Compatibility

Releases available from this repository are compliant with SonarQube 7.1, and above.

### Download

Binary packages are available in the release section.


### Release history

### 0.7.1
* Migrate project to Gradle

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
- Detached from backelite project (no active maintainers)
- Ensure compatibility with SonarQube 7.0
- Update Lizard complexity report parsing to use new API
- Remove use of deprecated metrics from Lizard complexity reports
- Warn instead of throwing uncatched exception if Lizard complexity report XML
  file is not available
- Ensure OCLint violation reports are properly parsed and sent to SonarQube
- Update available rules for OCLint
- Include description from OCLint rules
- Update Surefire report parsing to use new API
- Update Cobertura report parsing to use new API
- Remove support for legacy code coverage

### Prerequisites

- a Mac with Xcode
- [SonarQube](http://docs.codehaus.org/display/SONAR/Setup+and+Upgrade) and [SonarQube Scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) installed ([HomeBrew](http://brew.sh) installed and ```brew install sonar-runner```)
- [OCLint](http://oclint-docs.readthedocs.io/en/stable/) installed, version 0.13.1 is recommended.
- [lizard](https://github.com/terryyin/lizard) ([PIP](https://pip.pypa.io/en/stable/installing/) installed and ```sudo pip install lizard```)
- [fastlane](https://fastlane.tools/) (installed via [bundler](https://bundler.io/) and `bundle install`)
- [slather](https://github.com/SlatherOrg/slather) (```gem install slather```). Version 2.1.0 or above (2.4.4 since Xcode 9).

### Installation (once for all your Objective-C projects)
- Download the plugin binary into the $SONARQUBE_HOME/extensions/plugins directory
- Restart the SonarQube server.

### Configuration (once per project)
- Copy [sonar-project.properties](sample/sonar-project.properties) in your Xcode project root folder (along your .xcodeproj file)
- Edit the ```sonar-project.properties``` file to match your Xcode iOS/MacOS project
- Configure your project according to the [`Fastfile` example](sample/Fastfile) and [`Gemfile` example](sample/Gemfile)

### Contributing

Feel free to contribute to this plugin by issuing pull requests to this repository or to the [original one](https://github.com/Backelite/sonar-objective-c).

### License

SonarQube Plugin for Objective-C is released under the [GNU LGPL 3 license](http://www.gnu.org/licenses/lgpl.txt).
