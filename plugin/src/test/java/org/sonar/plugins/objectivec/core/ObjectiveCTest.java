/*
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.plugins.objectivec.ObjectiveCPlugin;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ObjectiveCTest {
    private MapSettings settings;
    private ObjectiveC language;

    @Before
    public void setUp() {
        PropertyDefinitions propertyDefinitions = new PropertyDefinitions(
                PropertyDefinition.builder(ObjectiveCPlugin.FILE_SUFFIXES_KEY)
                        .multiValues(true)
                        .build()
        );
        settings = new MapSettings(propertyDefinitions);
        language = new ObjectiveC(settings.asConfig());
    }

    @Test
    public void getFileSuffixes_withDefaultFileSuffixes() {
        String[] expected = {"h", "m", "mm"};

        String[] actual = language.getFileSuffixes();

        assertEquals(expected, actual);
    }

    @Test
    public void getFileSuffixes_withFileSuffixes() {
        String[] languageSuffixes = {"h", "m"};
        settings.setProperty(ObjectiveCPlugin.FILE_SUFFIXES_KEY, languageSuffixes);

        String[] actual = language.getFileSuffixes();

        assertEquals(languageSuffixes, actual);
    }
}
