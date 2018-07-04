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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ReportCollectorTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final Path resourcePath = Paths.get("src", "test", "resources", "surefire");

    @Test
    public void collect_withoutExistingDirectory() {
        List<File> availableReports = ReportCollector.collect("");

        assertTrue(availableReports.isEmpty());
    }

    @Test
    public void collect_withEmptyDirectory() {
        List<File> availableReports = ReportCollector.collect(temporaryFolder.getRoot().getAbsolutePath());

        assertTrue(availableReports.isEmpty());
    }

    @Test
    public void collect_withSingleReport() {
        Path documentPath = Paths.get(resourcePath.toString(), "empty");

        List<File> availableReports = ReportCollector.collect(documentPath.toString());

        assertEquals(1, availableReports.size());
    }

    @Test
    public void collect_withMultipleReports() {
        Path documentPath = Paths.get(resourcePath.toString(), "reports");

        List<File> availableReports = ReportCollector.collect(documentPath.toString());

        assertEquals(2, availableReports.size());
    }
}
