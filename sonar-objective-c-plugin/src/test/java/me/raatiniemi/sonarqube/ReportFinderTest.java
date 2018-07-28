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
package me.raatiniemi.sonarqube;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class ReportFinderTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ReportPatternFinder reportFinder;

    @Before
    public void setUp() {
        reportFinder = ReportFinder.create(temporaryFolder.getRoot());
    }

    @Nonnull
    private Optional<File> createFile(@Nonnull String filename) {
        try {
            return Optional.of(temporaryFolder.newFile(filename));
        } catch (IOException e) {
            fail(String.format("Unable to create file: %s", e.getMessage()));
            return Optional.empty();
        }
    }

    @Test
    public void findReportsMatching_withoutExistingDirectory() {
        ReportPatternFinder reportFinder = ReportFinder.create(new File("/tmp/do-not-exists"));

        Set<File> actual = reportFinder.findReportsMatching("*");

        assertTrue(actual.isEmpty());
    }

    @Test
    public void findReportsMatching_withEmptyDirectory() {
        Set<File> actual = reportFinder.findReportsMatching("*");

        assertTrue(actual.isEmpty());
    }

    @Test
    public void findReportsMatching_withFileNotMatchingPattern() {
        createFile("foobar.xml");

        Set<File> actual = reportFinder.findReportsMatching("bazquz.xml");

        assertTrue(actual.isEmpty());
    }

    @Test
    public void findReportsMatching_withFileMatchingPattern() {
        createFile("foobar.xml");
        Set<File> expected = new LinkedHashSet<>();
        expected.add(createFile("bazquz.xml").orElseThrow(IllegalStateException::new));

        Set<File> actual = reportFinder.findReportsMatching("bazquz.xml");

        assertEquals(expected, actual);
    }

    @Test
    public void findReportsMatching_withFilesMatchingPattern() {
        Set<File> expected = new LinkedHashSet<>();
        expected.add(createFile("bazquz.xml").orElseThrow(IllegalStateException::new));
        expected.add(createFile("foobar.xml").orElseThrow(IllegalStateException::new));

        Set<File> actual = reportFinder.findReportsMatching("*.xml");

        assertEquals(expected, actual);
    }

    @Test
    public void findReportMatching_withoutExistingDirectory() {
        ReportPatternFinder reportFinder = ReportFinder.create(new File("/tmp/do-not-exists"));

        Optional<File> actual = reportFinder.findReportMatching("*");

        assertFalse(actual.isPresent());
    }

    @Test
    public void findReportMatching_withEmptyDirectory() {
        Optional<File> actual = reportFinder.findReportMatching("*");

        assertFalse(actual.isPresent());
    }

    @Test
    public void findReportMatching_withFileNotMatchingPattern() {
        createFile("foobar.xml");

        Optional<File> actual = reportFinder.findReportMatching("bazquz.xml");

        assertFalse(actual.isPresent());
    }

    @Test
    public void findReportMatching_withFileMatchingPattern() {
        Optional<File> expected = createFile("bazquz.xml");
        createFile("foobar.xml");

        Optional<File> actual = reportFinder.findReportMatching("bazquz.xml");

        assertEquals(expected, actual);
    }

    @Test
    public void findReportMatching_withFilesMatchingPattern() {
        Optional<File> expected = createFile("bazquz.xml");
        createFile("foobar.xml");

        Optional<File> actual = reportFinder.findReportMatching("*.xml");

        assertEquals(expected, actual);
    }
}
