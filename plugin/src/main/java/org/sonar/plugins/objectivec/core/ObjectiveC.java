/*
 * Copyright © 2012 OCTO Technology, Backelite (${email})
 * Copyright (c) 2018 Tobias Raatiniemi
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
package org.sonar.plugins.objectivec.core;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;
import org.sonar.plugins.objectivec.ObjectiveCPlugin;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

public class ObjectiveC extends AbstractLanguage {
    public static final String KEY = "objc";

    private Configuration configuration;

    @SuppressWarnings("WeakerAccess")
    public ObjectiveC(@Nonnull Configuration configuration) {
        super(KEY, "Objective-C");

        this.configuration = configuration;
    }

    @Override
    public String[] getFileSuffixes() {
        String[] suffixes = filterEmptyStrings(configuration.getStringArray(ObjectiveCPlugin.FILE_SUFFIXES_KEY));
        if (suffixes.length == 0) {
            suffixes = StringUtils.split(ObjectiveCPlugin.FILE_SUFFIXES_DEFVALUE, ",");
        }
        return suffixes;
    }

    @Nonnull
    private String[] filterEmptyStrings(String[] suffixes) {
        return Stream.of(suffixes)
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .toArray(String[]::new);
    }
}
