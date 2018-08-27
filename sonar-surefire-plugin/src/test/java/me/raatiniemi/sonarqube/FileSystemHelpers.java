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

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.internal.SensorContextTester;

import javax.annotation.Nonnull;

/**
 * Collection of file system related helper functionality.
 * <p>
 * TODO: Improve name of class, {@link FileSystemHelpers} is non-descriptive.
 */
public final class FileSystemHelpers {
    private final SensorContextTester context;

    private FileSystemHelpers(@Nonnull SensorContextTester context) {
        this.context = context;
    }

    @Nonnull
    public static FileSystemHelpers create(@Nonnull SensorContextTester context) {
        return new FileSystemHelpers(context);
    }

    @Nonnull
    private TestInputFileBuilder buildInputFile(@Nonnull String relativePath, @Nonnull String language) {
        return new TestInputFileBuilder(context.module().key(), relativePath)
                .setLanguage(language)
                .initMetadata("1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n");
    }

    @Nonnull
    public DefaultInputFile createFile(@Nonnull String relativePath, @Nonnull String language) {
        return buildInputFile(relativePath, language)
                .setType(InputFile.Type.MAIN)
                .build();
    }

    @Nonnull
    public DefaultInputFile createTestFile(@Nonnull String relativePath, @Nonnull String language) {
        return buildInputFile(relativePath, language)
                .setType(InputFile.Type.TEST)
                .build();
    }

    public void addToFileSystem(@Nonnull DefaultInputFile inputFile) {
        context.fileSystem().add(inputFile);
    }
}
