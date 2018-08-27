/*
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
package org.sonar.plugins.objectivec.violations.oclint;

import com.google.common.io.Closeables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.utils.ValidationMessages;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public final class OCLintProfile extends ProfileDefinition {
    public static final String PROFILE_PATH = "/org/sonar/plugins/oclint/profile-oclint.xml";
    private static final Logger LOGGER = LoggerFactory.getLogger(OCLintProfile.class);

    private final OCLintProfileImporter profileImporter;

    public OCLintProfile(final OCLintProfileImporter importer) {
        profileImporter = importer;
    }

    @Override
    public RulesProfile createProfile(final ValidationMessages messages) {
        LOGGER.info("Creating OCLint Profile");
        Reader config = null;

        try {
            final InputStream resourceAsStream = getClass().getResourceAsStream(PROFILE_PATH);
            config = new InputStreamReader(resourceAsStream);

            final RulesProfile profile = profileImporter.importProfile(config, messages);
            profile.setName(OCLintRulesDefinition.REPOSITORY_NAME);
            profile.setLanguage("objc");

            return profile;
        } finally {
            Closeables.closeQuietly(config);
        }
    }
}
