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
package me.raatiniemi.sonar.objectivec.violations;

import com.google.common.io.Closeables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.utils.ValidationMessages;
import me.raatiniemi.sonar.objectivec.core.ObjectiveC;
import me.raatiniemi.sonar.oclint.OCLintProfile;
import me.raatiniemi.sonar.oclint.OCLintProfileImporter;

import java.io.InputStreamReader;
import java.io.Reader;

public class ObjectiveCProfile extends ProfileDefinition {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectiveCProfile.class);

    private final OCLintProfileImporter ocLintProfileImporter;

    public ObjectiveCProfile(final OCLintProfileImporter ocLintProfileImporter) {
        this.ocLintProfileImporter = ocLintProfileImporter;
    }

    @Override
    public RulesProfile createProfile(ValidationMessages messages) {


        LOGGER.info("Creating Objective-C Profile");

        Reader config = null;
        final RulesProfile profile = RulesProfile.create("Objective-C", ObjectiveC.KEY);
        profile.setDefaultProfile(true);

        try {
            config = new InputStreamReader(getClass().getResourceAsStream(OCLintProfile.PROFILE_PATH));
            RulesProfile ocLintRulesProfile = ocLintProfileImporter.importProfile(config, messages);
            for (ActiveRule rule : ocLintRulesProfile.getActiveRules()) {
                profile.addActiveRule(rule);
            }

            return profile;
        } finally {

            Closeables.closeQuietly(config);
        }
    }

}
