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
package org.sonar.plugins.objectivec.surefire;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class SurefireParserTest {
    private final Path resourcePath = Paths.get("src", "test", "resources", "surefire");

    @Test
    public void testParseFiles_withEmptyReport() {
        Path documentPath = Paths.get(resourcePath.toString(), "empty", "TEST-empty.xml");
        File[] availableReports = {documentPath.toFile()};

        List<TestReport> testReports = SurefireParser.parseFiles(availableReports);

        assertEquals(1, testReports.size());
    }

    @Test
    public void testParseFiles_withErrorReport() {
        File[] availableReports = {
                Paths.get(resourcePath.toString(), "error", "TEST-error.xml").toFile()
        };

        List<TestReport> testReports = SurefireParser.parseFiles(availableReports);

        assertEquals(1, testReports.size());
    }

    @Test
    public void testParseFiles_withOneTestCaseReport() {
        File[] availableReports = {
                Paths.get(resourcePath.toString(), "reports", "TEST-one-test-case.xml").toFile()
        };

        List<TestReport> testReports = SurefireParser.parseFiles(availableReports);

        assertEquals(1, testReports.size());
    }

    @Test
    public void testParseFiles_withMultipleTestCasesReport() {
        File[] availableReports = {
                Paths.get(resourcePath.toString(), "reports", "TEST-one-test-case.xml").toFile()
        };

        List<TestReport> testReports = SurefireParser.parseFiles(availableReports);

        assertEquals(1, testReports.size());
    }

    @Test
    public void testParseFiles_withReports() {
        Path reportPath = Paths.get(resourcePath.toString(), "reports");
        File[] availableReports = {
                Paths.get(reportPath.toString(), "TEST-one-test-case.xml").toFile(),
                Paths.get(reportPath.toString(), "TEST-multiple-test-cases.xml").toFile()
        };

        List<TestReport> testReports = SurefireParser.parseFiles(availableReports);

        assertEquals(2, testReports.size());
    }
}
