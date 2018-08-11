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

import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.plugins.objectivec.core.ObjectiveC;

import javax.annotation.Nonnull;
import java.util.Optional;

public abstract class SensorPersistence<T> implements SensorMeasurePersistence<T> {
    private final SensorContext context;
    private final FilePredicate languagePredicate;

    protected SensorPersistence(@Nonnull SensorContext context) {
        this.context = context;

        languagePredicate = context.fileSystem().predicates().hasLanguage(ObjectiveC.KEY);
    }

    @Nonnull
    protected SensorContext getContext() {
        return context;
    }

    @Nonnull
    protected Optional<InputFile> buildInputFile(@Nonnull FilePredicate filePredicate) {
        FilePredicate predicates = context.fileSystem()
                .predicates()
                .and(languagePredicate, filePredicate);

        return Optional.ofNullable(context.fileSystem().inputFile(predicates));
    }
}
