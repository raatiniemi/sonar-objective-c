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

package me.raatiniemi.sonar.core;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.sonar.api.utils.log.LogTester;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class XmlReportParserTest {
    @Rule
    public final LogTester logTester = new LogTester();

    private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private final Path resourcePath = Paths.get("src", "test", "resources");

    private SampleXmlReportParser reportParser;

    @Before
    public void setUp() throws Exception {
        reportParser = SampleXmlReportParser.create(factory.newDocumentBuilder());
    }

    @Test
    public void parse_withoutReportFile() {
        File file = new File("unknown-filename");

        Optional<SampleReport> value = reportParser.parse(file);

        assertFalse(value.isPresent());
        assertTrue(logTester.logs().contains("No XML report exist at path: unknown-filename"));
    }

    @Test
    public void parse_withEmptyReportFile() {
        Path documentPath = Paths.get(resourcePath.toString(), "empty.xml");

        Optional<SampleReport> value = reportParser.parse(documentPath.toFile());

        assertFalse(value.isPresent());
    }

    @Test
    public void parse_withReportFile() {
        Path documentPath = Paths.get(resourcePath.toString(), "report.xml");
        SampleReport report = SampleReport.from("basename", "3");

        Optional<SampleReport> value = reportParser.parse(documentPath.toFile());

        assertTrue(value.isPresent());
        assertEquals(report, value.get());
    }
}
