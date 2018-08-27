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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.profiles.ProfileImporter;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.utils.ValidationMessages;

import java.io.Reader;

public final class OCLintProfileImporter extends ProfileImporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(OCLintProfileImporter.class);
    private static final String UNABLE_TO_LOAD_DEFAULT_PROFILE = "Unable to load default OCLint profile";

    private final XMLProfileParser profileParser;

    public OCLintProfileImporter(final XMLProfileParser xmlProfileParser) {
        super(OCLintRulesDefinition.REPOSITORY_KEY, OCLintRulesDefinition.REPOSITORY_NAME);

        setSupportedLanguages("objc");
        profileParser = xmlProfileParser;
    }

    @Override
    public RulesProfile importProfile(final Reader reader, final ValidationMessages messages) {
        final RulesProfile profile = profileParser.parse(reader, messages);

        if (null == profile) {
            messages.addErrorText(UNABLE_TO_LOAD_DEFAULT_PROFILE);
            LOGGER.error(UNABLE_TO_LOAD_DEFAULT_PROFILE);
        }

        return profile;
    }
}
