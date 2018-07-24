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
package org.sonar.plugins.objectivec.violations.oclint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ReportParserTest {
    private final Path resourcePath = Paths.get("src", "test", "resources", "oclint");

    private ReportParser parser;

    @Before
    public void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        parser = ReportParser.create(factory.newDocumentBuilder());
    }

    @Test
    public void parse_withEmptyDocument() {
        Path documentPath = Paths.get(resourcePath.toString(), "empty.xml");

        Optional<List<Violation>> actual = parser.parse(documentPath.toFile());

        assertTrue(actual.isPresent());
        assertTrue(actual.get().isEmpty());
    }

    @Test
    public void parse_withSampleDocument() {
        Path documentPath = Paths.get(resourcePath.toString(), "oclint.xml");
        List<Violation> expected = buildExpectedViolationsForSample();

        Optional<List<Violation>> actual = parser.parse(documentPath.toFile());

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Nonnull
    private List<Violation> buildExpectedViolationsForSample() {
        List<Violation> expected = new ArrayList<>();
        Violation violation;

        violation = Violation.builder()
                .setPath("RASqlite/RASqlite.m")
                .setStartLine(281)
                .setRule("deep nested block")
                .setMessage("Block depth of 6 exceeds limit of 5")
                .build();
        expected.add(violation);

        violation = Violation.builder()
                .setPath("RASqlite/RASqlite.m")
                .setStartLine(305)
                .setRule("ivar assignment outside accessors or init")
                .build();
        expected.add(violation);

        violation = Violation.builder()
                .setPath("RASqlite/RASqlite.m")
                .setStartLine(707)
                .setRule("unused method parameter")
                .setMessage("The parameter 'commit' is unused.")
                .build();
        expected.add(violation);

        return expected;
    }
}
