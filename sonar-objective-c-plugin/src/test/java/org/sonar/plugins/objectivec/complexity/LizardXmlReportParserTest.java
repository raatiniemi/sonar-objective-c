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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class LizardXmlReportParserTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private final Path resourcePath = Paths.get("src", "test", "resources", "lizard");

    private LizardXmlReportParser reportParser;

    @Before
    public void setup() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        reportParser = LizardXmlReportParser.create(factory.newDocumentBuilder());
    }

    @Test
    public void parse_withoutReport() {
        Path documentPath = Paths.get(resourcePath.toString(), "non-existing.xml");

        Optional<Set<LizardMeasure>> actual = reportParser.parse(documentPath.toFile());

        assertFalse(actual.isPresent());
    }

    @Test
    public void parse_withCorrectFile() {
        Path documentPath = Paths.get(resourcePath.toString(), "correctFile.xml");
        Set<LizardMeasure> expected = new LinkedHashSet<>();
        expected.add(LizardMeasure.builder()
                .setPath("TargetName/ClassName.h")
                .setNumberOfFunctions(0)
                .setComplexity(0)
                .build());
        expected.add(LizardMeasure.builder()
                .setPath("TargetName/ClassName.m")
                .setNumberOfFunctions(2)
                .setComplexity(6)
                .build());

        Optional<Set<LizardMeasure>> actual = reportParser.parse(documentPath.toFile());

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    public void parse_withIncorrectFile() {
        Path documentPath = Paths.get(resourcePath.toString(), "incorrectFile.xml");

        Optional<Set<LizardMeasure>> actual = reportParser.parse(documentPath.toFile());

        assertFalse(actual.isPresent());
    }
}
