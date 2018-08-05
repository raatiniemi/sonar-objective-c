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
| Tests | YES | Uses [xctool](https://github.com/facebook/xctool), will probably switch to xcodebuild + [xcpretty](https://github.com/supermarin/xcpretty) soon |
| Code coverage | YES | Uses [slather](https://github.com/venmo/slather) |

### Compatibility

Releases available from this repository are compliant with SonarQube 5.6.x and above.

### Download

Binary packages are available in the release section.


### Release history

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
- [SonarQube](http://docs.codehaus.org/display/SONAR/Setup+and+Upgrade) and [SonarQube Runner](http://docs.codehaus.org/display/SONAR/Installing+and+Configuring+SonarQube+Runner) installed ([HomeBrew](http://brew.sh) installed and ```brew install sonar-runner```)
- [xcpretty](https://github.com/supermarin/xcpretty) (see instructions below)
- [xctool](https://github.com/facebook/xctool) ([HomeBrew](http://brew.sh) installed and ```brew install xctool```). If you are using Xcode 6, make sure to update xctool (```brew upgrade xctool```) to a version > 0.2.2.
- [OCLint](http://oclint-docs.readthedocs.io/en/stable/) installed. Version 0.11.0 recommended (0.13.0 since Xcode 9). 
- [slather](https://github.com/SlatherOrg/slather) (```gem install slather```). Version 2.1.0 or above (2.4.4 since Xcode 9).
- [lizard](https://github.com/terryyin/lizard) ([PIP](https://pip.pypa.io/en/stable/installing/) installed and ```sudo pip install lizard```)

### Installation of xcpretty with JUnit reports fix

At the time, xcpretty needs to be fixed to work with SonarQube. 

To install the fixed version, follow those steps :

	git clone https://github.com/Backelite/xcpretty.git
	cd xcpretty
	git checkout fix/duration_of_failed_tests_workaround
	gem build xcpretty.gemspec
	sudo gem install --both xcpretty-0.2.2.gem

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
