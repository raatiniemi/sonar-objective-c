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
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.utils.log.LogTester;
import org.sonar.api.utils.log.LoggerLevel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class SensorPersistenceTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public final LogTester logTester = new LogTester();

    private SensorContextTester sensorContext;
    private SampleSensorPersistence sensorPersistence;

    private FilePredicate filePredicate;

    @Before
    public void setUp() {
        logTester.setLevel(LoggerLevel.DEBUG);

        sensorContext = SensorContextTester.create(temporaryFolder.getRoot());
        sensorPersistence = SampleSensorPersistence.create(sensorContext);

        filePredicate = sensorContext.fileSystem().predicates().hasPath("basename");
    }

    @Nonnull
    private DefaultInputFile createFileForLanguage(@Nullable String language) {
        return new TestInputFileBuilder(sensorContext.module().key(), "basename")
                .initMetadata("1")
                .setLanguage(language)
                .build();
    }

    private void addFileToFileSystem(@Nonnull InputFile inputFile) {
        sensorContext.fileSystem().add(inputFile);
    }

    @Test
    public void buildInputFile_withoutAvailablePath() {
        Optional<InputFile> value = sensorPersistence.buildInputFile(filePredicate, "basename");

        assertFalse(value.isPresent());
        assertTrue(logTester.logs(LoggerLevel.WARN).contains("No path available for basename"));
    }

    @Test
    public void buildInputFile_withoutAvailableLanguage() {
        addFileToFileSystem(createFileForLanguage(null));

        Optional<InputFile> value = sensorPersistence.buildInputFile(filePredicate, "basename");

        assertFalse(value.isPresent());
        assertTrue(logTester.logs(LoggerLevel.DEBUG).contains("No language is available for basename"));
    }

    @Test
    public void buildInputFile_withUnknownLanguage() {
        addFileToFileSystem(createFileForLanguage("swift"));

        Optional<InputFile> value = sensorPersistence.buildInputFile(filePredicate, "basename");

        assertFalse(value.isPresent());
        assertTrue(logTester.logs(LoggerLevel.DEBUG).contains("basename belong to language swift"));
    }

    @Test
    public void buildInputFile_withFile() {
        addFileToFileSystem(createFileForLanguage("objc"));

        Optional<InputFile> value = sensorPersistence.buildInputFile(filePredicate, "basename");

        assertTrue(value.isPresent());
        assertTrue(logTester.logs().isEmpty());
    }
}
