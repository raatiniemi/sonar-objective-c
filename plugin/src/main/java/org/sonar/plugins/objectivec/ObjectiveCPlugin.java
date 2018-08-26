/**
 * backelite-sonar-objective-c-plugin - Enables analysis of Objective-C projects into SonarQube.
 * Copyright © 2012 OCTO Technology, Backelite (${email})
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sonar.plugins.objectivec;

import org.sonar.api.Plugin;
import org.sonar.plugins.objectivec.complexity.LizardSensor;
import org.sonar.plugins.objectivec.core.ObjectiveC;
import org.sonar.plugins.objectivec.coverage.CoberturaSensor;
import org.sonar.plugins.objectivec.surefire.SurefireSensor;
import org.sonar.plugins.objectivec.violations.ObjectiveCProfile;
import org.sonar.plugins.objectivec.violations.oclint.OCLintProfile;
import org.sonar.plugins.objectivec.violations.oclint.OCLintProfileImporter;
import org.sonar.plugins.objectivec.violations.oclint.OCLintRulesDefinition;
import org.sonar.plugins.objectivec.violations.oclint.OCLintSensor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectiveCPlugin implements Plugin {
    @Override
    public void define(@Nonnull Context context) {
        List<Class> extensions = new ArrayList<>();
        extensions.add(ObjectiveC.class);
        extensions.add(ObjectiveCSquidSensor.class);
        extensions.add(ObjectiveCProfile.class);
        extensions.add(SurefireSensor.class);
        extensions.add(CoberturaSensor.class);
        extensions.add(OCLintRulesDefinition.class);
        extensions.add(OCLintSensor.class);
        extensions.add(OCLintProfile.class);
        extensions.add(OCLintProfileImporter.class);
        extensions.add(LizardSensor.class);

        context.addExtensions(Collections.unmodifiableCollection(extensions));
    }

    // Global Objective C constants
    public static final String FALSE = "false";

    public static final String FILE_SUFFIXES_KEY = "sonar.objectivec.file.suffixes";
    public static final String FILE_SUFFIXES_DEFVALUE = "h,m,mm";

    public static final String PROPERTY_PREFIX = "sonar.objectivec";

    public static final String TEST_FRAMEWORK_KEY = PROPERTY_PREFIX + ".testframework";
    public static final String TEST_FRAMEWORK_DEFAULT = "ghunit";

}
