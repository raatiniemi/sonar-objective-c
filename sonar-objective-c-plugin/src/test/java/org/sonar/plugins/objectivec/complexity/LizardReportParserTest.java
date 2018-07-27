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
package org.sonar.plugins.objectivec.complexity;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class LizardReportParserTest {
    private final Path resourcePath = Paths.get("src", "test", "resources", "lizard");
    private final LizardReportParser reportParser = new LizardReportParser();

    @Test
    public void parseReportShouldReturnMapWhenXMLFileIsCorrect() {
        Path documentPath = Paths.get(resourcePath.toString(), "correctFile.xml");
        Set<LizardMeasure> expected = new LinkedHashSet<>();
        expected.add(LizardMeasure.builder()
                .setPath("App/Controller/Accelerate/AccelerationViewController.h")
                .setNumberOfFunctions(0)
                .setComplexity(0)
                .build());
        expected.add(LizardMeasure.builder()
                .setPath("App/Controller/Accelerate/AccelerationViewController.m")
                .setNumberOfFunctions(2)
                .setComplexity(6)
                .build());

        Collection<LizardMeasure> actual = reportParser.parseReport(documentPath.toFile());

        assertFalse(actual.isEmpty());
        assertEquals(expected, actual);
    }

    @Test
    public void parseReportShouldReturnNullWhenXMLFileIsIncorrect() {
        Path documentPath = Paths.get(resourcePath.toString(), "incorrectFile.xml");

        Collection<LizardMeasure> actual = reportParser.parseReport(documentPath.toFile());

        assertTrue(actual.isEmpty());
    }
}
