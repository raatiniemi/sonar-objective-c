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
package org.sonar.plugins.objectivec.coverage;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class ReportCollectorTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Nonnull
    private String pathToTemporaryDirectory() {
        return temporaryFolder.getRoot().getAbsolutePath();
    }

    private void addFileToFs(@Nonnull String filename) {
        try {
            temporaryFolder.newFile(filename);
        } catch (IOException e) {
            fail(String.format("Unable to create required file: %s", e.getMessage()));
        }
    }

    @Test
    public void collect_withEmptyDirectory() {
        List<File> availableReports = ReportCollector.collect(pathToTemporaryDirectory(), "cobertura.xml");

        assertTrue(availableReports.isEmpty());
    }

    @Test
    public void collect_withoutMatchingReports() {
        addFileToFs("filename.xml");

        List<File> availableReports = ReportCollector.collect(pathToTemporaryDirectory(), "cobertura.xml");

        assertTrue(availableReports.isEmpty());
    }

    @Test
    public void collect_withReport() {
        addFileToFs("cobertura.xml");

        List<File> availableReports = ReportCollector.collect(pathToTemporaryDirectory(), "cobertura.xml");

        assertEquals(1, availableReports.size());
    }

    @Test
    public void collect_withReports() {
        addFileToFs("exclude-this-file.xml");
        addFileToFs("cobertura-ut.xml");
        addFileToFs("cobertura-it.xml");

        List<File> availableReports = ReportCollector.collect(pathToTemporaryDirectory(), "cobertura*.xml");

        assertEquals(2, availableReports.size());
    }
}
